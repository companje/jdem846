package us.wthr.jdem846.model.processing.coloring;

import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.ProcessOption;

public class TerrainRuggednessIndexColoringOptionModel implements OptionModel
{
	
	private int band = 1;
	private String colorTint = "tri-green-yellow-red";
	
	
	public TerrainRuggednessIndexColoringOptionModel()
	{
		
	}
	

	@ProcessOption(id="us.wthr.jdem846.model.TerrainRuggednessIndexColoringOptionModel.band",
			label="Band",
			tooltip="",
			enabled=true)
	public int getBand() 
	{
		return band;
	}



	public void setBand(int band)
	{
		this.band = band;
	}

	
	@ProcessOption(id="us.wthr.jdem846.model.TerrainRuggednessIndexColoringOptionModel.colorTint",
			label="Color Tinting",
			tooltip="",
			enabled=true,
			listModel=ColorTintsListModel.class)
	public String getColorTint()
	{
		return colorTint;
	}

	public void setColorTint(String colorTint)
	{
		this.colorTint = colorTint;
	}
	

	public TerrainRuggednessIndexColoringOptionModel copy()
	{
		TerrainRuggednessIndexColoringOptionModel copy = new TerrainRuggednessIndexColoringOptionModel();
		copy.band = this.band;
		copy.colorTint = this.colorTint.toString();
		return copy;
	}
	
}
