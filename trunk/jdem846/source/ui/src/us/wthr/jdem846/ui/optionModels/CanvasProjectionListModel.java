package us.wthr.jdem846.ui.optionModels;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.canvas.CanvasProjectionTypeEnum;
import us.wthr.jdem846.ui.base.JComboBoxModel;

public class CanvasProjectionListModel extends JComboBoxModel<String>
{
	public CanvasProjectionListModel()
	{
		for (CanvasProjectionTypeEnum projectionEnum : CanvasProjectionTypeEnum.values()) {
			addItem(I18N.get(projectionEnum.projectionName()), projectionEnum.identifier());
		}
	}
}
