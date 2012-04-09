package us.wthr.jdem846.model.processing.render;

import us.wthr.jdem846.gis.projections.MapProjectionEnum;
import us.wthr.jdem846.model.OptionListModel;

public class MapProjectionListModel extends OptionListModel<String>
{
	
	public MapProjectionListModel()
	{
		for (MapProjectionEnum projectionEnum : MapProjectionEnum.values()) {
			addItem(projectionEnum.projectionName(), projectionEnum.identifier());
		}
	}
	
}
