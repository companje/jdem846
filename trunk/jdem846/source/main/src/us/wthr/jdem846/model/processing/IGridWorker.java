package us.wthr.jdem846.model.processing;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelGridDimensions;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.modelgrid.IModelGrid;
import us.wthr.jdem846.scripting.ScriptProxy;

public interface IGridWorker
{
	public WorkerTypeEnum getWorkerType();

	public void prepare() throws RenderEngineException;

	public void onProcessBefore() throws RenderEngineException;

	public void onModelPoint(double latitude, double longitude) throws RenderEngineException;

	public void onProcessAfter() throws RenderEngineException;

	public void dispose() throws RenderEngineException;

	public void setAndPrepare(ModelContext modelContext, IModelGrid modelGrid, ModelGridDimensions modelDimensions, GlobalOptionModel globalOptionModel, OptionModel processOptionModel,
			ScriptProxy script) throws RenderEngineException;

	public ModelContext getModelContext();

	public void setModelContext(ModelContext modelContext);

	public IModelGrid getModelGrid();

	public void setModelGrid(IModelGrid modelGrid);

	public GlobalOptionModel getGlobalOptionModel();

	public void setGlobalOptionModel(GlobalOptionModel globalOptionModel);

	public ModelDimensions getModelDimensions();

	public void setModelDimensions(ModelDimensions modelDimensions);

	public OptionModel getOptionModel();

	public void setOptionModel(OptionModel optionModel);

	public ScriptProxy getScript();

	public void setScript(ScriptProxy script);

}
