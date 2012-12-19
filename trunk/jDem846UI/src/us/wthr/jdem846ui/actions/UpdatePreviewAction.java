package us.wthr.jdem846ui.actions;

import us.wthr.jdem846ui.ICommandIds;

public class UpdatePreviewAction extends BasicAction
{
	public UpdatePreviewAction(String label, String viewId)
	{
		super(ICommandIds.CMD_UPDATE_PREVIEW, viewId, label, "/icons/eclipse/refresh.gif");
	}
}
