package us.wthr.jdem846ui.project;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.ElevationModel;
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
	
	public void fireOnElevationModelAdded(ElevationModel elevationModel)
	{
		for (ProjectChangeListener listener : listeners) {
			listener.onElevationModelAdded(elevationModel);
		}
	}
	
	public void fireOnElevationModelRemoved(ElevationModel elevationModel)
	{
		for (ProjectChangeListener listener : listeners) {
			listener.onElevationModelRemoved(elevationModel);
		}
	}

	public void fireOnProjectLoaded()
	{
		for (ProjectChangeListener listener : listeners) {
			listener.onProjectLoaded();
		}
	}
	
}
