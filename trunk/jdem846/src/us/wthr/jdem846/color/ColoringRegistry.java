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



import java.io.File;
import java.io.FilenameFilter;
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
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.annotations.DemColoring;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.Registry;
import us.wthr.jdem846.dbase.ClassLoadException;
import us.wthr.jdem846.exception.AnnotationIndexerException;
import us.wthr.jdem846.exception.GradientLoadException;
import us.wthr.jdem846.exception.RegistryException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


@Registry
public class ColoringRegistry implements AppRegistry
{
	private static Log log = Logging.getLog(ColoringRegistry.class);
	
	private static Map<String, ColoringInstance> instances = new HashMap<String, ColoringInstance>();

	
	protected static void addColoringInstance(Class<?> clazz) throws ClassLoadException
	{
		/*
		Class<?> clazz = null;
		try {
			clazz = Class.forName(clazzName, true, Thread.currentThread().getContextClassLoader());
		} catch (Exception ex) {
			log.error("Error loading class '" + clazzName + "' into classloader: " + ex.getMessage(), ex);
			throw new ClassLoadException(clazzName, "Error loading class '" + clazzName + "' into classloader", ex);
		}
		*/
			
		DemColoring annotation = (DemColoring) clazz.getAnnotation(DemColoring.class);
		
		String name = annotation.name();
		name = I18N.get(name, name);

		
		ColoringRegistry.instances.put(annotation.identifier(), new ColoringInstance(clazz.getName(), name, annotation.identifier(), annotation.allowGradientConfig(), annotation.needsMinMaxElevation()));
		//System.out.println("Adding coloring instance for " + clazzName + ": " + annotation.name());
		
		log.info("Adding coloring instance for " + clazz.getName() + ": " + annotation.name());
		
	}
	
	protected static void addColoringInstance(File gradientFile) throws RegistryException
	{
		if (!gradientFile.exists()) {
			throw new RegistryException(gradientFile.getAbsolutePath(), "Gradient file not found");
		}
		
		if (!gradientFile.canRead()) {
			throw new RegistryException(gradientFile.getAbsolutePath(), "Gradient file is unreadable");
		}
		
		GradientColoring coloring = null;
		
		try {
			coloring = new GradientColoring(gradientFile.getAbsolutePath());
		} catch (GradientLoadException ex) {
			throw new RegistryException(gradientFile.getAbsolutePath(), "Error when loading gradient file: " + ex.getMessage(), ex);
		}
		
		try {
			ColoringRegistry.instances.put(coloring.getIdentifier(), new ColoringInstance(coloring));
		} catch (Exception ex) {
			throw new RegistryException(gradientFile.getAbsolutePath(), "Error creating coloring instance: " + ex.getMessage(), ex);
		}
	}
	
	protected ColoringRegistry()
	{
		
	}
	
	
	@Initialize
	public static void init()
	{
		try {
			initByAnnotations();
		} catch (Exception ex) {
			log.warn("Error loading coloring drivers by annotations: " + ex.getMessage(), ex);
		}
		
		try {
			initByGradientFileSearch();
		} catch (Exception ex) {
			log.warn("Error loading coloring drivers by gradient file search: " + ex.getMessage(), ex);
		}
		
	}
	
	public static void initByGradientFileSearch() throws RegistryException
	{

		log.info("Static initialization of ColoringRegistry by gradient file search");
		String rootPath = JDem846Properties.getProperty("us.wthr.jdem846.gradients");
		

		//File rootPathFile = new File(ColoringRegistry.class.getResource(rootPath).getPath());
		File rootPathFile = JDemResourceLoader.getAsFile(rootPath);
		log.info("Searching " + rootPathFile.getAbsolutePath() + " for gradient files");
		
		if (!rootPathFile.exists()) {
			throw new RegistryException("Search path not found: " + rootPath);
		}
		
		File[] gradientFiles = rootPathFile.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name)
			{
				if (name == null)
					return false;
				
				if (name.toLowerCase().endsWith(".json")) {
					return true;
				} else {
					return false;
				}
			}
		});
		
		for (File gradientFile : gradientFiles) {
			addColoringInstance(gradientFile);
		}
		
	}
	
	public static void initByAnnotations() throws RegistryException
	{
		//System.out.println("Static initialization of ColoringRegistry");
		
		log.info("Static initialization of ColoringRegistry by annotations");
		
		List<Class<?>> clazzList = null;
		
		try {
			clazzList = DiscoverableAnnotationIndexer.getAnnotatedClasses(DemColoring.class.getName());
		} catch (AnnotationIndexerException ex) {
			throw new RegistryException("Failed to retrieve DemColoring classes: " + ex.getMessage(), ex);
		}
		
		try {
			if (clazzList != null) {
				for (Class<?> clazz : clazzList) {
					addColoringInstance(clazz);
				}
			}
		} catch (Exception ex) {
			throw new RegistryException("Error loading coloring class: " + ex.getMessage(), ex);
		}

	}
	
	//
	
	
	
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
