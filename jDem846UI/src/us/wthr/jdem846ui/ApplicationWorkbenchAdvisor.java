package us.wthr.jdem846ui;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.project.context.ProjectContext;
import us.wthr.jdem846.project.context.ProjectException;
import us.wthr.jdem846.prompt.FilePathPrompt;
import us.wthr.jdem846.prompt.FilePathPromptCallback;
import us.wthr.jdem846.prompt.FilePathPromptMode;
import us.wthr.jdem846ui.actions.StandardFileTypes;
import us.wthr.jdem846ui.observers.OptionValidationChangeObserver;
import us.wthr.jdem846ui.observers.ProjectLoadedObserver;
import us.wthr.jdem846ui.observers.RenderedModelSelectionObserver;
import us.wthr.jdem846ui.preferences.GeneralPreferencesPage;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static Log log = Logging.getLog(ApplicationWorkbenchAdvisor.class);
	
	private static final String PERSPECTIVE_ID = "jDem846UI.perspective";

	private String initialProject = null;
	
	public ApplicationWorkbenchAdvisor(String initialProject)
	{
		this.initialProject = initialProject;
	}
	
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.SHOW_MEMORY_MONITOR, true);
		super.initialize(configurer);
		
		// http://rajakannappan.blogspot.com/2009/10/eclipse-rcp-how-to-save-view-layouts.html
		configurer.setSaveAndRestore(true);
	}

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public void preStartup() {
		super.preStartup();
	}
	
	public boolean preShutdown(){  
		  
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();  
		String dialogBoxTitle = "Confirm Exit";  
		String question = "Exit " + JDem846Properties.getProperty("us.wthr.jdem846.applicationName") + "?";  
		return MessageDialog.openQuestion(shell, dialogBoxTitle, question);  
		  
	}  
	
	@Override
	public void postStartup() {
		super.postStartup();
		
		
		FilePathPrompt.setFilePathPromptCallback(new FilePathPromptCallback() {

			@Override
			public String prompt(FilePathPromptMode mode, String previous)
			{
				return "C:\\Users\\GillFamily\\Google Drive\\jDem Visuals\\Earth Flooding\\earthflooding.jdemgrid";
				//return promptForFilePath(previous);
			}
			
		});
		
		PreferenceManager pm = PlatformUI.getWorkbench().getPreferenceManager( );
		pm.remove("org.eclipse.ui.preferencePages.Workbench");
		pm.remove("org.eclipse.help.ui.browsersPreferencePage");
		
		pm.remove("org.eclipse.debug.ui.DebugPreferencePage");
		pm.remove("org.eclipse.jdt.ui.preferences.JavaBasePreferencePage");
		pm.remove("org.eclipse.rse.ui.preferences.RemoteSystemsPreferencePage");
		pm.remove("org.eclipse.team.ui.TeamPreferences");
		pm.remove("org.eclipse.wst.html.ui.preferences.web");
		pm.remove("org.eclipse.wst.jsdt.ui.preferences.JavaBasePreferencePage");
		pm.remove("org.eclipse.wst.xml.ui.preferences.xml");
		
		OptionValidationChangeObserver.getInstance();
		RenderedModelSelectionObserver.getInstance();
		ProjectLoadedObserver.getInstance();
		
		try {
			ProjectContext.initialize(initialProject);
		} catch (ProjectException ex) {
			ex.printStackTrace();
		}
		

		
	}
	
	protected IWorkbenchWindow getAWindow()
	{
		if (PlatformUI.getWorkbench().getWorkbenchWindows().length > 0) {
			return PlatformUI.getWorkbench().getWorkbenchWindows()[0];
		} else {
			return null;
		}
	}
	
	protected String promptForFilePath(String previousFile)
	{
		FileDialog dialog = null;
		
		try {
			
			new FileDialog (getAWindow().getShell(), SWT.OPEN);
		} catch (Exception ex) {
			log.warn("Error creating file dialog: " + ex.getMessage(), ex);
			return null;
		}
		String [] filterNames = StandardFileTypes.PROJECT_PROJECT_FILE_TYPES;
		String [] filterExtensions = StandardFileTypes.PROJECT_PROJECT_FILE_EXTENSIONS;
		String filterPath = "/";
		String platform = SWT.getPlatform();
		if (platform.equals("win32") || platform.equals("wpf")) {
			filterNames = StandardFileTypes.PROJECT_PROJECT_FILE_TYPES_WIN;
			filterExtensions = StandardFileTypes.PROJECT_PROJECT_FILE_EXTENSIONS_WIN;
			filterPath = "c:\\";
		}
		dialog.setFilterNames (filterNames);
		dialog.setFilterExtensions (filterExtensions);
		
		if (previousFile != null) {
			File f = new File(previousFile);
			dialog.setFileName(f.getName());
			dialog.setFilterPath(f.getAbsolutePath());
		} else {
			dialog.setFilterPath(filterPath);
		}
		
		
		return dialog.open();
	}

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public String getMainPreferencePageId()
	{
		return GeneralPreferencesPage.ID;
	}

	

}
