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
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.dbase.ClassLoadException;
import us.wthr.jdem846.exception.ProjectParseException;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.ElevationDataLoaderInstance;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.project.ProjectFileReader;
import us.wthr.jdem846.project.ProjectFileWriter;
import us.wthr.jdem846.project.ProjectModel;
import us.wthr.jdem846.render.EngineInstance;
import us.wthr.jdem846.render.EngineRegistry;
import us.wthr.jdem846.render.RenderEngine;
import us.wthr.jdem846.ui.ProjectPane.CreateModelListener;
import us.wthr.jdem846.ui.TopButtonBar.ButtonClickedListener;
import us.wthr.jdem846.util.ImageIcons;
import us.wthr.jdem846.util.TempFiles;

@SuppressWarnings("serial")
public class JdemFrame extends JFrame
{
	private static Log log = Logging.getLog(JdemFrame.class);
	private static JDem846Properties properties = new JDem846Properties(JDem846Properties.UI_PROPERTIES);
	
	private TabPane tabPane;
	private TopButtonBar topButtonBar;
	private MainMenuBar menuBar;
	
	private static JdemFrame instance = null;
	
	private JdemFrame()
	{
		

		// Set Properties
		this.setTitle(properties.getProperty("us.wthr.jdem846.ui.windowTitle"));
		this.setSize(properties.getIntProperty("us.wthr.jdem846.ui.windowWidth"), properties.getIntProperty("us.wthr.jdem846.ui.windowHeight"));
		this.setLocationRelativeTo(null);
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// Create components
		buildJMenuBar();
		
		
		topButtonBar = new TopButtonBar();
		tabPane = new TabPane();
		
		topButtonBar.add(Box.createHorizontalGlue());
		MemoryMonitor memory = new MemoryMonitor(1000);
		memory.start();
		topButtonBar.add(memory);
		
		// Add listeners
		topButtonBar.addButtonClickedListener(new ButtonClickedListener() {
			public void onExitClicked() {
				exitApplication();
			}
			public void onNewProjectClicked() {
				createNewProject(null);
			}
			public void onSaveProjectClicked() {
				saveProject();
			}
			public void onOpenProjectClicked() {
				openProject();
			}
		});
		
		addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent event) { }
			public void windowClosing(WindowEvent event) 
			{ 
				log.info("JdemFrame.windowClosing()");
				exitApplication();
			}
			public void windowDeactivated(WindowEvent event) { }
			public void windowDeiconified(WindowEvent event) { }
			public void windowIconified(WindowEvent event) { }
			public void windowOpened(WindowEvent event) { }
			
			public void windowClosed(WindowEvent event)
			{ 
				log.info("JdemFrame.windowClosed()");

			}
		});
		
		
		
		this.setJMenuBar(menuBar);
		
		this.setLayout(new BorderLayout());
		this.add(topButtonBar, BorderLayout.NORTH);
		this.add(tabPane, BorderLayout.CENTER);

		if (properties.getBooleanProperty("us.wthr.jdem846.ui.displayLogViewPanel")) {
			log.info("Log viewer panel is enabled");
			LogViewer logViewer = new LogViewer();
			logViewer.setPreferredSize(new Dimension(1000, 150));
			this.add(logViewer, BorderLayout.SOUTH);
			MainMenuBar.setInsertIndex(2);
		}
		
		this.setGlassPane(new WorkingGlassPane());
	}
	
	protected void buildJMenuBar()
	{
		JMenu menu;
		JMenuItem menuItem;
		
		menuBar = MainMenuBar.getInstance();

		//menu = new JMenu("File");
		ComponentMenu fileMenu = new ComponentMenu(this, "File", KeyEvent.VK_F);
		//menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
		
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_1, ActionEvent.ALT_MASK));
		
		fileMenu.add(new MenuItem("New", "/us/wthr/jdem846/ui/icons/document-new.png", KeyEvent.VK_N, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				createNewProject(null);
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK)));
		
		fileMenu.add(new MenuItem("Open", "/us/wthr/jdem846/ui/icons/document-open.png", KeyEvent.VK_O, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				openProject();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK)));
		
		fileMenu.addSeparator();
		
		fileMenu.add(new MenuItem("Save", "/us/wthr/jdem846/ui/icons/document-save.png", KeyEvent.VK_S, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				saveProject();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK)));
		
		fileMenu.addSeparator();
		
		fileMenu.add(new MenuItem("Exit", "/us/wthr/jdem846/ui/icons/application-exit.png", KeyEvent.VK_X, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				exitApplication();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK)));

		
		menuBar.add(Box.createHorizontalGlue());
		ComponentMenu helpMenu = new ComponentMenu(this, "Help", KeyEvent.VK_H);
		menuBar.add(helpMenu);
		helpMenu.add(new MenuItem("About jDem846...", "/us/wthr/jdem846/ui/icons/help-about.png", KeyEvent.VK_A, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onAbout();
			}
		}));

		
		MainMenuBar.setInsertIndex(1);
	}
	
	public void onAbout()
	{
		AboutDialog about = new AboutDialog(this);
		about.setVisible(true);
	}
	
	public boolean exitApplication()
	{
		int response = JOptionPane.showConfirmDialog(this,
				"Are you sure you'd like to exit?", 
				"Exit?", 
				JOptionPane.YES_NO_OPTION);
		
		// 0 = Yes
		// 1 = No
		
		if (response == JOptionPane.OK_OPTION) {
			
			log.info("Shutting down application");
			
			this.dispose();
			
			return true;
		} else {
			return false;
		}
	}
	
	public void saveProject()
	{
		Object tabObj = tabPane.getSelectedComponent();
		if (tabObj == null || !(tabObj instanceof ProjectPane)) {
			JOptionPane.showMessageDialog(getRootPane(),
				    "Please select a project tab before saving",
				    "Cannot Save",
				    JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		ProjectPane projectPane = (ProjectPane) tabObj;
		ProjectModel projectModel = projectPane.getProjectModel();
		
		
		
		JFileChooser chooser = new BasicFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JDEM Project", "xdem");
		chooser.setFileFilter(filter);
		
		if (projectModel.getLoadedFrom() != null) {
			String loadedFrom = projectModel.getLoadedFrom();
			File loadedFromFile = new File(loadedFrom);
			
			chooser.setCurrentDirectory(loadedFromFile);
			chooser.setSelectedFile(loadedFromFile);
		}
		
		
		chooser.setMultiSelectionEnabled(false);
		
	    int returnVal =  chooser.showSaveDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	File selectedFile = chooser.getSelectedFile();
	    	
	    	try {
	    		String path = selectedFile.getAbsolutePath();
	    		if (!path.toLowerCase().endsWith(".xdem")) {
	    			path = path + ".xdem";
	    		}
	    		
				ProjectFileWriter.writeProject(projectModel, path);
				
				
				setComponentTabTitle(tabPane.getSelectedIndex(), selectedFile.getName());
			} catch (IOException ex) {
				//ex.printStackTrace();
				log.warn("Error trying to write project to disk: " + ex.getMessage(), ex);
				JOptionPane.showMessageDialog(getRootPane(),
					    "Error occured trying to write project to disk",
					    "Save Failed",
					    JOptionPane.ERROR_MESSAGE);
			}
	    	
	    }
		
		
	}
	
	
	public void setComponentTabTitle(int index, String title)
	{
		tabPane.setTitleAt(index, title);
	}
	
	public void openProject()
	{
		log.info("Displaying open project dialog");
		JFileChooser chooser = new BasicFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JDEM Project", "xdem");
		chooser.setFileFilter(filter);
		chooser.setMultiSelectionEnabled(false);
	    int returnVal = chooser.showOpenDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	File selectedFile = chooser.getSelectedFile();
	    	createNewProject(selectedFile.getAbsolutePath());
	    }
	}
	
	public void createNewProject(String filePath)
	{
		log.info("Creating new project");
		try {
			ProjectModel projectModel = null;
			if (filePath != null) {
				projectModel = ProjectFileReader.readProject(filePath);
			}
			
			ProjectPane projectPane = new ProjectPane(projectModel);
			
			projectPane.addCreateModelListener(new CreateModelListener() {
				public void onCreateModel(DataPackage dataPackage, ModelOptions modelOptions) {
					onCreateModelTab(dataPackage, modelOptions);
				}
			});
			
			String title = "DEM Project";
			if (projectModel != null && projectModel.getLoadedFrom() != null) {
				File f = new File(projectModel.getLoadedFrom());
				title = f.getName();
			}
			
			tabPane.addTab(title, projectPane, true);
			tabPane.setSelectedComponent(projectPane);
			
		} catch (FileNotFoundException ex) {
			log.warn("Project file not found: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(getRootPane(),
				    "Cannot open project file: Not Found",
				    "Open Failed",
				    JOptionPane.ERROR_MESSAGE);
			
		} catch (IOException ex) {
			log.warn("IO error reading from disk: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(getRootPane(),
				    "Cannot open project file: Failed to read file from disk",
				    "Open Failed",
				    JOptionPane.ERROR_MESSAGE);

		} catch (ProjectParseException ex) {
			log.warn("Error parsing project: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(getRootPane(),
				    "Cannot open project file: Format is invalid",
				    "Open Failed",
				    JOptionPane.ERROR_MESSAGE);
		}
		
		
		
		
	}
	
	protected void onCreateModelTab(DataPackage dataPackage, ModelOptions modelOptions)
	{
		
		String engineIdentifier = modelOptions.getEngine();
		EngineInstance engineInstance = EngineRegistry.getInstance(engineIdentifier);
		
		RenderEngine engine;
		try {
			engine = engineInstance.getImpl();
		} catch (ClassLoadException ex) {
			ex.printStackTrace();
			log.error("Failed to load engine class '" + ex.getClassName() + "': " + ex.getMessage(), ex);
			
			JOptionPane.showMessageDialog(getRootPane(),
				    "Error creating instance of model engine " + engineInstance.getName(),
				    "Component Error",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		engine.setDataPackage(dataPackage);
		engine.setModelOptions(modelOptions);
		
		
		ElevationDataLoaderInstance dataLoaderInstance = engine.needsOutputFileOfType();
		if (dataLoaderInstance != null) {
			
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter(dataLoaderInstance.getName(), dataLoaderInstance.getExtension());
			chooser.setFileFilter(filter);
			chooser.setMultiSelectionEnabled(false);
		    int returnVal = chooser.showSaveDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	File selectedFile = chooser.getSelectedFile();
		    	modelOptions.setWriteTo(selectedFile.getAbsolutePath());
		    } else {
		    	JOptionPane.showMessageDialog(getRootPane(),
					    "An output file needs to be selected before continuing.",
					    "No File Selected",
					    JOptionPane.ERROR_MESSAGE);
		    	return;
		    }
			

		}
		
		if (engine.generatesImage()) {
			OutputImageViewPanel outputPanel = new OutputImageViewPanel(engine);
			tabPane.addTab("Model", outputPanel, true);
			tabPane.setSelectedComponent(outputPanel);
			outputPanel.startWorker();
		} else {
			
			DataGenerationViewPanel outputPanel = new DataGenerationViewPanel(engine);
			tabPane.addTab("Dataset", outputPanel, true);
			tabPane.setSelectedComponent(outputPanel);
			outputPanel.startWorker();
		}

	}
	
	public void setGlassVisible(boolean visible)
	{
		WorkingGlassPane glassPane = (WorkingGlassPane) this.getGlassPane();
		glassPane.setVisible(visible);
	}
	
	public void setGlassVisible(String text, boolean visible)
	{
		WorkingGlassPane glassPane = (WorkingGlassPane) this.getGlassPane();
		glassPane.setVisible(visible);
		glassPane.setShadeComponent(null);
		glassPane.setText(text);
	}
	
	public void setGlassVisible(String text, Component component, boolean visible)
	{
		WorkingGlassPane glassPane = (WorkingGlassPane) this.getGlassPane();
		glassPane.setVisible(visible);
		glassPane.setShadeComponent(component);
		glassPane.setText(text);
	}
	
	
	public static JdemFrame getInstance()
	{
		if (JdemFrame.instance == null) {
			JdemFrame.instance = new JdemFrame();
		}
		return JdemFrame.instance;
	}

}
