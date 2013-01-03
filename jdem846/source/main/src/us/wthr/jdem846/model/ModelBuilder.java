package us.wthr.jdem846.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemElevationModel;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.SimpleImageElevationModel;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.graphics.ImageCapture;
import us.wthr.jdem846.graphics.View;
import us.wthr.jdem846.graphics.ViewFactory;
import us.wthr.jdem846.graphics.framebuffer.ManagedConcurrentFrameBufferController;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.model.processing.GridFilterMethodStack;
import us.wthr.jdem846.model.processing.GridProcessMethodStack;
import us.wthr.jdem846.model.processing.render.ModelRenderOptionModel;
import us.wthr.jdem846.model.processing.render.ModelRenderer;
import us.wthr.jdem846.modelgrid.IFillControlledModelGrid;
import us.wthr.jdem846.modelgrid.IModelGrid;
import us.wthr.jdem846.modelgrid.ModelGridFactory;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.InterruptibleProcess;
import us.wthr.jdem846.render.ProcessInterruptListener;
import us.wthr.jdem846.scaling.ElevationScaler;
import us.wthr.jdem846.scaling.ElevationScalerEnum;
import us.wthr.jdem846.scaling.ElevationScalerFactory;
import us.wthr.jdem846.scripting.ScriptProxy;

public class ModelBuilder extends InterruptibleProcess implements IModelBuilder
{
	private static Log log = Logging.getLog(ModelBuilder.class);

	private ModelProgram modelProgram = null;

	private List<ModelProgram> modelPrograms = new ArrayList<ModelProgram>();

	private ModelProcessManifest modelProcessManifest;
	private ModelContext modelContext;
	private IFillControlledModelGrid modelGrid;
	private IModelGrid innerModelGrid = null;

	private ModelGridDimensions modelDimensions;
	private GlobalOptionModel globalOptionModel;
	private LatitudeProcessedList latitudeProcessedList = null;
	// private ModelCanvas modelCanvas;
	private BufferControlledRasterDataContainer bufferControlledRasterDataContainer;
	private ManagedConcurrentFrameBufferController frameBufferController;

	private boolean runLoadProcessor = true;
	private boolean runColorProcessor = true;
	private boolean runHillshadeProcessor = true;

	private boolean prepared = false;
	private boolean useScripting = true;

	private boolean isProcessing = false;

	private boolean dataLoaded = false;

	private ProgressTracker progressTracker = null;

	public ModelBuilder()
	{
		this(null);
	}

	public ModelBuilder(ProgressTracker progressTracker)
	{
		this.progressTracker = progressTracker;
	}

	public void dispose()
	{
		modelGrid.dispose();
		modelGrid = null;
	}

	public void prepare(ModelContext modelContext) throws RenderEngineException
	{
		this.modelProcessManifest = modelContext.getModelProcessManifest();
		globalOptionModel = modelProcessManifest.getGlobalOptionModel();

		if (progressTracker != null) {
			progressTracker.beginTask("Preparing model builder", 6 + globalOptionModel.getNumberOfThreads());
		}

		modelPrograms.clear();

		if (!globalOptionModel.getLimitCoordinates()) {
			globalOptionModel.setNorthLimit(modelContext.getNorth());
			globalOptionModel.setSouthLimit(modelContext.getSouth());
			globalOptionModel.setEastLimit(modelContext.getEast());
			globalOptionModel.setWestLimit(modelContext.getWest());
		}

		// +
		if (progressTracker != null) {
			progressTracker.worked(1);
		}

		ModelGridDimensions modelDimensions = ModelGridDimensions.getModelDimensions(modelContext);

		this.modelContext = modelContext;
		this.modelDimensions = modelDimensions;
		

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

		// +
		if (progressTracker != null) {
			progressTracker.worked(1);
		}

		bufferControlledRasterDataContainer = new BufferControlledRasterDataContainer(modelContext.getRasterDataContext(), globalOptionModel.getPrecacheStrategy(), modelDimensions.getLatitudeResolution(), globalOptionModel.getTileSize());
		modelContext.setRasterDataContext(bufferControlledRasterDataContainer);

		innerModelGrid = modelContext.getModelGridContext().getModelGrid();
		if (innerModelGrid == null) {
			try {
				innerModelGrid = ModelGridFactory.createBufferedModelGrid(modelContext);
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Error creating buffered model grid: " + ex.getMessage(), ex);
			}
			
			modelContext.getModelGridContext().setModelGrid(innerModelGrid);
		}
		
		
		modelGrid = modelContext.getModelGridContext().getFillControlledModelGrid();
		if (modelGrid == null) {
			
			try {
				modelGrid = ModelGridFactory.createFillControlledModelGrid(modelContext);
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Error creating fill controlled model grid: " + ex.getMessage(), ex);
			}
			
			modelGrid.setForceResetAndRunFilters(globalOptionModel.getForceResetAndRunFilters());
			modelContext.getModelGridContext().setFillControlledModelGrid(modelGrid);
			
			int dataRows = (int) MathExt.round((globalOptionModel.getNorthLimit() - globalOptionModel.getSouthLimit()) / modelDimensions.getTextureLatitudeResolution());
			this.latitudeProcessedList = new LatitudeProcessedList(globalOptionModel.getNorthLimit(), modelDimensions.getTextureLatitudeResolution(), dataRows);
		} else {

			this.latitudeProcessedList.reset();

		}

		// +
		if (progressTracker != null) {
			progressTracker.worked(1);
		}

		useScripting = globalOptionModel.getUseScripting();

		MapProjection mapProjection = null;

		try {

			mapProjection = globalOptionModel.getMapProjectionInstance();

		} catch (MapProjectionException ex) {
			throw new RenderEngineException("Error creating map projection: " + ex.getMessage(), ex);
		}

		int numberOfThreads = globalOptionModel.getNumberOfThreads();
		this.frameBufferController = new ManagedConcurrentFrameBufferController(globalOptionModel.getWidth(), globalOptionModel.getHeight(), numberOfThreads);

		// +
		if (progressTracker != null) {
			progressTracker.worked(1);
		}

		for (int i = 0; i < numberOfThreads; i++) {
			ModelProgram modelProgram;

			View view = ViewFactory.getViewInstance(modelContext, globalOptionModel, modelDimensions, mapProjection, modelContext.getScriptingContext().getScriptProxy(), modelGrid);

			try {
				modelProgram = modelProcessManifest.createModelProgram(null, null);
			} catch (Exception ex) {
				throw new RenderEngineException("Error creating model program: " + ex.getMessage(), ex);
			}

			RasterDataContext programRasterInstance = null;
			try {
				programRasterInstance = modelContext.getRasterDataContext().copy();
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Error creating raster data context copy: " + ex.getMessage(), ex);
			}
			
			
			IFillControlledModelGrid programModelGrid;
			try {
				programModelGrid = modelGrid.createDependentInstance(programRasterInstance);
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Error creating subinstance of model grid: " + ex.getMessage(), ex);
			}

			GridFilterMethodStack filterStack = modelProgram.getFilterStack();
			GridProcessMethodStack processStack = modelProgram.getProcessStack();

			programModelGrid.setGridFilters(filterStack);

			try {
				modelProgram.setRasterDataContext(programRasterInstance);
				modelProgram.setModelContext(modelContext);
				modelProgram.setModelGrid(programModelGrid);
				modelProgram.setModelDimensions(modelDimensions);
				modelProgram.setGlobalOptionModel(globalOptionModel);

				if (useScripting && modelContext.getScriptingContext() != null && modelContext.getScriptingContext().getScriptProxy() != null) {

					modelProgram.setScript(modelContext.getScriptingContext().getScriptProxy());
				} else {
					modelProgram.setScript(null);
				}

				modelProgram.prepare();

				modelPrograms.add(modelProgram);
			} catch (Exception ex) {
				throw new RenderEngineException("Error creating model program: " + ex.getMessage(), ex);
			}

			// +
			if (progressTracker != null) {
				progressTracker.worked(1);
			}
		}

		if (useScripting && modelContext.getScriptingContext() != null && modelContext.getScriptingContext().getScriptProxy() != null) {
			// modelContext.getScriptingContext().getScriptProxy().setModelContext(modelContext);
			ScriptProxy scriptProxy = modelContext.getScriptingContext().getScriptProxy();

			try {
				scriptProxy.setProperty("modelContext", modelContext);
				scriptProxy.setProperty("modelGrid", modelGrid);
				scriptProxy.setProperty("globalOptionModel", globalOptionModel);
				scriptProxy.setProperty("modelDimensions", modelDimensions);
			} catch (ScriptingException ex) {
				throw new RenderEngineException("Error setting script properties: " + ex.getMessage(), ex);
			}

		}

		// +
		if (progressTracker != null) {
			progressTracker.worked(1);
		}

		if (useScripting) {
			onInitialize();
		}

		// +
		if (progressTracker != null) {
			progressTracker.worked(1);
		}

		prepared = true;
	}

	@Override
	public ElevationModel process() throws RenderEngineException
	{
		this.onProcessBefore();
		
		if (!this.modelGrid.isCompleted()) {
			this.processModelData();
		} else {
			log.info("Model grid indicates that it is already completed. Skipping...");
		}
		this.processModelRender();
		this.onProcessAfter();
		this.onDestroy();
		return this.createElevationModel();
	}

	@Override
	public void processModelData() throws RenderEngineException
	{
		if (!isPrepared()) {
			throw new RenderEngineException("Model builder not yet prepared!");
		}

		if (!modelContainsData()) {
			log.info("Model contains no data. Skipping model build process");
			return;
		}

		// if (progressTracker != null) {
		// progressTracker.beginTask("Generating Model Data",
		// globalOptionModel.getNumberOfThreads());
		// }

		ProcessInterruptHandler interruptHandler = new ProcessInterruptHandler();
		GlobalOptionModel globalOptionModel = modelProcessManifest.getGlobalOptionModel();

		// setProcessing(true);
		// onProcessBefore();

		int numberOfThreads = globalOptionModel.getNumberOfThreads();
		double north = globalOptionModel.getNorthLimit();
		double south = globalOptionModel.getSouthLimit();
		double east = globalOptionModel.getEastLimit();
		double west = globalOptionModel.getWestLimit();

		double latitudeResolution = modelDimensions.getTextureLatitudeResolution();
		double longitudeResolution = modelDimensions.getTextureLongitudeResolution();

		if (numberOfThreads == 1) {
			ModelProgram modelProgram = this.modelPrograms.get(0);
			GridProcessChunkThread chunkThread = new GridProcessChunkThread(this.latitudeProcessedList, modelProgram, 0, north, south, east, west, latitudeResolution, longitudeResolution);
			chunkThread.run();

			if (progressTracker != null) {
				progressTracker.worked(1);
			}

		} else {
			GridProcessChunkThread[] threads = new GridProcessChunkThread[numberOfThreads];

			int dataRows = (int) MathExt.ceil((north - south) / latitudeResolution);

			int rowsPerThread = (int) MathExt.ceil((double) dataRows / (double) numberOfThreads);

			double chunkNorth = 0;
			double chunkSouth = 0;

			int threadNumber = 0;
			for (int y = 0; y <= dataRows; y += rowsPerThread) {

				chunkNorth = north - ((double) y * latitudeResolution);
				chunkSouth = chunkNorth - ((double) rowsPerThread * latitudeResolution);

				if (chunkSouth < south) {
					chunkSouth = south;
				}

				if (threadNumber >= 0 && threadNumber < modelPrograms.size()) {
					ModelProgram modelProgram = this.modelPrograms.get(threadNumber);
					threads[threadNumber] = new GridProcessChunkThread(this.latitudeProcessedList, modelProgram, threadNumber, chunkNorth, chunkSouth, east, west, latitudeResolution, longitudeResolution);

					threads[threadNumber].start();
				} else {
					log.warn("Invalid thread number: " + threadNumber);
				}

				threadNumber++;

			}

			boolean threadsActive = true;

			while (threadsActive) {
				threadsActive = false;

				for (GridProcessChunkThread thread : threads) {
					if (thread != null && !thread.isCompleted()) {
						threadsActive = true;
						break;
					}
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException ex) {
					log.warn("Thread sleep interrupted while waiting for grid process threads to complete: " + ex.getMessage(), ex);
				}
			}

			if (progressTracker != null) {
				progressTracker.worked(threads.length);
			}
		}
		
		this.modelGrid.setCompleted(true);
//		try {
//			ModelGridWriter.write("C:\\jdem\\temp\\modelgrid_test.jdemgrid", innerModelGrid);
//		} catch (IOException ex) {
//			// TODO Auto-generated catch block
//			ex.printStackTrace();
//		}

	}

	@Override
	public void processModelRender() throws RenderEngineException
	{
		if (!isPrepared()) {
			throw new RenderEngineException("Model builder not yet prepared!");
		}
		// onProcessAfter();

		// Reset the lat processed list to prepare for the render stage
		this.latitudeProcessedList.reset();
		frameBufferController.start();

		dataLoaded = true;
		
		log.info("Initializing final rendering...");
		MapProjection mapProjection = null;

		try {

			mapProjection = globalOptionModel.getMapProjectionInstance();

		} catch (MapProjectionException ex) {
			throw new RenderEngineException("Error creating map projection: " + ex.getMessage(), ex);
		}

		View view = ViewFactory.getViewInstance(modelContext, globalOptionModel, modelDimensions, mapProjection, modelContext.getScriptingContext().getScriptProxy(), modelGrid);

		ModelRenderer renderer = new ModelRenderer();
		renderer.setView(view);
		renderer.setFrameBuffer(frameBufferController.getPartialBuffer(0));
		renderer.setOptionModel(new ModelRenderOptionModel());
		renderer.setModelContext(modelContext);
		renderer.setGlobalOptionModel(globalOptionModel);
		renderer.setModelDimensions(modelDimensions);
		renderer.setScript(modelContext.getScriptingContext().getScriptProxy());
		renderer.setModelGrid(modelGrid);

		renderer.prepare();
		renderer.onProcessBefore();

		double latitudeResolution = modelDimensions.getModelLatitudeResolution();
		double longitudeResolution = modelDimensions.getModelLongitudeResolution();

		if (latitudeResolution <= 0) {
			throw new RenderEngineException("Invalid latitude resolution: " + latitudeResolution);
		}
		
		if (longitudeResolution <= 0) {
			throw new RenderEngineException("Invalid longitude resolution: " + longitudeResolution);
		}
		
		double north = globalOptionModel.getNorthLimit();
		double south = globalOptionModel.getSouthLimit();
		double east = globalOptionModel.getEastLimit();
		double west = globalOptionModel.getWestLimit();

		if (progressTracker != null) {
			progressTracker.beginTask("Final rendering", (int) MathExt.round((north - south) / latitudeResolution));
		}

		for (double latitude = north; latitude > south; latitude -= latitudeResolution) {
			if (this.latitudeProcessedList != null) {
				if (this.latitudeProcessedList.isLatitudeProcessed(latitude)) {
					continue;
				} else {
					this.latitudeProcessedList.setLatitudeProcessed(latitude);
				}
			}

			renderer.onLatitudeStart(latitude);

			for (double longitude = west; longitude <= east; longitude += longitudeResolution) {
				renderer.onModelPoint(latitude, longitude);
			}

			renderer.onLatitudeEnd(latitude);

			if (progressTracker != null) {
				progressTracker.worked(1);
				if (progressTracker.isCanceled()) {
					break;
				}
			}

		}

		renderer.onProcessAfter();
		renderer.dispose();

		this.frameBufferController.finish();
		while (frameBufferController.isAlive()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				log.warn("Thread sleep interrupted while waiting for grid process threads to complete: " + ex.getMessage(), ex);
			}
		}

	}

	@Override
	public ElevationModel createElevationModel() throws RenderEngineException
	{
		if (!isPrepared()) {
			throw new RenderEngineException("Model builder not yet prepared!");
		}

		ImageCapture imageCapture = this.frameBufferController.captureImage(this.globalOptionModel.getBackgroundColor().getRgba());

		if (progressTracker != null) {
			progressTracker.beginTask("Finalizing", 1);
		}

		ElevationModel elevationModel = null;

		if (globalOptionModel.getCreateJdemElevationModel()) {

			log.info("Compiling final elevation model");
			elevationModel = new JDemElevationModel(imageCapture);

			setJDemElevationModelProperties(elevationModel);
		} else {
			elevationModel = new SimpleImageElevationModel(imageCapture);
		}

		return elevationModel;
	}

	protected void setJDemElevationModelProperties(ElevationModel elevationModel) throws RenderEngineException
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
			try {
				elevationModel.setElevationHistogramModel(modelGrid.getElevationHistogramModel());
			} catch (DataSourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		elevationModel.setProperty("max-model-latitude", "" + this.globalOptionModel.getNorthLimit());
		elevationModel.setProperty("min-model-latitude", "" + this.globalOptionModel.getSouthLimit());

		elevationModel.setProperty("max-model-longitude", "" + this.globalOptionModel.getEastLimit());
		elevationModel.setProperty("min-model-longitude", "" + this.globalOptionModel.getWestLimit());

		elevationModel.setProperty("max-data-latitude", "" + modelContext.getNorth());
		elevationModel.setProperty("min-data-latitude", "" + modelContext.getSouth());

		elevationModel.setProperty("max-data-longitude", "" + modelContext.getEast());
		elevationModel.setProperty("min-data-longitude", "" + modelContext.getWest());

		elevationModel.setProperty("model-resolution-latitude", "" + modelDimensions.modelLatitudeResolution);
		elevationModel.setProperty("model-resolution-longitude", "" + modelDimensions.modelLongitudeResolution);

		elevationModel.setProperty("texture-resolution-latitude", "" + modelDimensions.textureLatitudeResolution);
		elevationModel.setProperty("texture-resolution-longitude", "" + modelDimensions.textureLongitudeResolution);

		elevationModel.setProperty("data-resolution-latitude", "" + modelDimensions.latitudeResolution);
		elevationModel.setProperty("data-resolution-longitude", "" + modelDimensions.longitudeResolution);

		elevationModel.setProperty("elevation-minimum", "" + modelContext.getRasterDataContext().getDataMinimumValue());
		elevationModel.setProperty("elevation-maximum-true", "" + modelContext.getRasterDataContext().getDataMaximumValueTrue());
		elevationModel.setProperty("elevation-maximum-scaled", "" + modelContext.getRasterDataContext().getDataMaximumValue());
		elevationModel.setProperty("elevation-minmax-estimated", "" + globalOptionModel.isEstimateElevationRange());

		elevationModel.setProperty("model-columns", "" + modelDimensions.outputWidth);
		elevationModel.setProperty("model-rows", "" + modelDimensions.outputHeight);

		elevationModel.setProperty("data-columns", "" + modelDimensions.dataColumns);
		elevationModel.setProperty("data-rows", "" + modelDimensions.dataRows);

		elevationModel.setProperty("render-projection", globalOptionModel.getRenderProjection());
		elevationModel.setProperty("elevation-scale", globalOptionModel.getElevationScale());
		elevationModel.setProperty("elevation-multiple", "" + globalOptionModel.getElevationMultiple());
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

	public void onProcessBefore() throws RenderEngineException
	{
		for (ModelProgram modelProgram : modelPrograms) {

			GridFilterMethodStack filterStack = modelProgram.getFilterStack();
			GridProcessMethodStack processStack = modelProgram.getProcessStack();

			try {
				filterStack.onProcessBefore();
				processStack.onProcessBefore();
			} catch (Exception ex) {
				throw new RenderEngineException("Exception thrown model worker during 'onProcessBefore': " + ex.getMessage(), ex);
			}

		}

		try {
			ScriptProxy scriptProxy = modelContext.getScriptingContext().getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.onProcessBefore();
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}

	}

	public void onProcessAfter() throws RenderEngineException
	{
		for (ModelProgram modelProgram : modelPrograms) {

			GridFilterMethodStack filterStack = modelProgram.getFilterStack();
			GridProcessMethodStack processStack = modelProgram.getProcessStack();

			try {
				filterStack.onProcessAfter();
				processStack.onProcessAfter();
			} catch (Exception ex) {
				throw new RenderEngineException("Exception thrown model worker during 'onProcessAfter': " + ex.getMessage(), ex);
			}

		}

		try {
			ScriptProxy scriptProxy = modelContext.getScriptingContext().getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.onProcessAfter();
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}

	}

	public void onDestroy() throws RenderEngineException
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
		boolean hasRasterData = modelContext.getRasterDataContext().getRasterDataListSize() > 0;
		boolean hasImageData = modelContext.getImageDataContext().getImageListSize() > 0;
		boolean hasModelGridData = modelContext.getModelGridContext().getGridLoadedFrom() != null;
		
		
		return (hasRasterData || hasImageData || hasModelGridData);
	}

	protected void setProcessing(boolean isProcessing)
	{
		this.isProcessing = isProcessing;
	}

	public boolean isProcessing()
	{
		return isProcessing;
	}

	public IModelGrid getModelGrid()
	{
		return modelGrid;
	}

}
