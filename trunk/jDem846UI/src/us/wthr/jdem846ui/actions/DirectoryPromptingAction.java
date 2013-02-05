package us.wthr.jdem846ui.actions;

import java.io.File;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846.JDem846Properties;

public abstract class DirectoryPromptingAction extends BasicAction
{

	private static String UI_STATE_PATH_PROPERTY = "us.wthr.jdem846.state.ui.fileChooser.path";
	
	public DirectoryPromptingAction(IWorkbenchWindow window, String id,
			String viewId, String label, String iconPath) {
		super(window, id, viewId, label, iconPath);
	}

	public DirectoryPromptingAction(IWorkbenchWindow window, String id,
			String viewId, String label) {
		super(window, id, viewId, label);
	}

	public DirectoryPromptingAction(String id, String viewId, String label,
			String iconPath) {
		super(id, viewId, label, iconPath);
	}

	public DirectoryPromptingAction(String id, String viewId, String label) {
		super(id, viewId, label);
	}

	
	
	protected String promptForProjectPath(String previousPath)
	{
		DirectoryDialog dialog = new DirectoryDialog(this.getWindow().getShell());
		
		if (previousPath != null) {
			dialog.setFilterPath(previousPath); 
		} else {

			String lastUsed = getLastUsedPath();
			if (lastUsed != null) {
				dialog.setFilterPath(lastUsed); 
			}
		}
	    String projectPath = dialog.open();
	    
	    if (projectPath != null) {
	    	setLastUsedPath(projectPath);
	    }
	    
	    return projectPath;
	}
	
	
	protected String getLastUsedPath()
	{
		 return validatePath(JDem846Properties.getProperty(UI_STATE_PATH_PROPERTY));
	}
	
	protected void setLastUsedPath(String path)
	{
		path = validatePath(path);
		if (path == null) {
			return;
		}
		
		JDem846Properties.setProperty(UI_STATE_PATH_PROPERTY, path);
	}
	
	protected String validatePath(String path)
	{
		if (path == null) {
			return null;
		}
		
		File f = new File(path);
		if (!f.exists()) {
			return null;
		}
		
		if (!f.isDirectory()) {
			return f.getParent();
		}
		
		return path;
	}
}
