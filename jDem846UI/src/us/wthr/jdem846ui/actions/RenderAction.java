package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846ui.ICommandIds;

public class RenderAction extends BasicAction
{

	
	public RenderAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_RENDER, viewId, label, "/icons/eclipse/model_create.gif");
	}
}
