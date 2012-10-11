package us.wthr.jdem846.model.listModels;

import us.wthr.jdem846.graphics.PerspectiveTypeEnum;
import us.wthr.jdem846.model.OptionListModel;

public class PerspectiveTypeListModel extends OptionListModel<String>
{
	
	public PerspectiveTypeListModel()
	{
		
		for (PerspectiveTypeEnum item : PerspectiveTypeEnum.values()) {
			addItem(item.perspectiveName(), item.identifier());
		}
	}
	
}
