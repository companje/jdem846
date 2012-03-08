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
import java.awt.Dimension;
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
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.MappedOptions;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.OptionChangeListener;
import us.wthr.jdem846.color.ColoringInstance;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.InvalidFileFormatException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.image.SimpleGeoImage;
//import us.wthr.jdem846.input.DataPackage;
//import us.wthr.jdem846.input.DataSource;
//import us.wthr.jdem846.input.DataSourceFactory;
//import us.wthr.jdem846.input.ElevationDataLoaderInstance;
//import us.wthr.jdem846.input.ElevationDataLoaderRegistry;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.rasterdata.RasterDataProviderFactory;
import us.wthr.jdem846.rasterdata.RasterData;

import us.wthr.jdem846.lighting.LightingContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.project.ProjectFiles;
import us.wthr.jdem846.project.ProjectModel;
import us.wthr.jdem846.project.ProjectTypeEnum;
import us.wthr.jdem846.render.EngineInstance;
import us.wthr.jdem846.render.EngineRegistry;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.scripting.ScriptProxy;
import us.wthr.jdem846.scripting.ScriptProxyFactory;
import us.wthr.jdem846.shapedata.ShapeDataContext;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.shapefile.exception.ShapeFileException;
import us.wthr.jdem846.ui.DataSetTree.DatasetSelectionListener;
import us.wthr.jdem846.ui.ModelOptionsPanel.GetAspectRatioHandler;
import us.wthr.jdem846.ui.ModelVisualizationPanel.ProjectionChangeListener;
import us.wthr.jdem846.ui.MonitoredThread.ProgressListener;
import us.wthr.jdem846.ui.OrderingButtonBar.OrderingButtonClickedListener;
import us.wthr.jdem846.ui.ProjectButtonBar.ButtonClickedListener;
import us.wthr.jdem846.ui.base.FileChooser;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Menu;
import us.wthr.jdem846.ui.base.MenuItem;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.ScrollPane;
import us.wthr.jdem846.ui.base.SplitPane;
import us.wthr.jdem846.ui.lighting.LightingOptionsPanel;
import us.wthr.jdem846.ui.panels.EmbeddedTabbedPane;
import us.wthr.jdem846.ui.projectionconfig.ProjectionConfigPanel;
import us.wthr.jdem846.ui.scripting.ScriptEditorPanel;

@SuppressWarnings("serial")
public class DemProjectPane extends JdemPanel implements Savable
{
	private static Log log = Logging.getLog(DemProjectPane.class);
	
	
	private DataSetTree datasetTree;
	private DataSetOptionsPanel datasetOptionsPanel;
	private OrderingButtonBar orderingButtonBar;
	private ModelOptionsPanel modelOptionsPanel;
	private LightingOptionsPanel lightingOptionsPanel;
	//private ProjectionConfigPanel projectionConfigPanel;
	//private GradientConfigPanel gradientConfigPanel;
	//private LightPositionConfigPanel lightPositionConfigPanel;
	
	private DataOverviewPanel regionOverviewPanel;
	private DataOverviewPanel layerOverviewPanel;
	//private ModelPreviewPane previewPane;
	private ModelVisualizationPanel visualizationPanel;
	//private DataInputLayoutPane layoutPane;
	private ScriptEditorPanel scriptPane;
	
	private ProjectButtonBar projectButtonBar;
	private Menu projectMenu;
	//private StatusBar statusBar;
	
	//private ProjectModel projectModel;
	
	private ModelContext modelContext;
	private ModelOptions modelOptions;
	private RasterDataContext rasterDataContext;
	private ShapeDataContext shapeDataContext;
	private ImageDataContext imageDataContext;
	private LightingContext lightingContext;
	
	private List<CreateModelListener> createModelListeners = new LinkedList<CreateModelListener>();
	
	private String projectLoadedFrom = null;
	private boolean ignoreValueChanges = false;
	
	private int lastRasterDataCount = 0;
	private int lastShapeDataCount = 0;
	
	public DemProjectPane()
	{
		initialize(null);
	}
	
	public DemProjectPane(ProjectModel projectModel)
	{
		initialize(projectModel);
	}

	
	protected void initialize(ProjectModel projectModel)
	{
		//dataPackage = new DataPackage(null);
		rasterDataContext = new RasterDataContext();
		modelOptions = new ModelOptions();
		shapeDataContext = new ShapeDataContext();
		imageDataContext = new ImageDataContext();
		lightingContext = new LightingContext();
		
		
		
		
		modelContext = ModelContext.createInstance(rasterDataContext, shapeDataContext, imageDataContext, lightingContext, modelOptions);
		
		//this.projectModel = projectModel;
		
		// Apply model options
		if (projectModel != null) {
			modelOptions.syncFromProjectModel(projectModel);
			lightingContext.syncFromProjectModel(projectModel);
			
			for (String filePath : projectModel.getInputFiles()) {
				addElevationDataset(filePath, false);
			}
					
			for (ShapeFileRequest shapeFile : projectModel.getShapeFiles()) {
				addShapeDataset(shapeFile.getPath(), shapeFile.getShapeDataDefinitionId(), false);
			}
			
			for (SimpleGeoImage imageRef : projectModel.getImageFiles()) {
				addImageryData(imageRef.getImageFile(), imageRef.getNorth(), imageRef.getSouth(), imageRef.getEast(), imageRef.getWest(), false);
			}
					
			projectLoadedFrom = projectModel.getLoadedFrom();
		}
		
		
		
		// Create Components
		datasetTree = new DataSetTree(modelContext);
		datasetOptionsPanel = new DataSetOptionsPanel();
		orderingButtonBar = new OrderingButtonBar();
		
		modelOptionsPanel = new ModelOptionsPanel();
		modelOptionsPanel.setModelOptions(modelOptions);
		
		lightingOptionsPanel = new LightingOptionsPanel();
		lightingOptionsPanel.setLightingContext(lightingContext);
		
		//gradientConfigPanel = new GradientConfigPanel();
		//projectionConfigPanel = new ProjectionConfigPanel();

		//lightPositionConfigPanel = new LightPositionConfigPanel();
		//lightPositionConfigPanel.setPreferredSize(new Dimension(200, 200));
		//lightPositionConfigPanel.setSize(new Dimension(200, 200));
		
		regionOverviewPanel = new DataOverviewPanel();
		layerOverviewPanel = new DataOverviewPanel();
		
		//layoutPane = new DataInputLayoutPane(modelContext);
		//previewPane = new ModelPreviewPane(modelContext);
		visualizationPanel = new ModelVisualizationPanel(modelContext);
		scriptPane = new ScriptEditorPanel();
		
		//statusBar = new StatusBar();
		//statusBar.setProgressVisible(false);
		
		projectButtonBar = new ProjectButtonBar(this);
		MainButtonBar.addToolBar(projectButtonBar);
		
		projectMenu = new ComponentMenu(this, I18N.get("us.wthr.jdem846.ui.projectPane.menu.project"), KeyEvent.VK_P);
		MainMenuBar.insertMenu(projectMenu);
		
		// Add listeners
		
		modelOptionsPanel.setGetAspectRatioHandler(new GetAspectRatioHandler() {
			public double getAspectRatio()
			{
				return (double)rasterDataContext.getDataColumns() / (double)rasterDataContext.getDataRows();
			}
		});
		
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
				onDataModelChanged(false, false);
			}
		});
		datasetOptionsPanel.addModelPreviewUpdateListener(new ModelPreviewUpdateListener() {
			public void updateModelPreview(boolean updateRasterLayer, boolean updateShapeLayer)
			{
				log.info("Dataset preview update requested.");
				onDataModelChanged(updateRasterLayer, updateShapeLayer);
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
		modelOptionsPanel.addOptionsChangedListener(new OptionsChangedListener() {
			public void onOptionsChanged(MappedOptions options)
			{
				onConfigurationChanged((ModelOptions)options);
			}
		});
		
		lightingOptionsPanel.addOptionsChangedListener(new OptionsChangedListener() {
			public void onOptionsChanged(MappedOptions options) {
				onConfigurationChanged(); // TODO: Do something with the lightingOptions
			}
		});
		
		modelOptions.addOptionChangeListener(new OptionChangeListener() {
			public void onOptionChanged(String key, Object oldValue, Object newValue)
			{
				
			}
		});
		
		
		lightingContext.addOptionChangeListener(new OptionChangeListener() {
			public void onOptionChanged(String key, Object oldValue, Object newValue)
			{
				
			}
		});
		
		visualizationPanel.addProjectionChangeListener(new ProjectionChangeListener() {
			public void onProjectionChanged(double rotateX, double rotateY, double rotateZ, double shiftX, double shiftY, double shiftZ, double zoom)
			{
				
				modelOptionsPanel.getModelOptions(false).getProjection().setRotateX(rotateX);
				modelOptionsPanel.getModelOptions(false).getProjection().setRotateY(rotateY);
				modelOptionsPanel.getModelOptions(false).getProjection().setRotateZ(rotateZ);
				
				modelOptionsPanel.getModelOptions(false).getProjection().setShiftX(shiftX);
				modelOptionsPanel.getModelOptions(false).getProjection().setShiftY(shiftY);
				modelOptionsPanel.getModelOptions(false).getProjection().setShiftZ(shiftZ);
				
				modelOptionsPanel.getModelOptions(false).getProjection().setZoom(zoom);
				
				modelOptionsPanel.applyOptionsToUI();
				applyOptionsToUI();
				//applyEngineSelectionConfiguration();
				
				onDataModelChanged(true, true);
				
			}
		});
		
		
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

		ScrollPane optionsScroll = new ScrollPane(modelOptionsPanel);
		ScrollPane lightingScroll = new ScrollPane(lightingOptionsPanel);
		
		optionsScroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		lightingScroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		EmbeddedTabbedPane leftTabPane = new EmbeddedTabbedPane();
		leftTabPane.add(I18N.get("us.wthr.jdem846.ui.projectPane.tab.data"), dataPanel);
		leftTabPane.add(I18N.get("us.wthr.jdem846.ui.projectPane.tab.setup"), optionsScroll);
		leftTabPane.add(I18N.get("us.wthr.jdem846.ui.projectPane.tab.lighting"), lightingScroll);
		
		EmbeddedTabbedPane leftLowerTabPane = new EmbeddedTabbedPane();
		leftLowerTabPane.add(I18N.get("us.wthr.jdem846.ui.projectPane.tab.modelOverview"), regionOverviewPanel);
		leftLowerTabPane.add(I18N.get("us.wthr.jdem846.ui.projectPane.tab.layerOverview"), layerOverviewPanel);
		
		final SplitPane leftSplit = new SplitPane(SplitPane.VERTICAL_SPLIT);
		leftSplit.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		leftSplit.add(leftTabPane);
		leftSplit.add(leftLowerTabPane);
		leftSplit.setResizeWeight(0);
		//leftSplit.setDividerSize(5);
		leftSplit.setDividerLocation(JDem846Properties.getIntProperty("us.wthr.jdem846.state.ui.demProjectPane.leftVerticalSplitPosition"));
		addLeft(leftSplit, false);
		
		
		ComponentAdapter dividerChangeListener = new ComponentAdapter() {
			public void componentResized(ComponentEvent arg0) {
				int location = leftSplit.getDividerLocation();
				JDem846Properties.setProperty("us.wthr.jdem846.state.ui.demProjectPane.leftVerticalSplitPosition", ""+location);
			}
		};
		leftTabPane.addComponentListener(dividerChangeListener);
		leftLowerTabPane.addComponentListener(dividerChangeListener);
		
		//leftSplit.get
		leftSplit.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent e) {
				log.info("Property: " + e.getPropertyName());
			}
			
		});
		
		//addLeft(leftTabPane, false);
		//addLeft(overviewPanel, false);
		/*
		this.addLeft(orderingButtonBar, false);
		this.addLeft(datasetTree, false);
		this.addLeft(datasetOptionsPanel, false);
		this.addLeft(modelOptionsPanel, false);
		*/
		
		//this.addCenter(I18N.get("us.wthr.jdem846.ui.projectPane.tab.layout"), layoutPane);
		this.addCenter(I18N.get("us.wthr.jdem846.ui.projectPane.tab.preview"), visualizationPanel);
		this.addCenter(I18N.get("us.wthr.jdem846.ui.projectPane.tab.script"), scriptPane);
		
		setLeftWidth(350);
		this.setRightVisible(false);
		this.setSouthVisible(false);
		//this.addRight(gradientConfigPanel, false);
		//this.addRight(lightPositionConfigPanel, false);
		//this.addRight(projectionConfigPanel, false);
		//this.addRight(overviewPanel, false);
		
		//this.setSouth(statusBar);
		
		
		loadDefaultScripting();
		
		//onConfigurationChanged();
		applyOptionsToUI();
		applyEngineSelectionConfiguration();
		onDataModelChanged(true, true);
	}
	
	
	public void loadDefaultScripting()
	{
		// If this script isn't null or it's longer than 0 characters, then we
		// can assume that the user has already provided one.
		if (modelOptions.getUserScript() != null && modelOptions.getUserScript().length() > 0) {
			return;
		}
		
		// Default/Hardcode to Groovy for now...
		
		String scriptTemplatePath = null;
		
		if (modelOptions.getScriptLanguage() == ScriptLanguageEnum.GROOVY) {
			scriptTemplatePath = modelOptions.getOption(ModelOptionNamesEnum.USER_SCRIPT_GROOVY_TEMPLATE);
		} else if (modelOptions.getScriptLanguage() == ScriptLanguageEnum.JYTHON) {
			scriptTemplatePath = modelOptions.getOption(ModelOptionNamesEnum.USER_SCRIPT_JYTHON_TEMPLATE);
		} else {
			// fail silently for now
			// TODO: Don't fail silently
			log.warn("Script language '" + modelOptions.getScriptLanguage() + "' is null or invalid; Cannot load template");
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
			modelOptions.setUserScript(scriptTemplate);
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
			//dataPackage.dispose();
		} catch (DataSourceException ex) {
			log.error("Failed to dispose of data proxy: " + ex.getMessage(), ex);
			ex.printStackTrace();
		}
		
		super.dispose();
		

	}
	
	public ProjectModel getProjectModel()
	{
		ModelOptions modelOptions = modelOptionsPanel.getModelOptions();
		applyOptionsToModel(modelOptions);
		
		ProjectModel projectModel = new ProjectModel();
		projectModel.setProjectType(ProjectTypeEnum.STANDARD_PROJECT);
		projectModel.setLoadedFrom(projectLoadedFrom);
		modelOptions.syncToProjectModel(projectModel);
		lightingContext.syncToProjectModel(projectModel);
		
		for (RasterData rasterData : rasterDataContext.getRasterDataList()) {
			projectModel.getInputFiles().add(rasterData.getFilePath());
		}

		for (ShapeFileRequest shapeFile : shapeDataContext.getShapeFiles()) {
			projectModel.getShapeFiles().add(shapeFile);
		}

		for (SimpleGeoImage image : imageDataContext.getImageList()) {
			projectModel.getImageFiles().add(image);
		}
		
		return projectModel;
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
					//statusBar.setProgress((int)(progress*100));

				}
				public void onStart()
				{
					//statusBar.setProgressVisible(true);
			    	//statusBar.setProgress(0);
			    	//statusBar.setStatus(I18N.get("us.wthr.jdem846.ui.projectPane.status.loading"));
				}
				public void onComplete()
				{
					//statusBar.setProgress(100);
			    	onDataModelChanged();
			    	//statusBar.setStatus(I18N.get("us.wthr.jdem846.ui.projectPane.status.done"));
			    	//statusBar.setProgressVisible(false);
				}
	    	});
	    	loader.start();
	    	
	    }
		
	}
	
	protected void onConfigurationChanged()
	{
		onConfigurationChanged(null);
	}
	
	protected void onConfigurationChanged(ModelOptions modelOptions)
	{
		if (ignoreValueChanges)
			return;
		
		
		if (modelOptions == null) {
			modelOptions = modelOptionsPanel.getModelOptions();
		}
		this.modelOptions = modelOptions;
		
		//modelOptions.setGradientLevels(gradientConfigPanel.getConfigString());
		////modelOptions.setLightingAzimuth(lightPositionConfigPanel.getSolarAzimuth());
		//modelOptions.setLightingElevation(lightPositionConfigPanel.getSolarElevation());
		//modelOptions.getProjection().setRotateX(projectionConfigPanel.getRotateX());
		//modelOptions.getProjection().setRotateY(projectionConfigPanel.getRotateY());
		//modelOptions.getProjection().setRotateZ(projectionConfigPanel.getRotateZ());
		modelOptions.setUserScript(scriptPane.getScriptContent());
		modelOptions.setScriptLanguage(scriptPane.getScriptLanguage());
		
		applyOptionsToUI();
		applyEngineSelectionConfiguration();
		
		onDataModelChanged(true, true);
	}

	
	protected void applyOptionsToUI()
	{
		if (ignoreValueChanges)
			return;
		
		ignoreValueChanges = true;
		
		//gradientConfigPanel.setGradientIdentifier(modelOptions.getColoringType());
		//gradientConfigPanel.setConfigString(modelOptions.getGradientLevels());
		//lightPositionConfigPanel.setSolarAzimuth(modelOptions.getLightingAzimuth());
		//lightPositionConfigPanel.setSolarElevation(modelOptions.getLightingElevation());

		//projectionConfigPanel.setRotation(modelOptions.getProjection().getRotateX(),
		//						modelOptions.getProjection().getRotateY(),
		//						modelOptions.getProjection().getRotateZ());
		
		scriptPane.setScriptLanguage(modelOptions.getScriptLanguage());
		if (modelOptions.getUserScript() != null && modelOptions.getUserScript().length() > 0) {
			scriptPane.setScriptContent(modelOptions.getUserScript());
		}
		
	
		
		ignoreValueChanges = false;
	}
	
	
	protected void applyEngineSelectionConfiguration()
	{
		
		String engineSelection = modelOptions.getEngine();
		//String coloringSelection = modelOptions.getColoringType();
		
		EngineInstance engineInstance = EngineRegistry.getInstance(engineSelection);
		//ColoringInstance coloringInstance = ColoringRegistry.getInstance(coloringSelection);
		
		//gradientConfigPanel.setVisible(coloringInstance.allowGradientConfig());
		//projectionConfigPanel.setVisible(engineInstance.uses3DProjection());
		//lightPositionConfigPanel.setVisible(engineInstance.usesLightDirection());
				
		//if (engineInstance.usesLightDirection()) {
		//		lightPositionConfigPanel.updatePreview(true);
		//}	
		
		
		
	}
	
	protected void applyOptionsToModel(ModelOptions modelOptions)
	{
		if (modelOptions == null) {
			modelOptions = this.modelOptions;
		}
		
		//modelOptions.setGradientLevels(gradientConfigPanel.getConfigString());
		
		
		
		//modelOptions.setLightingAzimuth(lightPositionConfigPanel.getSolarAzimuth());
		//modelOptions.setLightingElevation(lightPositionConfigPanel.getSolarElevation());

		//modelOptions.getProjection().setRotateX(projectionConfigPanel.getRotateX());
		//modelOptions.getProjection().setRotateY(projectionConfigPanel.getRotateY());
		//modelOptions.getProjection().setRotateZ(projectionConfigPanel.getRotateZ());
		
		
		modelOptions.setUserScript(scriptPane.getScriptContent());
		modelOptions.setScriptLanguage(scriptPane.getScriptLanguage());
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
		//dataPackage.removeDataSource(index);
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
		//} catch (ShapeFileException ex) {
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
	
	
	protected void addElevationDataset(String filePath, boolean triggerModelChanged)
	{
		
		//DataSource dataSource = null;
		RasterData rasterData = null;
		try {
			//dataSource = DataSourceFactory.loadDataSource(filePath);
			rasterData = RasterDataProviderFactory.loadRasterData(filePath);
		} catch (DataSourceException ex) {
			log.warn("Invalid file format: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(this.getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.add.elevation.loadFailed.invalidFormat.message") + ": " + "", //ex.getExtension(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.add.elevation.loadFailed.invalidFormat.title"),
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		//rasterData.calculateDataStats();
		try {
			rasterDataContext.addRasterData(rasterData);
		} catch (DataSourceException ex) {
			log.error("Failed to add raster data: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(this.getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.add.shapeData.loadFailed.invalidFormat.message") + ": " + "", //ex.getExtension(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.add.shapeData.loadFailed.invalidFormat.title"),
				    JOptionPane.ERROR_MESSAGE);
		}
		
		if (triggerModelChanged)
			onDataModelChanged();
	}
	
	public void onDataModelChanged()
	{
		onDataModelChanged(false, false);
	}
	
	public void onDataModelChanged(boolean forceRasterUpdate, boolean forceShapeUpdate)
	{
		
		if (rasterDataContext.getRasterDataListSize() + shapeDataContext.getShapeFiles().size() > 0) {
			//projectButtonBar.setButtonEnabled(ProjectButtonBar.BTN_CREATE, true);
			regionOverviewPanel.setValuesVisible(true);
		} else {
			//projectButtonBar.setButtonEnabled(ProjectButtonBar.BTN_CREATE, false);
			regionOverviewPanel.setValuesVisible(false);
		}
		
		projectButtonBar.setButtonEnabled(ProjectButtonBar.BTN_REMOVE, (datasetTree.getSelectedDatasetType() != DataSetTypes.UNSUPPORTED));
		
		/*
		inputList.clearInputData();
		inputList.clearSelection();
		
		for (DataSource dataSource : dataPackage.getDataSources()) {
			inputList.addInputData(dataSource);
		}
		*/
		
		try {
			rasterDataContext.prepare();
		} catch (DataSourceException ex) {
			log.warn("Failed to prepare raster data proxy: " + ex.getMessage(), ex);
		}
		
		try {
			rasterDataContext.calculateElevationMinMax(false);
		} catch (DataSourceException ex) {
			log.warn("Failed to calculate elevation min/max: " + ex.getMessage(), ex);
		}
		//layoutPane.update();
		
		boolean updateRaster = forceRasterUpdate || lastRasterDataCount != rasterDataContext.getRasterDataListSize();
		boolean updateShape = forceShapeUpdate || lastShapeDataCount != shapeDataContext.getShapeFiles().size();
		
		updatePreviewPane(updateRaster, updateShape);
		//previewPanel.update();
		
		regionOverviewPanel.setRows(rasterDataContext.getDataRows());
		regionOverviewPanel.setColumns(rasterDataContext.getDataColumns());
		regionOverviewPanel.setNorth(rasterDataContext.getNorth());
		regionOverviewPanel.setSouth(rasterDataContext.getSouth());
		regionOverviewPanel.setEast(rasterDataContext.getEast());
		regionOverviewPanel.setWest(rasterDataContext.getWest());
		regionOverviewPanel.setLatitudeResolution(rasterDataContext.getLatitudeResolution());
		regionOverviewPanel.setLongitudeResolution(rasterDataContext.getLongitudeResolution());
		//overviewPanel.setMaxElevation(dataPackage.getMaxElevation());
		//overviewPanel.setMinElevation(dataPackage.getMinElevation());
		
		datasetTree.updateTreeNodes();
		onDataSetSelected();
		
		lastRasterDataCount = rasterDataContext.getRasterDataListSize();
		lastShapeDataCount = shapeDataContext.getShapeFiles().size();
		
	}
	
	
	protected void updatePreviewPane(boolean updateRaster, boolean updateShape)
	{
		//previewPane.update(updateRaster, updateShape);
		visualizationPanel.update(updateRaster, true);
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
		
		//
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
		//layerOverviewPanel.invalidate();
	}
	
	public void updateShapeLayerOverview(int index)
	{
		if (index < 0) {
			return;
		}
		
		ShapeFileRequest shapeData = shapeDataContext.getShapeFiles().get(index);
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

		LightingContext lightingContext = this.lightingContext.copy();
		
		ModelOptions modelOptions = this.modelOptions.copy();
		
		String scriptContent = scriptPane.getScriptContent();
		ScriptProxy scriptProxy = null;
		ModelContext modelContext = null;
		
		try {

			scriptProxy = ScriptProxyFactory.createScriptProxy(ScriptLanguageEnum.GROOVY, scriptContent);

		} catch (Exception ex) {
			log.warn("Error compiling script: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(this.getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.onCreate.compileError.message") + ": " + ex.getMessage(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.onCreate.compileError.title"),
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		modelContext = ModelContext.createInstance(rasterDataContext, shapeDataContext, imageDataContext, lightingContext, modelOptions, scriptProxy);
		fireCreateModelListeners(modelContext);
	}
	
	public void fireCreateModelListeners(ModelContext modelContext)
	{

		for (CreateModelListener listener : createModelListeners) {
			listener.onCreateModel(modelContext);
		}
	}

	public void setSavedPath(String savedPath)
	{
		modelOptions.setWriteTo(savedPath);
	}
	
	public String getSavedPath()
	{
		return modelOptions.getWriteTo();
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

		FileNameExtensionFilter xdemFilter = new FileNameExtensionFilter(I18N.get("us.wthr.jdem846.ui.projectFormat.xdem.name"), "xdem");
		FileNameExtensionFilter zdemFilter = new FileNameExtensionFilter(I18N.get("us.wthr.jdem846.ui.projectFormat.zdem.name"), "zdem");
		
		chooser.addChoosableFileFilter(xdemFilter);
		chooser.addChoosableFileFilter(zdemFilter);
		chooser.setFileFilter(zdemFilter);
		chooser.setMultiSelectionEnabled(false);
		
	    int returnVal =  chooser.showSaveDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	File selectedFile = chooser.getSelectedFile();
	    	
	    	String path = selectedFile.getAbsolutePath();
	    	if (!path.toLowerCase().endsWith(".zdem")) {
	    		path = path + ".zdem";
	    	}
	    		
	    	saveTo(path);

	    } 
	}
	
	
	protected void saveTo(String saveTo)
	{
		try {
			
			ProjectModel projectModel = getProjectModel();
			
			ProjectFiles.write(projectModel, saveTo);
			
			setSavedPath(saveTo);
			log.info("Project file saved to " + saveTo);
			//File file = new File(saveTo);
			//setComponentTabTitle(tabPane.getSelectedIndex(), file.getName());
		} catch (Exception ex) {
			//ex.printStackTrace();
			log.warn("Error trying to write project to disk: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.jdemFrame.saveError.writeError.message"),
				    I18N.get("us.wthr.jdem846.ui.jdemFrame.saveError.writeError.title"),
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
}
