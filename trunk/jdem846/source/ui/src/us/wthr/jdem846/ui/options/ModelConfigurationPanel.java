package us.wthr.jdem846.ui.options;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelProcessContainer;
import us.wthr.jdem846.model.ModelProcessManifest;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.OptionModelChangeEvent;
import us.wthr.jdem846.model.OptionModelChangeListener;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.PropertyValidationResult;
import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.model.exceptions.OptionValidationException;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.dataload.GridLoadOptionModel;
import us.wthr.jdem846.model.processing.dataload.GridLoadProcessor;
import us.wthr.jdem846.model.processing.dataload.SurfaceNormalsOptionModel;
import us.wthr.jdem846.model.processing.dataload.SurfaceNormalsProcessor;
import us.wthr.jdem846.model.processing.shapes.ShapeOptionModel;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.ScrollPane;
import us.wthr.jdem846.ui.base.TabPane;
import us.wthr.jdem846.ui.panels.EmbeddedTabbedPane;

@SuppressWarnings("serial")
public class ModelConfigurationPanel extends Panel implements OptionModelChangeListener, ModelConfigurationChangeListener, OptionModelUIControl
{
	private static Log log = Logging.getLog(ModelConfigurationPanel.class);
	
	private ModelContext modelContext;
	
	private ProcessTypeConfigurationPanel coloringConfiguration;
	private ProcessTypeConfigurationPanel shadingConfiguration;
	//private ProcessTypeConfigurationPanel renderConfiguration;
	private DynamicOptionsPanel globalOptionsPanel;
	
	private GlobalOptionModel globalOptionModel;
	private OptionModelContainer globalOptionModelContainer;
	
	private List<ModelConfigurationChangeListener> modelConfigurationChangeListeners = new LinkedList<ModelConfigurationChangeListener>();
	
	public ModelConfigurationPanel(ModelContext modelContext, ModelProcessManifest modelProcessManifest, List<OptionModel> providedOptionModelList)
	{
		
		this.modelContext = modelContext;
		
		
		globalOptionModel = modelProcessManifest.getGlobalOptionModel();
		try {
			globalOptionModelContainer = new OptionModelContainer(globalOptionModel);
		} catch (InvalidProcessOptionException ex) {
			// TODO Display error dialog
			log.error("Error loading global option model container: " + ex.getMessage(), ex);
			return;
		}
		
		List<OptionModel> optionModelList = new LinkedList<OptionModel>();
		optionModelList.addAll(providedOptionModelList);
		
		//for (ModelProcessContainer modelProcessContainer : modelProcessManifest.getProcessList()) {
		//	optionModelList.add(modelProcessContainer.getOptionModel());
		//}
		
		//us.wthr.jdem846.ui.options.modelConfiguration.shadingProcessor.default
		//us.wthr.jdem846.ui.options.modelConfiguration.renderProcessor.default
		
		String defaultColoringProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.coloringProcessor.default");
		String defaultShadingProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.shadingProcessor.default");
		String defaultRenderProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.renderProcessor.default");
		
		
		
		globalOptionsPanel = new DynamicOptionsPanel(globalOptionModelContainer);
		coloringConfiguration = new ProcessTypeConfigurationPanel(GridProcessingTypesEnum.COLORING, defaultColoringProcessor, optionModelList);
		shadingConfiguration = new ProcessTypeConfigurationPanel(GridProcessingTypesEnum.SHADING, defaultShadingProcessor, optionModelList);
		//renderConfiguration = new ProcessTypeConfigurationPanel(GridProcessingTypesEnum.RENDER, defaultRenderProcessor, optionModelList);
		
		
		// Add Listeners
		//OptionModelChangeListener propertyChangeListener = new OptionModelChangeListener() {
			
		//};
		for (OptionModelPropertyContainer propertyContainer : globalOptionModelContainer.getProperties()) {
			propertyContainer.addOptionModelChangeListener(this);
		}

		coloringConfiguration.addModelConfigurationChangeListener(this);
		shadingConfiguration.addModelConfigurationChangeListener(this);
		//renderConfiguration.addModelConfigurationChangeListener(this);
		
		
		ScrollPane globalOptionsScroll = new ScrollPane(globalOptionsPanel);
		globalOptionsScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		EmbeddedTabbedPane tabPane = new EmbeddedTabbedPane();
		tabPane.setBorder(BorderFactory.createEmptyBorder());
		tabPane.add("General", globalOptionsScroll);
		tabPane.add("Coloring", coloringConfiguration);
		tabPane.add("Shading", shadingConfiguration);
		//tabPane.add("Rendering", renderConfiguration);
		
		setLayout(new BorderLayout());
		add(tabPane, BorderLayout.CENTER);
		
		
		
	}
	
	
	public OptionModelContainer getGlobalOptionModelContainer()
	{
		return this.globalOptionModelContainer;
	}
	
	public ModelProcessManifest getModelProcessManifest() throws Exception
	{
		ModelProcessManifest modelProcessManifest = new ModelProcessManifest();
		
		modelProcessManifest.setGlobalOptionModel(this.globalOptionModel.copy());
		
		//String defaultLoadProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.loadProcessor.default");
		//String defaultSurfaceNormalsProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.normalsProcessor.default");
		String defaultShapesProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.shapesProcessor.default");
		
		String coloringProcessId = coloringConfiguration.getCurrentProcessId();
		OptionModel coloringOptionModel = coloringConfiguration.getCurrentOptionModel();
		
		String shadingProcessId = shadingConfiguration.getCurrentProcessId();
		OptionModel shadingOptionModel = shadingConfiguration.getCurrentOptionModel();
		
		//String renderProcessId = renderConfiguration.getCurrentProcessId();
		//OptionModel renderOptionModel = renderConfiguration.getCurrentOptionModel();
		
		//modelProcessManifest.addProcessor(defaultLoadProcessor, new GridLoadOptionModel());
		//modelProcessManifest.addProcessor(defaultSurfaceNormalsProcessor, new SurfaceNormalsOptionModel());
		
		modelProcessManifest.addWorker(coloringProcessId, coloringOptionModel);
		modelProcessManifest.addWorker(shadingProcessId, shadingOptionModel);
		
		//modelProcessManifest.addWorker(defaultShapesProcessor, new ShapeOptionModel());
		//modelProcessManifest.addProcessor(renderProcessId, renderOptionModel);
		
		return modelProcessManifest;
	}
	
	
	public void refreshUI()
	{
		coloringConfiguration.refreshUI();
		shadingConfiguration.refreshUI();
		//renderConfiguration.refreshUI();
		globalOptionsPanel.refreshUI();
	}
	
	public void setControlErrorDisplayed(String id, boolean display, String message)
	{
		coloringConfiguration.setControlErrorDisplayed(id, display, message);
		shadingConfiguration.setControlErrorDisplayed(id, display, message);
		//renderConfiguration.setControlErrorDisplayed(id, display, message);
		globalOptionsPanel.setControlErrorDisplayed(id, display, message);
	}
	
	
	

	public boolean validateOptions()
	{
		
		List<PropertyValidationResult> results = new LinkedList<PropertyValidationResult>();
		
		List<OptionModelContainer> containers = new LinkedList<OptionModelContainer>();
		containers.add(globalOptionModelContainer);
		containers.add(coloringConfiguration.getCurrentOptionModelContainer());
		containers.add(shadingConfiguration.getCurrentOptionModelContainer());
		//containers.add(renderConfiguration.getCurrentOptionModelContainer());
		
		
		List<OptionValidationException> validationExceptions = new LinkedList<OptionValidationException>();
		
		for (OptionModelContainer container : containers) {
			if (container != null && container.getOptionModel() != null) {
				log.info("Performing validation on container for " + container.getOptionModel().getClass().getName());
				
				try {
					results.addAll(container.validateOptions(modelContext));
				} catch (ModelContainerException ex) {
					log.error("Model container error during validation: " + ex.getMessage(), ex);
				}
			}
			
		}
		
		boolean refreshOptionUI = false;
		for (PropertyValidationResult result : results) {
			if (result.getRefreshUI()) {
				refreshOptionUI = true;
			}
			
			boolean display = (result.getException() != null);
			String message = (result.getException() != null) ? result.getException().getMessage() : null;
			
			setControlErrorDisplayed(result.getId(), display, message);
			
		}
		
		if (refreshOptionUI) {
			refreshUI();
		}
		
		return validationExceptions.size() == 0;
	}
	
	public void onPropertyChanged(OptionModelChangeEvent e)
	{
		
		if (validateOptions()) {
			firePropertyChangeListeners(e);
		}
	}
	
	public void onProcessSelected(String processId)
	{
		fireProcessSelectedListeners(processId);
	}

	public void addModelConfigurationChangeListener(ModelConfigurationChangeListener listener)
	{
		modelConfigurationChangeListeners.add(listener);
	}
	
	public boolean removeModelConfigurationChangeListener(ModelConfigurationChangeListener listener)
	{
		return modelConfigurationChangeListeners.remove(listener);
	}
	
	protected void fireProcessSelectedListeners(String processId)
	{
		
		for (ModelConfigurationChangeListener listener : modelConfigurationChangeListeners) {
			listener.onProcessSelected(processId);
		}
		
	}
	
	protected void firePropertyChangeListeners(OptionModelChangeEvent e)
	{
		for (ModelConfigurationChangeListener listener : modelConfigurationChangeListeners) {
			listener.onPropertyChanged(e);
		}
	}
	
}
