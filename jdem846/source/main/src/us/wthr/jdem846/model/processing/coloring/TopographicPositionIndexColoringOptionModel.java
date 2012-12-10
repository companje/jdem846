package us.wthr.jdem846.model.processing.coloring;

import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.ProcessOption;

public class TopographicPositionIndexColoringOptionModel implements OptionModel
{

	private int band = 1;
	private String colorTint = "tri-green-yellow-red";

	public TopographicPositionIndexColoringOptionModel()
	{

	}

	@ProcessOption(id = "us.wthr.jdem846.model.TopographicPositionIndexColoringOptionModel.band", label = "Band", tooltip = "", visible = true)
	public int getBand()
	{
		return band;
	}

	public void setBand(int band)
	{
		this.band = band;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.TopographicPositionIndexColoringOptionModel.colorTint", label = "Color Tinting", tooltip = "", visible = true, listModel = ColorTintsListModel.class)
	public String getColorTint()
	{
		return colorTint;
	}

	public void setColorTint(String colorTint)
	{
		this.colorTint = colorTint;
	}

	public TopographicPositionIndexColoringOptionModel copy()
	{
		TopographicPositionIndexColoringOptionModel copy = new TopographicPositionIndexColoringOptionModel();
		copy.band = this.band;
		copy.colorTint = this.colorTint.toString();
		return copy;
	}

}
