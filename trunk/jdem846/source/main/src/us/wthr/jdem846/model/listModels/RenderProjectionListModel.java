package us.wthr.jdem846.model.listModels;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.model.OptionListModel;
import us.wthr.jdem846.canvas.CanvasProjectionTypeEnum;

public class RenderProjectionListModel extends OptionListModel<String>
{
	
	public RenderProjectionListModel()
	{
		for (CanvasProjectionTypeEnum projectionEnum : CanvasProjectionTypeEnum.values()) {
			addItem(I18N.get(projectionEnum.projectionName()), projectionEnum.identifier());
		}
	}
	
	
}
