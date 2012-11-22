package us.wthr.jdem846ui.project;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.model.OptionModelChangeEvent;

public class ProjectChangeBroker {
	
	private List<ProjectChangeListener> listeners = new LinkedList<ProjectChangeListener>();
	
	public ProjectChangeBroker()
	{
		
	}
	
	
	public boolean addProjectChangeListener(ProjectChangeListener l)
	{
		return this.listeners.add(l);
	}
	
	public boolean removeProjectChangeListener(ProjectChangeListener l)
	{
		return this.listeners.remove(l);
	}
	
	
	public void fireOnDataAdded()
	{
		for (ProjectChangeListener listener : listeners) {
			listener.onDataAdded();
		}
	}
	
	public void fireOnDataRemoved()
	{
		for (ProjectChangeListener listener : listeners) {
			listener.onDataRemoved();
		}
	}
	
	public void fireOnOptionChanged(OptionModelChangeEvent e)
	{
		for (ProjectChangeListener listener : listeners) {
			listener.onOptionChanged(e);
		}
	}
	
}
