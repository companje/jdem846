package us.wthr.jdem846ui.actions;

import us.wthr.jdem846ui.ICommandIds;

public class ExportModelAction extends BasicAction
{
	public ExportModelAction(String label, String viewId)
	{
		super(ICommandIds.CMD_EXPORT_MODEL, viewId, label, "/icons/eclipse/export.gif");
	}
}
