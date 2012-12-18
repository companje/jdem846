package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846ui.ICommandIds;

public class ZoomOutAction extends BasicZoomAction
{
	
	
	public ZoomOutAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_ZOOM_OUT, viewId, label, "/icons/eclipse/zoom_out.gif");
	}
}
