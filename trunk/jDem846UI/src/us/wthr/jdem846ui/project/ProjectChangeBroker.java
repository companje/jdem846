package us.wthr.jdem846ui.project;

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
		return this.listeners.add(l);
	}

	public boolean removeProjectChangeListener(ProjectChangeListener l)
	{
		return this.listeners.remove(l);
	}

	public void fireOnDataAdded(boolean ignoreInterim)
	{
		if (ignoreInterimEvents) {
			return;
		}

		ignoreInterimEvents = ignoreInterim;

		for (ProjectChangeListener listener : listeners) {
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

		for (ProjectChangeListener listener : listeners) {
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

		for (ProjectChangeListener listener : listeners) {
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

		for (ProjectChangeListener listener : listeners) {
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

		for (ProjectChangeListener listener : listeners) {
			listener.onElevationModelRemoved(elevationModel);
		}

		ignoreInterimEvents = false;
	}

	public void fireOnProjectLoaded(boolean ignoreInterim)
	{
		if (ignoreInterimEvents) {
			return;
		}

		ignoreInterimEvents = ignoreInterim;

		for (ProjectChangeListener listener : listeners) {
			listener.onProjectLoaded();
		}

		ignoreInterimEvents = false;
	}

}
