package us.wthr.jdem846.model.processing.render;

import us.wthr.jdem846.Projection;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.ProcessOption;

public class ModelRenderOptionModel implements OptionModel
{
	
	private String mapProjection;
	
	private Projection viewAngle; // TODO: Determine type
	
	
	
	public ModelRenderOptionModel()
	{
		
	}

	@ProcessOption(id="us.wthr.jdem846.model.ModelRenderOptionModel.mapProjection",
			label="Map Projection",
			tooltip="",
			enabled=true,
			listModel=MapProjectionListModel.class)
	public String getMapProjection()
	{
		return mapProjection;
	}


	public void setMapProjection(String mapProjection)
	{
		this.mapProjection = mapProjection;
	}




	@ProcessOption(id="us.wthr.jdem846.model.ModelRenderOptionModel.viewAngle",
			label="View Angle",
			tooltip="",
			enabled=true)
	public Projection getViewAngle()
	{
		return viewAngle;
	}


	public void setViewAngle(Projection viewAngle)
	{
		this.viewAngle = viewAngle;
	}

	

	
	
	
	
	
}
