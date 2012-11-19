package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846ui.ICommandIds;

public class RemoveDataAction extends BasicAction 
{

	
	public RemoveDataAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_REMOVE_DATA, viewId, label, "/icons/eclipse/data_remove.gif");

	}
}
