package us.wthr.jdem846.model.processing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import us.wthr.jdem846.AppRegistry;
import us.wthr.jdem846.DiscoverableAnnotationIndexer;
import us.wthr.jdem846.annotations.DemEngine;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.Registry;
import us.wthr.jdem846.exception.AnnotationIndexerException;
import us.wthr.jdem846.exception.RegistryException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.render.EngineInstance;
import us.wthr.jdem846.render.EngineRegistry;

@Registry
public class ModelProcessRegistry  implements AppRegistry
{
	
	private static Log log = Logging.getLog(ModelProcessRegistry.class);
	
	private static Map<String, ProcessInstance> processMap = new HashMap<String, ProcessInstance>();
	
	protected static void addProcessInstance(Class<?> clazz) throws ClassNotFoundException
	{
		GridProcessing annotation = (GridProcessing) clazz.getAnnotation(GridProcessing.class);

		String id = annotation.id();
		
		ProcessInstance processInstance = new ProcessInstance(clazz);
		
		if (annotation.enabled()) {
			ModelProcessRegistry.processMap.put(id, processInstance);
			log.info("Adding model processor instance for " + clazz.getName() + ": " + annotation.name());
		} else {
			log.info("Model Processor is disabled: " + clazz.getName());
		}
		
	}
	
	protected ModelProcessRegistry()
	{
		
	}
	
	@Initialize
	public static void init() throws RegistryException
	{
		log.info("Static initialization of ModelProcessRegistry");
		
		List<Class<?>> clazzList = null;
		
		try {
			clazzList = DiscoverableAnnotationIndexer.getAnnotatedClasses(GridProcessing.class.getName());
		} catch (AnnotationIndexerException ex) {
			throw new RegistryException("Failed to retrieve GridProcessing classes: " + ex.getMessage(), ex);
		}
		
		try {
			if (clazzList != null) {
				for (Class<?> clazz : clazzList) {
					addProcessInstance(clazz);
				}
			}
		} catch (Exception ex) {
			throw new RegistryException("Error loading model process class: " + ex.getMessage(), ex);
		}
	}

	
	public static ProcessInstance getInstance(String identifier)
	{
		return processMap.get(identifier);
	}
	
	public static List<ProcessInstance> getInstances()
	{
		List<ProcessInstance> instanceList = new LinkedList<ProcessInstance>();
		
		for (String identifier : processMap.keySet()) {
			instanceList.add(processMap.get(identifier));
		}
		
		return instanceList;
	}
}
