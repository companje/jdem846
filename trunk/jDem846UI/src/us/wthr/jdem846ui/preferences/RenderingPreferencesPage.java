package us.wthr.jdem846ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;


public class RenderingPreferencesPage extends BasicPreferencesPage
{
	
	public static final String ID = "jDem846UI.preferences.rendering";

	@Override
	protected void createFieldEditors()
	{
		
		addField(new ComboFieldEditor("us.wthr.jdem846.rendering.renderEngine", "Render Engine", new String[][] { { "Software", "software" }, { "OpenGL", "opengl" } }, getFieldEditorParent()));
		
		addField(new BooleanFieldEditor("us.wthr.jdem846.rendering.opengl.multisampling.enabled", "OpenGL Multisampling", getFieldEditorParent()));
		addField(new IntegerFieldEditor("us.wthr.jdem846.rendering.opengl.multisampling.samples", "Samples per Pixel", getFieldEditorParent()));
		
		addField(new BooleanFieldEditor("us.wthr.jdem846.rendering.standardResolutionRetrieval", "Nearest-Neighbor Data Retrieval", getFieldEditorParent()));
		addField(new BooleanFieldEditor("us.wthr.jdem846.rendering.interpolateToHigherResolution", "Interpolate Lower Resolution Data", getFieldEditorParent()));
		addField(new BooleanFieldEditor("us.wthr.jdem846.rendering.averageOverlappedData", "Average Overlapping Data", getFieldEditorParent()));
		
	}



}
