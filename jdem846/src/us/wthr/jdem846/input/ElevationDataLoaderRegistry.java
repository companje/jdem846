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
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.annotations.ElevationDataLoader;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.Registry;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


@Registry
public class ElevationDataLoaderRegistry  implements AppRegistry
{
	private static Log log = Logging.getLog(ElevationDataLoaderRegistry.class);
	
	private static Map<String, ElevationDataLoaderInstance> instanceMap = new HashMap<String, ElevationDataLoaderInstance>();
	
	protected ElevationDataLoaderRegistry()
	{
		
	}
	
	protected static void addElevationDataLoaderInstance(String clazzName) throws ClassNotFoundException
	{
		Class<?> clazz = Class.forName(clazzName, true, Thread.currentThread().getContextClassLoader());
		ElevationDataLoader annotation = (ElevationDataLoader) clazz.getAnnotation(ElevationDataLoader.class);
		
		String name = annotation.name();
		name = I18N.get(name, name);
		
		ElevationDataLoaderInstance instance = new ElevationDataLoaderInstance(clazzName, name, annotation.identifier(), annotation.extension());
		instanceMap.put(annotation.identifier(), instance);
	}
	
	@Initialize
	public static void init()
	{
		//System.out.println("Static initialization of ElevationDataLoaderRegistry");
		
		log.info("Static initialization of ElevationDataLoaderRegistry");
		
		try {
			//URL url = ClasspathUrlFinder.findClassBase(JDem846Properties.class);
			AnnotationDB db = new AnnotationDB();
			
			URL[] urls = ClasspathUrlFinder.findClassPaths();
			for (URL url : urls) {	
				log.info("Scanning Classpath URL: " + url);
				db.scanArchives(url);
			}
			//db.crossReferenceImplementedInterfaces();
			 	
			Map<String, Set<String>> annotationIndex = db.getAnnotationIndex();
			Set<String> colorClasses = annotationIndex.get(ElevationDataLoader.class.getName());
			
			if (colorClasses != null) {
				for (String clazzName : colorClasses) {
					addElevationDataLoaderInstance(clazzName);
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
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
