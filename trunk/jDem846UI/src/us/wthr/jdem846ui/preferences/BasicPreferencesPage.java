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
	
	private List<ManagedProperty> managedProperties = new LinkedList<ManagedProperty>();
	
	
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
		updateControlState();
	}
	
	
	@Override
	protected void performApply()
	{
		super.performApply();
		applyProperties();
		updateControlState();
		this.getApplyButton().setEnabled(false);
	}
	
	@Override
	public boolean performOk() 
	{
		applyProperties();
		updateControlState();
		return super.performOk();
	}
	
	
	protected void applyProperties()
	{
		for (ManagedProperty property : managedProperties) {
			applyProperty(property);
		}
	}
	
	protected void updateControlState()
	{
		
	}
	
	protected void applyProperty(ManagedProperty property )
	{
		JDem846Properties.setProperty(property.propertyId, property.getValue());
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		log.info("Property Changed: " + event.getProperty());
		this.getApplyButton().setEnabled(true);
		updateControlState();
	}
	
	public void addField(FieldEditor editor, IPropertyModifier modifier)
	{
		managedProperties.add(new ManagedProperty(editor, modifier));
		
		if (modifier != null) {
			String value = getPreferenceStore().getString(editor.getPreferenceName());
			value = modifier.onPreferenceFromProperty(editor.getPreferenceName(), value);
			getPreferenceStore().setValue(editor.getPreferenceName(), value);
		}
		
		super.addField(editor);
	}
	
	@Override
	public void addField(FieldEditor editor)
	{
		addField(editor, null);
	}
	
	
	
	protected interface IPropertyModifier
	{
		public String onPreferenceFromProperty(String propertyId, String value);
		public String onPropertyFromPreference(String propertyId, String value);
	}
	
	protected class ManagedProperty
	{
		String propertyId;
		IPropertyModifier modifier;
		FieldEditor fieldEditor;
		
		public ManagedProperty(FieldEditor fieldEditor, IPropertyModifier modifier)
		{
			this.fieldEditor = fieldEditor;
			this.propertyId = fieldEditor.getPreferenceName();
			this.modifier = modifier;
		}
		
		
		public String getValue()
		{
			String value = getPreferenceStore().getString(propertyId);
			if (modifier != null) {
				value = modifier.onPropertyFromPreference(propertyId, value);
			}
			return value;
		}
		
	}
	
}
