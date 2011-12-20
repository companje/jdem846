package us.wthr.jdem846.ui.optionModels;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.lighting.LightSourceSpecifyTypeEnum;
import us.wthr.jdem846.ui.base.JComboBoxModel;

public class LightSourceSpecifyTypeListModel extends JComboBoxModel<LightSourceSpecifyTypeEnum>
{
	
	
	
	public LightSourceSpecifyTypeListModel()
	{
		addItem(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.sourceSelect.azimuthAndElevation"), LightSourceSpecifyTypeEnum.BY_AZIMUTH_AND_ELEVATION);
		addItem(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.sourceSelect.dateAndTime"), LightSourceSpecifyTypeEnum.BY_DATE_AND_TIME);
	}
	
}
