package us.wthr.jdem846.render.render3;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.InterruptibleProcess;
import us.wthr.jdem846.render.ProcessInterruptListener;

public abstract class AbstractGridProcessor extends InterruptibleProcess implements GridProcessor, ModelPointHandler
{
	
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(AbstractGridProcessor.class);
	
	protected ModelContext modelContext;
	protected ModelGrid modelGrid;
	
	private boolean isProcessing = false;
	
	public AbstractGridProcessor(ModelContext modelContext, ModelGrid modelGrid)
	{
		this.modelContext = modelContext;
		this.modelGrid = modelGrid;
	}
	
	
	@Override
	public void process() throws RenderEngineException
	{
		final ModelPointCycler pointCycler = new ModelPointCycler(modelContext);
		
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
