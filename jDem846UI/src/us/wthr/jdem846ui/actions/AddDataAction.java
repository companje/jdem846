package us.wthr.jdem846ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846ui.Activator;
import us.wthr.jdem846ui.ICommandIds;

public class AddDataAction extends Action
{
	private final IWorkbenchWindow window;
	private int instanceNum = 0;
	private final String viewId;
	
	
	public AddDataAction(IWorkbenchWindow window, String label, String viewId)
	{
		this.window = window;
		this.viewId = viewId;
        setText(label);
        // The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_ADD_DATA);
        // Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_ADD_DATA);
		setImageDescriptor(Activator.getImageDescriptor("/icons/eclipse/data_add.gif"));
	}
}
