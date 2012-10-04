package us.wthr.jdem846.model.processing;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelGridDimensions;
import us.wthr.jdem846.model.ModelPoint;
import us.wthr.jdem846.model.ModelPointGrid;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.scripting.ScriptProxy;

public abstract class GridWorker
{
	
	protected ModelContext modelContext;
	protected ModelPointGrid modelGrid;
	protected GlobalOptionModel globalOptionModel;
	protected ModelDimensions modelDimensions;
	protected OptionModel optionModel;
	protected ScriptProxy script;
	
	
	public abstract WorkerTypeEnum getWorkerType();

	public abstract void prepare() throws RenderEngineException;
	public abstract void onProcessBefore() throws RenderEngineException;
	public abstract void onModelPoint(double latitude, double longitude) throws RenderEngineException;
	public abstract void onProcessAfter() throws RenderEngineException;
	public abstract void dispose() throws RenderEngineException;
	
	
	public void setAndPrepare(ModelContext modelContext, ModelPointGrid modelGrid, ModelGridDimensions modelDimensions, GlobalOptionModel globalOptionModel, OptionModel processOptionModel, ScriptProxy script) throws RenderEngineException
	{
		setModelContext(modelContext);
		setModelGrid(modelGrid);
		setGlobalOptionModel(globalOptionModel);
		setModelDimensions(modelDimensions);
		setOptionModel(processOptionModel);
		setScript(script);
		prepare();
	}
	
	public ModelContext getModelContext()
	{
		return modelContext;
	}


	public void setModelContext(ModelContext modelContext)
	{
		this.modelContext = modelContext;
	}


	public ModelPointGrid getModelGrid()
	{
		return modelGrid;
	}


	public void setModelGrid(ModelPointGrid modelGrid)
	{
		this.modelGrid = modelGrid;
	}


	public GlobalOptionModel getGlobalOptionModel()
	{
		return globalOptionModel;
	}


	public void setGlobalOptionModel(GlobalOptionModel globalOptionModel)
	{
		this.globalOptionModel = globalOptionModel;
	}


	public ModelDimensions getModelDimensions()
	{
		return modelDimensions;
	}


	public void setModelDimensions(ModelDimensions modelDimensions)
	{
		this.modelDimensions = modelDimensions;
	}


	public OptionModel getOptionModel()
	{
		return optionModel;
	}


	public void setOptionModel(OptionModel optionModel)
	{
		this.optionModel = optionModel;
	}


	public ScriptProxy getScript()
	{
		return script;
	}


	public void setScript(ScriptProxy script)
	{
		this.script = script;
	}
	
	
	protected double getElevationAtPoint(double latitude, double longitude)
	{
		ModelPoint modelPoint = modelGrid.get(latitude, longitude);
		if (modelPoint != null) {
			return modelPoint.getElevation();
		} else {
			return DemConstants.ELEV_NO_DATA;
		}
	}
	
}
