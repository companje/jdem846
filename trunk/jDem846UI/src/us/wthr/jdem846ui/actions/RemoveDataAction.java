package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846ui.ICommandIds;
import us.wthr.jdem846ui.project.ProjectContext;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.data.TreeSelectionAdapter;

public class RemoveDataAction extends BasicAction 
{

	private InputSourceData inputSourceData = null;
	
	public RemoveDataAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_REMOVE_DATA, viewId, label, "/icons/eclipse/data_remove.gif");
		setEnabled(false);
		
		DataView.addTreeSelectionListener(new TreeSelectionAdapter() {
			public void onSourceDataSelectionChanged(InputSourceData selectedData) {
				if (selectedData != null) {
					setEnabled(true);
				} else {
					setEnabled(false);
				}
				
				inputSourceData = selectedData;
			}

		});
		
	}
	
	@Override
	public void run() {
		super.run();
		
		if (inputSourceData == null) {
			return;
		}
		
		ProjectContext.getInstance().removeSourceData(inputSourceData);
		
	}
}
