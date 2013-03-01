package us.wthr.jdem846.project.context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.exception.ProjectParseException;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelProcessManifest;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.OptionModelChangeEvent;
import us.wthr.jdem846.model.OptionModelChangeListener;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.exceptions.ContextPrepareException;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.model.exceptions.ProcessContainerException;
import us.wthr.jdem846.model.processing.ModelProcessRegistry;
import us.wthr.jdem846.model.processing.ProcessInstance;
import us.wthr.jdem846.modelgrid.IModelGrid;
import us.wthr.jdem846.modelgrid.ModelGridContext;
import us.wthr.jdem846.project.ProcessMarshall;
import us.wthr.jdem846.project.ProjectFiles;
import us.wthr.jdem846.project.ProjectMarshall;
import us.wthr.jdem846.project.ProjectMarshaller;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.rasterdata.RasterDataProviderFactory;
import us.wthr.jdem846.rasterdata.RasterDataSource;
import us.wthr.jdem846.rasterdata.generic.IRasterDefinition;
import us.wthr.jdem846.scripting.ScriptingContext;
import us.wthr.jdem846.shapedata.ShapeDataContext;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.ui.RecentProjectTracker;

public class ProjectContext
{
	private static Log log = Logging.getLog(ProjectContext.class);

	// private static final StaticWindowInstanceProvider<ProjectContext>
	// instances = new StaticWindowInstanceProvider<ProjectContext>();
	private static ProjectContext INSTANCE = null;

	private ProjectChangeBroker projectChangeBroker = new ProjectChangeBroker();

	private ModelContext modelContext;
	private ModelProcessManifest modelProcessManifest;
	private RasterDataContext rasterDataContext;
	private ShapeDataContext shapeDataContext;
	private ImageDataContext imageDataContext;
	private ScriptingContext scriptingContext;
	private ModelGridContext modelGridContext;

	// private List<OptionModel> defaultOptionModelList;
	private Map<String, OptionModelContainer> optionModelContainerList;

	private List<ElevationModel> elevationModelList;

	private boolean ignoreOptionChanges = false;

	private String projectLoadedFrom = null;

	protected ProjectContext() throws ProjectException
	{
		this(null);
	}

	protected ProjectContext(String projectPath) throws ProjectException
	{
		initializeFromFile(projectPath);
	}

	protected void initializeFromFile(String projectPath) throws ProjectException
	{
		if (projectPath != null) {
			log.info("Initializing project from " + projectPath);
		}

		this.projectChangeBroker.fireOnBeforeProjectLoaded(projectLoadedFrom, projectPath, true);

		optionModelContainerList = new HashMap<String, OptionModelContainer>();
		elevationModelList = new ArrayList<ElevationModel>();

		this.projectLoadedFrom = projectPath;

		ProjectMarshall projectMarshall = null;

		if (projectPath != null) {
			projectMarshall = loadMarshalledProject(projectPath);
		} else {
			projectMarshall = new ProjectMarshall();
			try {
				projectMarshall.loadDefaults();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		if (projectMarshall.getElevationModels() != null) {
			this.elevationModelList.addAll(projectMarshall.getElevationModels());
		}

		rasterDataContext = new RasterDataContext();

		try {
			createOptionModelList(projectMarshall);
		} catch (Exception ex) {
			log.error("Error creating option model list: " + ex.getMessage(), ex);
			// TODO: Display error dialog

			// defaultOptionModelList = new LinkedList<OptionModel>();
		}

		try {
			modelProcessManifest = new ModelProcessManifest();
			modelProcessManifest.setGlobalOptionModel(getGlobalOptionModel());
		} catch (ProcessContainerException ex) {
			log.error("Error creating default model process manifest: " + ex.getMessage(), ex);
		}

		List<ProcessMarshall> processMarshalls = projectMarshall.getProcesses();
		for (ProcessMarshall processMarshall : processMarshalls) {

			try {
				OptionModelContainer container = getOptionModelContainer(processMarshall.getId());
				OptionModel optionModel = container.getOptionModel();
				modelProcessManifest.addWorker(processMarshall.getId(), optionModel);
			} catch (ProcessContainerException ex) {
				log.error("Error loading process worker '" + processMarshall.getId() + "': " + ex.getMessage(), ex);
			}

		}

		shapeDataContext = new ShapeDataContext();
		imageDataContext = new ImageDataContext();
		scriptingContext = new ScriptingContext();
		modelGridContext = new ModelGridContext();

		try {
			modelContext = ModelContext.createInstance(rasterDataContext, shapeDataContext, imageDataContext, modelGridContext, modelProcessManifest, scriptingContext);
		} catch (ModelContextException ex) {
			// TODO: Display error message dialog
			log.error("Exception creating model context: " + ex.getMessage(), ex);
		}

		// Apply model options
		if (projectMarshall != null) {
			// TODO: Load saved project into model

			for (RasterDataSource rasterDataSource : projectMarshall.getRasterFiles()) {
				addElevationDataset(rasterDataSource, false);
			}

			for (ShapeFileRequest shapeFile : projectMarshall.getShapeFiles()) {
				addShapeDataset(shapeFile.getPath(), shapeFile.getShapeDataDefinitionId(), false);
			}

			for (SimpleGeoImage imageRef : projectMarshall.getImageFiles()) {
				addImageryData(imageRef.getImageFile(), imageRef.getNorth(), imageRef.getSouth(), imageRef.getEast(), imageRef.getWest(), false);
			}

			if (projectMarshall.getModelGrid() != null) {
				modelGridContext.importModelGrid(projectMarshall.getModelGrid());
			}

			scriptingContext.setScriptLanguage(projectMarshall.getScriptLanguage());
			scriptingContext.setUserScript(projectMarshall.getUserScript());

		}

		projectChangeBroker.fireOnProjectLoaded(projectPath, true);

	}

	protected ProjectMarshall loadMarshalledProject(String projectPath) throws ProjectException
	{
		ProjectMarshall projectMarshall = null;

		if (projectPath != null) {
			try {
				projectMarshall = ProjectFiles.read(projectPath, false);

				RecentProjectTracker.addProject(projectPath);
			} catch (FileNotFoundException ex) {
				log.error("Project file not found: " + ex.getMessage(), ex);
				throw new ProjectException("Project file not found: " + ex.getMessage(), ex);
			} catch (IOException ex) {
				log.error("IO error reading from disk: " + ex.getMessage(), ex);
				throw new ProjectException("IO error reading from disk: " + ex.getMessage(), ex);
			} catch (ProjectParseException ex) {
				log.error("Error parsing project: " + ex.getMessage(), ex);
				throw new ProjectException("Error parsing project: " + ex.getMessage(), ex);
			}

			projectMarshall.setLoadedFrom(projectPath);
		}

		return projectMarshall;
	}

	protected void createOptionModelList(ProjectMarshall projectMarshall) throws Exception
	{
		GlobalOptionModel globalOptionModel = new GlobalOptionModel();

		OptionModelChangeListener optionModelChangeListener = new OptionModelChangeListener()
		{
			public void onPropertyChanged(OptionModelChangeEvent e)
			{
				log.info("Project context option changed");
				if (!ignoreOptionChanges) {
					projectChangeBroker.fireOnOptionChanged(e, true);
				}
			}
		};

		OptionModelContainer globalOptionModelContainer = new OptionModelContainer(globalOptionModel);
		globalOptionModelContainer.addOptionModelChangeListener(optionModelChangeListener);

		if (projectMarshall != null && projectMarshall.getGlobalOptions() != null) {

			for (String option : projectMarshall.getGlobalOptions().keySet()) {
				String value = projectMarshall.getGlobalOptions().get(option);
				if (value != null) {
					try {
						globalOptionModelContainer.setPropertyValueById(option, value);
					} catch (ModelContainerException ex) {
						log.warn("Property not found with id '" + option + "': " + ex.getMessage(), ex);
					}
				}
			}
		}

		this.optionModelContainerList.put(globalOptionModel.getClass().getCanonicalName(), globalOptionModelContainer);

		List<ProcessInstance> processList = ModelProcessRegistry.getInstances();
		for (ProcessInstance processInstance : processList) {

			ProcessMarshall processMarshall = projectMarshall.getProcessMarshall(processInstance.getId());

			OptionModel optionModel = processInstance.createOptionModel();
			OptionModelContainer optionModelContainer = new OptionModelContainer(optionModel);

			if (processMarshall != null) {

				for (String option : processMarshall.getOptions().keySet()) {
					String value = processMarshall.getOptions().get(option);
					try {
						optionModelContainer.setPropertyValueById(option, value);
					} catch (Exception ex) {
						log.warn("Option '" + option + "' cannot be set: " + ex.getMessage());
						// TODO: Display error dialog
					}
				}

			}
			optionModelContainer.addOptionModelChangeListener(optionModelChangeListener);
			optionModelContainerList.put(processInstance.getId(), optionModelContainer);

		}
	}

	public void addModelGridDataset(String filePath) throws ProjectException
	{
		addModelGridDataset(filePath, true);
	}

	protected void addModelGridDataset(String filePath, boolean triggerModelChanged) throws ProjectException
	{

		modelGridContext.importModelGrid(filePath);

		if (triggerModelChanged) {
			projectChangeBroker.fireOnDataAdded(true);
		}
	}

	public void addElevationDataset(final String filePath) throws ProjectException
	{
		addElevationDataset(filePath, null, true);
	}
	
	public void addElevationDataset(RasterDataSource rasterDataSource) throws ProjectException
	{
		addElevationDataset(rasterDataSource, true);
	}

	public void addElevationDataset(RasterDataSource rasterDataSource, final boolean triggerModelChanged) throws ProjectException
	{
		addElevationDataset(rasterDataSource.getFilePath(), rasterDataSource.getDefinition(), triggerModelChanged);
	}
	
	protected void addElevationDataset(final String filePath, final IRasterDefinition rasterDefinition, final boolean triggerModelChanged) throws ProjectException
	{

		RasterData rasterData = null;
		try {
			rasterData = RasterDataProviderFactory.loadRasterData(filePath, rasterDefinition);
		} catch (DataSourceException ex) {
			log.warn("Invalid file format: " + ex.getMessage(), ex);
			throw new ProjectException("Invalid file format: " + ex.getMessage(), ex);
		}

		try {
			rasterDataContext.addRasterData(rasterData);
		} catch (DataSourceException ex) {
			log.error("Failed to add raster data: " + ex.getMessage(), ex);
			throw new ProjectException("Failed to add raster data: " + ex.getMessage(), ex);
		}

		try {
			rasterDataContext.prepare();
		} catch (ContextPrepareException ex) {
			log.error("Failed to prepare raster data: " + ex.getMessage(), ex);
			throw new ProjectException("Failed to prepare raster data: " + ex.getMessage(), ex);
		}

		try {
			boolean estimate = this.modelProcessManifest.getGlobalOptionModel().isEstimateElevationRange();

			boolean updateDataMinMax = true;

			if (updateDataMinMax && (rasterDataContext.getRasterDataListSize() == 0 && imageDataContext.getImageListSize() > 0)) {
				estimate = true;
			}

			modelContext.updateContext(updateDataMinMax, estimate);
		} catch (ModelContextException ex) {
			// TODO: Display error dialog
			log.warn("Exception updating model context: " + ex.getMessage(), ex);
		}

		if (triggerModelChanged) {
			projectChangeBroker.fireOnDataAdded(true);
		}

	}

	public void addShapeDataset(String filePath, String shapeDataDefinitionId) throws ProjectException
	{
		addShapeDataset(filePath, shapeDataDefinitionId, true);
	}

	protected void addShapeDataset(String filePath, String shapeDataDefinitionId, boolean triggerModelChanged) throws ProjectException
	{
		try {
			shapeDataContext.addShapeFile(filePath, shapeDataDefinitionId);
		} catch (Exception ex) {
			throw new ProjectException("Error added shape data: " + ex.getMessage(), ex);
		}

		if (triggerModelChanged) {
			projectChangeBroker.fireOnDataAdded(true);
		}

	}

	public void addImageryData(String filePath) throws ProjectException
	{
		addImageryData(filePath, modelContext.getNorth(), modelContext.getSouth(), modelContext.getEast(), modelContext.getWest());
	}

	public void addImageryData(String filePath, double north, double south, double east, double west) throws ProjectException
	{
		addImageryData(filePath, north, south, east, west, true);
	}

	protected void addImageryData(String filePath, double north, double south, double east, double west, boolean triggerModelChanged) throws ProjectException
	{
		SimpleGeoImage image = null;

		try {
			image = new SimpleGeoImage(filePath, north, south, east, west);
			image.load(true);
		} catch (DataSourceException ex) {
			log.warn("Invalid file format: " + ex.getMessage(), ex);
			throw new ProjectException("Invalid file format: " + ex.getMessage(), ex);
		}

		imageDataContext.addImage(image);

		if (triggerModelChanged) {
			projectChangeBroker.fireOnDataAdded(true);
		}

	}

	public void removeSourceData(InputSourceData dataSource) throws DataSourceException
	{
		removeSourceData(dataSource, true);
	}

	public void removeSourceData(InputSourceData dataSource, boolean triggerModelChanged) throws DataSourceException
	{
		if (dataSource == null) {
			return;
		}

		if (dataSource instanceof RasterData) {
			removeElevationData((RasterData) dataSource, triggerModelChanged);
		} else if (dataSource instanceof ShapeFileRequest) {
			removeShapeData((ShapeFileRequest) dataSource, triggerModelChanged);
		} else if (dataSource instanceof SimpleGeoImage) {
			removeImageData((SimpleGeoImage) dataSource, triggerModelChanged);
		} else if (dataSource instanceof IModelGrid) {
			removeModelGridData(triggerModelChanged);
		}
	}

	public void removeModelGridData() throws DataSourceException
	{
		removeModelGridData(true);
	}

	public void removeModelGridData(boolean triggerModelChanged) throws DataSourceException
	{
		modelGridContext.unloadModelGrid();

		if (triggerModelChanged) {
			projectChangeBroker.fireOnDataAdded(true);
		}
	}

	public void removeElevationData(int index) throws DataSourceException
	{
		removeElevationData(index, true);
	}

	public void removeElevationData(int index, boolean triggerModelChanged) throws DataSourceException
	{
		rasterDataContext.removeRasterData(index);

		if (triggerModelChanged) {
			projectChangeBroker.fireOnDataAdded(true);
		}
	}

	public void removeElevationData(RasterData rasterData) throws DataSourceException
	{
		removeElevationData(rasterData, true);
	}

	public void removeElevationData(RasterData rasterData, boolean triggerModelChanged) throws DataSourceException
	{
		rasterDataContext.removeRasterData(rasterData);

		if (triggerModelChanged) {
			projectChangeBroker.fireOnDataAdded(true);
		}
	}

	public void removeShapeData(int index) throws DataSourceException
	{
		removeShapeData(index, true);
	}

	public void removeShapeData(int index, boolean triggerModelChanged) throws DataSourceException
	{
		shapeDataContext.removeShapeFile(index);

		if (triggerModelChanged) {
			projectChangeBroker.fireOnDataAdded(true);
		}
	}

	public void removeShapeData(ShapeFileRequest shapeFileRequest) throws DataSourceException
	{
		removeShapeData(shapeFileRequest, true);
	}

	public void removeShapeData(ShapeFileRequest shapeFileRequest, boolean triggerModelChanged) throws DataSourceException
	{
		shapeDataContext.removeShapeFile(shapeFileRequest);

		if (triggerModelChanged) {
			projectChangeBroker.fireOnDataAdded(true);
		}
	}

	public void removeImageData(int index) throws DataSourceException
	{
		removeImageData(index, true);
	}

	public void removeImageData(int index, boolean triggerModelChanged) throws DataSourceException
	{
		SimpleGeoImage image = imageDataContext.removeImage(index);
		if (image.isLoaded()) {
			image.unload();
		}

		if (triggerModelChanged) {
			projectChangeBroker.fireOnDataAdded(true);
		}
	}

	public void removeImageData(SimpleGeoImage simpleGeoImage) throws DataSourceException
	{
		removeImageData(simpleGeoImage, true);
	}

	public void removeImageData(SimpleGeoImage simpleGeoImage, boolean triggerModelChanged) throws DataSourceException
	{
		imageDataContext.removeImage(simpleGeoImage);

		if (simpleGeoImage.isLoaded()) {
			simpleGeoImage.unload();
		}

		if (triggerModelChanged) {
			projectChangeBroker.fireOnDataAdded(true);
		}
	}

	public ProjectChangeBroker getProjectChangeBroker()
	{
		return projectChangeBroker;
	}

	public ModelContext getModelContext()
	{
		return modelContext;
	}

	public ModelProcessManifest getModelProcessManifest()
	{
		return modelProcessManifest;
	}

	public RasterDataContext getRasterDataContext()
	{
		return rasterDataContext;
	}

	public ShapeDataContext getShapeDataContext()
	{
		return shapeDataContext;
	}

	public ImageDataContext getImageDataContext()
	{
		return imageDataContext;
	}

	public ModelGridContext getModelGridContext()
	{
		return modelGridContext;
	}

	public ScriptingContext getScriptingContext()
	{
		return scriptingContext;
	}

	public List<OptionModel> getDefaultOptionModelList()
	{
		List<OptionModel> optionList = new ArrayList<OptionModel>();
		for (Entry<String, OptionModelContainer> entry : optionModelContainerList.entrySet()) {
			optionList.add(entry.getValue().getOptionModel());
		}
		return optionList;
	}

	public List<OptionModelContainer> getDefaultOptionModelContainerList()
	{
		List<OptionModelContainer> containerList = new ArrayList<OptionModelContainer>();
		for (Entry<String, OptionModelContainer> entry : optionModelContainerList.entrySet()) {
			containerList.add(entry.getValue());
		}
		return containerList;
	}

	public OptionModelContainer getOptionModelContainer(Class<?> clazz)
	{
		for (Entry<String, OptionModelContainer> entry : optionModelContainerList.entrySet()) {
			if (entry.getValue().getOptionModel().getClass().equals(clazz)) {
				return entry.getValue();
			}
		}

		return null;
	}

	public OptionModelContainer getOptionModelContainer(String processId)
	{
		return optionModelContainerList.get(processId);
		/*
		 * ProcessInstance processInstance =
		 * ModelProcessRegistry.getInstance(processId);
		 * 
		 * if (processInstance != null) { Class<?> clazz =
		 * processInstance.getOptionModelClass();
		 * 
		 * return ProjectContext.getInstance().getOptionModelContainer(clazz);
		 * 
		 * } else { log.info("Process not found with id " + processId); return
		 * null; }
		 */
	}

	public OptionModelContainer getGlobalOptionModelContainer()
	{
		return optionModelContainerList.get(GlobalOptionModel.class.getCanonicalName());
	}

	public GlobalOptionModel getGlobalOptionModel()
	{
		return (GlobalOptionModel) getGlobalOptionModelContainer().getOptionModel();
	}

	public List<ElevationModel> getElevationModelList()
	{
		List<ElevationModel> listCopy = new LinkedList<ElevationModel>(elevationModelList);
		return listCopy;
	}

	public void addElevationModel(ElevationModel elevationModel)
	{
		this.elevationModelList.add(elevationModel);
		this.projectChangeBroker.fireOnElevationModelAdded(elevationModel, true);
	}

	public boolean removeElevationModel(ElevationModel elevationModel)
	{
		boolean wasRemoved = this.elevationModelList.remove(elevationModel);
		if (wasRemoved) {
			this.projectChangeBroker.fireOnElevationModelRemoved(elevationModel, true);
		}
		return wasRemoved;
	}

	public boolean getIgnoreOptionChanges()
	{
		return ignoreOptionChanges;
	}

	public void setIgnoreOptionChanges(boolean ignoreOptionChanges)
	{
		this.ignoreOptionChanges = ignoreOptionChanges;
	}

	public void save()
	{
		save(projectLoadedFrom);
	}

	public void save(GetSaveLocationCallback cb)
	{
		save(cb.getSaveLocation(projectLoadedFrom));
	}

	public void save(String saveTo)
	{
		log.info("Save to " + saveTo);

		if (saveTo != null) {
			try {

				ProjectMarshall projectMarshall = ProjectMarshaller.marshallProject(modelContext);
				projectMarshall.getElevationModels().addAll(this.elevationModelList);
				ProjectFiles.write(projectMarshall, saveTo);

				this.projectLoadedFrom = saveTo;

				// RecentProjectTracker.addProject(saveTo);

				log.info("Project file saved to " + saveTo);
			} catch (Exception ex) {
				log.warn("Error trying to write project to disk: " + ex.getMessage(), ex);

				return;
			}
		} else {
			log.info("Save path is null, cannot save");
		}

	}

	public static void initialize() throws ProjectException
	{
		ProjectContext.initialize(null);
	}

	public static void initialize(String projectPath) throws ProjectException
	{
		if (ProjectContext.INSTANCE == null) {
			ProjectContext.INSTANCE = new ProjectContext(projectPath);
		} else {
			ProjectContext.INSTANCE.initializeFromFile(projectPath);
		}
	}

	public static ProjectContext getInstance()
	{
		if (ProjectContext.INSTANCE == null) {
			try {
				ProjectContext.initialize();
			} catch (ProjectException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
		return ProjectContext.INSTANCE;
	}

	public void addProjectChangeListener(ProjectChangeListener l)
	{
		this.projectChangeBroker.addProjectChangeListener(l);
	}

	public boolean removeProjectChangeListener(ProjectChangeListener l)
	{
		return this.projectChangeBroker.removeProjectChangeListener(l);
	}

}
