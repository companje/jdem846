package us.wthr.jdem846ui.preferences;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class DefaultsPreferencesPage extends BasicPreferencesPage
{
	private static Log log = Logging.getLog(DefaultsPreferencesPage.class);
	public static final String ID = "jDem846UI.preferences.defaults";
	
	private static String[][] displayProperties = {
		{ "us.wthr.jdem846.defaults.author", "us.wthr.jdem846.ui.preferencesDialog.defaults.author" },
		{ "us.wthr.jdem846.defaults.author-contact", "us.wthr.jdem846.ui.preferencesDialog.defaults.author-contact" },
		{ "us.wthr.jdem846.defaults.institution", "us.wthr.jdem846.ui.preferencesDialog.defaults.institution" },
		{ "us.wthr.jdem846.defaults.institution-contact", "us.wthr.jdem846.ui.preferencesDialog.defaults.institution-contact" },
		{ "us.wthr.jdem846.defaults.institution-address", "us.wthr.jdem846.ui.preferencesDialog.defaults.institution-address" },
		{ "us.wthr.jdem846.defaults.subject", "us.wthr.jdem846.ui.preferencesDialog.defaults.subject" },
		{ "us.wthr.jdem846.defaults.description", "us.wthr.jdem846.ui.preferencesDialog.defaults.description" }
	};
	
	@Override
	public void init(IWorkbench workbench)
	{
		super.init(workbench);
		setDescription("Allows the setting of default values which will be applied to rendered elevation model properties.");
	}
	
	@Override
	protected void createFieldEditors()
	{
		for (String[] property : displayProperties) {
			addField(new StringFieldEditor(property[0], I18N.get(property[1]) + ":", getFieldEditorParent()));
		}
	}

}
