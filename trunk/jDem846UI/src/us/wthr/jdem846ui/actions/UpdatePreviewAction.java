package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846ui.ICommandIds;
import us.wthr.jdem846ui.observers.ModelPreviewChangeObserver;

public class UpdatePreviewAction extends BasicAction
{
	public UpdatePreviewAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_UPDATE_PREVIEW, viewId, label, "/icons/eclipse/refresh.gif");
	}

	@Override
	public void run()
	{
		ModelPreviewChangeObserver.getInstance().update(false, true, true);
	}
	
	
	
	
}
