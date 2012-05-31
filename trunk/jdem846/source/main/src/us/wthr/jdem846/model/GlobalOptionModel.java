package us.wthr.jdem846.model;

import us.wthr.jdem846.canvas.CanvasProjectionTypeEnum;
import us.wthr.jdem846.model.annotations.Order;
import us.wthr.jdem846.model.annotations.ProcessOption;
import us.wthr.jdem846.model.annotations.ValueBounds;
import us.wthr.jdem846.model.listModels.ElevationScalerListModel;
import us.wthr.jdem846.model.listModels.PixelStackDepthListModel;
import us.wthr.jdem846.model.listModels.PlanetListModel;
import us.wthr.jdem846.model.listModels.RenderProjectionListModel;
import us.wthr.jdem846.model.listModels.SubpixelGridSizeListModel;
import us.wthr.jdem846.model.processing.ModelHeightWidthValidator;
import us.wthr.jdem846.scaling.ElevationScalerEnum;

public class GlobalOptionModel implements OptionModel
{
	
	private boolean useScripting = false;
	private int width = 2000;
	private int height = 2000;
	private boolean maintainAspectRatio = true;
	private String planet = "Earth";
	private boolean estimateElevationRange = true;
	private boolean limitCoordinates = false;
	private double northLimit = 90.0;
	private double southLimit = -90.0;
	private double eastLimit = 180.0;
	private double westLimit = -180.0;
	private RgbaColor backgroundColor = new RgbaColor(255, 255, 255, 255);
	private double elevationMultiple = 1.0;
	private String elevationScale = ElevationScalerEnum.LINEAR.identifier();
	private String renderProjection = CanvasProjectionTypeEnum.PROJECT_FLAT.identifier();
	private int subpixelGridSize = 1;
	private int pixelStackDepth = 1;
	
	private double latitudeSlices = -1;
	private double longitudeSlices = -1;
	
	private boolean getStandardResolutionElevation = false;
	private boolean interpolateData = true;
	private boolean averageOverlappedData = true;
	private String precacheStrategy = "tiled";
	
	private boolean useDiskCachedModelGrid = false;
	private boolean disposeGridOnComplete = true;
	
	public GlobalOptionModel()
	{
		
	}
	
	
	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.useScripting",
					label="Use Scripting",
					tooltip="Enable scripting during the modeling process",
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
			tooltip="Model image width",
			enabled=true,
			validator=ModelHeightWidthValidator.class)
	@Order(1)
	@ValueBounds(minimum=1)
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
			tooltip="Model image height",
			enabled=true)
	@Order(2)
	@ValueBounds(minimum=1)
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
			tooltip="Maintain model dimensions aspect ratio in relation to raster data bounds",
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
	@ValueBounds(minimum=-90.0, 
			maximum=90.0, 
			stepSize=.1)
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
	@ValueBounds(minimum=-90.0, 
			maximum=90.0, 
			stepSize=.1)
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
	@ValueBounds(minimum=-360.0, 
			maximum=360.0, 
			stepSize=.1)
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
	@ValueBounds(minimum=-360.0, 
			maximum=360.0, 
			stepSize=.1)
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
			tooltip="Model image background color",
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
	@ValueBounds(minimum=0,
				stepSize=0.1)
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
	@ValueBounds(minimum=1, 
				maximum=16)
	public int getSubpixelGridSize()
	{
		return subpixelGridSize;
	}


	public void setSubpixelGridSize(int subpixelGridSize)
	{
		this.subpixelGridSize = subpixelGridSize;
	}
	
	
	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.pixelStackDepth",
			label="Pixel Stack Depth",
			tooltip="",
			enabled=true,
			listModel=PixelStackDepthListModel.class)
	@Order(16)
	@ValueBounds(minimum=0, 
				maximum=32)
	public int getPixelStackDepth()
	{
		return pixelStackDepth;
	}


	public void setPixelStackDepth(int pixelStackDepth)
	{
		this.pixelStackDepth = pixelStackDepth;
	}


	public double getLatitudeSlices()
	{
		return latitudeSlices;
	}


	public void setLatitudeSlices(double latitudeSlices)
	{
		this.latitudeSlices = latitudeSlices;
	}


	public double getLongitudeSlices()
	{
		return longitudeSlices;
	}


	public void setLongitudeSlices(double longitudeSlices)
	{
		this.longitudeSlices = longitudeSlices;
	}

	

	public boolean getStandardResolutionElevation()
	{
		return getStandardResolutionElevation;
	}


	public void setGetStandardResolutionElevation(
			boolean getStandardResolutionElevation)
	{
		this.getStandardResolutionElevation = getStandardResolutionElevation;
	}


	public boolean getInterpolateData()
	{
		return interpolateData;
	}


	public void setInterpolateData(boolean interpolateData)
	{
		this.interpolateData = interpolateData;
	}


	public boolean getAverageOverlappedData()
	{
		return averageOverlappedData;
	}


	public void setAverageOverlappedData(boolean averageOverlappedData)
	{
		this.averageOverlappedData = averageOverlappedData;
	}
	
	


	public String getPrecacheStrategy()
	{
		return precacheStrategy;
	}


	public void setPrecacheStrategy(String precacheStrategy)
	{
		this.precacheStrategy = precacheStrategy;
	}

	
	
	@ProcessOption(id="us.wthr.jdem846.model.GlobalOptionModel.useDiskCachedModelGrid",
			label="Use Disk Cache",
			tooltip="",
			enabled=true)
	@Order(16)
	public boolean getUseDiskCachedModelGrid()
	{
		return useDiskCachedModelGrid;
	}


	public void setUseDiskCachedModelGrid(boolean useDiskCachedModelGrid)
	{
		this.useDiskCachedModelGrid = useDiskCachedModelGrid;
	}

	
	
	
	public boolean getDisposeGridOnComplete()
	{
		return disposeGridOnComplete;
	}


	public void setDisposeGridOnComplete(boolean disposeGridOnComplete)
	{
		this.disposeGridOnComplete = disposeGridOnComplete;
	}


	public GlobalOptionModel copy()
	{
		GlobalOptionModel copy = new GlobalOptionModel();
		
		copy.useScripting = this.useScripting;
		copy.width = this.width;
		copy.height = this.height;
		copy.maintainAspectRatio = this.maintainAspectRatio; 
		copy.planet = this.planet;
		copy.estimateElevationRange = this.estimateElevationRange; 
		copy.limitCoordinates = this.limitCoordinates;
		copy.northLimit = this.northLimit;
		copy.southLimit = this.southLimit;
		copy.eastLimit = this.eastLimit;
		copy.westLimit = this.westLimit;
		copy.backgroundColor = this.backgroundColor.copy();
		copy.elevationMultiple = this.elevationMultiple;
		copy.elevationScale = this.elevationScale; 
		copy.renderProjection = this.renderProjection;
		copy.subpixelGridSize = this.subpixelGridSize;
		copy.pixelStackDepth = this.pixelStackDepth;
		copy.longitudeSlices = this.longitudeSlices;
		copy.latitudeSlices = this.latitudeSlices;
		copy.averageOverlappedData = this.averageOverlappedData;
		copy.getStandardResolutionElevation = this.getStandardResolutionElevation;
		copy.interpolateData = this.interpolateData;
		copy.precacheStrategy = this.precacheStrategy;
		copy.useDiskCachedModelGrid = this.useDiskCachedModelGrid;
		return copy;
	}
	
}
