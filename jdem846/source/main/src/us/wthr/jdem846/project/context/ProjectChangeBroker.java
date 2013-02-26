package us.wthr.jdem846.project.context;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.model.OptionModelChangeEvent;

public class ProjectChangeBroker
{

	private List<ProjectChangeListener> listeners = new LinkedList<ProjectChangeListener>();

	private boolean ignoreInterimEvents = false;

	public ProjectChangeBroker()
	{

	}

	public boolean addProjectChangeListener(ProjectChangeListener l)
	{
		synchronized (listeners) {
			return this.listeners.add(l);
		}
	}

	public boolean removeProjectChangeListener(ProjectChangeListener l)
	{
		synchronized (listeners) {
			return this.listeners.remove(l);
		}
	}

	public void fireOnDataAdded(boolean ignoreInterim)
	{
		if (ignoreInterimEvents) {
			return;
		}

		ignoreInterimEvents = ignoreInterim;

		List<ProjectChangeListener> listenersList = getListenersListCopy();
		for (ProjectChangeListener listener : listenersList) {
			listener.onDataAdded();
		}

		ignoreInterimEvents = false;
	}

	public void fireOnDataRemoved(boolean ignoreInterim)
	{
		if (ignoreInterimEvents) {
			return;
		}

		ignoreInterimEvents = ignoreInterim;

		List<ProjectChangeListener> listenersList = getListenersListCopy();
		for (ProjectChangeListener listener : listenersList) {
			listener.onDataRemoved();
		}

		ignoreInterimEvents = false;
	}

	public void fireOnOptionChanged(OptionModelChangeEvent e, boolean ignoreInterim)
	{
		if (ignoreInterimEvents) {
			return;
		}

		ignoreInterimEvents = ignoreInterim;

		List<ProjectChangeListener> listenersList = getListenersListCopy();
		for (ProjectChangeListener listener : listenersList) {
			listener.onOptionChanged(e);
		}

		ignoreInterimEvents = false;
	}

	public void fireOnElevationModelAdded(ElevationModel elevationModel, boolean ignoreInterim)
	{
		if (ignoreInterimEvents) {
			return;
		}

		ignoreInterimEvents = ignoreInterim;

		List<ProjectChangeListener> listenersList = getListenersListCopy();
		for (ProjectChangeListener listener : listenersList) {
			listener.onElevationModelAdded(elevationModel);
		}

		ignoreInterimEvents = false;
	}

	public void fireOnElevationModelRemoved(ElevationModel elevationModel, boolean ignoreInterim)
	{
		if (ignoreInterimEvents) {
			return;
		}

		ignoreInterimEvents = ignoreInterim;

		List<ProjectChangeListener> listenersList = getListenersListCopy();
		for (ProjectChangeListener listener : listenersList) {
			listener.onElevationModelRemoved(elevationModel);
		}

		ignoreInterimEvents = false;
	}

	public void fireOnBeforeProjectLoaded(String filePathOld, String filePathNew, boolean ignoreInterim)
	{
		if (ignoreInterimEvents) {
			return;
		}

		ignoreInterimEvents = ignoreInterim;
		
		List<ProjectChangeListener> listenersList = getListenersListCopy();
		for (ProjectChangeListener listener : listenersList) {
			listener.onBeforeProjectLoaded(filePathOld, filePathNew);
		}

		ignoreInterimEvents = false;
	}
	
	
	public void fireOnProjectLoaded(String filePath, boolean ignoreInterim)
	{
		if (ignoreInterimEvents) {
			return;
		}

		ignoreInterimEvents = ignoreInterim;
		
		List<ProjectChangeListener> listenersList = getListenersListCopy();
		for (ProjectChangeListener listener : listenersList) {
			listener.onProjectLoaded(filePath);
		}

		ignoreInterimEvents = false;
	}
	
	
	protected List<ProjectChangeListener> getListenersListCopy()
	{
		List<ProjectChangeListener> copy = new LinkedList<ProjectChangeListener>();
		synchronized(listeners) {
			copy.addAll(listeners);
		}
		
		return copy;
	}
	
}
