package us.wthr.jdem846.model;

import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;
import us.wthr.jdem846.model.exceptions.ProcessContainerException;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.IGridWorker;

public class ModelProcessContainer
{

	private IGridWorker gridWorker;
	private OptionModel optionModel;
	private OptionModelContainer optionModelContainer;

	public ModelProcessContainer(IGridWorker gridWorker, OptionModel optionModel) throws ProcessContainerException
	{
		this.gridWorker = gridWorker;
		this.optionModel = optionModel;

		try {
			optionModelContainer = new OptionModelContainer(optionModel);
		} catch (InvalidProcessOptionException ex) {
			throw new ProcessContainerException("Error creating option model container: " + ex.getMessage(), ex);
		}
	}

	public GridProcessingTypesEnum getProcessPhaseType()
	{
		return gridWorker.getClass().getAnnotation(GridProcessing.class).type();
	}
	
	public String getProcessId()
	{
		return gridWorker.getClass().getAnnotation(GridProcessing.class).id();
	}

	public IGridWorker getGridWorker()
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
