package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846ui.ICommandIds;

public class AddDataAction extends BasicAction
{

	
	public AddDataAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_ADD_DATA, viewId, label, "/icons/eclipse/data_add.gif");

	}

	
	
}
