package us.wthr.jdem846.ui.options;

import us.wthr.jdem846.model.OptionModelChangeEvent;

public interface ModelConfigurationChangeListener
{
	
	public void onProcessSelected(String processId);
	public void onPropertyChanged(OptionModelChangeEvent e);
}
