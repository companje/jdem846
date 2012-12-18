package us.wthr.jdem846ui.preferences;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846ui.Activator;

public abstract class BasicPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	private static Log log = Logging.getLog(BasicPreferencesPage.class);
	
	private List<String> managedProperties = new LinkedList<String>();
	
	
	public BasicPreferencesPage()
	{
		super(FieldEditorPreferencePage.GRID);
	}
	
	@Override
	public void init(IWorkbench workbench)
	{
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}
	
	
	@Override
	protected void performDefaults()
	{
		super.performDefaults();
	}
	
	
	@Override
	protected void performApply()
	{
		super.performApply();
		applyProperties();
		
		this.getApplyButton().setEnabled(false);
	}
	
	@Override
	public boolean performOk() 
	{
		applyProperties();
		return super.performOk();
	}
	
	
	protected void applyProperties()
	{
		for (String property : managedProperties) {
			applyProperty(property);
		}
	}
	
	
	protected void applyProperty(String id)
	{
		JDem846Properties.setProperty(id, getPreferenceStore().getString(id));
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		log.info("Property Changed: " + event.getProperty());
		this.getApplyButton().setEnabled(true);
	}
	
	@Override
	public void addField(FieldEditor editor)
	{
		managedProperties.add(editor.getPreferenceName());
		super.addField(editor);
	}
}
