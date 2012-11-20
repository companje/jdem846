package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846ui.ICommandIds;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.data.TreeSelectionListener;

public class RemoveDataAction extends BasicAction 
{

	
	public RemoveDataAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_REMOVE_DATA, viewId, label, "/icons/eclipse/data_remove.gif");
		setEnabled(false);
		
		DataView.addTreeSelectionListener(new TreeSelectionListener() {
			public void onSelectionChanged(InputSourceData selectedData) {
				if (selectedData != null) {
					setEnabled(true);
				} else {
					setEnabled(false);
				}
			}
		});
		
	}
}
