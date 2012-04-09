package us.wthr.jdem846.model.listModels;

import us.wthr.jdem846.model.OptionListModel;

public class SubpixelGridSizeListModel extends OptionListModel<Integer>
{
	
	public SubpixelGridSizeListModel()
	{
		addItem("1x1 (off)", 1);
		addItem("2x2", 2);
		addItem("4x4", 4);
		addItem("8x8", 8);
	}
	
}
