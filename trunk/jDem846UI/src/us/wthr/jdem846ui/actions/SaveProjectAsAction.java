package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846ui.ICommandIds;

public class SaveProjectAsAction extends BasicAction
{
	public SaveProjectAsAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_SAVE, viewId, label, "/icons/eclipse/project_saveas.gif");
	}
}
