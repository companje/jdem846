package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846.project.context.ProjectContext;
import us.wthr.jdem846ui.ICommandIds;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.data.TreeSelectionAdapter;

public class DeleteRenderedModelAction extends BasicAction 
{
	
	private ElevationModel elevationModel = null;
	
	
	public DeleteRenderedModelAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_DELETE_MODEL, viewId, label, "/icons/eclipse/delete_config.gif");
		setEnabled(false);
		
		DataView.addTreeSelectionListener(new TreeSelectionAdapter() {
			public void onSourceDataSelectionChanged(InputSourceData selectedData) {
				
			}

			@Override
			public void onRenderedModelSelectionChanged(ElevationModel elevationModel)
			{
				if (elevationModel != null) {
					setEnabled(true);
				} else {
					setEnabled(false);
				}
				DeleteRenderedModelAction.this.elevationModel = elevationModel;
			}
			
		});
		
	}
	
	@Override
	public void run() {
		super.run();
		
		if (elevationModel == null) {
			return;
		}
		
		ProjectContext.getInstance().removeElevationModel(elevationModel);
		
	}
}
