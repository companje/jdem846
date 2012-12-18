package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846ui.ICommandIds;

public class ZoomActualAction extends BasicZoomAction
{
	
	
	public ZoomActualAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_ZOOM_ACTUAL, viewId, label, "/icons/eclipse/zoom_100.gif");
	}
}
