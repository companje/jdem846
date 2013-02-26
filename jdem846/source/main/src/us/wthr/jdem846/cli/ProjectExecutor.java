package us.wthr.jdem846.cli;

import java.util.List;
import java.util.Map.Entry;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.RegistryKernel;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.export.ExportCompletedListener;
import us.wthr.jdem846.export.ModelImageExporter;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelBuilder;
import us.wthr.jdem846.model.ModelProcessManifest;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.exceptions.ContextPrepareException;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.modelgrid.ModelGridContext;
import us.wthr.jdem846.project.context.ProjectContext;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.scripting.ScriptingContext;
import us.wthr.jdem846.shapedata.ShapeDataContext;


public class ProjectExecutor
{
	private static Log log = null;
	
	private ModelContext modelContext;
	private ModelProcessManifest modelProcessManifest;
	private RasterDataContext rasterDataContext;
	private ShapeDataContext shapeDataContext;
	private ImageDataContext imageDataContext;
	private ScriptingContext scriptingContext;
	private ModelGridContext modelGridContext;
	
	private List<OptionModel> defaultOptionModelList;
	private List<OptionModelContainer> defaultOptionModelContainerList;
	
	protected static void bootstrapSystemProperties()
	{
		
		if (System.getProperty("us.wthr.jdem846.installPath") == null) {
			System.setProperty("us.wthr.jdem846.installPath", System.getProperty("user.dir"));
		}
		if (System.getProperty("us.wthr.jdem846.resourcesPath") == null) {
			System.setProperty("us.wthr.jdem846.resourcesPath", System.getProperty("us.wthr.jdem846.installPath"));
		}
		
		if (System.getProperty("us.wthr.jdem846.userSettingsPath") == null) {
			System.setProperty("us.wthr.jdem846.userSettingsPath", System.getProperty("user.home") + "/.jdem846");
		}
		
		if (System.getProperty("us.wthr.jdem846.testOutputPath") == null) {
			System.setProperty("us.wthr.jdem846.testOutputPath", System.getProperty("user.dir") + "/test-output");
		}
	}
	
	
	public static void initialize(boolean initRegistry) throws Exception
	{
		bootstrapSystemProperties();
		
		JDem846Properties.initializeApplicationProperties();
		JDem846Properties.initializeUserProperties();
		

		if (initRegistry) {
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
		}
		
		

	}
	
	
	
	protected static String getOptionPreceededBy(String[] args, String param)
	{
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(param) && args.length > i + 1) {
				return args[i + 1];
			}
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		
		try {
			initialize(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(ProjectExecutor.class);
		
		ProjectRunPlan runPlan = createProjectRunPlan(args);
		
		ProjectExecutor executer = new ProjectExecutor();
		try {
			
			executer.executeProject(runPlan);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static ProjectRunPlan createProjectRunPlan(String[] args)
	{
		String projectPath = getOptionPreceededBy(args, "-p");
		String saveImageTo = getOptionPreceededBy(args, "-o");
		
		ProjectRunPlan runPlan = new ProjectRunPlan(projectPath, saveImageTo);
		
		for (int i = 0; i < args.length; i++) {
			
			if (args[i].contains("=")) {
				String property = args[i].split("=")[0];
				String value = args[i].split("=")[1];
				runPlan.addOptionOverride(property, value);
			}
		}


		return runPlan;
	}
	
	public void executeProject(final ProjectRunPlan runPlan) throws Exception
	{
		
		
		
		log.info("Loading project '" + runPlan.getProjectPath() + "'");
		ProjectContext.initialize(runPlan.getProjectPath());

		ElevationModel elevationModel = run(runPlan);
		
		if (elevationModel != null) {
			ModelImageExporter.exportModelImage(elevationModel, runPlan.getSaveImagePath(), new ExportCompletedListener() {
				@Override
				public void onSaveSuccessful()
				{	
					log.info("Model saved to '" + runPlan.getSaveImagePath() + "'");
				}
	
				@Override
				public void onSaveFailed(Exception ex)
				{
					log.error("Error exporting model image to '" + runPlan.getSaveImagePath() + "': " + ex.getMessage(), ex);
				}
			});
		} else {
			log.warn("Recieved null elevation model. Cannot save");
		}
		//ProjectContext.getInstance().save();

	}
	
	protected ElevationModel run(ProjectRunPlan runPlan)
	{
		log.info("Model rendering task starting");
		
		
		long start = 0;
		long elapsed = 0;
		
		ModelContext modelContext = null;
		
		try {
			modelContext = ProjectContext.getInstance().getModelContext();
		} catch (DataSourceException ex) {
			log.error("Error fetching model context from project: " + ex.getMessage(), ex);
			return null;
		}
		
		
		GlobalOptionModel globalOptionModel = modelContext.getModelProcessManifest().getGlobalOptionModel();
		
		globalOptionModel.setGetStandardResolutionElevation(JDem846Properties.getBooleanProperty("us.wthr.jdem846.rendering.standardResolutionRetrieval"));
		globalOptionModel.setInterpolateData(JDem846Properties.getBooleanProperty("us.wthr.jdem846.rendering.interpolateToHigherResolution"));
		globalOptionModel.setAverageOverlappedData(JDem846Properties.getBooleanProperty("us.wthr.jdem846.rendering.averageOverlappedData"));
		globalOptionModel.setPrecacheStrategy(JDem846Properties.getProperty("us.wthr.jdem846.performance.precacheStrategy"));
		globalOptionModel.setTileSize(JDem846Properties.getIntProperty("us.wthr.jdem846.performance.tileSize"));
		
		
		for (Entry<String, String> optionOverride : runPlan.getOptionOverrides().entrySet()) {
			try {
				if (modelContext.getModelProcessManifest().getPropertyById(optionOverride.getKey()) != null) {
					log.info("Setting property '" + optionOverride.getKey() + "'");
					modelContext.getModelProcessManifest().setPropertyById(optionOverride.getKey(), optionOverride.getValue());
				}
			} catch (ModelContainerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		
		if (globalOptionModel.getLimitCoordinates()) {
			
			double optNorthLimit = globalOptionModel.getNorthLimit();
			double optSouthLimit = globalOptionModel.getSouthLimit();
			double optEastLimit = globalOptionModel.getEastLimit();
			double optWestLimit = globalOptionModel.getWestLimit();
			
			if (optNorthLimit != DemConstants.ELEV_NO_DATA)
				modelContext.setNorthLimit(optNorthLimit);
			if (optSouthLimit != DemConstants.ELEV_NO_DATA)
				modelContext.setSouthLimit(optSouthLimit);
			if (optEastLimit != DemConstants.ELEV_NO_DATA)
				modelContext.setEastLimit(optEastLimit);
			if (optWestLimit != DemConstants.ELEV_NO_DATA)
				modelContext.setWestLimit(optWestLimit);
		}
		
		try {
			modelContext.updateContext();
		} catch (ModelContextException ex) {
			log.error("Error preparing model context: " + ex.getMessage(), ex);
			return null;
		}
		
		try {
			modelContext.getScriptingContext().prepare();
		} catch (ContextPrepareException ex) {
			log.error("Error preparing script: " + ex.getMessage(), ex);
			return null;
		}
		
		ModelBuilder modelBuilder = new ModelBuilder();
		
		log.info("Initializing model builder...");
		try {
			modelBuilder.prepare(modelContext);
		} catch (RenderEngineException ex) {
			log.error("Error preparing model: " + ex.getMessage(), ex);
			return null;
		}
		
		ElevationModel elevationModel = null;
		
		log.info("Processing...");
		start = System.currentTimeMillis();
		
		try {
			elevationModel = modelBuilder.process();
		} catch (RenderEngineException ex) {
			log.error("Error processing model: " + ex.getMessage(), ex);
			return null;
		}
		
		//OutputProduct<ModelCanvas> product = engine.generate(false, false);
		elapsed = (System.currentTimeMillis() - start) / 1000;
		

		
		log.info("Completed render task in " + elapsed + " seconds");

		return elevationModel;
	}
	
	
	
}
