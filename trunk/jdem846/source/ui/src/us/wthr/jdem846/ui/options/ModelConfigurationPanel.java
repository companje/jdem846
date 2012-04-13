package us.wthr.jdem846.ui.options;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelProcessContainer;
import us.wthr.jdem846.model.ModelProcessManifest;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.dataload.GridLoadOptionModel;
import us.wthr.jdem846.model.processing.dataload.GridLoadProcessor;
import us.wthr.jdem846.model.processing.dataload.SurfaceNormalsOptionModel;
import us.wthr.jdem846.model.processing.dataload.SurfaceNormalsProcessor;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.ScrollPane;
import us.wthr.jdem846.ui.base.TabPane;
import us.wthr.jdem846.ui.panels.EmbeddedTabbedPane;

@SuppressWarnings("serial")
public class ModelConfigurationPanel extends Panel
{
	private static Log log = Logging.getLog(ModelConfigurationPanel.class);
	
	private ProcessTypeConfigurationPanel coloringConfiguration;
	private ProcessTypeConfigurationPanel shadingConfiguration;
	private ProcessTypeConfigurationPanel renderConfiguration;
	private DynamicOptionsPanel globalOptionsPanel;
	
	private GlobalOptionModel globalOptionModel;
	private OptionModelContainer globalOptionModelContainer;
	
	public ModelConfigurationPanel(ModelProcessManifest modelProcessManifest)
	{
		
		
		
		
		globalOptionModel = new GlobalOptionModel();
		try {
			globalOptionModelContainer = new OptionModelContainer(globalOptionModel);
		} catch (InvalidProcessOptionException ex) {
			// TODO Display error dialog
			log.error("Error loading global option model container: " + ex.getMessage(), ex);
			return;
		}
		
		List<OptionModel> optionModelList = new LinkedList<OptionModel>();
		for (ModelProcessContainer modelProcessContainer : modelProcessManifest.getProcessList()) {
			optionModelList.add(modelProcessContainer.getOptionModel());
		}
		
		//us.wthr.jdem846.ui.options.modelConfiguration.shadingProcessor.default
		//us.wthr.jdem846.ui.options.modelConfiguration.renderProcessor.default
		
		String defaultColoringProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.coloringProcessor.default");
		String defaultShadingProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.shadingProcessor.default");
		String defaultRenderProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.renderProcessor.default");
		
		globalOptionsPanel = new DynamicOptionsPanel(globalOptionModelContainer);
		coloringConfiguration = new ProcessTypeConfigurationPanel(GridProcessingTypesEnum.COLORING, defaultColoringProcessor, optionModelList);
		shadingConfiguration = new ProcessTypeConfigurationPanel(GridProcessingTypesEnum.SHADING, defaultShadingProcessor, optionModelList);
		renderConfiguration = new ProcessTypeConfigurationPanel(GridProcessingTypesEnum.RENDER, defaultRenderProcessor, optionModelList);
		
		ScrollPane globalOptionsScroll = new ScrollPane(globalOptionsPanel);
		
		EmbeddedTabbedPane tabPane = new EmbeddedTabbedPane();
		
		tabPane.add("General", globalOptionsScroll);
		tabPane.add("Coloring", coloringConfiguration);
		tabPane.add("Shading", shadingConfiguration);
		tabPane.add("Rendering", renderConfiguration);
		
		setLayout(new BorderLayout());
		add(tabPane, BorderLayout.CENTER);
	}
	
	
	public ModelProcessManifest getModelProcessManifest()
	{
		ModelProcessManifest modelProcessManifest = new ModelProcessManifest();
		
		modelProcessManifest.setGlobalOptionModel(this.globalOptionModel.copy());
		
		String defaultLoadProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.loadProcessor.default");
		String defaultSurfaceNormalsProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.normalsProcessor.default");
		
		
		String coloringProcessId = coloringConfiguration.getCurrentProcessId();
		OptionModel coloringOptionModel = coloringConfiguration.getCurrentOptionModel();
		
		String shadingProcessId = shadingConfiguration.getCurrentProcessId();
		OptionModel shadingOptionModel = shadingConfiguration.getCurrentOptionModel();
		
		String renderProcessId = renderConfiguration.getCurrentProcessId();
		OptionModel renderOptionModel = renderConfiguration.getCurrentOptionModel();
		
		modelProcessManifest.addProcessor(defaultLoadProcessor, new GridLoadOptionModel());
		modelProcessManifest.addProcessor(defaultSurfaceNormalsProcessor, new SurfaceNormalsOptionModel());
		
		modelProcessManifest.addProcessor(coloringProcessId, coloringOptionModel);
		modelProcessManifest.addProcessor(shadingProcessId, shadingOptionModel);
		modelProcessManifest.addProcessor(renderProcessId, renderOptionModel);
		
		return modelProcessManifest;
	}
	
}
