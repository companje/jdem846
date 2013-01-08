package us.wthr.jdem846ui.actions;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846ui.ICommandIds;
import us.wthr.jdem846ui.project.ProjectContext;
import us.wthr.jdem846ui.project.ProjectException;

public class OpenProjectAction extends BasicAction
{
	private static Log log = Logging.getLog(OpenProjectAction.class);

	public OpenProjectAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_OPEN, viewId, label, "/icons/eclipse/project_open.gif");
	}

	@Override
	public void run()
	{
		super.run();

		String filePath = promptForFilePath(null);
		if (filePath != null) {
			try {
				ProjectContext.initialize(filePath);
			} catch (ProjectException e) {
				e.printStackTrace();
			}
		}

	}

	protected String promptForFilePath(String previousFile)
	{
		FileDialog dialog = new FileDialog(this.getWindow().getShell(), SWT.OPEN);
		String[] filterNames = StandardFileTypes.PROJECT_PROJECT_FILE_TYPES;
		String[] filterExtensions = StandardFileTypes.PROJECT_PROJECT_FILE_EXTENSIONS;
		String filterPath = "/";
		String platform = SWT.getPlatform();
		if (platform.equals("win32") || platform.equals("wpf")) {
			filterNames = StandardFileTypes.PROJECT_PROJECT_FILE_TYPES_WIN;
			filterExtensions = StandardFileTypes.PROJECT_PROJECT_FILE_EXTENSIONS_WIN;
			filterPath = "c:\\";
		}
		dialog.setFilterNames(filterNames);
		dialog.setFilterExtensions(filterExtensions);

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
