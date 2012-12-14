package us.wthr.jdem846ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import us.wthr.jdem846.JDem846Properties;

public class ApplicationPreferenceInitializer extends AbstractPreferenceInitializer
{

	@Override
	public void initializeDefaultPreferences()
	{
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault("us.wthr.jdem846.general.ui.jdemFrame.displayMemoryMonitor", true);
		
		for (Object key : JDem846Properties.getPropertyNames()) {
			String sKey = (String) key;
			store.setValue(sKey, JDem846Properties.getProperty(sKey));
		}

		
	}

}
