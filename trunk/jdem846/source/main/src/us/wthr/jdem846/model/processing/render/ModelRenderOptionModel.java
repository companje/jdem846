package us.wthr.jdem846.model.processing.render;


import us.wthr.jdem846.gis.projections.MapProjectionEnum;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.model.annotations.ProcessOption;

public class ModelRenderOptionModel implements OptionModel
{
	
	private String mapProjection = MapProjectionEnum.EQUIRECTANGULAR.identifier();
	private ViewPerspective viewAngle = ViewPerspective.fromString("rotate:[30.0,0,0];shift:[0,0,0];zoom:[1.0]");
	
	
	
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
	public ViewPerspective getViewAngle()
	{
		return viewAngle;
	}


	public void setViewAngle(ViewPerspective viewAngle)
	{
		this.viewAngle = viewAngle;
	}

	
	public ModelRenderOptionModel copy()
	{
		ModelRenderOptionModel copy = new ModelRenderOptionModel();
		
		copy.mapProjection = this.mapProjection;
		copy.viewAngle = this.viewAngle.copy();
		
		return copy;
	}
	
	
	
	
	
}
