package us.wthr.jdem846.render.render3;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.InterruptibleProcess;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.ProcessInterruptListener;
import us.wthr.jdem846.scripting.ScriptProxy;

public class ModelBuilder extends InterruptibleProcess implements GridProcessor
{
	private static Log log = Logging.getLog(ModelBuilder.class);
	
	private ModelContext modelContext;
	private ModelGrid modelGrid;

	private GridLoadProcessor gridLoadProcessor;
	private GridColorProcessor gridColorProcessor;
	private GridHillshadeProcessor gridHillshadeProcessor;
	
	private boolean prepared = false;
	private boolean isProcessing = false;
	
	
	public ModelBuilder(ModelContext modelContext, ModelGrid modelGrid)
	{
		this.modelContext = modelContext;
		this.modelGrid = modelGrid;
		
		
		
	}
	
	public void prepare() throws RenderEngineException
	{

		gridLoadProcessor = new GridLoadProcessor(modelContext, modelGrid);
		gridLoadProcessor.prepare();
		
		gridColorProcessor = new GridColorProcessor(modelContext, modelGrid);
		gridColorProcessor.prepare();
		
		if (modelContext.getLightingContext().isLightingEnabled()) {
			gridHillshadeProcessor = new GridHillshadeProcessor(modelContext, modelGrid);
			gridHillshadeProcessor.prepare();
		}
		
		prepared = true;
	}
	
	
	public void process() throws RenderEngineException
	{
		if (!isPrepared()) {
			throw new RenderEngineException("Model builder not yet prepared!");
		}
		
		
		this.setProcessInterruptListener(new ProcessInterruptListener() {
			public void onProcessCancelled()
			{
				if (gridLoadProcessor != null) {
					gridLoadProcessor.cancel();
				}
				
				if (gridColorProcessor != null) {
					gridColorProcessor.cancel();
				}
				
				if (gridHillshadeProcessor != null) {
					gridHillshadeProcessor.cancel();
				}
			}
			public void onProcessPaused()
			{
				if (gridLoadProcessor != null) {
					gridLoadProcessor.pause();
				}
				
				if (gridColorProcessor != null) {
					gridColorProcessor.pause();
				}
				
				if (gridHillshadeProcessor != null) {
					gridHillshadeProcessor.pause();
				}
			}
			public void onProcessResumed()
			{
				if (gridLoadProcessor != null) {
					gridLoadProcessor.resume();
				}
				
				if (gridColorProcessor != null) {
					gridColorProcessor.resume();
				}
				
				if (gridHillshadeProcessor != null) {
					gridHillshadeProcessor.resume();
				}
			}
		});
		
		setProcessing(true);
		
		onTileBefore(modelContext.getModelCanvas());
		
		log.info("Filling model grid...");
		gridLoadProcessor.process();
		
		this.checkPause();
		if (this.isCancelled()) {
			log.info("Model builder was cancelled. Exiting in incomplete state.");
			setProcessing(false);
			return;
		}
		
		log.info("Calculating grid colors...");
		gridColorProcessor.process();
		
		this.checkPause();
		if (this.isCancelled()) {
			log.info("Model builder was cancelled. Exiting in incomplete state.");
			setProcessing(false);
			return;
		}
		
		if (modelContext.getLightingContext().isLightingEnabled()) {
			log.info("Generating hill shading...");
			gridHillshadeProcessor.process();
		}
		
		this.checkPause();
		if (this.isCancelled()) {
			log.info("Model builder was cancelled. Exiting in incomplete state.");
			setProcessing(false);
			return;
		}
		
		onTileAfter(modelContext.getModelCanvas());
		
		setProcessing(false);
	}
	
	protected void setProcessing(boolean isProcessing)
	{
		this.isProcessing = isProcessing;
	}
	
	public boolean isProcessing()
	{
		return isProcessing;
	}
	
	public boolean isPrepared()
	{
		return prepared;
	}
	
	protected void onTileBefore(ModelCanvas modelCanvas) throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.onTileBefore(modelContext, modelCanvas);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
	}
	
	protected void onTileAfter(ModelCanvas modelCanvas) throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.onTileBefore(modelContext, modelCanvas);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
	}
	
	
	
	
}