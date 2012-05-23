package us.wthr.jdem846.model.processing;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelGridDimensions;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.model.ModelPointGrid;
import us.wthr.jdem846.model.OptionModel;

public interface GridProcessor
{
	public boolean isProcessing();
	
	public void setAndPrepare(ModelContext modelContext, ModelPointGrid modelGrid, ModelGridDimensions modelDimensions, GlobalOptionModel globalOptionModel, OptionModel processOptionModel) throws RenderEngineException;
	public void setModelContext(ModelContext modelContext);
	public void setModelGrid(ModelPointGrid modelGrid);
	public void setModelDimensions(ModelGridDimensions modelDimensions);
	public void setGlobalOptionModel(GlobalOptionModel globalOptionModel);
	public void setProcessOptionModel(OptionModel processOptionModel);
	
	public void prepare() throws RenderEngineException;
	public void process() throws RenderEngineException;
	
}
