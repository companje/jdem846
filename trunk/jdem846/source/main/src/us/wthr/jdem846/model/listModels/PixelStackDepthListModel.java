package us.wthr.jdem846.model.listModels;

import us.wthr.jdem846.model.OptionListModel;

public class PixelStackDepthListModel extends OptionListModel<Integer>
{
	
	public PixelStackDepthListModel()
	{
		
		
		for (int i = 1; i <= 32; i++) {
			addItem(""+i, i);
		}
		
		addItem("Unlimited", 0);
	}
	
}
