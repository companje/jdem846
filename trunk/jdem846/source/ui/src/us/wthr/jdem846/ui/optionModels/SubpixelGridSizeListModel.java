package us.wthr.jdem846.ui.optionModels;

import us.wthr.jdem846.ui.base.JComboBoxModel;

public class SubpixelGridSizeListModel extends JComboBoxModel<Integer>
{
	
	
	public SubpixelGridSizeListModel()
	{
		addItem("1x1 (off)", 1);
		addItem("2x2", 2);
		addItem("4x4", 4);
		addItem("8x8", 8);
	}
	
}
