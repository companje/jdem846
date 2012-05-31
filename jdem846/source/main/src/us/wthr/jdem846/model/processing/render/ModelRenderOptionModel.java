package us.wthr.jdem846.model.processing.render;


import us.wthr.jdem846.gis.projections.MapProjectionEnum;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.model.annotations.Order;
import us.wthr.jdem846.model.annotations.ProcessOption;
import us.wthr.jdem846.model.annotations.ValueBounds;

public class ModelRenderOptionModel implements OptionModel
{
	
	private String mapProjection = MapProjectionEnum.EQUIRECTANGULAR.identifier();
	private ViewPerspective viewAngle = ViewPerspective.fromString("rotate:[30.0,0,0];shift:[0,0,0];zoom:[1.0]");
	private int forceAlpha = 255;
	
	
	
	public ModelRenderOptionModel()
	{
		
	}

	@ProcessOption(id="us.wthr.jdem846.model.ModelRenderOptionModel.mapProjection",
			label="Map Projection",
			tooltip="",
			enabled=true,
			listModel=MapProjectionListModel.class)
	@Order(100)
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
	@Order(200)
	public ViewPerspective getViewAngle()
	{
		return viewAngle;
	}


	public void setViewAngle(ViewPerspective viewAngle)
	{
		this.viewAngle = viewAngle;
	}

	
	
	@ProcessOption(id="us.wthr.jdem846.model.ModelRenderOptionModel.forceAlpha",
			label="Model Transparency",
			tooltip="",
			enabled=true)
	@ValueBounds(minimum=0, 
				maximum=255)
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
		
		copy.mapProjection = this.mapProjection;
		copy.viewAngle = this.viewAngle.copy();
		copy.forceAlpha = this.forceAlpha;
		
		return copy;
	}
	
	
	
	
	
}
