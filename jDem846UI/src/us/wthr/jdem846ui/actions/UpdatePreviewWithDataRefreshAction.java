package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.project.context.ProjectContext;
import us.wthr.jdem846ui.ICommandIds;
import us.wthr.jdem846ui.observers.ModelPreviewChangeObserver;

public class UpdatePreviewWithDataRefreshAction extends BasicAction
{
	public UpdatePreviewWithDataRefreshAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_UPDATE_PREVIEW_WITH_DATA_REFRESH, viewId, label, "/icons/eclipse/refresh_with_sync.gif");
	}

	@Override
	public void run()
	{
		boolean estimate = ProjectContext.getInstance().getModelContext().getModelProcessManifest().getGlobalOptionModel().isEstimateElevationRange();
		
		try {
			ProjectContext.getInstance().getModelContext().updateContext(true, estimate);
		} catch (ModelContextException ex) {
			// TODO: Display dialog
			ex.printStackTrace();
		}
		
		ModelPreviewChangeObserver.getInstance().update(true, true, true);
	}
}
