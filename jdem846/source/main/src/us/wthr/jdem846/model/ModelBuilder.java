package us.wthr.jdem846.model;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.model.processing.coloring.GridColorProcessor;
import us.wthr.jdem846.model.processing.dataload.GridHillshadeProcessor;
import us.wthr.jdem846.model.processing.dataload.GridLoadProcessor;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.ProcessInterruptListener;
import us.wthr.jdem846.render.scaling.ElevationScaler;
import us.wthr.jdem846.render.scaling.ElevationScalerFactory;
import us.wthr.jdem846.scripting.ScriptProxy;

public class ModelBuilder extends AbstractGridProcessor implements GridProcessor
{
	private static Log log = Logging.getLog(ModelBuilder.class);

	private GridLoadProcessor gridLoadProcessor;
	private GridColorProcessor gridColorProcessor;
	private GridHillshadeProcessor gridHillshadeProcessor;
	
	private boolean runLoadProcessor = true;
	private boolean runColorProcessor = true;
	private boolean runHillshadeProcessor = true;
	
	private boolean prepared = false;
	private boolean useScripting = true;
	
	public ModelBuilder(ModelContext modelContext, ModelGrid modelGrid)
	{
		super(modelContext, modelGrid);
		

		
	}
	
	public void dispose()
	{
		
	}
	
	public void prepare() throws RenderEngineException
	{
		
		useScripting = modelContext.getModelOptions().useScripting();
		
		double minimumElevation = modelContext.getRasterDataContext().getDataMinimumValue();
		double maximumElevationTrue = modelContext.getRasterDataContext().getDataMaximumValueTrue();
		
		
		ElevationScaler elevationScaler = null;
		try {
			elevationScaler = ElevationScalerFactory.createElevationScaler(modelContext.getModelOptions().getElevationScaler(), modelContext.getModelOptions().getElevationMultiple(), minimumElevation, maximumElevationTrue);
		} catch (Exception ex) {
			throw new RenderEngineException("Error creating elevation scaler: " + ex.getMessage(), ex);
		}
		modelContext.getRasterDataContext().setElevationScaler(elevationScaler);

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
		
		if (!modelContainsData()) {
			log.info("Model contains no data. Skipping model build process");
			return;
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
		
		if (useScripting) {
			onTileBefore(modelContext.getModelCanvas());
		}
		
		if (runLoadProcessor()) {
			log.info("Filling model grid...");
			gridLoadProcessor.process();
		}
		
		this.checkPause();
		if (this.isCancelled()) {
			log.info("Model builder was cancelled. Exiting in incomplete state.");
			setProcessing(false);
			return;
		}
		
		if (runColorProcessor()) {
			log.info("Calculating grid colors...");
			gridColorProcessor.process();
		}
		
		this.checkPause();
		if (this.isCancelled()) {
			log.info("Model builder was cancelled. Exiting in incomplete state.");
			setProcessing(false);
			return;
		}
		
		if (runHillshadeProcessor() && modelContext.getLightingContext().isLightingEnabled()) {
			log.info("Generating hill shading...");
			gridHillshadeProcessor.process();
		}
		
		this.checkPause();
		if (this.isCancelled()) {
			log.info("Model builder was cancelled. Exiting in incomplete state.");
			setProcessing(false);
			return;
		}
		
		if (useScripting) {
			onTileAfter(modelContext.getModelCanvas());
		}
		
		setProcessing(false);
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

	public GridLoadProcessor getGridLoadProcessor()
	{
		return gridLoadProcessor;
	}

	public GridColorProcessor getGridColorProcessor()
	{
		return gridColorProcessor;
	}

	public GridHillshadeProcessor getGridHillshadeProcessor()
	{
		return gridHillshadeProcessor;
	}

	public boolean runLoadProcessor()
	{
		return runLoadProcessor;
	}

	public void setRunLoadProcessor(boolean runLoadProcessor)
	{
		this.runLoadProcessor = runLoadProcessor;
	}

	public boolean runColorProcessor()
	{
		return runColorProcessor;
	}

	public void setRunColorProcessor(boolean runColorProcessor)
	{
		this.runColorProcessor = runColorProcessor;
	}

	public boolean runHillshadeProcessor()
	{
		return runHillshadeProcessor;
	}

	public void setRunHillshadeProcessor(boolean runHillshadeProcessor)
	{
		this.runHillshadeProcessor = runHillshadeProcessor;
	}
	
	
	public boolean useScripting()
	{
		return useScripting;
	}

	public void setUseScripting(boolean useScripting)
	{
		this.useScripting = useScripting;
	}
	
}
