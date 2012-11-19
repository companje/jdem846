package us.wthr.jdem846ui.actions.logview;

import us.wthr.jdem846ui.ICommandIds;
import us.wthr.jdem846ui.actions.BasicAction;


public class ClearLogConsoleAction extends BasicAction
{
	private int instanceNum = 0;
	
	public ClearLogConsoleAction(String label, String viewId)
	{
		super(ICommandIds.CMD_CLEAR_LOG_CONSOLE, viewId, label, "/icons/eclipse/clear.gif");
	}
}
