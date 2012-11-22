package us.wthr.jdem846ui.project;

import us.wthr.jdem846.model.OptionModelChangeEvent;

public interface ProjectChangeListener {
	
	public void onDataAdded();
	public void onDataRemoved();
	public void onOptionChanged(OptionModelChangeEvent e);
	
}
