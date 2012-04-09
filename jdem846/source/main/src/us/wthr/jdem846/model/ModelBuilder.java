package us.wthr.jdem846.model;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.Projection;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.model.processing.coloring.HypsometricColorProcessor;
import us.wthr.jdem846.model.processing.dataload.SurfaceNormalsProcessor;
import us.wthr.jdem846.model.processing.dataload.GridLoadProcessor;
import us.wthr.jdem846.canvas.CanvasProjection;
import us.wthr.jdem846.canvas.CanvasProjectionFactory;
import us.wthr.jdem846.canvas.CanvasProjectionTypeEnum;
import us.wthr.jdem846.render.InterruptibleProcess;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.render.ProcessInterruptListener;
import us.wthr.jdem846.scaling.ElevationScaler;
import us.wthr.jdem846.scaling.ElevationScalerEnum;
import us.wthr.jdem846.scaling.ElevationScalerFactory;
import us.wthr.jdem846.scripting.ScriptProxy;

public class ModelBuilder extends AbstractGridProcessor implements GridProcessor
{
	private static Log log = Logging.getLog(ModelBuilder.class);

	private GridLoadProcessor gridLoadProcessor;
	private HypsometricColorProcessor gridColorProcessor;
	private SurfaceNormalsProcessor gridHillshadeProcessor;
	
	private boolean runLoadProcessor = true;
	private boolean runColorProcessor = true;
	private boolean runHillshadeProcessor = true;
	
	private boolean prepared = false;
	private boolean useScripting = true;
	
	public ModelBuilder()
	{
		//super(modelContext);
		
		
		
	}
	
	public void dispose()
	{
		
	}
	
	public void prepare() throws RenderEngineException
	{
		GlobalOptionModel globalOptionModel = this.getGlobalOptionModel();
		
	
		if (modelGrid == null) {
			modelGrid = new ModelGrid(globalOptionModel.getNorthLimit(), 
					globalOptionModel.getSouthLimit(), 
					globalOptionModel.getEastLimit(), 
					globalOptionModel.getWestLimit(), 
					modelDimensions.getOutputLatitudeResolution(), 
					modelDimensions.getOutputLongitudeResolution());
		}
		
		/*
		CanvasProjectionTypeEnum projectionType,
		MapProjection mapProjection,
		double north,
		double south,
		double east,
		double west,
		double width,
		double height,
		Planet planet,
		double elevationMultiple,
		double minimumValue,
		double maximumValue,
		ModelDimensions modelDimensions,
		Projection projection
		*/
		/*
		CanvasProjection canvasProjection = CanvasProjectionFactory.create( 
				CanvasProjectionTypeEnum.getCanvasProjectionEnumFromIdentifier(globalOptionModel.getRenderProjection()),
				modelContext.getMapProjection(),
				modelContext.getNorth(),
				modelContext.getSouth(),
				modelContext.getEast(),
				modelContext.getWest(),
				modelDimensions.getOutputWidth(),
				modelDimensions.getOutputHeight(),
				PlanetsRegistry.getPlanet(globalOptionModel.getPlanet()),
				modelContext.getRasterDataContext().getDataMinimumValue(),
				modelContext.getRasterDataContext().getDataMaximumValue(),
				modelDimensions,
				new Projection());
		*/	
				
		ModelCanvas modelCanvas = new ModelCanvas(modelDimensions.getOutputWidth(), 
													modelDimensions.getOutputHeight(), 
													globalOptionModel.getSubpixelGridSize(), 
													globalOptionModel.getBackgroundColor(), 
													null);
		modelContext.setModelCanvas(modelCanvas);
		
		useScripting = globalOptionModel.isUseScripting();
		
		double minimumElevation = modelContext.getRasterDataContext().getDataMinimumValue();
		double maximumElevationTrue = modelContext.getRasterDataContext().getDataMaximumValueTrue();
		
		
		ElevationScaler elevationScaler = null;
		ElevationScalerEnum elevationScalerEnum = ElevationScalerEnum.getElevationScalerEnumFromIdentifier(globalOptionModel.getElevationScale());
		try {
			elevationScaler = ElevationScalerFactory.createElevationScaler(elevationScalerEnum, globalOptionModel.getElevationMultiple(), minimumElevation, maximumElevationTrue);
		} catch (Exception ex) {
			throw new RenderEngineException("Error creating elevation scaler: " + ex.getMessage(), ex);
		}
		modelContext.getRasterDataContext().setElevationScaler(elevationScaler);

		prepared = true;
	}
	
	
	public void process() throws RenderEngineException
	{
		
	}
	
	public void process(ModelProcessList modelProcessList) throws RenderEngineException
	{
		if (!isPrepared()) {
			throw new RenderEngineException("Model builder not yet prepared!");
		}
		
		if (!modelContainsData()) {
			log.info("Model contains no data. Skipping model build process");
			return;
		}
		
		ProcessInterruptHandler interruptHandler = new ProcessInterruptHandler();
		GlobalOptionModel globalOptionModel = this.getGlobalOptionModel();
		
		setProcessing(true);
		
		if (useScripting) {
			onTileBefore(modelContext.getModelCanvas());
		}
		
		
		for (ModelProcessContainer processContainer : modelProcessList.getProcessList()) {
			
			AbstractGridProcessor gridProcessor = processContainer.getGridProcessor();
			OptionModel optionModel = processContainer.getOptionModel();
			
			GridProcessing annotation = gridProcessor.getClass().getAnnotation(GridProcessing.class);
			String name = gridProcessor.getClass().getName();
			if (annotation != null) {
				name = annotation.name();
			}
			
			if (gridProcessor instanceof InterruptibleProcess) {
				interruptHandler.setInterruptibleProcess((InterruptibleProcess)gridProcessor);
			} else {
				interruptHandler.setInterruptibleProcess(null);
			}
			
			log.info("Preparing processor: " + name + "'");
			gridProcessor.setAndPrepare(modelContext, modelGrid, modelDimensions, globalOptionModel, optionModel);
			
			log.info("Executing processor: " + name + "'");
			gridProcessor.process();
			
			this.checkPause();
			if (this.isCancelled()) {
				log.info("Model builder was cancelled. Exiting in incomplete state.");
				setProcessing(false);
				return;
			}
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

	public HypsometricColorProcessor getGridColorProcessor()
	{
		return gridColorProcessor;
	}

	public SurfaceNormalsProcessor getGridHillshadeProcessor()
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
	
	
	
	
	class ProcessInterruptHandler implements ProcessInterruptListener
	{
		private InterruptibleProcess interruptibleProcess;
		
		
		
		public void setInterruptibleProcess(InterruptibleProcess interruptibleProcess)
		{
			this.interruptibleProcess = interruptibleProcess;
		}


		public void onProcessCancelled()
		{
			if (this.interruptibleProcess != null) {
				interruptibleProcess.cancel();
			}
		}

		
		public void onProcessPaused()
		{
			if (interruptibleProcess != null) {
				interruptibleProcess.pause();
			}
		}

		public void onProcessResumed()
		{
			if (interruptibleProcess != null) {
				interruptibleProcess.resume();
			}
		}
		
	}
}
