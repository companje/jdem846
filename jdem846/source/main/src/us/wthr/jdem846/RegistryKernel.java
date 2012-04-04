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
import java.util.List;
import java.util.Map;

import us.wthr.jdem846.annotations.Registry;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.exception.AnnotationIndexerException;
import us.wthr.jdem846.exception.RegistryException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Manages the detection and static initialization of component registries within the application.
 * 
 * @author Kevin M. Gill
 *
 */
public class RegistryKernel
{
	private static Log log = Logging.getLog(RegistryKernel.class);
	
	private Map<String, Class<AppRegistry>> registryClasses = new HashMap<String, Class<AppRegistry>>();
	
	public RegistryKernel()
	{
		
	}
	
	public void init() throws RegistryException
	{
		
		List<Class<?>> clazzList = null;
		
		try {
			clazzList = DiscoverableAnnotationIndexer.getAnnotatedClasses(Registry.class.getName());
		} catch (AnnotationIndexerException ex) {
			throw new RegistryException("Failed to retrieve registry classes: " + ex.getMessage(), ex);
		}
		
		if (clazzList != null) {
			for (Class<?> clazz : clazzList) {
				initializeRegistry((Class<AppRegistry>)clazz);
			}
		}
		
		
		/*
		try {

			AnnotationDB db = new AnnotationDB();
			URL[] urls = ClasspathUrlFinder.findClassPaths();
			for (URL url : urls) {
				log.info("Scanning Classpath URL: " + url);
				db.scanArchives(url);
			}
			//db.crossReferenceImplementedInterfaces();
			 	
			Map<String, Set<String>> annotationIndex = db.getAnnotationIndex();
			Set<String> regClasses = annotationIndex.get(Registry.class.getName());
			
			if (regClasses != null) {
				for (String clazzName : regClasses) {
					initializeRegistry(clazzName);
				}
			}
			
		} catch (Exception ex) {
			log.error("Failure in registry initialization: " + ex.getMessage(), ex);
			//ex.printStackTrace();
			throw new  RegistryException("Failure in registry initialization", ex);
		}
		*/
	}
	
	@SuppressWarnings("unchecked")
	protected void initializeRegistry(Class<AppRegistry> clazz) throws RegistryException
	{
		/*
		Class<AppRegistry> clazz = null;
		try {
			clazz = (Class<AppRegistry>) Class.forName(clazzName, true, Thread.currentThread().getContextClassLoader());
			registryClasses.put(clazzName, clazz);
		} catch(ClassNotFoundException ex) {
			ex.printStackTrace();
			throw new RegistryException(clazzName, "Failed to load class '" + clazzName + "'", ex);
		}
		*/
		
		boolean initMethodInvoked = false;
		
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.getAnnotation(Initialize.class) != null) {
				log.info("Initializing " + clazz.getName() + "." + method.getName());
				//System.out.println("RegistryKernel: Initializing " + clazzName + "." + method.getName());
				try {
					method.invoke(JDem846Properties.class);
					initMethodInvoked = true;
				} catch (Exception ex) {
					//ex.printStackTrace();
					throw new RegistryException(clazz.getName(), "Failed to invoke initialization method", ex);
				}
			}
		}
		
		if (!initMethodInvoked) {
			throw new RegistryException(clazz.getName(), "Initialization method not found for '" + clazz.getName() + "'");
		}
		
	}
	
	
}
