package us.wthr.jdem846.model.processing.render;

import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.Order;
import us.wthr.jdem846.model.annotations.ProcessOption;
import us.wthr.jdem846.model.annotations.ValueBounds;

public class ModelRenderOptionModel implements OptionModel
{

	private int forceAlpha = 255;

	public ModelRenderOptionModel()
	{

	}

	@ProcessOption(id = "us.wthr.jdem846.model.ModelRenderOptionModel.forceAlpha", label = "Model Transparency", tooltip = "", visible = true)
	@ValueBounds(minimum = 0, maximum = 255)
	@Order(300)
	public int getForceAlpha()
	{
		return forceAlpha;
	}

	public void setForceAlpha(int forceAlpha)
	{
		this.forceAlpha = forceAlpha;
	}

	public ModelRenderOptionModel copy()
	{
		ModelRenderOptionModel copy = new ModelRenderOptionModel();

		copy.forceAlpha = this.forceAlpha;

		return copy;
	}

}
