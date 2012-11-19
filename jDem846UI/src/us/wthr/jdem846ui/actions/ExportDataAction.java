package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846ui.ICommandIds;

public class ExportDataAction extends BasicAction {

	
	public ExportDataAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_EXPORT_DATA, viewId, label, "/icons/eclipse/data_export.gif");

	}
}
