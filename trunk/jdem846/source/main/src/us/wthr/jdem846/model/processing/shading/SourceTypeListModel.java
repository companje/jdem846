package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.lighting.LightSourceSpecifyTypeEnum;
import us.wthr.jdem846.model.OptionListModel;

public class SourceTypeListModel extends OptionListModel<String>
{
	
	public SourceTypeListModel()
	{
		addItem(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.sourceSelect.azimuthAndElevation"), LightSourceSpecifyTypeEnum.BY_AZIMUTH_AND_ELEVATION.optionValue());
		addItem(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.sourceSelect.dateAndTime"), LightSourceSpecifyTypeEnum.BY_DATE_AND_TIME.optionValue());
	}
	
}
