package us.wthr.jdem846.model.processing;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelGridDimensions;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.modelgrid.IModelGrid;
import us.wthr.jdem846.scripting.ScriptProxy;

public abstract class GridWorker implements IGridWorker
{

	protected ModelContext modelContext;
	protected IModelGrid modelGrid;
	protected GlobalOptionModel globalOptionModel;
	protected ModelDimensions modelDimensions;
	protected OptionModel optionModel;
	protected ScriptProxy script;

	@Override
	public void setAndPrepare(ModelContext modelContext, IModelGrid modelGrid, ModelGridDimensions modelDimensions, GlobalOptionModel globalOptionModel, OptionModel processOptionModel,
			ScriptProxy script) throws RenderEngineException
	{
		setModelContext(modelContext);
		setModelGrid(modelGrid);
		setGlobalOptionModel(globalOptionModel);
		setModelDimensions(modelDimensions);
		setOptionModel(processOptionModel);
		setScript(script);
		prepare();
	}

	@Override
	public ModelContext getModelContext()
	{
		return modelContext;
	}

	@Override
	public void setModelContext(ModelContext modelContext)
	{
		this.modelContext = modelContext;
	}

	@Override
	public IModelGrid getModelGrid()
	{
		return modelGrid;
	}

	@Override
	public void setModelGrid(IModelGrid modelGrid)
	{
		this.modelGrid = modelGrid;
	}

	@Override
	public GlobalOptionModel getGlobalOptionModel()
	{
		return globalOptionModel;
	}

	@Override
	public void setGlobalOptionModel(GlobalOptionModel globalOptionModel)
	{
		this.globalOptionModel = globalOptionModel;
	}

	@Override
	public ModelDimensions getModelDimensions()
	{
		return modelDimensions;
	}

	@Override
	public void setModelDimensions(ModelDimensions modelDimensions)
	{
		this.modelDimensions = modelDimensions;
	}

	@Override
	public OptionModel getOptionModel()
	{
		return optionModel;
	}

	@Override
	public void setOptionModel(OptionModel optionModel)
	{
		this.optionModel = optionModel;
	}

	@Override
	public ScriptProxy getScript()
	{
		return script;
	}

	@Override
	public void setScript(ScriptProxy script)
	{
		this.script = script;
	}

}
