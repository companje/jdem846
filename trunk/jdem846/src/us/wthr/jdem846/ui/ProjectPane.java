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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelOptions;
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
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.shapefile.exception.ShapeFileException;
import us.wthr.jdem846.ui.DataSetTree.DatasetSelectionListener;
import us.wthr.jdem846.ui.ModelOptionsPanel.OptionsChangedListener;
import us.wthr.jdem846.ui.MonitoredThread.ProgressListener;
import us.wthr.jdem846.ui.OrderingButtonBar.OrderingButtonClickedListener;
import us.wthr.jdem846.ui.ProjectButtonBar.ButtonClickedListener;
import us.wthr.jdem846.ui.base.FileChooser;
import us.wthr.jdem846.ui.base.Menu;
import us.wthr.jdem846.ui.base.MenuItem;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.SplitPane;
import us.wthr.jdem846.ui.base.TabPane;

@SuppressWarnings("serial")
public class ProjectPane extends JdemPanel
{
	private static Log log = Logging.getLog(ProjectPane.class);
	
	//private InputGridList inputList;
	private ModelOptionsPanel modelOptionsPanel;
	//private VisualPreviewPanel previewPanel;
	private DataOverviewPanel overviewPanel;
	private ProjectButtonBar projectButtonBar;
	private DataSetOptionsPanel datasetOptionsPanel;
	private OrderingButtonBar orderingButtonBar;
	private DataInputLayoutPane layoutPane;
	private ModelPreviewPane previewPane;
	
	private StatusBar statusBar;
	private DataSetTree datasetTree;
	
	private Panel westPanel;
	private Panel southPanel;
	
	private DataPackage dataPackage;
	private ModelOptions modelOptions;

	private SplitPane splitPane;
	private String projectLoadedFrom = null;
	
	private List<CreateModelListener> createModelListeners = new LinkedList<CreateModelListener>();
	
	private Menu projectMenu;
	
	public ProjectPane(ProjectModel projectModel)
	{
		initialize(projectModel);
	}
	
	
	public ProjectPane()
	{
		initialize(null);
	}
	
	protected void initialize(ProjectModel projectModel)
	{
		// Create Data
		dataPackage = new DataPackage(null);
		modelOptions = new ModelOptions();
		
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
		
		//dataPackage.addShapeFile(new ShapeFileRequest("C:/srv/elevation/Shapefiles/Nashua NH/hydrography/NHDArea.shp", "usgs-hydrography"));
		//dataPackage.addShapeFile(new ShapeFileRequest("C:/srv/elevation/Shapefiles/Nashua NH/hydrography/NHDFlowline.shp", "usgs-hydrography"));
		//dataPackage.addShapeFile(new ShapeFileRequest("C:/srv/elevation/Shapefiles/Nashua NH/hydrography/NHDWaterbody.shp", "usgs-hydrography"));
		
		//dataPackage.addShapeFile(new ShapeFileRequest("C:/srv/elevation/Shapefiles/Nashua NH/transportation/Trans_RailFeature.shp", null));
		//dataPackage.addShapeFile(new ShapeFileRequest("C:/srv/elevation/Shapefiles/Nashua NH/transportation/Trans_RailFeature.shp", "usgs-transportation-rail"));
		//dataPackage.addShapeFile(new ShapeFileRequest("C:/srv/elevation/Shapefiles/Nashua NH/transportation/Trans_AirportRunway.shp", "usgs-transportation-runways"));
		//dataPackage.addShapeFile(new ShapeFileRequest("C:/srv/elevation/Shapefiles/Nashua NH/transportation/Trans_RoadSegment.shp", "usgs-transportation-roads"));
		
		
		// Create Components
		projectButtonBar = new ProjectButtonBar();
		//previewPanel = new VisualPreviewPanel(dataPackage, modelOptions);
		modelOptionsPanel = new ModelOptionsPanel();
		//inputList = new InputGridList();
		overviewPanel = new DataOverviewPanel();
		statusBar = new StatusBar();
		statusBar.setProgressVisible(false);
		datasetTree = new DataSetTree(dataPackage);
		datasetOptionsPanel = new DataSetOptionsPanel();
		orderingButtonBar = new OrderingButtonBar();
		layoutPane = new DataInputLayoutPane(dataPackage, modelOptions);
		previewPane = new ModelPreviewPane(dataPackage, modelOptions);
		
		westPanel = new Panel();
		southPanel = new Panel();
		
		projectMenu = new ComponentMenu(this, I18N.get("us.wthr.jdem846.ui.projectPane.menu.project"), KeyEvent.VK_P);
		MainMenuBar.insertMenu(projectMenu);
		
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

		
	
		// Set component properties
		modelOptionsPanel.setModelOptions(modelOptions);
		
		// Add Listeners
		
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
		
		/*
		inputList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (inputList.getSelectedRow() >= 0) {
					projectButtonBar.setButtonEnabled(ProjectButtonBar.BTN_REMOVE, true);
				} else {
					projectButtonBar.setButtonEnabled(ProjectButtonBar.BTN_REMOVE, false);
				}
			}
		});
		*/
		
		modelOptionsPanel.addOptionsChangedListener(new OptionsChangedListener() {
			public void onOptionsChanged(ModelOptions options) {
				log.info("Options have changed.");
				layoutPane.setModelOptions(options);
				layoutPane.update();
				previewPane.setModelOptions(options);
				previewPane.update();
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
				westPanel.revalidate();
			}
		});
		
		// Set Layout
		
		/*
		JPanel gridPanel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		gridPanel.setLayout(gridbag);
		GridBagConstraints constraints = new GridBagConstraints();
		*/
		/*
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 2.0;
		constraints.gridwidth = 1;
		gridbag.setConstraints(previewPanel, constraints);
		gridPanel.add(previewPanel);
		*/
		/*
		constraints.weightx = 1.0;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.gridwidth  = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(modelOptionsPanel, constraints);
		gridPanel.add(modelOptionsPanel);
		*/
		/*
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		constraints.gridwidth  = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(projectButtonBar, constraints);
		gridPanel.add(projectButtonBar);
		*/
		/*
		tableScroll = new JScrollPane(inputList);
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.gridwidth  = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(tableScroll, constraints);
		gridPanel.add(tableScroll);
		*/
		//constraints.weighty = 0.0;
		//gridbag.setConstraints(overviewPanel, constraints);
		//gridPanel.add(overviewPanel);

		//constraints.weighty = 0.0;
//		gridbag.setConstraints(statusBar, constraints);
		//gridPanel.add(statusBar);
		
		//layoutPane = new DataInputLayoutPane(dataPackage, modelOptions);
		//previewPane = new ModelPreviewPane(dataPackage, modelOptions);
		TabPane centerPanel = new TabPane();
		centerPanel.addTab(I18N.get("us.wthr.jdem846.ui.projectPane.tab.options"), modelOptionsPanel);
		centerPanel.addTab(I18N.get("us.wthr.jdem846.ui.projectPane.tab.layout"), layoutPane);
		centerPanel.addTab(I18N.get("us.wthr.jdem846.ui.projectPane.tab.preview"), previewPane);
		//centerPanel.add(previewPanel, "Layout Preview");
		centerPanel.setTabPlacement(TabPane.BOTTOM);
		
		//addInputData("C:/Documents and Settings/a345926/My Documents/testdata/ned_64087130.flt");
		
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.PAGE_AXIS));
		Box box = Box.createVerticalBox();
        box.add(overviewPanel);
        box.add(statusBar);
		southPanel.add(box, BorderLayout.PAGE_END);
		
		
		westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.PAGE_AXIS));
		box = Box.createVerticalBox();
		//orderingButtonBar.setAlignmentX(LEFT_ALIGNMENT);
		box.add(orderingButtonBar);
        box.add(datasetTree);
        box.add(datasetOptionsPanel);
        westPanel.add(box, BorderLayout.PAGE_END);
		
		splitPane = new SplitPane(SplitPane.HORIZONTAL_SPLIT, westPanel, centerPanel);
		splitPane.setDividerLocation(280);
		splitPane.setOneTouchExpandable(true);
		splitPane.setAlignmentY(TOP_ALIGNMENT);
		
		this.setLayout(new BorderLayout());
		this.add(projectButtonBar, BorderLayout.NORTH);
		this.add(splitPane, BorderLayout.CENTER);
		
		
		this.add(southPanel, BorderLayout.SOUTH);
		
		onDataModelChanged();
		
		
	}
	
	public void dispose() throws ComponentException
	{
		log.info("Closing project pane.");
		
		MainMenuBar.removeMenu(projectMenu);
		
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
		dataPackage.calculateElevationMinMax(false);
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
	
	public interface CreateModelListener
	{
		public void onCreateModel(DataPackage dataPackage, ModelOptions modelOptions);
	}
	
	
}
