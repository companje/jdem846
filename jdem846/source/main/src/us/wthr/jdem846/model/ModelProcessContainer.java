package us.wthr.jdem846.model;

import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;
import us.wthr.jdem846.model.exceptions.ProcessContainerException;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.model.processing.GridWorker;

public class ModelProcessContainer
{
	
	private GridWorker gridWorker;
	private OptionModel optionModel;
	private OptionModelContainer optionModelContainer;
	
	public ModelProcessContainer(GridWorker gridWorker, OptionModel optionModel) throws ProcessContainerException
	{
		this.gridWorker = gridWorker;
		this.optionModel = optionModel;
		
		try {
			optionModelContainer = new OptionModelContainer(optionModel);
		} catch (InvalidProcessOptionException ex) {
			throw new ProcessContainerException("Error creating option model container: " + ex.getMessage(), ex);
		}
	}
	
	public String getProcessId()
	{
		return gridWorker.getClass().getAnnotation(GridProcessing.class).id();
	}

	public GridWorker getGridWorker()
	{
		return gridWorker;
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
