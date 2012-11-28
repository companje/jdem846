package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846ui.ICommandIds;
import us.wthr.jdem846ui.tasks.RenderTaskLauncher;

public class RenderAction extends BasicAction
{

	
	public RenderAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_RENDER, viewId, label, "/icons/eclipse/model_create.gif");
	}

	@Override
	public void run() 
	{
		super.run();
		
		RenderTaskLauncher.getInstance().launchRenderTask();
	}
	
	
	
}
