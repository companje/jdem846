/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import us.wthr.jdem846.DataSetTypes;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemElevationModel;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.MappedOptions;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.OptionChangeListener;
import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.lighting.LightingContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelProcessManifest;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.OptionModelChangeEvent;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.model.exceptions.ContextPrepareException;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.model.exceptions.ProcessContainerException;
import us.wthr.jdem846.model.exceptions.ProcessCreateException;
import us.wthr.jdem846.model.processing.ModelProcessRegistry;
import us.wthr.jdem846.model.processing.ProcessInstance;
import us.wthr.jdem846.project.ProcessMarshall;
import us.wthr.jdem846.project.ProjectFiles;
import us.wthr.jdem846.project.ProjectMarshall;
import us.wthr.jdem846.project.ProjectMarshaller;
import us.wthr.jdem846.project.ProjectModel;
import us.wthr.jdem846.project.ProjectTypeEnum;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.rasterdata.RasterDataProviderFactory;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.scripting.ScriptProxy;
import us.wthr.jdem846.scripting.ScriptProxyFactory;
import us.wthr.jdem846.scripting.ScriptingContext;
import us.wthr.jdem846.shapedata.ShapeDataContext;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.ui.DataSetTree.DatasetSelectionListener;
import us.wthr.jdem846.ui.ModelOptionsPanel.GetAspectRatioHandler;
import us.wthr.jdem846.ui.ModelVisualizationPanel.ProjectionChangeListener;
import us.wthr.jdem846.ui.MonitoredThread.ProgressListener;
import us.wthr.jdem846.ui.OrderingButtonBar.OrderingButtonClickedListener;
import us.wthr.jdem846.ui.ProjectButtonBar.ButtonClickedListener;
import us.wthr.jdem846.ui.base.FileChooser;
import us.wthr.jdem846.ui.base.Menu;
import us.wthr.jdem846.ui.base.MenuItem;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.ScrollPane;
import us.wthr.jdem846.ui.base.SplitPane;
import us.wthr.jdem846.ui.lighting.LightingOptionsPanel;
import us.wthr.jdem846.ui.options.ModelConfigurationChangeListener;
import us.wthr.jdem846.ui.options.ModelConfigurationPanel;
import us.wthr.jdem846.ui.panels.EmbeddedTabbedPane;
import us.wthr.jdem846.ui.scripting.ScriptEditorPanel;

@SuppressWarnings("serial")
public class DemProjectPane extends JdemPanel implements Savable
{
	private static Log log = Logging.getLog(DemProjectPane.class);
	
	private DemProjectPane instance;
	
	private DataSetTree datasetTree;
	private DataSetOptionsPanel datasetOptionsPanel;
	private OrderingButtonBar orderingButtonBar;
	private ModelConfigurationPanel modelConfigurationPanel;

	private DataOverviewPanel regionOverviewPanel;
	private DataOverviewPanel layerOverviewPanel;
	private ModelVisualizationPanel visualizationPanel;
	private ScriptEditorPanel scriptPane;
	private RenderPane renderPane;
	private SplitPane configSplit;
	
	private ProjectButtonBar projectButtonBar;
	private Menu projectMenu;

	private ModelContext modelContext;
	
	
	private ModelProcessManifest modelProcessManifest;

	
	private RasterDataContext rasterDataContext;
	private ShapeDataContext shapeDataContext;
	private ImageDataContext imageDataContext;
	private ScriptingContext scriptingContext;

	private List<CreateModelListener> createModelListeners = new LinkedList<CreateModelListener>();
	
	private String projectLoadedFrom = null;
	private boolean ignoreValueChanges = false;
	
	private int lastRasterDataCount = 0;
	private int lastShapeDataCount = 0;
	private int lastImageDataCount = 0;
	
	public DemProjectPane()
	{
		this(null);
	}
	
	public DemProjectPane(ProjectMarshall projectMarshall)
	{
		initialize(projectMarshall);
	}

	
	protected void initialize(ProjectMarshall projectMarshall)
	{
		this.instance = this;
		
		rasterDataContext = new RasterDataContext();

		List<OptionModel> defaultOptionModelList = null;
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
					
			projectLoadedFrom = projectMarshall.getLoadedFrom();
		}
		
		
		
		// Create Components
		datasetTree = new DataSetTree(modelContext);
		datasetOptionsPanel = new DataSetOptionsPanel();
		orderingButtonBar = new OrderingButtonBar();
		
		modelConfigurationPanel = new ModelConfigurationPanel(modelContext, modelProcessManifest, defaultOptionModelList);

		regionOverviewPanel = new DataOverviewPanel();
		layerOverviewPanel = new DataOverviewPanel();
		

		visualizationPanel = new ModelVisualizationPanel(modelContext);
		try {
			modelProcessManifest = modelConfigurationPanel.getModelProcessManifest();
		} catch (Exception ex) {
			log.error("Error creating initial model process manifest: " + ex.getMessage(), ex);
		}
		
		
		modelContext.setModelProcessManifest(modelProcessManifest);
		
		
		scriptPane = new ScriptEditorPanel();
		
		renderPane = new RenderPane(true);

		projectButtonBar = new ProjectButtonBar(this);
		MainButtonBar.addToolBar(projectButtonBar);
		
		projectMenu = new ComponentMenu(this, I18N.get("us.wthr.jdem846.ui.projectPane.menu.project"), KeyEvent.VK_P);
		MainMenuBar.insertMenu(projectMenu);

		
		projectMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.projectPane.menu.project.add"), JDem846Properties.getProperty("us.wthr.jdem846.ui.project.addData"), KeyEvent.VK_A, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				openInputData();
			}
		}));
		
		projectMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.projectPane.menu.project.remove"), JDem846Properties.getProperty("us.wthr.jdem846.ui.project.removeData"), KeyEvent.VK_R, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int selectedType = datasetTree.getSelectedDatasetType();
				int selectedIndex = datasetTree.getSelectedDatasetIndex();
				removeInputData(selectedType, selectedIndex);
			}
		}));

		projectMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.projectPane.menu.project.create"), JDem846Properties.getProperty("us.wthr.jdem846.ui.project.createModel"), KeyEvent.VK_C, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onCreateModel();
			}
		}));
		
		projectMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.projectPane.menu.project.export"), JDem846Properties.getProperty("us.wthr.jdem846.ui.project.exportData"), KeyEvent.VK_E, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onExportData();
			}
		}));
		
		
		projectButtonBar.addButtonClickedListener(new ButtonClickedListener() {
			public void onAddClicked() {
				openInputData();
			}
			public void onCreateClicked() {
				onCreateModel();
			}
			public void onRemoveClicked() {
				int selectedType = datasetTree.getSelectedDatasetType();
				int selectedIndex = datasetTree.getSelectedDatasetIndex();
				removeInputData(selectedType, selectedIndex);
			}
			public void onExportClicked() {
				onExportData();
			}
		});
		
		orderingButtonBar.addOrderingButtonClickedListener(new OrderingButtonClickedListener() {
			public void onMoveBottom()
			{
				int type = datasetTree.getSelectedDatasetType();
				int index = datasetTree.getSelectedDatasetIndex();
				moveDataSetToPosition(type, index, Integer.MAX_VALUE);
			}
			public void onMoveDown()
			{
				int type = datasetTree.getSelectedDatasetType();
				int index = datasetTree.getSelectedDatasetIndex();
				moveDataSetToPosition(type, index, index+1);
			}
			public void onMoveTop()
			{
				int type = datasetTree.getSelectedDatasetType();
				int index = datasetTree.getSelectedDatasetIndex();
				moveDataSetToPosition(type, index, 0);
			}
			public void onMoveUp()
			{
				int type = datasetTree.getSelectedDatasetType();
				int index = datasetTree.getSelectedDatasetIndex();
				moveDataSetToPosition(type, index, index-1);
			}
		});
		
		
		datasetOptionsPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onDataModelChanged(false, false, false, true);
			}
		});
		datasetOptionsPanel.addModelPreviewUpdateListener(new ModelPreviewUpdateListener() {
			public void updateModelPreview(boolean updateRasterLayer, boolean updateShapeLayer, boolean updateImageLayer)
			{
				log.info("Dataset preview update requested.");
				onDataModelChanged(updateRasterLayer, updateShapeLayer, updateImageLayer, true);
			}
		});
		
		datasetTree.addDatasetSelectionListener(new DatasetSelectionListener() {
			public void onDatasetSelected(Object dataObject, int type, int index)
			{
				datasetOptionsPanel.clear();
				if (type != DataSetTypes.UNSUPPORTED) {
					switch(type) {
					case DataSetTypes.ELEVATION:
						datasetOptionsPanel.setElevationDataSet(rasterDataContext.getRasterDataList().get(index));
						break;
					case DataSetTypes.SHAPE_POLYGON:
					case DataSetTypes.SHAPE_POLYLINE:
						datasetOptionsPanel.setShapeDataSet(shapeDataContext.getShapeFiles().get(index));
						break;
					case DataSetTypes.IMAGERY:
						datasetOptionsPanel.setSimpleGeoImage(imageDataContext.getImageList().get(index));
						break;
					}
					
					projectButtonBar.setButtonEnabled(ProjectButtonBar.BTN_REMOVE, true);
				} else {
					projectButtonBar.setButtonEnabled(ProjectButtonBar.BTN_REMOVE, false);
				}
				onDataSetSelected(type, index);
				revalidate();
			}
		});
		
		
		
		// Add change listeners
		
		modelConfigurationPanel.addModelConfigurationChangeListener(new ModelConfigurationChangeListener() {
			public void onProcessSelected(String processId)
			{
				log.info("** New Process Selected: " + processId);
				try {
					modelProcessManifest = modelConfigurationPanel.getModelProcessManifest();
					modelContext.setModelProcessManifest(modelProcessManifest);
				} catch (Exception ex) {
					log.error("Error fetching model process manifest from configuration panel: " + ex.getMessage(), ex);
				}
				
				// TODO: On configuration changed
				onConfigurationChanged();
			}
			public void onPropertyChanged(OptionModelChangeEvent e)
			{
				log.info("** Property change for " + e.getPropertyName() + " from " + e.getOldValue() + " to " + e.getNewValue());
				try {
					modelProcessManifest = modelConfigurationPanel.getModelProcessManifest();
					modelContext.setModelProcessManifest(modelProcessManifest);
				} catch (Exception ex) {
					log.error("Error fetching model process manifest from configuration panel: " + ex.getMessage(), ex);
				}
				
				// TODO: On configuration changed
				onConfigurationChanged();
			}			
		});
		

		
		visualizationPanel.addProjectionChangeListener(new ProjectionChangeListener() {
			public void onProjectionChanged(double rotateX, double rotateY, double rotateZ, double shiftX, double shiftY, double shiftZ, double zoom)
			{
				
				log.info("Projection Changed to Rotate: " + rotateX + "/" + rotateY + "/" + rotateZ + ", Shift: "  + shiftX + "/" + shiftY + "/" + shiftZ + ", Zoom: " + zoom);
				
				ViewPerspective viewAngle = new ViewPerspective(rotateX, 
																rotateY, 
																rotateZ,
																shiftX,
																shiftY,
																shiftZ,
																zoom);
				
				
				try {
					modelConfigurationPanel.getGlobalOptionModelContainer().setPropertyValueById("us.wthr.jdem846.model.GlobalOptionModel.viewAngle", viewAngle);
				} catch (ModelContainerException ex) {
					// TODO: Display an error dialog
					log.warn("Error setting view angle: " + ex.getMessage(), ex);
				}
				
			}
		});
		
		
		@SuppressWarnings("unused")
		ChangeListener basicChangeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				onConfigurationChanged();
			}
		};
		

		
		Panel dataPanel = new Panel();
		dataPanel.setLayout(new BorderLayout());
		dataPanel.add(orderingButtonBar, BorderLayout.NORTH);
		dataPanel.add(datasetTree, BorderLayout.CENTER);
		dataPanel.add(datasetOptionsPanel, BorderLayout.SOUTH);

		EmbeddedTabbedPane leftLowerTabPane = new EmbeddedTabbedPane();
		leftLowerTabPane.add(I18N.get("us.wthr.jdem846.ui.projectPane.tab.modelOverview"), regionOverviewPanel);
		leftLowerTabPane.add(I18N.get("us.wthr.jdem846.ui.projectPane.tab.layerOverview"), layerOverviewPanel);
		
		final SplitPane leftSplit = new SplitPane(SplitPane.VERTICAL_SPLIT);
		leftSplit.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		leftSplit.add(dataPanel);
		leftSplit.add(leftLowerTabPane);
		leftSplit.setResizeWeight(0);
		leftSplit.setDividerLocation(JDem846Properties.getIntProperty("us.wthr.jdem846.state.ui.demProjectPane.leftVerticalSplitPosition"));
		addLeft(leftSplit, false);
		
		
		
		ComponentAdapter dividerChangeListener = new ComponentAdapter() {
			public void componentResized(ComponentEvent arg0) {
				int location = leftSplit.getDividerLocation();
				JDem846Properties.setProperty("us.wthr.jdem846.state.ui.demProjectPane.leftVerticalSplitPosition", ""+location);
				
				int configSplitLocation = configSplit.getDividerLocation();
				JDem846Properties.setProperty("us.wthr.jdem846.state.ui.demProjectPane.rightHorizontalSplitPosition", ""+configSplitLocation);
				
				int leftWidth = getLeftWidth();
				
				JDem846Properties.setProperty("us.wthr.jdem846.state.ui.demProjectPane.leftHorizontalSplitPosition", ""+leftWidth);
			}
		};
		
		dataPanel.addComponentListener(dividerChangeListener);
		leftLowerTabPane.addComponentListener(dividerChangeListener);
		
		this.setRightVisible(false);
		modelConfigurationPanel.addComponentListener(dividerChangeListener);
		
		leftSplit.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent e) {
				log.info("Property: " + e.getPropertyName());
			}
			
		});
		
		
		configSplit = new SplitPane(SplitPane.HORIZONTAL_SPLIT);
		configSplit.add(visualizationPanel);
		configSplit.add(modelConfigurationPanel);
		configSplit.setBorder(BorderFactory.createEmptyBorder());
		configSplit.setResizeWeight(1.0);
		

		this.addCenter(I18N.get("us.wthr.jdem846.ui.projectPane.tab.preview"), configSplit);
		this.addCenter(I18N.get("us.wthr.jdem846.ui.projectPane.tab.script"), scriptPane);
		this.addCenter("Render", renderPane);
		
		
		
		ComponentListener setLeftRightWidthsAdapter = new ComponentAdapter() {
			public void componentShown(ComponentEvent e)
			{
				setLeftWidth(JDem846Properties.getIntProperty("us.wthr.jdem846.state.ui.demProjectPane.leftHorizontalSplitPosition"));
				configSplit.setDividerLocation(JDem846Properties.getIntProperty("us.wthr.jdem846.state.ui.demProjectPane.rightHorizontalSplitPosition"));
				removeComponentListener(this);
			}
		};
		addComponentListener(setLeftRightWidthsAdapter);

		this.setSouthVisible(false);

		
		initializeScripting(projectMarshall);
		initializeLoadedElevationModels(projectMarshall);

		applyOptionsToUI();
		onDataModelChanged(true, true, true, true);
	}
	
	
	protected void initializeLoadedElevationModels(ProjectMarshall projectMarshall)
	{
		if (projectMarshall == null)
			return;
		
		if (projectMarshall.getElevationModels() == null)
			return;
		
		for (JDemElevationModel jdemElevationModel : projectMarshall.getElevationModels()) {
			renderPane.display(jdemElevationModel);
		}
	}
	
	public List<OptionModel> createDefaultOptionModelList(ProjectMarshall projectMarshall) throws Exception
	{
		List<OptionModel> defaultOptionModelList = new LinkedList<OptionModel>();
		
		GlobalOptionModel globalOptionModel = new GlobalOptionModel();
		
		if (projectMarshall != null && projectMarshall.getGlobalOptions() != null) {
			
			OptionModelContainer globalOptionModelContainer = new OptionModelContainer(globalOptionModel);
			
			for (String option : projectMarshall.getGlobalOptions().keySet()) {
				String value = projectMarshall.getGlobalOptions().get(option);
				if (value != null) {
					globalOptionModelContainer.setPropertyValueById(option, value);
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
	
	public void initializeScripting(ProjectMarshall projectMarshall)
	{
		// If this script isn't null or it's longer than 0 characters, then we
		// can assume that the user has already provided one.
		///if (modelOptions.getUserScript() != null && modelOptions.getUserScript().length() > 0) {
		//	return;
		//}
		
		// Default/Hardcode to Groovy for now...
		
		
		
		if (projectMarshall != null && projectMarshall.getUserScript() != null && projectMarshall.getUserScript().length() > 0) {
			scriptingContext.setScriptLanguage(projectMarshall.getScriptLanguage());
			scriptingContext.setUserScript(projectMarshall.getUserScript());
			
			try {
				scriptingContext.prepare();
			} catch (ContextPrepareException ex) {
				log.error("Error preparing initial scripting context: " + ex.getMessage(), ex);
				// TODO: Display error dialog
			}
			
		} else {
		
			String scriptTemplatePath = null;
			
			if (scriptingContext.getScriptLanguage() == ScriptLanguageEnum.GROOVY) {
				scriptTemplatePath = JDem846Properties.getProperty("us.wthr.jdem846.userScript.groovy.template");
			} else if (scriptingContext.getScriptLanguage() == ScriptLanguageEnum.JYTHON) {
				scriptTemplatePath = JDem846Properties.getProperty("us.wthr.jdem846.userScript.jython.template");
			} else if (scriptingContext.getScriptLanguage() == ScriptLanguageEnum.SCALA) {
				scriptTemplatePath = JDem846Properties.getProperty("us.wthr.jdem846.userScript.scala.template");
			} else {
				// fail silently for now
				// TODO: Don't fail silently
				log.warn("Script language '" + scriptingContext.getScriptLanguage() + "' is null or invalid; Cannot load template");
				return;
			}
	
			String scriptTemplate = null;
			try {
				scriptTemplate = loadTemplateFile(scriptTemplatePath);
			} catch (Exception ex) {
				log.error("Error when loading script template file from '" + scriptTemplatePath + "': " + ex.getMessage(), ex);
				JOptionPane.showMessageDialog(getRootPane(),
						I18N.get("us.wthr.jdem846.ui.projectPane.scripting.loadTemplateFailure.message"),
					    I18N.get("us.wthr.jdem846.ui.projectPane.scripting.loadTemplateFailure.title"),
					    JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (scriptTemplate != null) {
				scriptingContext.setUserScript(scriptTemplate);
			}
		
		}
		
		// TODO: Reapply scripting
		scriptPane.setScriptLanguage(scriptingContext.getScriptLanguage());
		if (scriptingContext.getUserScript() != null && scriptingContext.getUserScript().length() > 0) {
			scriptPane.setScriptContent(scriptingContext.getUserScript());
		}
	}
	
	
	
	
	
	protected String loadTemplateFile(String path) throws IOException
	{
		if (path == null) {
			log.warn("Cannot load template file: path is null");
			return null;
		}
		
		log.info("Loading script template file from path '" + path + "'");
		StringBuffer templateBuffer = new StringBuffer();

		
		BufferedInputStream in = new BufferedInputStream(JDemResourceLoader.getAsInputStream(path));
		
		int length = 0;
		byte[] buffer = new byte[1024];
		
		while((length = in.read(buffer)) > 0) {
			templateBuffer.append(new String(buffer, 0, length));
		}
		
		return templateBuffer.toString();
	}
	
	
	
	public void dispose() throws ComponentException
	{
		log.info("Closing project pane.");
		
		MainMenuBar.removeMenu(projectMenu);
		MainButtonBar.removeToolBar(projectButtonBar);
		
		try {
			rasterDataContext.dispose();
		} catch (DataSourceException ex) {
			log.error("Failed to dispose of data proxy: " + ex.getMessage(), ex);
			ex.printStackTrace();
		}
		
		super.dispose();
		

	}
	
	
	public void onExportData()
	{
		log.warn("Export not yet implemented");
		
		DataExportDialog export = new DataExportDialog(modelContext);
		export.setModal(true);
		export.setVisible(true);
	}
	
	protected void openInputData()
	{
		final FileChooser chooser = new FileChooser();
		FileFilter acceptAll = chooser.getAcceptAllFileFilter();
		
		// TODO: Restore File Filter functionality
		//for (ElevationDataLoaderInstance instance : ElevationDataLoaderRegistry.getInstances()) {
		//	FileNameExtensionFilter filter = new FileNameExtensionFilter(instance.getName(), instance.getExtension());
			//chooser.addChoosableFileFilter(filter);
		//}
		chooser.setFileFilter(acceptAll);
		chooser.setMultiSelectionEnabled(true);
	    int returnVal = chooser.showOpenDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	

	    	MonitoredThread loader = new MonitoredThread() {
				public void run()
				{
					this.fireOnStartListeners();
					File[] selectedFiles = chooser.getSelectedFiles();
			    	for(int i = 0; i < selectedFiles.length; i++) {
			    		File file = selectedFiles[i];
			    		if (file.exists()) {
			    			
			    			String extension = file.getName().substring(file.getName().lastIndexOf(".")+1);
			    			
			    			if (extension != null && extension.equalsIgnoreCase("shp")) {
			    				addShapeDataset(file.getAbsolutePath(), null, false);
			    			} else if (extension != null && (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png"))) {
			    				addImageryData(file.getAbsolutePath(), false);
			    			} else {
			    				addElevationDataset(file.getAbsolutePath(), false);
			    			}
			    			

			    			
			    			
			    			double progress = (((double)i / (double)selectedFiles.length));
			    			this.fireProgressListeners(progress);
			    		}
			    	}
			    	this.fireOnCompleteListeners();
				}
	    	};
	    	loader.addProgressListener(new ProgressListener() {
				public void onProgress(double progress)
				{
					
				}
				public void onStart()
				{
					SharedStatusBar.setStatus(I18N.get("us.wthr.jdem846.ui.projectPane.status.loading"));
				}
				public void onComplete()
				{
			    	onDataModelChanged();
			    	SharedStatusBar.setStatus(I18N.get("us.wthr.jdem846.ui.projectPane.status.done"));
				}
	    	});
	    	loader.start();
	    	
	    }
		
	}
	

	protected void onConfigurationChanged()
	{
		if (ignoreValueChanges)
			return;
		

		applyOptionsToUI();
		updateScriptingContext();
		onDataModelChanged(false, false, false, true);
	}

	
	protected void applyOptionsToUI()
	{
		if (ignoreValueChanges)
			return;
		
		ignoreValueChanges = true;

		// TODO: Reapply scripting
		
		/*
		scriptPane.setScriptLanguage(scriptingContext.getScriptLanguage());
		if (scriptingContext.getUserScript() != null && scriptingContext.getUserScript().length() > 0) {
			scriptPane.setScriptContent(scriptingContext.getUserScript());
		}
		
		*/
		
		ignoreValueChanges = false;
	}
	
	
	protected void updateScriptingContext()
	{

		scriptingContext.setUserScript(scriptPane.getScriptContent());
		scriptingContext.setScriptLanguage(scriptPane.getScriptLanguage());
		
		if (this.modelProcessManifest.getGlobalOptionModel().getUseScripting()) {
			try {
				scriptingContext.prepare();
			} catch (Exception ex) {
				log.warn("Error compiling script: " + ex.getMessage(), ex);
				JOptionPane.showMessageDialog(this.getRootPane(),
					    I18N.get("us.wthr.jdem846.ui.projectPane.onCreate.compileError.message") + ": " + ex.getMessage(),
					    I18N.get("us.wthr.jdem846.ui.projectPane.onCreate.compileError.title"),
					    JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	
	protected void removeInputData(int type, int index)
	{
		try {
			if (type == DataSetTypes.ELEVATION) {
				removeElevationData(index);
			} else if (type == DataSetTypes.SHAPE_POLYGON ||
						type == DataSetTypes.SHAPE_POLYLINE) {
				removeShapeData(index);
			} else if (type == DataSetTypes.IMAGERY) {
				removeImageData(index);
			}
		} catch (Exception ex) {
			log.error("Failed to remove input data: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(getRootPane(),
					I18N.get("us.wthr.jdem846.ui.projectPane.remove.removeError.message"),
				    I18N.get("us.wthr.jdem846.ui.projectPane.remove.removeError.title"),
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	protected void removeElevationData(int index) throws DataSourceException
	{
		log.info("Removing elevation data #" + index);
		if (index < 0) {
			JOptionPane.showMessageDialog(getRootPane(),
					I18N.get("us.wthr.jdem846.ui.projectPane.remove.nothingSelected.message"),
				    I18N.get("us.wthr.jdem846.ui.projectPane.remove.nothingSelected.title"),
				    JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		rasterDataContext.removeRasterData(index);
		onDataModelChanged();
	}
	
	protected void removeShapeData(int index) throws DataSourceException 
	{
		log.info("Removing shape data #" + index);
		if (index < 0) {
			JOptionPane.showMessageDialog(getRootPane(),
					I18N.get("us.wthr.jdem846.ui.projectPane.remove.nothingSelected.message"),
					I18N.get("us.wthr.jdem846.ui.projectPane.remove.nothingSelected.title"),
				    JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		shapeDataContext.removeShapeFile(index);
		onDataModelChanged();
	}
	
	protected void removeImageData(int index) throws DataSourceException 
	{
		log.info("Removing image data #" + index);
		if (index < 0) {
			JOptionPane.showMessageDialog(getRootPane(),
					I18N.get("us.wthr.jdem846.ui.projectPane.remove.nothingSelected.message"),
					I18N.get("us.wthr.jdem846.ui.projectPane.remove.nothingSelected.title"),
				    JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		SimpleGeoImage image = imageDataContext.removeImage(index);
		if (image.isLoaded()) {
			image.unload();
		}
		
		onDataModelChanged();
	}
	
	
	protected void addShapeDataset(String filePath, String shapeDataDefinitionId, boolean triggerModelChanged)
	{
		try {
			shapeDataContext.addShapeFile(filePath, shapeDataDefinitionId);
			if (triggerModelChanged)
				onDataModelChanged();
		} catch (Exception ex) {
			ex.printStackTrace();
			
			JOptionPane.showMessageDialog(getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.add.shapefile.loadFailed.message") + ": " + ex.getMessage(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.add.shapefile.loadFailed.title"),
				    JOptionPane.ERROR_MESSAGE);
		}
		
		
		
	}
	
	protected void addImageryData(String filePath, boolean triggerModelChanged)
	{
		addImageryData(filePath, modelContext.getNorth(), modelContext.getSouth(), modelContext.getEast(), modelContext.getWest(), triggerModelChanged);
	}
	
	protected void addImageryData(String filePath, double north, double south, double east, double west, boolean triggerModelChanged)
	{
		SimpleGeoImage image = null;
		
		try {
			image = new SimpleGeoImage(filePath, north, south, east, west);
			image.load();
		} catch (DataSourceException ex) {
			log.warn("Invalid file format: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(this.getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.add.image.loadFailed.message") + ": " + "", //ex.getExtension(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.add.image.loadFailed.title"),
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		imageDataContext.addImage(image);
		
		if (triggerModelChanged)
			onDataModelChanged();
	}
	
	
	protected void addElevationDataset(final String filePath, final boolean triggerModelChanged)
	{

		RasterData rasterData = null;
		try {
			rasterData = RasterDataProviderFactory.loadRasterData(filePath);
		} catch (DataSourceException ex) {
			log.warn("Invalid file format: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(instance.getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.add.elevation.loadFailed.invalidFormat.message") + ": " + "", //ex.getExtension(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.add.elevation.loadFailed.invalidFormat.title"),
				    JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			rasterDataContext.addRasterData(rasterData);
		} catch (DataSourceException ex) {
			log.error("Failed to add raster data: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(instance.getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.add.shapeData.loadFailed.invalidFormat.message") + ": " + "", //ex.getExtension(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.add.shapeData.loadFailed.invalidFormat.title"),
				    JOptionPane.ERROR_MESSAGE);
		}
		
		if (triggerModelChanged)
			onDataModelChanged();
		

	}
	
	public void onDataModelChanged()
	{
		onDataModelChanged(false, false, false, false);
	}
	
	public void onDataModelChanged(boolean forceRasterUpdate, boolean forceShapeUpdate, boolean forceImageUpdate, boolean optionsChanged)
	{
		
		if (rasterDataContext.getRasterDataListSize() + shapeDataContext.getShapeFiles().size() > 0) {
			regionOverviewPanel.setValuesVisible(true);
		} else {
			regionOverviewPanel.setValuesVisible(false);
		}
		
		projectButtonBar.setButtonEnabled(ProjectButtonBar.BTN_REMOVE, (datasetTree.getSelectedDatasetType() != DataSetTypes.UNSUPPORTED));
		
		
		try {
			rasterDataContext.prepare();
		} catch (ContextPrepareException ex) {
			log.warn("Failed to prepare raster data proxy: " + ex.getMessage(), ex);
		}
		
		
		boolean updateRaster = forceRasterUpdate || lastRasterDataCount != rasterDataContext.getRasterDataListSize();
		boolean updateShape = forceShapeUpdate || lastShapeDataCount != shapeDataContext.getShapeFiles().size();
		boolean updateImage = forceImageUpdate || lastImageDataCount != imageDataContext.getImageListSize();
		
		try {
			boolean estimate = this.modelProcessManifest.getGlobalOptionModel().isEstimateElevationRange();
			modelContext.updateContext(updateRaster, estimate);
		} catch (ModelContextException ex) {
			// TODO: Display error dialog
			log.warn("Exception updating model context: " + ex.getMessage(), ex);
		}

		this.modelConfigurationPanel.validateOptions();
		
		regionOverviewPanel.setRows(modelContext.getModelDimensions().getDataRows());
		regionOverviewPanel.setColumns(modelContext.getModelDimensions().getDataColumns());
		regionOverviewPanel.setNorth(modelContext.getModelDimensions().getNorth());
		regionOverviewPanel.setSouth(modelContext.getModelDimensions().getSouth());
		regionOverviewPanel.setEast(modelContext.getModelDimensions().getEast());
		regionOverviewPanel.setWest(modelContext.getModelDimensions().getWest());
		regionOverviewPanel.setLatitudeResolution(modelContext.getModelDimensions().getLatitudeResolution());
		regionOverviewPanel.setLongitudeResolution(modelContext.getModelDimensions().getLongitudeResolution());
		
		datasetTree.updateTreeNodes();
		onDataSetSelected();
		
		lastRasterDataCount = rasterDataContext.getRasterDataListSize();
		lastShapeDataCount = shapeDataContext.getShapeFiles().size();
		lastImageDataCount = imageDataContext.getImageListSize();
		
		updatePreviewPane(updateRaster, updateShape, updateImage, optionsChanged);
	}
	
	
	protected void updatePreviewPane(boolean updateRaster, boolean updateShape, boolean updateImage, boolean optionsChanged)
	{
		visualizationPanel.update(updateRaster || updateShape || updateImage, optionsChanged);
	}
	
	
	public void onDataSetSelected()
	{
		onDataSetSelected(DataSetTypes.UNSUPPORTED, -1);
	}
	
	public void onDataSetSelected(int type, int index)
	{
		int dsCount = 0;
		
		if (type == DataSetTypes.UNSUPPORTED)
			type = datasetTree.getSelectedDatasetType();
		if (index < 0)
			index = datasetTree.getSelectedDatasetIndex();
		
		if (type == DataSetTypes.ELEVATION)
			dsCount = rasterDataContext.getRasterDataListSize();
		else if (type == DataSetTypes.SHAPE_POLYGON || type == DataSetTypes.SHAPE_POLYLINE)
			dsCount = shapeDataContext.getShapeFiles().size();
		else if (type == DataSetTypes.IMAGERY)
			dsCount = imageDataContext.getImageListSize();
		
		orderingButtonBar.setButtonEnabled(OrderingButtonBar.BTN_MOVE_TOP, (index > 0));
		orderingButtonBar.setButtonEnabled(OrderingButtonBar.BTN_MOVE_UP, (index > 0));
		
		orderingButtonBar.setButtonEnabled(OrderingButtonBar.BTN_MOVE_DOWN, (index < dsCount - 1));
		orderingButtonBar.setButtonEnabled(OrderingButtonBar.BTN_MOVE_BOTTOM, (index < dsCount - 1));
		
		if (index >= 0) {
			updateLayerOverview(type, index);
		} else {
			resetLayerOverview();
		}
	}
	
	public void updateLayerOverview(int type, int index)
	{
		if (type == DataSetTypes.ELEVATION)
			updateRasterLayerOverview(index);
		else if (type == DataSetTypes.SHAPE_POLYGON || type == DataSetTypes.SHAPE_POLYLINE)
			updateShapeLayerOverview(index);
		else if (type == DataSetTypes.IMAGERY)
			updateImageLayerOverview(index);
	}
	
	protected void resetLayerOverview()
	{
		layerOverviewPanel.setNorth(0);
		layerOverviewPanel.setSouth(0);
		layerOverviewPanel.setEast(0);
		layerOverviewPanel.setWest(0);
		layerOverviewPanel.setRows(0);
		layerOverviewPanel.setColumns(0);
		layerOverviewPanel.repaint();
	}
	
	public void updateRasterLayerOverview(int index)
	{
		if (index < 0) {
			return;
		}
		
		RasterData rasterData = rasterDataContext.getRasterDataList().get(index);
		layerOverviewPanel.setNorth(rasterData.getNorth());
		layerOverviewPanel.setSouth(rasterData.getSouth());
		layerOverviewPanel.setEast(rasterData.getEast());
		layerOverviewPanel.setWest(rasterData.getWest());
		layerOverviewPanel.setRows(rasterData.getRows());
		layerOverviewPanel.setColumns(rasterData.getColumns());
		layerOverviewPanel.setLatitudeResolution(rasterData.getLatitudeResolution());
		layerOverviewPanel.setLongitudeResolution(rasterData.getLongitudeResolution());
		layerOverviewPanel.repaint();
	}
	
	public void updateShapeLayerOverview(int index)
	{
		if (index < 0) {
			return;
		}

		layerOverviewPanel.setNorth(0);
		layerOverviewPanel.setSouth(0);
		layerOverviewPanel.setEast(0);
		layerOverviewPanel.setWest(0);
		layerOverviewPanel.setRows(0);
		layerOverviewPanel.setColumns(0);
		layerOverviewPanel.setLatitudeResolution(0);
		layerOverviewPanel.setLongitudeResolution(0);
		layerOverviewPanel.repaint();
	}
	
	public void updateImageLayerOverview(int index)
	{
		if (index < 0) {
			return;
		}
		
		SimpleGeoImage image = imageDataContext.getImageList().get(index);
		layerOverviewPanel.setNorth(image.getNorth());
		layerOverviewPanel.setSouth(image.getSouth());
		layerOverviewPanel.setEast(image.getEast());
		layerOverviewPanel.setWest(image.getWest());
		layerOverviewPanel.setRows(image.getHeight());
		layerOverviewPanel.setColumns(image.getWidth());
		layerOverviewPanel.setLatitudeResolution(image.getLatitudeResolution());
		layerOverviewPanel.setLongitudeResolution(image.getLongitudeResolution());
		layerOverviewPanel.repaint();
	}
	
	public void moveDataSetToPosition(int type, int fromIndex, int toIndex)
	{
		int dsCount = 0;

		if (type == DataSetTypes.UNSUPPORTED)
			return;
		
		if (type == DataSetTypes.ELEVATION)
			dsCount = rasterDataContext.getRasterDataListSize();
		else if (type == DataSetTypes.SHAPE_POLYGON || type == DataSetTypes.SHAPE_POLYLINE)
			dsCount = shapeDataContext.getShapeFiles().size();
		else if (type == DataSetTypes.IMAGERY)
			dsCount = imageDataContext.getImageListSize();
		
		// make sure index is valid (0 is already top, cannot move further)
		if (fromIndex < 0 || fromIndex >= dsCount || toIndex < 0 ) 
			return;
		
		if (toIndex >= dsCount)
			toIndex = dsCount - 1;
		
		try {
			if (type == DataSetTypes.ELEVATION) {
				RasterData rasterData = rasterDataContext.removeRasterData(fromIndex);
				rasterDataContext.getRasterDataList().add(toIndex, rasterData);
	
			} else if (type == DataSetTypes.SHAPE_POLYGON || type == DataSetTypes.SHAPE_POLYLINE) {
				ShapeFileRequest sfr = shapeDataContext.removeShapeFile(fromIndex);
				shapeDataContext.getShapeFiles().add(toIndex, sfr);
			} else if (type == DataSetTypes.IMAGERY) {
				SimpleGeoImage image = imageDataContext.removeImage(fromIndex);
				imageDataContext.getImageList().add(toIndex, image);
			}
		} catch (Exception ex) {
			log.error("Failed to move data positions: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(this.getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.moveDataSetToPosition.moveError.message") + ": " + ex.getMessage(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.moveDataSetToPosition.moveError.title"),
				    JOptionPane.ERROR_MESSAGE);
		}
		onDataModelChanged();
	}
	
	
	
	public void addCreateModelListener(CreateModelListener listener)
	{
		createModelListeners.add(listener);
	}
	
	public void removeCreateModelListener(CreateModelListener listener)
	{
		createModelListeners.remove(listener);
	}
	
	
	public void onCreateModel()
	{

		RasterDataContext rasterDataContext;
		try {
			rasterDataContext = this.rasterDataContext.copy();
		} catch (DataSourceException ex) {
			log.error("Failed to copy raster data: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(this.getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.onCreateModel.copyDataRasterFailure.message") + ": " + ex.getMessage(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.onCreateModel.copyDataRasterFailure.title"),
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		ShapeDataContext shapeDataContext;
		try {
			shapeDataContext = this.shapeDataContext.copy();
		} catch (DataSourceException ex) {
			log.error("Failed to copy shape data: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(this.getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.onCreateModel.copyShapeDataFailure.message") + ": " + ex.getMessage(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.onCreateModel.copyShapeDataFailure.title"),
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		ImageDataContext imageDataContext;
		try {
			imageDataContext = this.imageDataContext.copy();
		} catch (DataSourceException ex) {
			log.error("Failed to copy image data: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(this.getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.onCreateModel.copyImageDataFailure.message") + ": " + ex.getMessage(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.onCreateModel.copyImageDataFailure.title"),
				    JOptionPane.ERROR_MESSAGE);
			return;
		}

		ScriptingContext scriptingContext;
		
		try {
			scriptingContext = this.scriptingContext.copy();
			scriptingContext.setUserScript(scriptPane.getScriptContent());
			scriptingContext.setScriptLanguage(scriptPane.getScriptLanguage());
			scriptingContext.prepare();
		} catch (Exception ex) {
			log.warn("Error compiling script: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(this.getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.onCreate.compileError.message") + ": " + ex.getMessage(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.onCreate.compileError.title"),
				    JOptionPane.ERROR_MESSAGE);
			return;
		}

		ModelProcessManifest modelProcessManifest = null;
		
		try {
			modelProcessManifest = modelConfigurationPanel.getModelProcessManifest();
		} catch (Exception ex) {
			log.error("Error retrieving model process manifest from configuration panel: " + ex.getMessage(), ex);
		}
		//modelProcessManifest.getGlobalOptionModel().setLatitudeSlices(-1);
		//modelProcessManifest.getGlobalOptionModel().setLongitudeSlices(-1);
		modelProcessManifest.getGlobalOptionModel().setGetStandardResolutionElevation(JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.standardResolutionRetrieval"));
		modelProcessManifest.getGlobalOptionModel().setInterpolateData(JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.interpolateToHigherResolution"));
		modelProcessManifest.getGlobalOptionModel().setAverageOverlappedData(JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.averageOverlappedData"));
		modelProcessManifest.getGlobalOptionModel().setPrecacheStrategy(JDem846Properties.getProperty("us.wthr.jdem846.performance.precacheStrategy"));
		modelProcessManifest.getGlobalOptionModel().setTileSize(JDem846Properties.getIntProperty("us.wthr.jdem846.performance.tileSize"));

		//modelProcessManifest.getGlobalOptionModel().getViewAngle().setRotateX(0.0);
		//modelProcessManifest.getGlobalOptionModel().getViewAngle().setRotateY(0.0);
		//modelProcessManifest.getGlobalOptionModel().getViewAngle().setRotateZ(0.0);
		
		ModelContext modelContextCopy = null;
		try {
			modelContextCopy = ModelContext.createInstance(rasterDataContext, shapeDataContext, imageDataContext, modelProcessManifest, scriptingContext);
		} catch (ModelContextException ex) {
			// TODO: Display error dialog
			log.error("Exception updating model context: " + ex.getMessage(), ex);
		}
		
		
		renderPane.render(modelContextCopy);
		this.setComponentVisible(renderPane);
	}
	
	public void fireCreateModelListeners(ModelContext modelContext)
	{

		for (CreateModelListener listener : createModelListeners) {
			listener.onCreateModel(modelContext);
		}
	}

	public void setSavedPath(String savedPath)
	{
		this.projectLoadedFrom = savedPath;
	}
	
	public String getSavedPath()
	{
		return this.projectLoadedFrom;
	}

	@Override
	public void save()
	{
		String saveTo = getSavedPath();
		if (saveTo == null) {
			saveAs();
		} else { 
			saveTo(saveTo);
		}
	}

	@Override
	public void saveAs()
	{
		FileChooser chooser = new FileChooser();

		FileNameExtensionFilter zdemFilter = new FileNameExtensionFilter(I18N.get("us.wthr.jdem846.ui.projectFormat.jdemprj.name"), "jdemprj");

		chooser.addChoosableFileFilter(zdemFilter);
		chooser.setFileFilter(zdemFilter);
		chooser.setMultiSelectionEnabled(false);
		
	    int returnVal =  chooser.showSaveDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	File selectedFile = chooser.getSelectedFile();
	    	
	    	String path = selectedFile.getAbsolutePath();
	    	if (!path.toLowerCase().endsWith(".jdemprj")) {
	    		path = path + ".jdemprj";
	    	}
	    		
	    	saveTo(path);

	    } 
	}
	
	
	protected void saveTo(String saveTo)
	{
		try {

			ProjectMarshall projectMarshall = ProjectMarshaller.marshallProject(modelContext);
			
			List<JDemElevationModel> modelList = this.renderPane.getJdemElevationModels();
			projectMarshall.getElevationModels().addAll(modelList);
			
			
			ProjectFiles.write(projectMarshall, saveTo);
			
			setSavedPath(saveTo);
			
			RecentProjectTracker.addProject(saveTo);
			
			log.info("Project file saved to " + saveTo);
			SharedStatusBar.setStatus("Project file saved to " + saveTo);
			
		} catch (Exception ex) {
			log.warn("Error trying to write project to disk: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.jdemFrame.saveError.writeError.message"),
				    I18N.get("us.wthr.jdem846.ui.jdemFrame.saveError.writeError.title"),
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
}
