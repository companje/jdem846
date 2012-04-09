package us.wthr.jdem846.model.processing;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.annotations.GridProcessing;

public class ProcessInstance
{
	private static Log log = Logging.getLog(ProcessInstance.class);
	
	private Class<?> clazz;
	private GridProcessing annotation;
	
	public ProcessInstance(Class<?> clazz)
	{
		this.clazz = clazz;
		
		annotation = (GridProcessing) clazz.getAnnotation(GridProcessing.class);
	}
	
	public String getId()
	{
		return annotation.id();
	}
	
	public String getName()
	{
		return annotation.name();
	}
	
	public GridProcessingTypesEnum getType()
	{
		return annotation.type();
	}
	
	public Class<?> getOptionModelClass()
	{
		return annotation.optionModel();
	}
	
	public boolean isEnabled()
	{
		return annotation.enabled();
	}
}
