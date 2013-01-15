/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import us.wthr.jdem846.annotations.Destroy;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.OnShutdown;
import us.wthr.jdem846.annotations.Service;
import us.wthr.jdem846.annotations.ServiceRuntime;
import us.wthr.jdem846.exception.AnnotationIndexerException;
import us.wthr.jdem846.exception.ServiceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ServiceKernel extends Thread
{
	private static Log log = Logging.getLog(ServiceKernel.class);
	private static ServiceKernel instance;

	private Map<String, ServiceThread> services = new HashMap<String, ServiceThread>();
	private List<ServiceThreadListener> serviceThreadListeners = new LinkedList<ServiceThreadListener>();

	private ThreadGroup serviceThreadGroup;

	private boolean shutdownInitiated = false;

	public ServiceKernel()
	{
		setName("us.wthr.jdem846.ServiceKernel");
		if (instance == null) {
			instance = this;
		}
	}

	public void run()
	{
		log.info("Starting ServiceKernel Thread");

		while (serviceThreadGroup.activeCount() > 0) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				log.error("Interruption in ServerKernel thread: " + e.getMessage(), e);
			}
		}

		log.warn("Leaving ServiceKernel Thread");

		if (shutdownInitiated == false) {
			try {
				invokeOnShutdowns();
			} catch (ServiceException e) {
				log.error("Exception during service OnShutdown: " + e.getMessage(), e);
			}
		}

		try {
			invokeDestroys();
		} catch (ServiceException e) {
			log.error("Exception during service destruction: " + e.getMessage(), e);
		}

		fireServiceThreadListeners();
	}

	public static void initiateApplicationShutdown()
	{
		log.info("Initiating application shutdown");

		if (ServiceKernel.instance == null) {
			log.warn("ServiceKernel instance is null. Cannot initiate shutdown");
			return;
		}

		try {
			ServiceKernel.instance.invokeOnShutdowns();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void initializeServices() throws ServiceException
	{

		log.info("Initializing Services...");

		serviceThreadGroup = new ThreadGroup("JDEM Service Thread Group");

		List<Class<?>> clazzList = null;

		try {
			clazzList = DiscoverableAnnotationIndexer.getAnnotatedClasses(Service.class.getName());
		} catch (AnnotationIndexerException ex) {
			throw new ServiceException("Failed to retrieve service classes: " + ex.getMessage(), ex);
		}

		try {
			if (clazzList != null) {
				for (Class<?> clazz : clazzList) {
					initializeService((Class<AppService>) clazz);
				}
			}
		} catch (Exception ex) {
			throw new ServiceException("Failure in service initialization: " + ex.getMessage(), ex);
		}

	}

	public void invokeOnShutdowns() throws ServiceException
	{
		log.info("Invoking OnShutdown on services...");

		shutdownInitiated = true;

		for (String name : services.keySet()) {

			ServiceThread serviceThread = services.get(name);

			try {
				serviceOnShutdown(name, serviceThread.getService());
			} catch (ServiceException ex) {
				log.info("Error during service OnShutdown: " + ex.getMessage(), ex);
			}

		}
	}

	public void invokeDestroys() throws ServiceException
	{

		log.info("Destroying services...");

		for (String name : services.keySet()) {

			ServiceThread serviceThread = services.get(name);

			try {
				serviceOnDestroy(name, serviceThread.getService());
			} catch (ServiceException ex) {
				log.info("Error during service destruction: " + ex.getMessage(), ex);
			}

		}
	}

	protected void serviceOnDestroy(String name, AppService service) throws ServiceException
	{
		log.info("Destroying service '" + name + "'");
		executeAnnotatedMethod(name, service, Destroy.class);
	}

	protected void serviceOnShutdown(String name, AppService service) throws ServiceException
	{
		log.info("OnShutdown invoke on service '" + "" + "'");
		executeAnnotatedMethod(name, service, OnShutdown.class);
	}

	protected void serviceInitialize(String name, AppService service) throws ServiceException
	{
		log.info("Initialization of service '" + name + "'");
		executeAnnotatedMethod(name, service, Initialize.class);
	}

	@SuppressWarnings("unchecked")
	protected void initializeService(Class<AppService> clazz) throws ServiceException
	{
		/*
		 * Class<AppService> clazz = null; try { clazz = (Class<AppService>)
		 * Class.forName(clazzName, true,
		 * Thread.currentThread().getContextClassLoader());
		 * //serviceClasses.put(clazzName, clazz); }
		 * catch(ClassNotFoundException ex) { //ex.printStackTrace();
		 * log.error("Failed to load class '" + clazzName + "': " +
		 * ex.getMessage(), ex); throw new ServiceException(clazzName,
		 * "Failed to load class '" + clazzName + "'", ex); }
		 */

		Service annotation = (Service) clazz.getAnnotation(Service.class);
		String name = annotation.name();
		boolean deamon = annotation.deamon();
		boolean enabled = annotation.enabled();

		if (!enabled) {
			log.info("Service is disabled: " + name);
			// System.out.println("Service is disabled: " + name);
			return;
		}

		AppService instance = null;
		try {
			instance = clazz.newInstance();
		} catch (Exception ex) {
			// ex.printStackTrace();
			log.error("Failed to create new instance of '" + clazz.getName() + "': " + ex.getMessage(), ex);
			throw new ServiceException(clazz.getName(), "Failed to create new instance of '" + clazz.getName() + "'", ex);
		}

		serviceInitialize(name, instance);

		/*
		 * Method initialize = getAnnotatedMethod(clazz, Initialize.class); if
		 * (initialize != null) { try { initialize.invoke(instance); } catch
		 * (Exception ex) { //ex.printStackTrace();
		 * log.error("Failed to invoke initialization method on '" + clazzName +
		 * "': " + ex.getMessage(), ex); throw new ServiceException(clazzName,
		 * "Failed to invoke initialization method on '" + clazzName + "'", ex);
		 * } }
		 */

		ServiceThread serviceThread = new ServiceThread(serviceThreadGroup, instance, name, deamon);

		serviceThread.addServiceRuntimeExitListener(new ServiceRuntimeExitListener()
		{
			public void onServiceRuntimeExited(String name, AppService service, boolean isRuntime)
			{
				log.info("Service '" + name + "' exited");

				try {
					serviceOnDestroy(name, service);
				} catch (ServiceException e) {
					log.error("Failed to destroy service '" + name + "': " + e.getMessage(), e);
				}

			}
		});

		serviceThread.start();

		services.put(name, serviceThread);

	}

	protected void executeAnnotatedMethod(String name, AppService service, Class annotation) throws ServiceException
	{
		Method method = getAnnotatedMethod((Class<AppService>) service.getClass(), annotation);
		if (method != null) {
			try {
				method.invoke(service);
			} catch (Exception ex) {
				log.error("Failed to invoke destroy method on service '" + name + "': " + ex.getMessage(), ex);
				throw new ServiceException(service.getClass().getCanonicalName(), "Failed to invoke destroy method on service '" + name + "'", ex);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected Method getAnnotatedMethod(Class<AppService> clazz, Class annotation)
	{
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.getAnnotation(annotation) != null) {
				return method;
			}
		}
		return null;
	}

	class ServiceThread extends Thread
	{
		private Log log = Logging.getLog(ServiceThread.class);

		private AppService service;
		private String serviceName;
		private boolean isRuntime;

		private List<ServiceRuntimeExitListener> serviceRuntimeExitListeners = new LinkedList<ServiceRuntimeExitListener>();

		public ServiceThread(ThreadGroup serviceThreadGroup, AppService service, String serviceName, boolean deamon)
		{
			super(serviceThreadGroup, serviceName);
			this.serviceName = serviceName;
			this.service = service;
			this.setDaemon(deamon);
		}

		@Override
		public void start()
		{

			super.start();

		}

		@SuppressWarnings("unchecked")
		public void run()
		{
			Method serviceRuntime = getAnnotatedMethod((Class<AppService>) service.getClass(), ServiceRuntime.class);

			if (serviceRuntime != null) {

				isRuntime = true;

				try {
					serviceRuntime.invoke(service);
				} catch (Exception ex) {
					log.error("Failure in service runtime method: " + ex.getMessage(), ex);
					ex.printStackTrace();
				}
				log.info("Leaving execution thread for service '" + serviceName + "'");

			}

			this.fireServiceRuntimeExitListeners();
		}

		public boolean isRuntime()
		{
			return isRuntime;
		}

		public AppService getService()
		{
			return service;
		}

		public String getServiceName()
		{
			return serviceName;
		}

		public void addServiceRuntimeExitListener(ServiceRuntimeExitListener listener)
		{
			this.serviceRuntimeExitListeners.add(listener);
		}

		public boolean removeServiceRuntimeExitListener(ServiceRuntimeExitListener listener)
		{
			return this.serviceRuntimeExitListeners.remove(listener);
		}

		public void fireServiceRuntimeExitListeners()
		{
			for (ServiceRuntimeExitListener listener : this.serviceRuntimeExitListeners) {
				listener.onServiceRuntimeExited(serviceName, service, isRuntime);
			}
		}

	}

	public void addServiceThreadListener(ServiceThreadListener listener)
	{
		this.serviceThreadListeners.add(listener);
	}

	public boolean removeServiceThreadListener(ServiceThreadListener listener)
	{
		return this.serviceThreadListeners.remove(listener);
	}

	public void fireServiceThreadListeners()
	{
		for (ServiceThreadListener listener : this.serviceThreadListeners) {
			listener.onServiceThreadExited();
		}
	}

	public interface ServiceRuntimeExitListener
	{
		public void onServiceRuntimeExited(String name, AppService service, boolean isRuntime);
	}

	public interface ServiceThreadListener
	{
		public void onServiceThreadExited();
	}
}
