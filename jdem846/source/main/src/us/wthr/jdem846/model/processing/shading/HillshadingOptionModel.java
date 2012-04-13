package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.lighting.LightSourceSpecifyTypeEnum;
import us.wthr.jdem846.model.AzimuthElevationAngles;
import us.wthr.jdem846.model.LightingDate;
import us.wthr.jdem846.model.LightingTime;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.Order;
import us.wthr.jdem846.model.annotations.ProcessOption;

public class HillshadingOptionModel implements OptionModel
{
	
	private boolean lightingEnabled = true;
	private String sourceType = LightSourceSpecifyTypeEnum.BY_AZIMUTH_AND_ELEVATION.optionValue();
	private AzimuthElevationAngles sourceLocation = new AzimuthElevationAngles(315.0, 25.0); 
	private LightingDate sunlightDate = new LightingDate(System.currentTimeMillis());
	private LightingTime sunlightTime = new LightingTime(System.currentTimeMillis());
	private boolean recalcLightForEachPoint = false;
	private double lightZenith = 90.0;
	private double darkZenith = 108.0;
	private double lightMultiple = 1.0;
	private double lightIntensity = 0.75;
	private double darkIntensity = 1.0;
	private int spotExponent = 1;
	private boolean rayTraceShadows = false;
	private double shadowIntensity = 0.4;
	
	public HillshadingOptionModel()
	{
		
	}

	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.lightingEnabled",
			label="Lighting Enabled",
			tooltip="",
			enabled=true)
	@Order(0)
	public boolean isLightingEnabled()
	{
		return lightingEnabled;
	}

	public void setLightingEnabled(boolean lightingEnabled)
	{
		this.lightingEnabled = lightingEnabled;
	}

	

	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.sourceType",
			label="Source Type",
			tooltip="",
			enabled=true,
			listModel=SourceTypeListModel.class)
	@Order(10)
	public String getSourceType()
	{
		return sourceType;
	}

	public void setSourceType(String sourceType)
	{
		this.sourceType = sourceType;
	}

	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.sourceLocation",
			label="Source Location",
			tooltip="",
			enabled=true)
	@Order(20)
	public AzimuthElevationAngles getSourceLocation()
	{
		return sourceLocation;
	}

	public void setSourceLocation(AzimuthElevationAngles sourceLocation)
	{
		this.sourceLocation = sourceLocation;
	}

	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.sunlightDate",
			label="Sunlight Date (GMT)",
			tooltip="",
			enabled=true)
	@Order(30)
	public LightingDate getSunlightDate()
	{
		return sunlightDate;
	}

	public void setSunlightDate(LightingDate sunlightDate)
	{
		this.sunlightDate = sunlightDate;
	}

	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.sunlightTime",
			label="Sunlight Time (GMT)",
			tooltip="",
			enabled=true)
	@Order(40)
	public LightingTime getSunlightTime()
	{
		return sunlightTime;
	}

	public void setSunlightTime(LightingTime sunlightTime)
	{
		this.sunlightTime = sunlightTime;
	}
	
	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.recalcLightForEachPoint",
			label="Recalc Light For Each Point",
			tooltip="",
			enabled=true)
	@Order(50)
	public boolean isRecalcLightForEachPoint()
	{
		return recalcLightForEachPoint;
	}

	public void setRecalcLightForEachPoint(boolean recalcLightForEachPoint)
	{
		this.recalcLightForEachPoint = recalcLightForEachPoint;
	}
	
	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.lightZenith",
			label="Light Zenith",
			tooltip="",
			enabled=true)
	@Order(60)
	public double getLightZenith()
	{
		return lightZenith;
	}

	public void setLightZenith(double lightZenith)
	{
		this.lightZenith = lightZenith;
	}
	
	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.darkZenith",
			label="Dark Zenith",
			tooltip="",
			enabled=true)
	@Order(70)
	public double getDarkZenith()
	{
		return darkZenith;
	}

	public void setDarkZenith(double darkZenith)
	{
		this.darkZenith = darkZenith;
	}

	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.lightMultiple",
			label="Light Multiple",
			tooltip="",
			enabled=true)
	@Order(80)
	public double getLightMultiple()
	{
		return lightMultiple;
	}

	public void setLightMultiple(double lightMultiple)
	{
		this.lightMultiple = lightMultiple;
	}

	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.lightIntensity",
			label="Light Intensity",
			tooltip="",
			enabled=true)
	@Order(90)
	public double getLightIntensity()
	{
		return lightIntensity;
	}

	public void setLightIntensity(double lightIntensity)
	{
		this.lightIntensity = lightIntensity;
	}
	
	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.darkIntensity",
			label="Dark Intensity",
			tooltip="",
			enabled=true)
	@Order(100)
	public double getDarkIntensity()
	{
		return darkIntensity;
	}

	public void setDarkIntensity(double darkIntensity)
	{
		this.darkIntensity = darkIntensity;
	}

	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.spotExponent",
			label="Spot Exponent",
			tooltip="",
			enabled=true)
	@Order(110)
	public int getSpotExponent()
	{
		return spotExponent;
	}

	public void setSpotExponent(int spotExponent)
	{
		this.spotExponent = spotExponent;
	}

	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.rayTraceShadows",
			label="Ray Trace Shadows",
			tooltip="",
			enabled=true)
	@Order(120)
	public boolean isRayTraceShadows()
	{
		return rayTraceShadows;
	}

	public void setRayTraceShadows(boolean rayTraceShadows)
	{
		this.rayTraceShadows = rayTraceShadows;
	}

	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.shadowIntensity",
			label="Shadow Intensity",
			tooltip="",
			enabled=true)
	@Order(130)
	public double getShadowIntensity()
	{
		return shadowIntensity;
	}

	public void setShadowIntensity(double shadowIntensity)
	{
		this.shadowIntensity = shadowIntensity;
	}
	
	
	public HillshadingOptionModel copy()
	{
		HillshadingOptionModel copy = new HillshadingOptionModel();
		
		copy.lightingEnabled = this.lightingEnabled;
		copy.sourceType = this.sourceType;
		copy.sourceLocation = this.sourceLocation.copy();
		copy.sunlightDate = this.sunlightDate.copy();
		copy.sunlightTime = this.sunlightTime.copy();
		copy.recalcLightForEachPoint = this.recalcLightForEachPoint;
		copy.lightZenith = this.lightZenith;
		copy.darkZenith = this.darkZenith;
		copy.lightMultiple = this.lightMultiple;
		copy.lightIntensity = this.lightIntensity;
		copy.darkIntensity = this.darkIntensity;
		copy.spotExponent = this.spotExponent;
		copy.rayTraceShadows = this.rayTraceShadows;
		copy.shadowIntensity = this.shadowIntensity;
		
		return copy;
	}
}
