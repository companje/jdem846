package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.data.TreeSelectionListener;

public abstract class BasicZoomAction extends BasicAction
{

	public BasicZoomAction(IWorkbenchWindow window, String id, String viewId, String label, String iconPath)
	{
		super(window, id, viewId, label, iconPath);
		setActionEnableListeners();
	}

	public BasicZoomAction(IWorkbenchWindow window, String id, String viewId, String label)
	{
		super(window, id, viewId, label);
		setActionEnableListeners();
	}

	public BasicZoomAction(String id, String viewId, String label, String iconPath)
	{
		super(id, viewId, label, iconPath);
		setActionEnableListeners();
	}

	public BasicZoomAction(String id, String viewId, String label)
	{
		super(id, viewId, label);
		setActionEnableListeners();
	}
	
	
	protected void setActionEnableListeners()
	{
		setEnabled(false);
		
		DataView.addTreeSelectionListener(new TreeSelectionListener() {
			public void onSourceDataSelectionChanged(InputSourceData selectedData) {
				setEnabled(false);
			}

			@Override
			public void onRenderedModelSelectionChanged(ElevationModel elevationModel)
			{
				if (elevationModel != null) {
					setEnabled(true);
				} else {
					setEnabled(false);
				}
			}
		});
	}
}
