package us.wthr.jdem846.model;

import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;
import us.wthr.jdem846.model.exceptions.ProcessContainerException;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;

public class ModelProcessContainer
{
	
	private AbstractGridProcessor gridProcessor;
	private OptionModel optionModel;
	private OptionModelContainer optionModelContainer;
	
	public ModelProcessContainer(AbstractGridProcessor gridProcessor, OptionModel optionModel) throws ProcessContainerException
	{
		this.gridProcessor = gridProcessor;
		this.optionModel = optionModel;
		
		try {
			optionModelContainer = new OptionModelContainer(optionModel);
		} catch (InvalidProcessOptionException ex) {
			throw new ProcessContainerException("Error creating option model container: " + ex.getMessage(), ex);
		}
	}

	public AbstractGridProcessor getGridProcessor()
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
