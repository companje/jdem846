package us.wthr.jdem846.model;

import us.wthr.jdem846.model.processing.AbstractGridProcessor;

public class ModelProcessContainer
{
	
	AbstractGridProcessor gridProcessor;
	OptionModel optionModel;
	
	public ModelProcessContainer(AbstractGridProcessor gridProcessor, OptionModel optionModel)
	{
		this.gridProcessor = gridProcessor;
		this.optionModel = optionModel;
	}

	public AbstractGridProcessor getGridProcessor()
	{
		return gridProcessor;
	}

	public OptionModel getOptionModel()
	{
		return optionModel;
	}
	
	
	
}
