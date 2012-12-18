package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846ui.ICommandIds;

public class ZoomInAction extends BasicZoomAction
{
	
	public ZoomInAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_ZOOM_IN, viewId, label, "/icons/eclipse/zoom_in.gif");
	}
	
}
