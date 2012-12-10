package us.wthr.jdem846.model.processing.coloring;

import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.ProcessOption;

public class RoughnessColoringOptionModel implements OptionModel
{
	private int band = 1;
	private String colorTint = "tri-green-yellow-red";

	public RoughnessColoringOptionModel()
	{

	}

	@ProcessOption(id = "us.wthr.jdem846.model.RoughnessColoringOptionModel.band", label = "Band", tooltip = "", visible = true)
	public int getBand()
	{
		return band;
	}

	public void setBand(int band)
	{
		this.band = band;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.RoughnessColoringOptionModel.colorTint", label = "Color Tinting", tooltip = "", visible = true, listModel = ColorTintsListModel.class)
	public String getColorTint()
	{
		return colorTint;
	}

	public void setColorTint(String colorTint)
	{
		this.colorTint = colorTint;
	}

	public RoughnessColoringOptionModel copy()
	{
		RoughnessColoringOptionModel copy = new RoughnessColoringOptionModel();
		copy.band = this.band;
		copy.colorTint = this.colorTint.toString();
		return copy;
	}

}
