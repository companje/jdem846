package us.wthr.jdem846.model.processing.coloring;

import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.ProcessOption;

public class TerrainRuggednessIndexColoringOptionModel implements OptionModel
{
	
	private int band = 1;
	
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



	public TerrainRuggednessIndexColoringOptionModel copy()
	{
		TerrainRuggednessIndexColoringOptionModel copy = new TerrainRuggednessIndexColoringOptionModel();
		return copy;
	}
	
}
