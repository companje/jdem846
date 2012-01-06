package us.wthr.jdem846.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.exception.ScriptCompilationFailedException;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.project.ProjectFiles;
import us.wthr.jdem846.project.ProjectModel;
import us.wthr.jdem846.project.ProjectTypeEnum;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.OutputProduct;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.scripting.ScriptShell;
import us.wthr.jdem846.scripting.ScriptShellFactory;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.tasks.RunnableTask;
import us.wthr.jdem846.tasks.TaskControllerService;
import us.wthr.jdem846.tasks.TaskStatusListener;
import us.wthr.jdem846.ui.ScriptProjectButtonBar.ButtonClickedListener;
import us.wthr.jdem846.ui.base.FileChooser;
import us.wthr.jdem846.ui.base.Menu;
import us.wthr.jdem846.ui.base.MenuItem;
import us.wthr.jdem846.ui.base.ProgressBar;
import us.wthr.jdem846.ui.scripting.ScriptEditorPanel;

@SuppressWarnings("serial")
public class ScriptProjectPane extends JdemPanel implements Savable
{
	
	private static Log log = Logging.getLog(ScriptProjectPane.class);
	
	private ModelOptions modelOptions;
	
	private ScriptProjectButtonBar projectButtonBar;
	private Menu projectMenu;
	private ScriptEditorPanel scriptPane;
	private LogOutputPanel logViewer;
	
	private RunnableTask renderTask;
	private TaskStatusListener taskStatusListener;
	
	
	private ProcessWorkingSpinner spinner;
	
	public ScriptProjectPane()
	{
		initialize(null);
	}
	
	public ScriptProjectPane(ProjectModel projectModel)
	{
		initialize(projectModel);
	}
	
	protected void initialize(ProjectModel projectModel)
	{
		modelOptions = new ModelOptions();
		
		if (projectModel != null) {
			modelOptions.syncFromProjectModel(projectModel);
			modelOptions.setWriteTo(projectModel.getLoadedFrom());
		}
		
		scriptPane = new ScriptEditorPanel();
		logViewer = new LogOutputPanel();
		
		projectButtonBar = new ScriptProjectButtonBar(this);
		MainButtonBar.addToolBar(projectButtonBar);
		projectButtonBar.addButtonClickedListener(new ButtonClickedListener() {
			public void onExecuteClicked()
			{
				onExecuteScript();
			}
		});
		
		spinner = new ProcessWorkingSpinner();
		spinner.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		spinner.step();
		projectButtonBar.add( Box.createHorizontalGlue() );
		projectButtonBar.add(spinner);
		
		
		projectMenu = new ComponentMenu(this, I18N.get("us.wthr.jdem846.ui.projectPane.menu.project"), KeyEvent.VK_P);
		MainMenuBar.insertMenu(projectMenu);
		projectMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.scriptProjectPane.menu.execute.label"), JDem846Properties.getProperty("us.wthr.jdem846.ui.project.createModel"), KeyEvent.VK_E, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onExecuteScript();
			}
		}));
		
		
		addCenter(I18N.get("us.wthr.jdem846.ui.scriptProjectPane.scriptTab.label"), scriptPane);
		addSouth(I18N.get("us.wthr.jdem846.ui.scriptProjectPane.logTab.label"), logViewer);
		
		this.setRightVisible(false);
		this.setLeftVisible(false);
		
	
		if (projectModel.getUserScript() != null && projectModel.getUserScript().length() > 0) {
			scriptPane.setScriptContent(projectModel.getUserScript());
			scriptPane.setScriptLanguage(projectModel.getScriptLanguage());
		} else {
			loadDefaultScripting();
		}
		
	}
	
	protected void onExecuteScript()
	{
		
		final String scriptCode = scriptPane.getScriptContent();
		final ScriptLanguageEnum language = scriptPane.getScriptLanguage();
		
		
		renderTask = new RunnableTask("Model Render Task") {
			
			public void run() throws RenderEngineException
			{
				log.info("Script execution starting");
				this.setStoppable(false);
				
				long start = 0;
				long elapsed = 0;
				
				start = System.currentTimeMillis();
				
				try {
					ScriptShell shell = ScriptShellFactory.getScriptShell(language);
					shell.evaluate(scriptCode);
				} catch (ScriptCompilationFailedException ex) {
					JOptionPane.showMessageDialog(getRootPane(),
							I18N.get("us.wthr.jdem846.ui.scriptProjectPane.compileError.message") + ": " + ex.getMessage(),
						    I18N.get("us.wthr.jdem846.ui.scriptProjectPane.compileError.title"),
						    JOptionPane.ERROR_MESSAGE);
				} catch (ScriptingException ex) {
					JOptionPane.showMessageDialog(getRootPane(),
							I18N.get("us.wthr.jdem846.ui.scriptProjectPane.scriptError.message") + ": " + ex.getMessage(),
						    I18N.get("us.wthr.jdem846.ui.scriptProjectPane.scriptError.title"),
						    JOptionPane.ERROR_MESSAGE);
				}
				
				
				elapsed = (System.currentTimeMillis() - start) / 1000;
				log.info("Completed render task in " + elapsed + " seconds");

			}
			
			@Override
			public void cancel()
			{
			
			}
			
			@Override
			public void pause()
			{
				
			}
			
			@Override
			public void resume()
			{
				
			}
		};
		
		taskStatusListener = new TaskStatusListener() {
			public void taskStarting(RunnableTask task)
			{
				setProcessWorkingState(true);
			}
			public void taskFailed(RunnableTask task, Throwable thrown)
			{
				setProcessWorkingState(false);
			}
			public void taskCompleted(RunnableTask task)
			{
				setProcessWorkingState(false);
			}
			public void taskCancelled(RunnableTask task)
			{
				setProcessWorkingState(false);
			}
			public void taskPaused(RunnableTask task)
			{
				//spinner.stop();
			}
			public void taskResumed(RunnableTask task)
			{
				//spinner.start();
			}
		};
		
		TaskControllerService.addTask(renderTask, taskStatusListener);
	}
	
	
	protected void setProcessWorkingState(boolean working)
	{
		if (working) {
			spinner.start();
			projectButtonBar.setButtonEnabled(ScriptProjectButtonBar.BTN_EXECUTE, false);
		} else {
			spinner.stop();
			projectButtonBar.setButtonEnabled(ScriptProjectButtonBar.BTN_EXECUTE, true);
		}
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
			scriptTemplatePath = JDem846Properties.getProperty("us.wthr.jdem846.ui.scriptProject.defaultScriptTemplate.groovy");
		} else if (modelOptions.getScriptLanguage() == ScriptLanguageEnum.JYTHON) {
			scriptTemplatePath = JDem846Properties.getProperty("us.wthr.jdem846.ui.scriptProject.defaultScriptTemplate.jython");
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
			scriptPane.setScriptContent(scriptTemplate);
			scriptPane.setScriptLanguage(modelOptions.getScriptLanguage());
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
		log.info("Closing script project pane.");
		
		MainMenuBar.removeMenu(projectMenu);
		MainButtonBar.removeToolBar(projectButtonBar);
		
		super.dispose();
		

	}
	

	public ProjectModel getProjectModel()
	{
		//ModelOptions modelOptions = modelOptionsPanel.getModelOptions();
		//applyOptionsToModel(modelOptions);
		
		ProjectModel projectModel = new ProjectModel();
		projectModel.setProjectType(ProjectTypeEnum.SCRIPT_PROJECT);
		projectModel.setLoadedFrom(this.getSavedPath());
		modelOptions.syncToProjectModel(projectModel);
		//lightingContext.syncToProjectModel(projectModel);
		
		projectModel.setUserScript(scriptPane.getScriptContent());
		projectModel.setScriptLanguage(scriptPane.getScriptLanguage());

		return projectModel;
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
