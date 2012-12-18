package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846ui.ICommandIds;

public class ZoomFitAction extends BasicZoomAction
{
	
	
	public ZoomFitAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_ZOOM_FIT, viewId, label, "/icons/eclipse/zoom_fit.gif");
	}
}