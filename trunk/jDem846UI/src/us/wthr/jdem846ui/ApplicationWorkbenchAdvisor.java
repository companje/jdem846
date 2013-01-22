package us.wthr.jdem846ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846ui.observers.OptionValidationChangeObserver;
import us.wthr.jdem846ui.observers.RenderedModelSelectionObserver;
import us.wthr.jdem846ui.preferences.GeneralPreferencesPage;
import us.wthr.jdem846ui.project.ProjectContext;
import us.wthr.jdem846ui.project.ProjectException;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

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
	}

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public void preStartup() {
		// TODO Auto-generated method stub
		super.preStartup();
		
		try {
			ProjectContext.initialize(initialProject);
		} catch (ProjectException ex) {
			ex.printStackTrace();
		}
		
		OptionValidationChangeObserver.getInstance();
		RenderedModelSelectionObserver.getInstance();
		//IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
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
		
		PreferenceManager pm = PlatformUI.getWorkbench().getPreferenceManager( );
		pm.remove("org.eclipse.ui.preferencePages.Workbench");
		pm.remove("org.eclipse.help.ui.browsersPreferencePage");

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
