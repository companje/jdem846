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
import java.util.List;

import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.Registry;
import us.wthr.jdem846.exception.RegistryException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/**
 * Manages the detection and static initialization of component registries
 * within the application.
 * 
 * @author Kevin M. Gill
 * 
 */
public class RegistryKernel
{
	private static Log log = Logging.getLog(RegistryKernel.class);

	public RegistryKernel()
	{

	}

	public void init() throws RegistryException
	{

		List<Class<?>> clazzList = null;

		try {
			clazzList = DiscoverableAnnotationIndexer.getAnnotatedClasses(Registry.class.getName());
		} catch (Exception ex) {
			throw new RegistryException("Failed to retrieve registry classes: " + ex.getMessage(), ex);
		}

		if (clazzList != null) {
			for (Class<?> clazz : clazzList) {
				initializeRegistry(clazz);
			}
		}

	}

	protected void initializeRegistry(Class<?> clazz) throws RegistryException
	{

		boolean initMethodInvoked = false;

		if (!AppRegistry.class.isAssignableFrom(clazz)) {
			log.info("Not An AppRegistry Class: " + clazz.getName());
			return;
		}

		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.getAnnotation(Initialize.class) != null) {
				StartupLoadNotifyQueue.add("Initializing " + clazz.getName() + "." + method.getName());
				log.info("Initializing " + clazz.getName() + "." + method.getName());

				try {
					method.invoke(JDem846Properties.class);
					initMethodInvoked = true;
				} catch (Exception ex) {
					// ex.printStackTrace();
					throw new RegistryException(clazz.getName(), "Failed to invoke initialization method", ex);
				}
			}
		}

		if (!initMethodInvoked) {
			throw new RegistryException(clazz.getName(), "Initialization method not found for '" + clazz.getName() + "'");
		}

	}

}
