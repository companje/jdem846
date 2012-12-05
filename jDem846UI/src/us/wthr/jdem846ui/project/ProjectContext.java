package us.wthr.jdem846ui.project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.exception.ProjectParseException;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelProcessManifest;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.OptionModelChangeEvent;
import us.wthr.jdem846.model.OptionModelChangeListener;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.exceptions.ContextPrepareException;
import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.model.exceptions.ProcessContainerException;
import us.wthr.jdem846.model.processing.ModelProcessRegistry;
import us.wthr.jdem846.model.processing.ProcessInstance;
import us.wthr.jdem846.project.ProcessMarshall;
import us.wthr.jdem846.project.ProjectFiles;
import us.wthr.jdem846.project.ProjectMarshall;
import us.wthr.jdem846.project.ProjectMarshaller;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.rasterdata.RasterDataProviderFactory;
import us.wthr.jdem846.scripting.ScriptingContext;
import us.wthr.jdem846.shapedata.ShapeDataContext;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.ui.RecentProjectTracker;

public class ProjectContext {
	private static Log log = Logging.getLog(ProjectContext.class);
	
	
	//private static final StaticWindowInstanceProvider<ProjectContext> instances = new StaticWindowInstanceProvider<ProjectContext>();
	private static  ProjectContext INSTANCE = null;

	private ProjectChangeBroker projectChangeBroker = new ProjectChangeBroker();
	
	private ModelContext modelContext;
	private ModelProcessManifest modelProcessManifest;
	private RasterDataContext rasterDataContext;
	private ShapeDataContext shapeDataContext;
	private ImageDataContext imageDataContext;
	private ScriptingContext scriptingContext;
	
	
	private List<OptionModel> defaultOptionModelList;
	private List<OptionModelContainer> defaultOptionModelContainerList;
	
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
		
		
		defaultOptionModelContainerList = new LinkedList<OptionModelContainer>();
		elevationModelList = new LinkedList<ElevationModel>();
		
		this.projectLoadedFrom = projectPath;
		
		ProjectMarshall projectMarshall = null;
		
		if (projectPath != null) {
			projectMarshall = loadMarshalledProject(projectPath);
		} else {
			projectMarshall = new ProjectMarshall();
		}
		
		if (projectMarshall.getElevationModels() != null) {
			this.elevationModelList.addAll(projectMarshall.getElevationModels());
		}
		
		rasterDataContext = new RasterDataContext();

		try {
			defaultOptionModelList = createDefaultOptionModelList(projectMarshall);
		} catch (Exception ex) {
			log.error("Error creating option model list: " + ex.getMessage(), ex);
			// TODO: Display error dialog
			
			defaultOptionModelList = new LinkedList<OptionModel>();
		}
		
		try {
			modelProcessManifest = new ModelProcessManifest();
			if (defaultOptionModelList != null && defaultOptionModelList.size() > 0) {
				modelProcessManifest.setGlobalOptionModel((GlobalOptionModel)defaultOptionModelList.get(0));
			}
		} catch (ProcessContainerException ex) {
			log.error("Error creating default model process manifest: " + ex.getMessage(), ex);
		}
		
		
		shapeDataContext = new ShapeDataContext();
		imageDataContext = new ImageDataContext();
		scriptingContext = new ScriptingContext();

		
		try {
			modelContext = ModelContext.createInstance(rasterDataContext, shapeDataContext, imageDataContext, modelProcessManifest, scriptingContext);
		} catch (ModelContextException ex) {
			// TODO: Display error message dialog
			log.error("Exception creating model context: " + ex.getMessage(), ex);
		}

		// Apply model options
		if (projectMarshall != null) {
			// TODO: Load saved project into model
			
			for (String filePath : projectMarshall.getRasterFiles()) {
				addElevationDataset(filePath, false);
			}
					
			for (ShapeFileRequest shapeFile : projectMarshall.getShapeFiles()) {
				addShapeDataset(shapeFile.getPath(), shapeFile.getShapeDataDefinitionId(), false);
			}
			
			for (SimpleGeoImage imageRef : projectMarshall.getImageFiles()) {
				addImageryData(imageRef.getImageFile(), imageRef.getNorth(), imageRef.getSouth(), imageRef.getEast(), imageRef.getWest(), false);
			}
					
		}
		
		
		OptionModelChangeListener optionModelChangeListener = new OptionModelChangeListener() {
			public void onPropertyChanged(OptionModelChangeEvent e) {
				log.info("Project context option changed");
				if (!ignoreOptionChanges) {
					projectChangeBroker.fireOnOptionChanged(e);
				}
			}
		};
		
		for (OptionModel optionModel : defaultOptionModelList) {
			try {
				OptionModelContainer optionModelContainer = new OptionModelContainer(optionModel);
				optionModelContainer.addOptionModelChangeListener(optionModelChangeListener);
				defaultOptionModelContainerList.add(optionModelContainer);
			} catch (InvalidProcessOptionException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	
		
		projectChangeBroker.fireOnProjectLoaded();
		
		
	}
	
	
	protected ProjectMarshall loadMarshalledProject(String projectPath) throws ProjectException
	{
		ProjectMarshall projectMarshall = null;

		if (projectPath != null) {
			try {
				projectMarshall = ProjectFiles.read(projectPath);
				
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
	
	protected List<OptionModel> createDefaultOptionModelList(ProjectMarshall projectMarshall) throws Exception
	{
		List<OptionModel> defaultOptionModelList = new LinkedList<OptionModel>();
		
		GlobalOptionModel globalOptionModel = new GlobalOptionModel();
		
		if (projectMarshall != null && projectMarshall.getGlobalOptions() != null) {
			
			OptionModelContainer globalOptionModelContainer = new OptionModelContainer(globalOptionModel);
			
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
		
		defaultOptionModelList.add(globalOptionModel);
		
		
		
		List<ProcessInstance> processList = ModelProcessRegistry.getInstances();
		for (ProcessInstance processInstance : processList) {
			
			
			ProcessMarshall processMarshall = projectMarshall.getProcessMarshall(processInstance.getId());
			
			
				OptionModel optionModel = processInstance.createOptionModel();
				
				if (processMarshall != null) {
					OptionModelContainer optionModelContainer = new OptionModelContainer(optionModel);
					
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
				
				defaultOptionModelList.add(optionModel);
			
			
		}
		
		return defaultOptionModelList;
	}
	
	
	public void addElevationDataset(final String filePath) throws ProjectException
	{
		addElevationDataset(filePath, true);
	}
	
	protected void addElevationDataset(final String filePath, final boolean triggerModelChanged) throws ProjectException
	{

		RasterData rasterData = null;
		try {
			rasterData = RasterDataProviderFactory.loadRasterData(filePath);
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
			projectChangeBroker.fireOnDataAdded();
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
			projectChangeBroker.fireOnDataAdded();
		}

	}
	
	
	public void addImageryData(String filePath) throws ProjectException
	{
		addImageryData(filePath, modelContext.getNorth(), modelContext.getSouth(), modelContext.getEast(), modelContext.getWest());
	}
	
	public void addImageryData(String filePath, double north, double south, double east, double west) throws ProjectException
	{
		addImageryData(filePath, north, south, east, west);
	}
	
	
	
	protected void addImageryData(String filePath, double north, double south, double east, double west, boolean triggerModelChanged) throws ProjectException
	{
		SimpleGeoImage image = null;
		
		try {
			image = new SimpleGeoImage(filePath, north, south, east, west);
			image.load();
		} catch (DataSourceException ex) {
			log.warn("Invalid file format: " + ex.getMessage(), ex);
			throw new ProjectException("Invalid file format: " + ex.getMessage(), ex);
		}
		
		imageDataContext.addImage(image);
		
		if (triggerModelChanged) {
			projectChangeBroker.fireOnDataAdded();
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

	public ScriptingContext getScriptingContext() 
	{
		return scriptingContext;
	}

	
	
	public List<OptionModel> getDefaultOptionModelList() {
		return defaultOptionModelList;
	}

	public List<OptionModelContainer> getDefaultOptionModelContainerList() {
		return defaultOptionModelContainerList;
	}
	
	
	public OptionModelContainer getOptionModelContainer(Class<?> clazz)
	{

		for (OptionModelContainer optionModelContainer : defaultOptionModelContainerList) {
			if (optionModelContainer.getOptionModel().getClass().equals(clazz)) {
				return optionModelContainer;
			}
		}
			
		return null;
	}
	
	
	public OptionModelContainer getOptionModelContainer(String processId)
	{
		ProcessInstance processInstance = ModelProcessRegistry.getInstance(processId);
		
		if (processInstance != null) {
			Class<?> clazz = processInstance.getOptionModelClass();
			
			return ProjectContext.getInstance().getOptionModelContainer(clazz);

		} else {
			log.info("Process not found with id " + processId);
			return null;
		}
	}
	
	
	public List<ElevationModel> getElevationModelList() {
		List<ElevationModel> listCopy = new LinkedList<ElevationModel>(elevationModelList);
		return listCopy;
	}
	
	public void addElevationModel(ElevationModel elevationModel)
	{
		this.elevationModelList.add(elevationModel);
		this.projectChangeBroker.fireOnElevationModelAdded(elevationModel);
	}
	
	public boolean removeElevationModel(ElevationModel elevationModel)
	{
		boolean wasRemoved = this.elevationModelList.remove(elevationModel);
		if (wasRemoved) {
			this.projectChangeBroker.fireOnElevationModelRemoved(elevationModel);
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
				
				//RecentProjectTracker.addProject(saveTo);
				
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
