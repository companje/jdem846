package us.wthr.jdem846ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class GeneralPreferencesPage extends BasicPreferencesPage
{
	private static Log log = Logging.getLog(GeneralPreferencesPage.class);
	public static final String ID = "jDem846UI.preferences.general";
	

	
	@Override
	protected void createFieldEditors()
	{
		
		addField(new ComboFieldEditor("us.wthr.jdem846.general.ui.i18n.default", "Language", new String[][] { { "English", "en" } }, getFieldEditorParent()));
		
		addField(new IntegerFieldEditor("us.wthr.jdem846.performance.tileSize", "Cache Size (Rows of Data)", getFieldEditorParent()));
		
		addField(new BooleanFieldEditor("us.wthr.jdem846.general.ui.jdemFrame.displayMemoryMonitor", "Display Memory Monitor", getFieldEditorParent()));
		addField(new BooleanFieldEditor("us.wthr.jdem846.general.ui.displayLogViewPanel", "Display Console", getFieldEditorParent()));
		
		addField(new BooleanFieldEditor("us.wthr.jdem846.general.ui.console.limitOuput", "Limit Console", getFieldEditorParent()));
		addField(new IntegerFieldEditor("us.wthr.jdem846.general.ui.console.bufferSize", "Max Console Length", getFieldEditorParent()));
		
		//this.getApplyButton().setEnabled(false);
//		addField(new DirectoryFieldEditor("PATH", "&Directory preference:",
//		        getFieldEditorParent()));
//		    addField(new BooleanFieldEditor("BOOLEAN_VALUE",
//		        "&An example of a boolean preference", getFieldEditorParent()));
//
//		    addField(new RadioGroupFieldEditor("CHOICE",
//		        "An example of a multiple-choice preference", 1,
//		        new String[][] { { "&Choice 1", "choice1" },
//		            { "C&hoice 2", "choice2" } }, getFieldEditorParent()));
//		    addField(new StringFieldEditor("MySTRING1", "A &text preference:",
//		        getFieldEditorParent()));
//		    addField(new StringFieldEditor("MySTRING2", "A &text preference:",
//		        getFieldEditorParent()));

	}
	
	



}
