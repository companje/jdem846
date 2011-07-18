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
import us.wthr.jdem846.JDemMain;
import us.wthr.jdem846.annotations.DemEngine;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.Registry;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


@Registry
public class EngineRegistry  implements AppRegistry
{
	private static Log log = Logging.getLog(EngineRegistry.class);
	
	private static Map<String, EngineInstance> engineMap = new HashMap<String, EngineInstance>();
	

	protected static void addEngineInstance(String clazzName) throws ClassNotFoundException
	{
		Class<?> clazz = Class.forName(clazzName, true, Thread.currentThread().getContextClassLoader());
		DemEngine annotation = (DemEngine) clazz.getAnnotation(DemEngine.class);

		EngineInstance engineInstance = new EngineInstance(clazzName, annotation.name(), annotation.identifier());
		engineInstance.setEnabled(annotation.enabled());
		engineInstance.setUsesWidth(annotation.usesWidth());
		engineInstance.setUsesHeight(annotation.usesHeight());
		engineInstance.setUsesBackgroundColor(annotation.usesBackgroundColor());
		engineInstance.setUsesColoring(annotation.usesColoring());
		engineInstance.setUsesHillshading(annotation.usesHillshading());
		engineInstance.setUsesLightMultiple(annotation.usesLightMultiple());
		engineInstance.setUsesSpotExponent(annotation.usesSpotExponent());
		engineInstance.setUsesTileSize(annotation.usesTileSize());
		engineInstance.setGeneratesImage(annotation.generatesImage());
		engineInstance.setNeedsOutputFileOfType(annotation.needsOutputFileOfType());
		
		if (annotation.enabled()) {
			EngineRegistry.engineMap.put(annotation.identifier(), engineInstance);
			log.info("Adding render engine instance for " + clazzName + ": " + annotation.name());
			//System.out.println("Adding render engine instance for " + clazzName + ": " + annotation.name());
		} else {
			log.info("Render engine is disabled: " + clazzName);
			//System.out.println("Render engine is disabled: " + clazzName);
		}
		
	}
	
	protected EngineRegistry()
	{
		
	}
	
	@Initialize
	public static void init()
	{
		//System.out.println("Static initialization of EngineRegistry");
		log.info("Static initialization of EngineRegistry");
		
		try {
			URL url = ClasspathUrlFinder.findClassBase(JDemMain.class);
			AnnotationDB db = new AnnotationDB();
			db.scanArchives(url);
			db.crossReferenceImplementedInterfaces();
			 	
			Map<String, Set<String>> annotationIndex = db.getAnnotationIndex();
			Set<String> engineClasses = annotationIndex.get(DemEngine.class.getName());
			
			if (engineClasses != null) {
				for (String clazzName : engineClasses) {
					addEngineInstance(clazzName);
				}
			}
			
		} catch (Exception ex) {
			log.error("Failure in engine registry initialization: " + ex.getMessage(), ex);
			//ex.printStackTrace();
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
