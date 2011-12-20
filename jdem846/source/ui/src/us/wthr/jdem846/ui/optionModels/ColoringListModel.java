package us.wthr.jdem846.ui.optionModels;

import java.util.List;

import us.wthr.jdem846.color.ColoringInstance;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.ui.base.JComboBoxModel;

public class ColoringListModel extends JComboBoxModel<String>
{
	
	public ColoringListModel()
	{
		List<ColoringInstance> colorings = ColoringRegistry.getInstances();
		for (ColoringInstance colorInstance : colorings) {
			addItem(colorInstance.getName(), colorInstance.getIdentifier());
		}
	}
	
}