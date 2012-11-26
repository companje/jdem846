package us.wthr.jdem846ui.observers;

import us.wthr.jdem846ui.project.ProjectChangeListener;
import us.wthr.jdem846ui.project.ProjectContext;

public abstract class ProjectChangeObserver implements ProjectChangeListener 
{
	
	public ProjectChangeObserver()
	{
		ProjectContext.getInstance().addProjectChangeListener(this);
	}
	
}
