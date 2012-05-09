package us.wthr.jdem846.model.processing;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.exceptions.ProcessCreateException;

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
	
	public OptionModel createOptionModel() throws ProcessCreateException
	{
		Class<?> clazz = getOptionModelClass();
		
		if (clazz == null) {
			throw new ProcessCreateException("Cannot create option model: class not specified");
		}
		
		OptionModel optionModel = null;
		
		try {
			optionModel = (OptionModel) clazz.newInstance();
		} catch (Exception ex) {
			throw new ProcessCreateException("Error creating new option model with class " + clazz.getName() + ": " + ex.getMessage(), ex);
		} 
		
		return optionModel;
	}
	
	public Class<?> getProcessorClass()
	{
		return clazz;
	}
	
	public boolean isEnabled()
	{
		return annotation.enabled();
	}
}
