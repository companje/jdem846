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

package us.wthr.jdem846.color;



import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

import us.wthr.jdem846.AppRegistry;
import us.wthr.jdem846.JDemMain;
import us.wthr.jdem846.annotations.DemColoring;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.Registry;
import us.wthr.jdem846.dbase.ClassLoadException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


@Registry
public class ColoringRegistry implements AppRegistry
{
	private static Log log = Logging.getLog(ColoringRegistry.class);
	
	private static Map<String, ColoringInstance> instances = new HashMap<String, ColoringInstance>();

	
	protected static void addColoringInstance(String clazzName) throws ClassLoadException
	{
		Class<?> clazz = null;
		try {
			clazz = Class.forName(clazzName, true, Thread.currentThread().getContextClassLoader());
		} catch (Exception ex) {
			log.error("Error loading class '" + clazzName + "' into classloader: " + ex.getMessage(), ex);
			throw new ClassLoadException(clazzName, "Error loading class '" + clazzName + "' into classloader", ex);
		}
			
		DemColoring annotation = (DemColoring) clazz.getAnnotation(DemColoring.class);
		
		String name = annotation.name();
		name = I18N.get(name, name);

		
		ColoringRegistry.instances.put(annotation.identifier(), new ColoringInstance(clazzName, name, annotation.identifier(), annotation.allowGradientConfig(), annotation.needsMinMaxElevation()));
		//System.out.println("Adding coloring instance for " + clazzName + ": " + annotation.name());
		
		log.info("Adding coloring instance for " + clazzName + ": " + annotation.name());
		
	}
	
	protected ColoringRegistry()
	{
		
	}
	
	@Initialize
	public static void init()
	{
		//System.out.println("Static initialization of ColoringRegistry");
		
		log.info("Static initialization of ColoringRegistry");
		
		try {

			URL url = ClasspathUrlFinder.findClassBase(JDemMain.class);
			log.info("Checking " + url + " for components");

			AnnotationDB db = new AnnotationDB();
			db.scanArchives(url);
			db.crossReferenceImplementedInterfaces();
					 	
			Map<String, Set<String>> annotationIndex = db.getAnnotationIndex();
			Set<String> colorClasses = annotationIndex.get(DemColoring.class.getName());
					
			if (colorClasses != null) {
				for (String clazzName : colorClasses) {
					addColoringInstance(clazzName);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static ColoringInstance getInstance(String identifier)
	{
		return instances.get(identifier);
	}
	
	public static List<ColoringInstance> getInstances()
	{
		List<ColoringInstance> instanceList = new LinkedList<ColoringInstance>();
		
		for (String identifier : instances.keySet()) {
			instanceList.add(instances.get(identifier));
		}
		
		return instanceList;
	}
	
}
