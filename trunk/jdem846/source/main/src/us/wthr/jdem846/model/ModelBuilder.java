package us.wthr.jdem846.model;

import java.util.Date;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemElevationModel;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.Projection;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;
import us.wthr.jdem846.model.processing.GridPointFilter;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.model.processing.coloring.HypsometricColorProcessor;
import us.wthr.jdem846.model.processing.dataload.SurfaceNormalsProcessor;
import us.wthr.jdem846.model.processing.dataload.GridLoadProcessor;
import us.wthr.jdem846.canvas.CanvasProjection;
import us.wthr.jdem846.canvas.CanvasProjectionFactory;
import us.wthr.jdem846.canvas.CanvasProjectionTypeEnum;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.InterruptibleProcess;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.render.ProcessInterruptListener;
import us.wthr.jdem846.scaling.ElevationScaler;
import us.wthr.jdem846.scaling.ElevationScalerEnum;
import us.wthr.jdem846.scaling.ElevationScalerFactory;
import us.wthr.jdem846.scripting.ScriptProxy;

public class ModelBuilder extends InterruptibleProcess
{
	private static Log log = Logging.getLog(ModelBuilder.class);

	private ModelProcessManifest modelProcessManifest;
	private ModelContext modelContext;
	private FillControlledModelGrid modelGrid;
	private ModelGridDimensions modelDimensions;
	private GlobalOptionModel globalOptionModel;
	private ModelCanvas modelCanvas;
	private BufferControlledRasterDataContainer bufferControlledRasterDataContainer;
	
	private boolean runLoadProcessor = true;
	private boolean runColorProcessor = true;
	private boolean runHillshadeProcessor = true;
	
	private boolean prepared = false;
	private boolean useScripting = true;
	
	private boolean isProcessing = false;
	
	private boolean dataLoaded = false;
	
	public ModelBuilder()
	{

		
	}
	
	public void dispose()
	{
		modelGrid.dispose();
		modelGrid = null;
	}
	
	public void prepare(ModelContext modelContext,  ModelProcessManifest modelProcessManifest) throws RenderEngineException
	{
		
		globalOptionModel = modelProcessManifest.getGlobalOptionModel();
		
		if (!globalOptionModel.getLimitCoordinates()) {
			globalOptionModel.setNorthLimit(modelContext.getNorth());
			globalOptionModel.setSouthLimit(modelContext.getSouth());
			globalOptionModel.setEastLimit(modelContext.getEast());
			globalOptionModel.setWestLimit(modelContext.getWest());
		}
		
		
		ModelGridDimensions modelDimensions = ModelGridDimensions.getModelDimensions(modelContext);
	
		this.modelContext = modelContext;
		this.modelDimensions = modelDimensions;
		this.modelProcessManifest = modelProcessManifest;
		
		
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
		
		
		modelContext.getRasterDataContext().setAvgOfAllRasterValues(globalOptionModel.getAverageOverlappedData());
		modelContext.getRasterDataContext().setInterpolate(globalOptionModel.getInterpolateData());
		modelContext.getRasterDataContext().setScaled(true);
		
		
		bufferControlledRasterDataContainer = new BufferControlledRasterDataContainer(modelContext.getRasterDataContext(), globalOptionModel.getPrecacheStrategy(), modelDimensions.getLatitudeResolution(), globalOptionModel.getTileSize());
		modelContext.setRasterDataContext(bufferControlledRasterDataContainer);
		
		boolean useDiskCachedModelGrid = globalOptionModel.getUseDiskCachedModelGrid();
		
		ModelPointGrid innerModelGrid = null;
		if (modelGrid == null) {
			if (useDiskCachedModelGrid) {
				try {
					innerModelGrid = new DiskCachedModelGrid(globalOptionModel.getNorthLimit(), 
							globalOptionModel.getSouthLimit(), 
							globalOptionModel.getEastLimit(), 
							globalOptionModel.getWestLimit(), 
							modelDimensions.getOutputLatitudeResolution(), 
							modelDimensions.getOutputLongitudeResolution(),
							modelContext.getRasterDataContext().getDataMinimumValue(),
							modelContext.getRasterDataContext().getDataMaximumValue());
				} catch (Exception ex) {
					throw new RenderEngineException("Error creating disk cached model grid: " + ex.getMessage(), ex);
				}
			} else {
				
				innerModelGrid = new BufferedModelGrid(globalOptionModel.getNorthLimit(), 
						globalOptionModel.getSouthLimit(), 
						globalOptionModel.getEastLimit(), 
						globalOptionModel.getWestLimit(), 
						modelDimensions.getOutputLatitudeResolution(), 
						modelDimensions.getOutputLongitudeResolution(),
						modelContext.getRasterDataContext().getDataMinimumValue(),
						modelContext.getRasterDataContext().getDataMaximumValue());
						//modelContext.getRasterDataContext());
				innerModelGrid.reset();

			}
			
			
			modelGrid = new FillControlledModelGrid(globalOptionModel.getNorthLimit(), 
							globalOptionModel.getSouthLimit(), 
							globalOptionModel.getEastLimit(), 
							globalOptionModel.getWestLimit(), 
							modelDimensions.getOutputLatitudeResolution(), 
							modelDimensions.getOutputLongitudeResolution(),
							modelContext.getRasterDataContext().getDataMinimumValue(),
							modelContext.getRasterDataContext().getDataMaximumValue(),
							(modelContext.getImageDataContext().getImageListSize() > 0),
							modelContext.getRasterDataContext(),
							innerModelGrid,
							(globalOptionModel.getUseScripting() ? modelContext.getScriptingContext().getScriptProxy() : null));
			modelGrid.setForceResetAndRunFilters(globalOptionModel.getForceResetAndRunFilters());
		}
		
		if (modelCanvas == null) {
			modelCanvas = new ModelCanvas(modelDimensions.getOutputWidth(), 
														modelDimensions.getOutputHeight(), 
														globalOptionModel.getPixelStackDepth(),
														globalOptionModel.getSubpixelGridSize(), 
														globalOptionModel.getBackgroundColor(), 
														null);
			modelContext.setModelCanvas(modelCanvas);
			
			
		} else {
			modelCanvas.reset();
		}

		
		useScripting = globalOptionModel.getUseScripting();
		
		if (useScripting 
				&& modelContext.getScriptingContext() != null 
				&& modelContext.getScriptingContext().getScriptProxy() != null) {
			modelContext.getScriptingContext().getScriptProxy().setModelContext(modelContext);
		}
		
		
		if (useScripting) {
			onInitialize();
		}
		
		prepared = true;
	}
	
	
	public JDemElevationModel process() throws RenderEngineException
	{
		if (!isPrepared()) {
			throw new RenderEngineException("Model builder not yet prepared!");
		}
		
		if (!modelContainsData()) {
			log.info("Model contains no data. Skipping model build process");
			return null;
		}
		
		ProcessInterruptHandler interruptHandler = new ProcessInterruptHandler();
		GlobalOptionModel globalOptionModel = modelProcessManifest.getGlobalOptionModel();
		

		setProcessing(true);
		
		if (useScripting && !this.isCancelled()) {
			onModelBefore();
		}
		
		
		if (!this.isCancelled()) {
			for (ModelProcessContainer processContainer : modelProcessManifest.getProcessList()) {
				
				if (useScripting && !this.isCancelled()) {
					onProcessBefore(processContainer);
				}
				
				GridProcessor gridProcessor = processContainer.getGridProcessor();
				OptionModel optionModel = processContainer.getOptionModel();
				
				GridProcessing annotation = gridProcessor.getClass().getAnnotation(GridProcessing.class);
				String name = gridProcessor.getClass().getName();
				if (annotation != null) {
					name = annotation.name();
				}
				
				//if (annotation.type() != GridProcessingTypesEnum.RENDER) {// && dataLoaded) {
				//	continue;
				//}  
				
				if (annotation.type() == GridProcessingTypesEnum.DATA_LOAD) {// && dataLoaded) {
					continue;
				}  
				
				
				if (gridProcessor instanceof InterruptibleProcess) {
					interruptHandler.setInterruptibleProcess((InterruptibleProcess)gridProcessor);
				} else {
					interruptHandler.setInterruptibleProcess(null);
				}
				
				log.info("Preparing processor: '" + name + "'");
				gridProcessor.setAndPrepare(modelContext, modelGrid, modelDimensions, globalOptionModel, optionModel);
				
				
				if (annotation.isFilter() && gridProcessor instanceof GridPointFilter) {
					modelGrid.addGridFilter((GridPointFilter)gridProcessor);
				}
				
			//	
				
				
				
				
				
			//	if (useScripting && !this.isCancelled()) {
			//		onProcessAfter(processContainer);
			//	}
				
				this.checkPause();
				if (this.isCancelled()) {
					log.info("Model builder was cancelled. Exiting in incomplete state.");
					setProcessing(false);
					try {
						return modelContext.getModelCanvas().getJdemElevationModel();
					} catch (ModelContextException ex) {
						throw new RenderEngineException("Error fetching JDEM elevation model: " + ex.getMessage(), ex);
					}
				}
			}
		}
		
		
		
		if (!this.isCancelled()) {
			for (ModelProcessContainer processContainer : modelProcessManifest.getProcessList()) {
				
				if (useScripting && !this.isCancelled()) {
					onProcessBefore(processContainer);
				}
				
				GridProcessor gridProcessor = processContainer.getGridProcessor();
				OptionModel optionModel = processContainer.getOptionModel();
				
				GridProcessing annotation = gridProcessor.getClass().getAnnotation(GridProcessing.class);
				String name = gridProcessor.getClass().getName();
				if (annotation != null) {
					name = annotation.name();
				}
				
				if (!annotation.isFilter() && annotation.enabled()) {
					log.info("Executing processor: '" + name + "'");
					gridProcessor.process();
				}
			}
		}
		
		
		
		if (!this.isCancelled()) {
			for (ModelProcessContainer processContainer : modelProcessManifest.getProcessList()) {
				
				if (useScripting && !this.isCancelled()) {
					onProcessBefore(processContainer);
				}
				
				GridProcessor gridProcessor = processContainer.getGridProcessor();
				OptionModel optionModel = processContainer.getOptionModel();
				
				GridProcessing annotation = gridProcessor.getClass().getAnnotation(GridProcessing.class);
				String name = gridProcessor.getClass().getName();
				if (annotation != null) {
					name = annotation.name();
				}
				
				if (useScripting && !this.isCancelled() && annotation.enabled()) {
					onProcessAfter(processContainer);
				}
			}
		}
		
		dataLoaded = true;
		
		if (useScripting && !this.isCancelled()) {
			onModelAfter();
		}
		
		if (useScripting) {
			onDestroy();
		}
		
		if (globalOptionModel.getDisposeGridOnComplete()) {
			dispose();
		}
		
		
		setProcessing(false);
		
		JDemElevationModel elevationModel = null;
		
		if (globalOptionModel.getCreateJdemElevationModel()) {
			try {
				log.info("Compiling final elevation model");
				elevationModel = modelContext.getModelCanvas().getJdemElevationModel();
			} catch (ModelContextException ex) {
				throw new RenderEngineException("Error fetching JDEM elevation model: " + ex.getMessage(), ex);
			}
			setJDemElevationModelProperties(elevationModel);
		}
		
		return elevationModel;
	}
	

	protected void setJDemElevationModelProperties(JDemElevationModel elevationModel)
	{

		
		elevationModel.setProperty("subject", JDem846Properties.getProperty("us.wthr.jdem846.defaults.subject"));
		elevationModel.setProperty("description", JDem846Properties.getProperty("us.wthr.jdem846.defaults.description"));
		elevationModel.setProperty("author", JDem846Properties.getProperty("us.wthr.jdem846.defaults.author"));
		elevationModel.setProperty("author-contact", JDem846Properties.getProperty("us.wthr.jdem846.defaults.author-contact"));
		elevationModel.setProperty("institution", JDem846Properties.getProperty("us.wthr.jdem846.defaults.institution"));
		elevationModel.setProperty("institution-contact", JDem846Properties.getProperty("us.wthr.jdem846.defaults.institution-contact"));
		elevationModel.setProperty("institution-address", JDem846Properties.getProperty("us.wthr.jdem846.defaults.institution-address"));
		elevationModel.setProperty("render-date", (new Date()).toString());
		elevationModel.setProperty("product-version", JDem846Properties.getProperty("us.wthr.jdem846.version"));
		
		if (modelGrid != null) {
			elevationModel.setElevationHistogramModel(modelGrid.getElevationHistogramModel());
		}

		elevationModel.setProperty("max-model-latitude", ""+this.globalOptionModel.getNorthLimit());
		elevationModel.setProperty("min-model-latitude", ""+this.globalOptionModel.getSouthLimit());

		elevationModel.setProperty("max-model-longitude", ""+this.globalOptionModel.getEastLimit());
		elevationModel.setProperty("min-model-longitude", ""+this.globalOptionModel.getWestLimit());

		elevationModel.setProperty("max-data-latitude", ""+modelContext.getNorth());
		elevationModel.setProperty("min-data-latitude", ""+modelContext.getSouth());

		elevationModel.setProperty("max-data-longitude", ""+modelContext.getEast());
		elevationModel.setProperty("min-data-longitude", ""+modelContext.getWest());
		
		elevationModel.setProperty("model-resolution-latitude", ""+modelDimensions.outputLatitudeResolution);
		elevationModel.setProperty("model-resolution-longitude", ""+modelDimensions.outputLongitudeResolution);

		elevationModel.setProperty("data-resolution-latitude", ""+modelDimensions.latitudeResolution);
		elevationModel.setProperty("data-resolution-longitude", ""+modelDimensions.longitudeResolution);
		
		elevationModel.setProperty("elevation-minimum", ""+modelContext.getRasterDataContext().getDataMinimumValue());
		elevationModel.setProperty("elevation-maximum-true", ""+modelContext.getRasterDataContext().getDataMaximumValueTrue());
		elevationModel.setProperty("elevation-maximum-scaled", ""+modelContext.getRasterDataContext().getDataMaximumValue());
		elevationModel.setProperty("elevation-minmax-estimated", ""+globalOptionModel.isEstimateElevationRange());
		
		elevationModel.setProperty("model-columns", ""+modelDimensions.outputWidth);
		elevationModel.setProperty("model-rows", ""+modelDimensions.outputHeight);
	
		elevationModel.setProperty("data-columns", ""+modelDimensions.dataColumns);
		elevationModel.setProperty("data-rows", ""+modelDimensions.dataRows);
		
		elevationModel.setProperty("render-projection", globalOptionModel.getRenderProjection());
		elevationModel.setProperty("elevation-scale", globalOptionModel.getElevationScale());
		elevationModel.setProperty("elevation-multiple", ""+globalOptionModel.getElevationMultiple());
		elevationModel.setProperty("planet", globalOptionModel.getPlanet());
		
		
		try {
			String projection = (String) modelContext.getModelProcessManifest().getPropertyById("us.wthr.jdem846.model.ModelRenderOptionModel.mapProjection");
			if (projection != null) {
				elevationModel.setProperty("projection", projection);
			}
		} catch (ModelContainerException ex) {
			log.warn("Error fetching projection: " + ex.getMessage(), ex);
		}
		
		
		try {
			ViewPerspective viewPerspective = (ViewPerspective) modelContext.getModelProcessManifest().getPropertyById("us.wthr.jdem846.model.ModelRenderOptionModel.viewAngle");
			if (viewPerspective != null) {
				elevationModel.setProperty("view-perspective", viewPerspective.toString());
			}
		} catch (ModelContainerException ex) {
			log.warn("Error fetching view perspective: " + ex.getMessage(), ex);
		}
		
	}
	
	

	public boolean isPrepared()
	{
		return prepared;
	}
	

	
	protected void onInitialize() throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptingContext().getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.initialize();
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
	}
	
	protected void onModelBefore() throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptingContext().getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.onModelBefore();
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
	}
	
	protected void onModelAfter() throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptingContext().getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.onModelAfter();
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
	}
	
	protected void onProcessBefore(ModelProcessContainer modelProcessContainer) throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptingContext().getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.onProcessBefore(modelProcessContainer);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
	}
	
	protected void onProcessAfter(ModelProcessContainer modelProcessContainer) throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptingContext().getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.onProcessAfter(modelProcessContainer);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
	}

	protected void onDestroy() throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptingContext().getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.destroy();
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
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

	public ModelPointGrid getModelGrid()
	{
		return modelGrid;
	}
	
	

}
