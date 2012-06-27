package us.wthr.jdem846.ui.optionModels;

import us.wthr.jdem846.image.ImageTypeEnum;
import us.wthr.jdem846.ui.base.JComboBoxModel;

public class ImageFormatListModel extends JComboBoxModel<String>
{
	
	
	public ImageFormatListModel()
	{

		for (ImageTypeEnum type : ImageTypeEnum.values()) {
			if (type.formatName() != null) {
				addItem(type.formatName(), type.formatName());
			}
		}
		
	}
	
}
