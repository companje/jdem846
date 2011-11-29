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

package us.wthr.jdem846.render;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

import us.wthr.jdem846.AppRegistry;
import us.wthr.jdem846.DiscoverableAnnotationIndexer;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.annotations.DemColoring;
import us.wthr.jdem846.annotations.DemEngine;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.Registry;
import us.wthr.jdem846.exception.AnnotationIndexerException;
import us.wthr.jdem846.exception.RegistryException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


@Registry
public class EngineRegistry  implements AppRegistry
{
	private static Log log = Logging.getLog(EngineRegistry.class);
	
	private static Map<String, EngineInstance> engineMap = new HashMap<String, EngineInstance>();
	

	protected static void addEngineInstance(Class<?> clazz) throws ClassNotFoundException
	{
		//Class<?> clazz = Class.forName(clazzName, true, Thread.currentThread().getContextClassLoader());
		DemEngine annotation = (DemEngine) clazz.getAnnotation(DemEngine.class);

		String name = annotation.name();
		name = I18N.get(name, name);
		
		EngineInstance engineInstance = new EngineInstance(clazz);
		
		
		if (annotation.enabled()) {
			EngineRegistry.engineMap.put(annotation.identifier(), engineInstance);
			log.info("Adding render engine instance for " + clazz.getName() + ": " + annotation.name());
			//System.out.println("Adding render engine instance for " + clazzName + ": " + annotation.name());
		} else {
			log.info("Render engine is disabled: " + clazz.getName());
			//System.out.println("Render engine is disabled: " + clazzName);
		}
		
	}
	
	protected EngineRegistry()
	{
		
	}
	
	@Initialize
	public static void init() throws RegistryException
	{
		//System.out.println("Static initialization of EngineRegistry");
		log.info("Static initialization of EngineRegistry");
		
		List<Class<?>> clazzList = null;
		
		try {
			clazzList = DiscoverableAnnotationIndexer.getAnnotatedClasses(DemEngine.class.getName());
		} catch (AnnotationIndexerException ex) {
			throw new RegistryException("Failed to retrieve DemEngine classes: " + ex.getMessage(), ex);
		}
		
		try {
			if (clazzList != null) {
				for (Class<?> clazz : clazzList) {
					addEngineInstance(clazz);
				}
			}
		} catch (Exception ex) {
			throw new RegistryException("Error loading engine class: " + ex.getMessage(), ex);
		}
	}

	
	public static EngineInstance getInstance(String identifier)
	{
		return engineMap.get(identifier);
	}
	
	public static List<EngineInstance> getInstances()
	{
		List<EngineInstance> instanceList = new LinkedList<EngineInstance>();
		
		for (String identifier : engineMap.keySet()) {
			instanceList.add(engineMap.get(identifier));
		}
		
		return instanceList;
	}
	
}
