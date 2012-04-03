package us.wthr.jdem846.render.render3;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.InterruptibleProcess;
import us.wthr.jdem846.render.ProcessInterruptListener;

public abstract class AbstractGridProcessor extends InterruptibleProcess implements GridProcessor, ModelPointHandler
{
	
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
	
	
	protected void setProcessing(boolean isProcessing)
	{
		this.isProcessing = isProcessing;
	}
	
	public boolean isProcessing()
	{
		return isProcessing;
	}
}
