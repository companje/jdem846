package us.wthr.jdem846ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;


public class RenderingPreferencesPage extends BasicPreferencesPage
{
	
	public static final String ID = "jDem846UI.preferences.rendering";

	
	private ComboFieldEditor renderEngineEditor;
	private BooleanFieldEditor multisamplingEditor;
	private IntegerFieldEditor samplesPerPixelEditor;
	private BooleanFieldEditor nearestNeighborEditor;
	private BooleanFieldEditor interpolateEditor;
	private BooleanFieldEditor averageOverlappingEditor;
	private IntegerFieldEditor maxTextureSizeEditor;
	
	@Override
	protected void createFieldEditors()
	{
		
		
		renderEngineEditor = new ComboFieldEditor("us.wthr.jdem846.rendering.renderEngine", "Render Engine", new String[][] { { "Software", "software" }, { "OpenGL", "opengl" } }, getFieldEditorParent());
		addField(renderEngineEditor);
		
		
		multisamplingEditor = new BooleanFieldEditor("us.wthr.jdem846.rendering.opengl.multisampling.enabled", "OpenGL Multisampling", getFieldEditorParent());
		addField(multisamplingEditor);
		
		samplesPerPixelEditor = new IntegerFieldEditor("us.wthr.jdem846.rendering.opengl.multisampling.samples", "Samples per Pixel", getFieldEditorParent());
		addField(samplesPerPixelEditor);
		
		
		
		nearestNeighborEditor = new BooleanFieldEditor("us.wthr.jdem846.rendering.standardResolutionRetrieval", "Nearest-Neighbor Data Retrieval", getFieldEditorParent());
		addField(nearestNeighborEditor);
		
		interpolateEditor = new BooleanFieldEditor("us.wthr.jdem846.rendering.interpolateToHigherResolution", "Interpolate Lower Resolution Data", getFieldEditorParent());
		addField(interpolateEditor);
		
		averageOverlappingEditor = new BooleanFieldEditor("us.wthr.jdem846.rendering.averageOverlappedData", "Average Overlapping Data", getFieldEditorParent());
		addField(averageOverlappingEditor);
		
		maxTextureSizeEditor = new IntegerFieldEditor("us.wthr.jdem846.rendering.maxTextureSize", "Max Texture Size", getFieldEditorParent());
		addField(maxTextureSizeEditor);
		
		updateControlState();
	}

	@Override
	protected void updateControlState()
	{
		IPreferenceStore preferenceStore = getPreferenceStore();
		
		if (preferenceStore.getString("us.wthr.jdem846.rendering.renderEngine").equalsIgnoreCase("opengl")) {
			multisamplingEditor.setEnabled(true, getFieldEditorParent());
			samplesPerPixelEditor.setEnabled(true, getFieldEditorParent());
		} else {
			multisamplingEditor.setEnabled(false, getFieldEditorParent());
			samplesPerPixelEditor.setEnabled(false, getFieldEditorParent());
		}
	}


}
