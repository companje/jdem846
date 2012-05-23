package us.wthr.jdem846.model;

import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;
import us.wthr.jdem846.model.exceptions.ProcessContainerException;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;
import us.wthr.jdem846.model.processing.GridProcessor;

public class ModelProcessContainer
{
	
	private GridProcessor gridProcessor;
	private OptionModel optionModel;
	private OptionModelContainer optionModelContainer;
	
	public ModelProcessContainer(GridProcessor gridProcessor, OptionModel optionModel) throws ProcessContainerException
	{
		this.gridProcessor = gridProcessor;
		this.optionModel = optionModel;
		
		try {
			optionModelContainer = new OptionModelContainer(optionModel);
		} catch (InvalidProcessOptionException ex) {
			throw new ProcessContainerException("Error creating option model container: " + ex.getMessage(), ex);
		}
	}
	
	public String getProcessId()
	{
		return gridProcessor.getClass().getAnnotation(GridProcessing.class).id();
	}

	public GridProcessor getGridProcessor()
	{
		return gridProcessor;
	}

	public OptionModel getOptionModel()
	{
		return optionModel;
	}
	
	public OptionModelContainer getOptionModelContainer()
	{
		return optionModelContainer;
	}

}
