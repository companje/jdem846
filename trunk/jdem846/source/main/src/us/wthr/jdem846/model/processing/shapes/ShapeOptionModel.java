package us.wthr.jdem846.model.processing.shapes;

import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.Order;
import us.wthr.jdem846.model.annotations.ProcessOption;

public class ShapeOptionModel implements OptionModel
{

	private boolean enabled = true;
	
	public ShapeOptionModel()
	{
		
	}
	
	
	
	@ProcessOption(id = "us.wthr.jdem846.model.processing.coloring.HillshadingOptionModel.enabled"
					, label = "Enabled"
					, tooltip = ""
					, visible = true)
	@Order(100)
	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	@Override
	public ShapeOptionModel copy()
	{
		ShapeOptionModel copy = new ShapeOptionModel();
		copy.enabled = this.enabled;
		return copy;
	}

}
