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

package us.wthr.jdem846.input;

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
import us.wthr.jdem846.annotations.DemEngine;
import us.wthr.jdem846.annotations.ElevationDataLoader;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.Registry;
import us.wthr.jdem846.exception.AnnotationIndexerException;
import us.wthr.jdem846.exception.RegistryException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


@Registry
@Deprecated
public class ElevationDataLoaderRegistry  implements AppRegistry
{
	private static Log log = Logging.getLog(ElevationDataLoaderRegistry.class);
	
	private static Map<String, ElevationDataLoaderInstance> instanceMap = new HashMap<String, ElevationDataLoaderInstance>();
	
	protected ElevationDataLoaderRegistry()
	{
		
	}
	
	protected static void addElevationDataLoaderInstance(Class<?> clazz) throws ClassNotFoundException
	{
		//Class<?> clazz = Class.forName(clazzName, true, Thread.currentThread().getContextClassLoader());
		ElevationDataLoader annotation = (ElevationDataLoader) clazz.getAnnotation(ElevationDataLoader.class);
		
		if (!annotation.enabled())
			return;
		
		String name = annotation.name();
		name = I18N.get(name, name);
		
		
		ElevationDataLoaderInstance instance = new ElevationDataLoaderInstance(clazz.getName(), name, annotation.identifier(), annotation.extension());
		instanceMap.put(annotation.identifier(), instance);
	}
	
	@Initialize
	public static void init() throws RegistryException
	{
		//System.out.println("Static initialization of ElevationDataLoaderRegistry");
		
		log.info("Static initialization of ElevationDataLoaderRegistry");
		
		List<Class<?>> clazzList = null;
		
		try {
			clazzList = DiscoverableAnnotationIndexer.getAnnotatedClasses(ElevationDataLoader.class.getName());
		} catch (AnnotationIndexerException ex) {
			throw new RegistryException("Failed to retrieve elevation loader classes: " + ex.getMessage(), ex);
		}
		
		try {
			if (clazzList != null) {
				for (Class<?> clazz : clazzList) {
					addElevationDataLoaderInstance(clazz);
				}
			}
		} catch (Exception ex) {
			throw new RegistryException("Error loading elevation loader class: " + ex.getMessage(), ex);
		}
		
	}
	

	
	
	public static ElevationDataLoaderInstance getInstance(String identifier)
	{
		return instanceMap.get(identifier);
	}
	
	public static List<ElevationDataLoaderInstance> getInstances()
	{
		List<ElevationDataLoaderInstance> instanceList = new LinkedList<ElevationDataLoaderInstance>();
		for (String identifier : instanceMap.keySet()) {
			instanceList.add(instanceMap.get(identifier));
		}
		
		return instanceList;
	}
	
}
