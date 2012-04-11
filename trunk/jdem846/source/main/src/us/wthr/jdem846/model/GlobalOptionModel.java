package us.wthr.jdem846.model;

import us.wthr.jdem846.model.annotations.Order;
import us.wthr.jdem846.model.annotations.ProcessOption;
import us.wthr.jdem846.model.listModels.ElevationScalerListModel;
import us.wthr.jdem846.model.listModels.PlanetListModel;
import us.wthr.jdem846.model.listModels.RenderProjectionListModel;
import us.wthr.jdem846.model.listModels.SubpixelGridSizeListModel;

public class GlobalOptionModel implements OptionModel
{
	
	private boolean useScripting;
	private int width;
	private int height;
	private boolean maintainAspectRatio;
	private String planet;
	private boolean estimateElevationRange;
	private boolean limitCoordinates;
	private double northLimit;
	private double southLimit;
	private double eastLimit;
	private double westLimit;
	private RgbaColor backgroundColor;
	private double elevationMultiple;
	private String elevationScale;
	private String renderProjection;
	private int subpixelGridSize;
	
	public GlobalOptionModel()
	{
		
	}
	
	
	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.useScripting",
					label="Use Scripting",
					tooltip="",
					enabled=true)
	@Order(0)
	public boolean getUseScripting()
	{
		return useScripting;
	}

	public void setUseScripting(boolean useScripting)
	{
		this.useScripting = useScripting;
	}

	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.width",
			label="Width",
			tooltip="",
			enabled=true)
	@Order(1)
	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.height",
			label="Height",
			tooltip="",
			enabled=true)
	@Order(2)
	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.maintainAspectRatio",
			label="Maintain Aspect Ratio",
			tooltip="",
			enabled=true)
	@Order(3)
	public boolean getMaintainAspectRatio()
	{
		return maintainAspectRatio;
	}

	public void setMaintainAspectRatio(boolean maintainAspectRatio)
	{
		this.maintainAspectRatio = maintainAspectRatio;
	}

	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.planet",
			label="Planet",
			tooltip="",
			enabled=true,
			listModel=PlanetListModel.class)
	@Order(4)
	public String getPlanet()
	{
		return planet;
	}

	public void setPlanet(String planet)
	{
		this.planet = planet;
	}

	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.estimateElevationRange",
			label="Estimate Elevation Min/Max",
			tooltip="",
			enabled=true)
	@Order(5)
	public boolean isEstimateElevationRange()
	{
		return estimateElevationRange;
	}

	public void setEstimateElevationRange(boolean estimateElevationRange)
	{
		this.estimateElevationRange = estimateElevationRange;
	}

	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.limitCoordinates",
			label="Limit Coordinates",
			tooltip="",
			enabled=true)
	@Order(6)
	public boolean getLimitCoordinates()
	{
		return limitCoordinates;
	}

	public void setLimitCoordinates(boolean limitCoordinates)
	{
		this.limitCoordinates = limitCoordinates;
	}
	
	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.northLimit",
			label="North Limit",
			tooltip="",
			enabled=true)
	@Order(7)
	public double getNorthLimit()
	{
		return northLimit;
	}

	public void setNorthLimit(double northLimit)
	{
		this.northLimit = northLimit;
	}
	
	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.southLimit",
			label="South Limit",
			tooltip="",
			enabled=true)
	@Order(8)
	public double getSouthLimit()
	{
		return southLimit;
	}

	public void setSouthLimit(double southLimit)
	{
		this.southLimit = southLimit;
	}
	
	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.eastLimit",
			label="East Limit",
			tooltip="",
			enabled=true)
	@Order(9)
	public double getEastLimit()
	{
		return eastLimit;
	}

	public void setEastLimit(double eastLimit)
	{
		this.eastLimit = eastLimit;
	}

	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.westLimit",
			label="West Limit",
			tooltip="",
			enabled=true)
	@Order(10)
	public double getWestLimit()
	{
		return westLimit;
	}

	public void setWestLimit(double westLimit)
	{
		this.westLimit = westLimit;
	}
	
	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.backgroundColor",
			label="Background Color",
			tooltip="",
			enabled=true)
	@Order(11)
	public RgbaColor getBackgroundColor()
	{
		return backgroundColor;
	}

	public void setBackgroundColor(RgbaColor backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}

	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.elevationMultiple",
			label="Elevation Multiple",
			tooltip="",
			enabled=true)
	@Order(12)
	public double getElevationMultiple()
	{
		return elevationMultiple;
	}

	public void setElevationMultiple(double elevationMultiple)
	{
		this.elevationMultiple = elevationMultiple;
	}

	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.elevationScale",
			label="Elevation Scale",
			tooltip="",
			enabled=true,
			listModel=ElevationScalerListModel.class)
	@Order(13)
	public String getElevationScale()
	{
		return elevationScale;
	}

	public void setElevationScale(String elevationScale)
	{
		this.elevationScale = elevationScale;
	}
	
	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.renderProjection",
			label="Render Projection",
			tooltip="",
			enabled=true,
			listModel=RenderProjectionListModel.class)
	@Order(14)
	public String getRenderProjection()
	{
		return renderProjection;
	}


	public void setRenderProjection(String renderProjection)
	{
		this.renderProjection = renderProjection;
	}

	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.subpixelGridSize",
			label="Subpixel Grid Size",
			tooltip="",
			enabled=true,
			listModel=SubpixelGridSizeListModel.class)
	@Order(15)
	public int getSubpixelGridSize()
	{
		return subpixelGridSize;
	}


	public void setSubpixelGridSize(int subpixelGridSize)
	{
		this.subpixelGridSize = subpixelGridSize;
	}
	
	
	// TODO: Remove the following options...
	
	private AzimuthElevationAngles sourceLocation; // TODO: Define a type
	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.sourceLocation",
			label="Source Location",
			tooltip="",
			enabled=true)
	@Order(16)
	public AzimuthElevationAngles getSourceLocation()
	{
		return sourceLocation;
	}

	public void setSourceLocation(AzimuthElevationAngles sourceLocation)
	{
		this.sourceLocation = sourceLocation;
	}
	
	
	
	private ViewPerspective viewAngle; // TODO: Determine type
	
	@ProcessOption(id="us.wthr.jdem846.model.ModelRenderOptionModel.viewAngle",
			label="View Angle",
			tooltip="",
			enabled=true)
	@Order(17)
	public ViewPerspective getViewAngle()
	{
		return viewAngle;
	}


	public void setViewAngle(ViewPerspective viewAngle)
	{
		this.viewAngle = viewAngle;
	}
	
}
