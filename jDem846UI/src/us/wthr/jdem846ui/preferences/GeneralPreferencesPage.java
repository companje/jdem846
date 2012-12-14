package us.wthr.jdem846ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import us.wthr.jdem846ui.Activator;

public class GeneralPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	@Override
	public void init(IWorkbench arg0)
	{
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		
	}
	
	@Override
	protected void performDefaults()
	{
		super.performDefaults();
	}
	
	
	@Override
	public boolean performOk() 
	{
		
		return super.performOk();
	}

	@Override
	protected void createFieldEditors()
	{
		addField(new ComboFieldEditor("us.wthr.jdem846.general.ui.i18n.default", "Language", new String[][] { { "English", "en" } }, getFieldEditorParent()));
		
		addField(new BooleanFieldEditor("us.wthr.jdem846.general.ui.jdemFrame.displayMemoryMonitor", "Display Memory Monitor", getFieldEditorParent()));
		addField(new BooleanFieldEditor("us.wthr.jdem846.general.ui.displayLogViewPanel", "Display Console", getFieldEditorParent()));
		
		addField(new BooleanFieldEditor("us.wthr.jdem846.general.ui.console.limitOuput", "Limit Console", getFieldEditorParent()));
		addField(new IntegerFieldEditor("us.wthr.jdem846.general.ui.console.bufferSize", "Max Console Length", getFieldEditorParent()));
		
		
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
		    
		    
		    /*
		     * 
		     * private ComboBox cmbGeneralLanguage;   us.wthr.jdem846.general.ui.i18n.default
	private ComboBox cmbGeneralDefaultImageFormat;
	private JGoodiesColorThemeListModel colorThemeListModel;
	private ComboBox cmbGeneralColorTheme;
	private CheckBox chkGeneralDisplayToolbarText;
	private CheckBox chkGeneralAntialiasedScriptEditorText;
	private CheckBox chkGeneralDisplayMemoryMonitor;   us.wthr.jdem846.general.ui.jdemFrame.displayMemoryMonitor
	private CheckBox chkGeneralDisplayLogPanel;   us.wthr.jdem846.general.ui.displayLogViewPanel
	private CheckBox chkGeneralPreviewModelDuringRender;   us.wthr.jdem846.general.ui.renderInProcessPreviewing	
	private CheckBox chkGeneralLimitConsoleOutput; us.wthr.jdem846.general.ui.console.limitOuput
	private NumberTextField txtGeneralConsoleBufferSize; us.wthr.jdem846.general.ui.console.bufferSize
	private CheckBox chkGeneralReportUsage;
		     */
	}

}
