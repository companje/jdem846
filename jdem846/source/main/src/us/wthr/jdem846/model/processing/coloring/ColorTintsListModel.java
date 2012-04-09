package us.wthr.jdem846.model.processing.coloring;

import java.util.List;

import us.wthr.jdem846.color.ColoringInstance;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.model.OptionListModel;

public class ColorTintsListModel extends OptionListModel<String>
{
	
	public ColorTintsListModel()
	{
		List<ColoringInstance> colorings = ColoringRegistry.getInstances();
		for (ColoringInstance colorInstance : colorings) {
			addItem(colorInstance.getName(), colorInstance.getIdentifier());
		}
	}
	
	
}
