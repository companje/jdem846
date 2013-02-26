package us.wthr.jdem846ui.actions;

import java.io.File;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.project.context.GetSaveLocationCallback;
import us.wthr.jdem846.project.context.ProjectContext;
import us.wthr.jdem846ui.ICommandIds;

public class SaveProjectAction extends DirectoryPromptingAction
{
	private static Log log = Logging.getLog(SaveProjectAction.class);
	

	public SaveProjectAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_SAVE, viewId, label, "/icons/eclipse/project_save.gif");
	}
	
	@Override
	public void run() {
		super.run();
		
		ProjectContext.getInstance().save(new GetSaveLocationCallback() {
			public String getSaveLocation(String saveLocation) {

				if (saveLocation != null) {
					File f = new File(saveLocation);
					if (!f.exists() || !f.canWrite()) {
						saveLocation = null;
					}
				}
				
				if (saveLocation == null) {
					return promptForProjectPath(saveLocation);
				} else {
					return saveLocation;
				}
			}
		});
	}
	/*
	protected String promptForFilePath(String previousFile)
	{
		FileDialog dialog = new FileDialog (this.getWindow().getShell(), SWT.SAVE);
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
	}*/
}
