package us.wthr.jdem846ui.observers;

import us.wthr.jdem846.project.context.ProjectChangeAdapter;
import us.wthr.jdem846.project.context.ProjectContext;

public abstract class ProjectChangeObserver extends ProjectChangeAdapter
{
	
	
	public ProjectChangeObserver()
	{
		ProjectContext.getInstance().addProjectChangeListener(this);
	}
	
	
	
}
