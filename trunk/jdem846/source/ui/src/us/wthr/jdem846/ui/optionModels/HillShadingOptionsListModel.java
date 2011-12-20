package us.wthr.jdem846.ui.optionModels;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.ui.base.JComboBoxModel;

public class HillShadingOptionsListModel extends JComboBoxModel<Integer>
{
	
	public HillShadingOptionsListModel()
	{
		addItem(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.hillShadeOptions.lighten"), DemConstants.HILLSHADING_LIGHTEN);
		addItem(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.hillShadeOptions.darken"), DemConstants.HILLSHADING_DARKEN);
		addItem(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.hillShadeOptions.combined"), DemConstants.HILLSHADING_COMBINED);
		addItem(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.hillShadeOptions.none"), DemConstants.HILLSHADING_NONE);
	}
}
