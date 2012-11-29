package us.wthr.jdem846ui.actions;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846ui.ICommandIds;
import us.wthr.jdem846ui.project.GetSaveLocationCallback;
import us.wthr.jdem846ui.project.ProjectContext;

public class SaveProjectAsAction extends BasicAction
{
	// Merge this shit with SaveProjectAction
	public static final String[] GENERIC_FILE_TYPES = {"Data Files", "All Files (*)"};
	public static final String[] GENERIC_FILE_TYPES_WIN = {"Data Files", "All Files (*.*)"};
	
	public static final String[] GENERIC_FILE_EXTENSIONS = {"*.jdemprj", "*"};
	public static final String[] GENERIC_FILE_EXTENSIONS_WIN = {"*.jdemprj", "*.*"};
	
	
	public SaveProjectAsAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_SAVE, viewId, label, "/icons/eclipse/project_saveas.gif");
	}
	
	@Override
	public void run() {
		super.run();
		
		ProjectContext.getInstance().save(new GetSaveLocationCallback() {
			public String getSaveLocation(String previousSaveLocation) {
				return promptForFilePath(previousSaveLocation);
			}
		});
	}
	

	protected String promptForFilePath(String previousFile)
	{
		FileDialog dialog = new FileDialog (this.getWindow().getShell(), SWT.SAVE);
		String [] filterNames = GENERIC_FILE_TYPES;
		String [] filterExtensions = GENERIC_FILE_EXTENSIONS;
		String filterPath = "/";
		String platform = SWT.getPlatform();
		if (platform.equals("win32") || platform.equals("wpf")) {
			filterNames = GENERIC_FILE_TYPES_WIN;
			filterExtensions = GENERIC_FILE_EXTENSIONS_WIN;
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
}
