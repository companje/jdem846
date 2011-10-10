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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
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
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.color.ColoringInstance;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.InvalidFileFormatException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.DataSource;
import us.wthr.jdem846.input.DataSourceFactory;
import us.wthr.jdem846.input.ElevationDataLoaderInstance;
import us.wthr.jdem846.input.ElevationDataLoaderRegistry;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.project.ProjectModel;
import us.wthr.jdem846.render.EngineInstance;
import us.wthr.jdem846.render.EngineRegistry;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.shapefile.exception.ShapeFileException;
import us.wthr.jdem846.ui.DataSetTree.DatasetSelectionListener;
import us.wthr.jdem846.ui.MonitoredThread.ProgressListener;
import us.wthr.jdem846.ui.OrderingButtonBar.OrderingButtonClickedListener;
import us.wthr.jdem846.ui.ProjectButtonBar.ButtonClickedListener;
import us.wthr.jdem846.ui.base.FileChooser;
import us.wthr.jdem846.ui.base.Menu;
import us.wthr.jdem846.ui.base.MenuItem;
import us.wthr.jdem846.ui.projectionconfig.ProjectionConfigPanel;

@SuppressWarnings("serial")
public class DemProjectPane extends JdemPanel
{
	private static Log log = Logging.getLog(DemProjectPane.class);
	
	
	private DataSetTree datasetTree;
	private DataSetOptionsPanel datasetOptionsPanel;
	private OrderingButtonBar orderingButtonBar;
	private ModelOptionsPanel modelOptionsPanel;
	private ProjectionConfigPanel projectionConfigPanel;
	private GradientConfigPanel gradientConfigPanel;
	private LightPositionConfigPanel lightPositionConfigPanel;
	
	private DataOverviewPanel overviewPanel;
	private ModelPreviewPane previewPane;
	private DataInputLayoutPane layoutPane;
	
	private ProjectButtonBar projectButtonBar;
	private Menu projectMenu;
	private StatusBar statusBar;
	
	private ProjectModel projectModel;
	private DataPackage dataPackage;
	private ModelOptions modelOptions;
	
	private List<CreateModelListener> createModelListeners = new LinkedList<CreateModelListener>();
	
	private String projectLoadedFrom = null;
	private boolean ignoreValueChanges = false;
	
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
		dataPackage = new DataPackage(null);
		modelOptions = new ModelOptions();
		this.projectModel = projectModel;
		
		// Apply model options
		if (projectModel != null) {
			modelOptions.syncFromProjectModel(projectModel);
					
			for (String filePath : projectModel.getInputFiles()) {
				addElevationDataset(filePath, false);
			}
					
			for (ShapeFileRequest shapeFile : projectModel.getShapeFiles()) {
				addShapeDataset(shapeFile.getPath(), shapeFile.getShapeDataDefinitionId(), false);
			}

					
			projectLoadedFrom = projectModel.getLoadedFrom();
		}
		
		
		
		// Create Components
		datasetTree = new DataSetTree(dataPackage);
		datasetOptionsPanel = new DataSetOptionsPanel();
		orderingButtonBar = new OrderingButtonBar();
		
		modelOptionsPanel = new ModelOptionsPanel();
		modelOptionsPanel.setModelOptions(modelOptions);
		
		gradientConfigPanel = new GradientConfigPanel();
		projectionConfigPanel = new ProjectionConfigPanel();

		lightPositionConfigPanel = new LightPositionConfigPanel();
		lightPositionConfigPanel.setPreferredSize(new Dimension(200, 200));
		lightPositionConfigPanel.setSize(new Dimension(200, 200));
		
		overviewPanel = new DataOverviewPanel();
		
		layoutPane = new DataInputLayoutPane(dataPackage, modelOptions);
		previewPane = new ModelPreviewPane(dataPackage, modelOptions);
		
		statusBar = new StatusBar();
		statusBar.setProgressVisible(false);
		
		projectButtonBar = new ProjectButtonBar(this);
		MainButtonBar.addToolBar(projectButtonBar);
		
		projectMenu = new ComponentMenu(this, I18N.get("us.wthr.jdem846.ui.projectPane.menu.project"), KeyEvent.VK_P);
		MainMenuBar.insertMenu(projectMenu);
		
		// Add listeners
		projectMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.projectPane.menu.project.add"), JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/list-add.png", KeyEvent.VK_A, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				openInputData();
			}
		}));
		
		projectMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.projectPane.menu.project.remove"), JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/list-remove.png", KeyEvent.VK_R, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int selectedType = datasetTree.getSelectedDatasetType();
				int selectedIndex = datasetTree.getSelectedDatasetIndex();
				removeInputData(selectedType, selectedIndex);
			}
		}));

		projectMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.projectPane.menu.project.create"), JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/stock_update-data.png", KeyEvent.VK_C, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				fireCreateModelListeners();
			}
		}));
		
		projectMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.projectPane.menu.project.export"), JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/data_export.png", KeyEvent.VK_E, new ActionListener() {
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
				fireCreateModelListeners();
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
				onDataModelChanged();
			}
		});
		datasetTree.addDatasetSelectionListener(new DatasetSelectionListener() {
			public void onDatasetSelected(Object dataObject, int type, int index)
			{
				datasetOptionsPanel.clear();
				if (type != DataSetTypes.UNSUPPORTED) {
					switch(type) {
					case DataSetTypes.ELEVATION:
						datasetOptionsPanel.setElevationDataSet(dataPackage.getDataSources().get(index));
						break;
					case DataSetTypes.SHAPE_POLYGON:
					case DataSetTypes.SHAPE_POLYLINE:
						datasetOptionsPanel.setShapeDataSet(dataPackage.getShapeFiles().get(index));
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
			public void onOptionsChanged(ModelOptions options)
			{
				onConfigurationChanged(options);
			}
		});
		
		ChangeListener basicChangeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				onConfigurationChanged();
			}
		};
		
		projectionConfigPanel.addChangeListener(basicChangeListener);
		gradientConfigPanel.addChangeListener(basicChangeListener);
		
		
		lightPositionConfigPanel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				onConfigurationChanged();
			}
		});
		
		
		
		this.addLeft(orderingButtonBar, false);
		this.addLeft(datasetTree, false);
		this.addLeft(datasetOptionsPanel, false);
		this.addLeft(modelOptionsPanel, false);
		
		this.addCenter(I18N.get("us.wthr.jdem846.ui.projectPane.tab.layout"), layoutPane);
		this.addCenter(I18N.get("us.wthr.jdem846.ui.projectPane.tab.preview"), previewPane);

		this.addRight(gradientConfigPanel, false);
		this.addRight(lightPositionConfigPanel, false);
		this.addRight(projectionConfigPanel, false);
		this.addRight(overviewPanel, false);
		
		this.setSouth(statusBar);
		
		
		onConfigurationChanged();
		onDataModelChanged();
	}
	
	
	
	
	
	
	public void dispose() throws ComponentException
	{
		log.info("Closing project pane.");
		
		MainMenuBar.removeMenu(projectMenu);
		MainButtonBar.removeToolBar(projectButtonBar);
		
		try {
			dataPackage.dispose();
		} catch (DataSourceException ex) {
			log.error("Failed to dispose of data package: " + ex.getMessage(), ex);
			ex.printStackTrace();
		}
		
		super.dispose();
		

	}
	
	public ProjectModel getProjectModel()
	{
		ModelOptions modelOptions = modelOptionsPanel.getModelOptions();
		applyOptionsToModel(modelOptions);
		
		ProjectModel projectModel = new ProjectModel();
		
		projectModel.setLoadedFrom(projectLoadedFrom);
		modelOptions.syncToProjectModel(projectModel);

		
		for (DataSource dataSource : dataPackage.getDataSources()) {
			projectModel.getInputFiles().add(dataSource.getFilePath());
		}
		
		for (ShapeFileRequest shapeFile : dataPackage.getShapeFiles()) {
			projectModel.getShapeFiles().add(shapeFile);
		}
		
		return projectModel;
	}
	
	public void onExportData()
	{
		log.warn("Export not yet implemented");
		
		DataExportDialog export = new DataExportDialog(this.dataPackage);
		export.setModal(true);
		export.setVisible(true);
	}
	
	protected void openInputData()
	{
		final FileChooser chooser = new FileChooser();
		FileFilter acceptAll = chooser.getAcceptAllFileFilter();
		for (ElevationDataLoaderInstance instance : ElevationDataLoaderRegistry.getInstances()) {
			FileNameExtensionFilter filter = new FileNameExtensionFilter(instance.getName(), instance.getExtension());
			//chooser.setFileFilter(filter);
			chooser.addChoosableFileFilter(filter);
		}
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
			    			
			    			if (file.getName().substring(file.getName().lastIndexOf(".")+1).equalsIgnoreCase("shp")) {
			    				addShapeDataset(file.getAbsolutePath(), null, false);
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
					statusBar.setProgress((int)(progress*100));

				}
				public void onStart()
				{
					statusBar.setProgressVisible(true);
			    	statusBar.setProgress(0);
			    	statusBar.setStatus(I18N.get("us.wthr.jdem846.ui.projectPane.status.loading"));
				}
				public void onComplete()
				{
					statusBar.setProgress(100);
			    	onDataModelChanged();
			    	statusBar.setStatus(I18N.get("us.wthr.jdem846.ui.projectPane.status.done"));
			    	statusBar.setProgressVisible(false);
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
		
		applyOptionsToUI();
		applyEngineSelectionConfiguration();
	}

	
	protected void applyOptionsToUI()
	{
		if (ignoreValueChanges)
			return;
		
		ignoreValueChanges = true;
		
		gradientConfigPanel.setGradientIdentifier(modelOptions.getColoringType());
		gradientConfigPanel.setConfigString(modelOptions.getGradientLevels());
		lightPositionConfigPanel.setSolarAzimuth(modelOptions.getLightingAzimuth());
		lightPositionConfigPanel.setSolarElevation(modelOptions.getLightingElevation());

		projectionConfigPanel.setRotation(modelOptions.getProjection().getRotateX(),
								modelOptions.getProjection().getRotateY(),
								modelOptions.getProjection().getRotateZ());
		
		
		ignoreValueChanges = false;
	}
	
	
	protected void applyEngineSelectionConfiguration()
	{
		
		String engineSelection = modelOptions.getEngine();
		String coloringSelection = modelOptions.getColoringType();
		
		EngineInstance engineInstance = EngineRegistry.getInstance(engineSelection);
		ColoringInstance coloringInstance = ColoringRegistry.getInstance(coloringSelection);
		
		gradientConfigPanel.setVisible(coloringInstance.allowGradientConfig());
		projectionConfigPanel.setVisible(engineInstance.usesProjection());
		lightPositionConfigPanel.setVisible(engineInstance.usesLightDirection());
				
		if (engineInstance.usesLightDirection()) {
				lightPositionConfigPanel.updatePreview(true);
		}	
		
		
		
	}
	
	protected void applyOptionsToModel(ModelOptions modelOptions)
	{
		if (modelOptions == null) {
			modelOptions = this.modelOptions;
		}
		
		modelOptions.setGradientLevels(gradientConfigPanel.getConfigString());

		modelOptions.setLightingAzimuth(lightPositionConfigPanel.getSolarAzimuth());
		modelOptions.setLightingElevation(lightPositionConfigPanel.getSolarElevation());

		modelOptions.getProjection().setRotateX(projectionConfigPanel.getRotateX());
		modelOptions.getProjection().setRotateY(projectionConfigPanel.getRotateY());
		modelOptions.getProjection().setRotateZ(projectionConfigPanel.getRotateZ());
	}
	
	
	protected void removeInputData(int type, int index)
	{
		if (type == DataSetTypes.ELEVATION) {
			removeElevationData(index);
		} else if (type == DataSetTypes.SHAPE_POLYGON ||
					type == DataSetTypes.SHAPE_POLYLINE) {
			removeShapeData(index);
		}
	}
	
	protected void removeElevationData(int index)
	{
		log.info("Removing elevation data #" + index);
		if (index < 0) {
			JOptionPane.showMessageDialog(getRootPane(),
					I18N.get("us.wthr.jdem846.ui.projectPane.remove.nothingSelected.message"),
				    I18N.get("us.wthr.jdem846.ui.projectPane.remove.nothingSelected.title"),
				    JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		dataPackage.removeDataSource(index);
		onDataModelChanged();
	}
	
	protected void removeShapeData(int index) 
	{
		log.info("Removing shape data #" + index);
		if (index < 0) {
			JOptionPane.showMessageDialog(getRootPane(),
					I18N.get("us.wthr.jdem846.ui.projectPane.remove.nothingSelected.message"),
					I18N.get("us.wthr.jdem846.ui.projectPane.remove.nothingSelected.title"),
				    JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		dataPackage.removeShapeFile(index);
		onDataModelChanged();
	}
	
	
	protected void addShapeDataset(String filePath, String shapeDataDefinitionId, boolean triggerModelChanged)
	{
		try {
			dataPackage.addShapeFile(filePath, shapeDataDefinitionId);
			if (triggerModelChanged)
				onDataModelChanged();
		} catch (ShapeFileException ex) {
			// TODO:
			ex.printStackTrace();
			
			JOptionPane.showMessageDialog(getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.add.shapefile.loadFailed.message") + ": " + ex.getMessage(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.add.shapefile.loadFailed.title"),
				    JOptionPane.ERROR_MESSAGE);
		}
		
		
		
	}
	
	
	protected void addElevationDataset(String filePath, boolean triggerModelChanged)
	{
		
		DataSource dataSource = null;
		
		try {
			dataSource = DataSourceFactory.loadDataSource(filePath);
		} catch (InvalidFileFormatException ex) {
			log.warn("Invalid file format: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(this.getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.add.elevation.loadFailed.invalidFormat.message") + ": " + ex.getExtension(),
				    I18N.get("us.wthr.jdem846.ui.projectPane.add.elevation.loadFailed.invalidFormat.title"),
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		dataSource.calculateDataStats();
		
		dataPackage.addDataSource(dataSource);
		
		
		if (triggerModelChanged)
			onDataModelChanged();
	}
	
	public void onDataModelChanged()
	{
		if (dataPackage.getDataSourceCount() > 0) {
			//projectButtonBar.setButtonEnabled(ProjectButtonBar.BTN_CREATE, true);
			overviewPanel.setValuesVisible(true);
		} else {
			//projectButtonBar.setButtonEnabled(ProjectButtonBar.BTN_CREATE, false);
			overviewPanel.setValuesVisible(false);
		}
		
		projectButtonBar.setButtonEnabled(ProjectButtonBar.BTN_REMOVE, (datasetTree.getSelectedDatasetType() != DataSetTypes.UNSUPPORTED));
		
		/*
		inputList.clearInputData();
		inputList.clearSelection();
		
		for (DataSource dataSource : dataPackage.getDataSources()) {
			inputList.addInputData(dataSource);
		}
		*/
		
		dataPackage.prepare();
		try {
			dataPackage.calculateElevationMinMax(false);
		} catch (DataSourceException ex) {
			log.warn("Failed to calculate elevation min/max: " + ex.getMessage(), ex);
		}
		layoutPane.update();
		previewPane.update();
		//previewPanel.update();
		
		overviewPanel.setRows((int)dataPackage.getRows());
		overviewPanel.setColumns((int)dataPackage.getColumns());
		overviewPanel.setMaxLatitude(dataPackage.getMaxLatitude());
		overviewPanel.setMinLatitude(dataPackage.getMinLatitude());
		overviewPanel.setMaxLongitude(dataPackage.getMaxLongitude());
		overviewPanel.setMinLongitude(dataPackage.getMinLongitude());
		overviewPanel.setMaxElevation(dataPackage.getMaxElevation());
		overviewPanel.setMinElevation(dataPackage.getMinElevation());
		
		datasetTree.updateTreeNodes();
		onDataSetSelected();
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
			dsCount = dataPackage.getDataSources().size();
		else if (type == DataSetTypes.SHAPE_POLYGON || type == DataSetTypes.SHAPE_POLYLINE)
			dsCount = dataPackage.getShapeFiles().size();
		
		
		orderingButtonBar.setButtonEnabled(OrderingButtonBar.BTN_MOVE_TOP, (index > 0));
		orderingButtonBar.setButtonEnabled(OrderingButtonBar.BTN_MOVE_UP, (index > 0));
		
		orderingButtonBar.setButtonEnabled(OrderingButtonBar.BTN_MOVE_DOWN, (index < dsCount - 1));
		orderingButtonBar.setButtonEnabled(OrderingButtonBar.BTN_MOVE_BOTTOM, (index < dsCount - 1));
		
	}
	
	public void moveDataSetToPosition(int type, int fromIndex, int toIndex)
	{
		int dsCount = 0;

		if (type == DataSetTypes.UNSUPPORTED)
			return;
		
		if (type == DataSetTypes.ELEVATION)
			dsCount = dataPackage.getDataSources().size();
		else if (type == DataSetTypes.SHAPE_POLYGON || type == DataSetTypes.SHAPE_POLYLINE)
			dsCount = dataPackage.getShapeFiles().size();
		
		// make sure index is valid (0 is already top, cannot move further)
		if (fromIndex < 0 || fromIndex >= dsCount || toIndex < 0 ) 
			return;
		
		if (toIndex >= dsCount)
			toIndex = dsCount - 1;
		
		if (type == DataSetTypes.ELEVATION) {
			DataSource ds = dataPackage.removeDataSource(fromIndex);
			dataPackage.getDataSources().add(toIndex, ds);
		} else if (type == DataSetTypes.SHAPE_POLYGON || type == DataSetTypes.SHAPE_POLYLINE) {
			ShapeFileRequest sfr = dataPackage.removeShapeFile(fromIndex);
			dataPackage.getShapeFiles().add(toIndex, sfr);
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
	
	public void fireCreateModelListeners()
	{

		for (CreateModelListener listener : createModelListeners) {
			listener.onCreateModel(dataPackage.copy(), modelOptions.copy());
		}
	}

	
	
}
