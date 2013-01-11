package us.wthr.jdem846ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ScaleFieldEditor;

import us.wthr.jdem846.math.MathExt;

public class PreviewingPreferencesPage extends BasicPreferencesPage
{
	public static final String ID = "jDem846UI.preferences.previewing";

	@Override
	protected void createFieldEditors()
	{
		
		//addField(new DoubleFieldEditor("us.wthr.jdem846.general.ui.console.bufferSize", "Max Console Length", getFieldEditorParent()));
		
		addField(new BooleanFieldEditor("us.wthr.jdem846.previewing.ui.autoUpdate", "Auto Update", getFieldEditorParent()));
		addField(new BooleanFieldEditor("us.wthr.jdem846.previewing.ui.scripting", "Use Scripting", getFieldEditorParent()));
		
		addField(new BooleanFieldEditor("us.wthr.jdem846.previewing.ui.standardResolutionRetrieval", "Nearest-Neighbor Data Retrieval (Preview)", getFieldEditorParent()));
		addField(new BooleanFieldEditor("us.wthr.jdem846.previewing.ui.interpolateToHigherResolution", "Interpolate Lower Resolution Data (Preview)", getFieldEditorParent()));
		addField(new BooleanFieldEditor("us.wthr.jdem846.previewing.ui.averageOverlappedData", "Average Overlapping Data (Preview)", getFieldEditorParent()));
		
		
		IPropertyModifier qualityValueModifier = new IPropertyModifier() {
			@Override
			public String onPropertyFromPreference(String propertyId, String value)
			{
				double d = Double.parseDouble(value);
				return ""+(d / 100.0);
			}
			
			@Override
			public String onPreferenceFromProperty(String propertyId, String value)
			{
				double d = Double.parseDouble(value);
				if (d <= 1.0) {
					d *= 100.0;
				}
				return (new Integer((int)MathExt.round((d)))).toString();
			}
		};
		
		ScaleFieldEditor previewTextureQuality = new ScaleFieldEditor("us.wthr.jdem846.previewing.ui.previewTextureQuality", "Texture Quality", getFieldEditorParent());
		previewTextureQuality.setMaximum(100);
		previewTextureQuality.setMinimum(2);
		previewTextureQuality.setIncrement(5);
		previewTextureQuality.setPageIncrement(10);
		addField(previewTextureQuality, qualityValueModifier);
		
		
		ScaleFieldEditor previewModelQuality = new ScaleFieldEditor("us.wthr.jdem846.previewing.ui.previewModelQuality", "Model Quality", getFieldEditorParent());
		previewModelQuality.setMaximum(100);
		previewModelQuality.setMinimum(2);
		previewModelQuality.setIncrement(5);
		previewModelQuality.setPageIncrement(10);
		addField(previewModelQuality, qualityValueModifier);
		
		/*
		 * us.wthr.jdem846.previewing.ui.previewModelQuality=0.15
us.wthr.jdem846.previewing.ui.previewTextureQuality=0.15
us.wthr.jdem846.previewing.ui.rasterPreview=true
us.wthr.jdem846.previewing.ui.standardResolutionRetrieval=true
us.wthr.jdem846.previewing.ui.interpolateToHigherResolution=false
us.wthr.jdem846.previewing.ui.averageOverlappedData=false
us.wthr.jdem846.previewing.ui.maxPreviewSlices=300
us.wthr.jdem846.previewing.ui.minPreviewSlices=10
us.wthr.jdem846.previewing.ui.paintBaseGrid=true
us.wthr.jdem846.previewing.ui.paintLightSourceLines=true
us.wthr.jdem846.previewing.ui.autoUpdate=true
us.wthr.jdem846.previewing.ui.scripting=true
		 */
		
	}
	


}
