package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846ui.ICommandIds;

public class OpenProjectAction extends BasicAction
{

	
	
	public OpenProjectAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_OPEN, viewId, label, "/icons/eclipse/project_open.gif");
	}
	
}
