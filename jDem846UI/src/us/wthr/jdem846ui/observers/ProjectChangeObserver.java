package us.wthr.jdem846ui.observers;

import us.wthr.jdem846ui.project.ProjectChangeAdapter;
import us.wthr.jdem846ui.project.ProjectContext;

public abstract class ProjectChangeObserver extends ProjectChangeAdapter
{
	
	
	public ProjectChangeObserver()
	{
		ProjectContext.getInstance().addProjectChangeListener(this);
	}
	
	
	
}
