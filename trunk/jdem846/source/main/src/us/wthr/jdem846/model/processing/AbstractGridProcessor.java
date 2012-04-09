package us.wthr.jdem846.model.processing;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelGridDimensions;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.model.ModelPoint;
import us.wthr.jdem846.model.ModelPointCycler;
import us.wthr.jdem846.model.ModelPointHandler;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.render.InterruptibleProcess;
import us.wthr.jdem846.render.ProcessInterruptListener;

public abstract class AbstractGridProcessor extends InterruptibleProcess implements GridProcessor, ModelPointHandler
{
	
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(AbstractGridProcessor.class);
	
	protected ModelContext modelContext;
	protected ModelGrid modelGrid;
	protected GlobalOptionModel globalOptionModel;
	protected OptionModel processOptionModel; 
	protected ModelGridDimensions modelDimensions;
	
	private boolean isProcessing = false;
	
	public AbstractGridProcessor()
	{
		
	}
	
	public AbstractGridProcessor(ModelContext modelContext, ModelGrid modelGrid)
	{
		setModelContext(modelContext);
		setModelGrid(modelGrid);
	}
	
	
	public void setAndPrepare(ModelContext modelContext, ModelGrid modelGrid, ModelGridDimensions modelDimensions, GlobalOptionModel globalOptionModel, OptionModel processOptionModel) throws RenderEngineException
	{
		setModelContext(modelContext);
		setModelGrid(modelGrid);
		setModelDimensions(modelDimensions);
		setGlobalOptionModel(globalOptionModel);
		setProcessOptionModel(processOptionModel);
		prepare();
	}
	
	@Override
	public void process() throws RenderEngineException
	{
		final ModelPointCycler pointCycler = new ModelPointCycler(modelContext, modelDimensions);
		
		this.setProcessInterruptListener(new ProcessInterruptListener() {
			public void onProcessCancelled()
			{
				pointCycler.cancel();
			}
			public void onProcessPaused()
			{
				pointCycler.pause();
			}
			public void onProcessResumed()
			{
				pointCycler.resume();
			}
		});
		
		this.setProcessing(true);
		
		pointCycler.forEachModelPoint(this);
		
		this.setProcessing(false);
	}
	
	
	public void setModelContext(ModelContext modelContext)
	{
		this.modelContext = modelContext;
	}
	
	public void setModelGrid(ModelGrid modelGrid)
	{
		this.modelGrid = modelGrid;
	}
	
	
	
	public ModelGridDimensions getModelDimensions()
	{
		return modelDimensions;
	}

	public void setModelDimensions(ModelGridDimensions modelDimensions)
	{
		this.modelDimensions = modelDimensions;
	}

	public GlobalOptionModel getGlobalOptionModel()
	{
		return globalOptionModel;
	}

	public void setGlobalOptionModel(GlobalOptionModel globalOptionModel)
	{
		this.globalOptionModel = globalOptionModel;
	}

	public OptionModel getProcessOptionModel()
	{
		return processOptionModel;
	}

	public void setProcessOptionModel(OptionModel processOptionModel)
	{
		this.processOptionModel = processOptionModel;
	}

	protected boolean modelContainsData()
	{
		return (modelContext.getRasterDataContext().getRasterDataListSize() > 0 ||
				modelContext.getImageDataContext().getImageListSize() > 0);
	}
	
	protected void setProcessing(boolean isProcessing)
	{
		this.isProcessing = isProcessing;
	}
	
	public boolean isProcessing()
	{
		return isProcessing;
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

	@Override
	public void onCycleStart() throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onModelLatitudeStart(double latitude)
			throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onModelPoint(double latitude, double longitude)
			throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onModelLatitudeEnd(double latitude)
			throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onCycleEnd() throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}
	
	
	
}
