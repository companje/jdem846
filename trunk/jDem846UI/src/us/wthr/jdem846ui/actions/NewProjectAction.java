package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846ui.ICommandIds;
import us.wthr.jdem846ui.project.ProjectContext;
import us.wthr.jdem846ui.project.ProjectException;

public class NewProjectAction extends BasicAction
{

	public NewProjectAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_NEW, viewId, label, "/icons/eclipse/project_new.gif");

	}
	
	@Override
	public void run() {
		super.run();

		try {
			ProjectContext.initialize(null);
		} catch (ProjectException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
