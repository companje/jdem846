package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.ProcessOption;

public class HillshadingOptionModel implements OptionModel
{
	
	private boolean lightingEnabled;
	private String sourceType;
	private Object sourceLocation; // TODO: Define a type
	private long sunlightDate;
	private long sunlightTime;
	private boolean recalcLightForEachPoint;
	private double lightZenith;
	private double darkZenith;
	private double lightMultiple;
	private double lightIntensity;
	private double darkIntensity;
	private int spotExponent;
	private boolean rayTraceShadows;
	private double shadowIntensity;
	
	public HillshadingOptionModel()
	{
		
	}

	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.lightingEnabled",
			label="Lighting Enabled",
			tooltip="",
			enabled=true)
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
	public Object getSourceLocation()
	{
		return sourceLocation;
	}

	public void setSourceLocation(Object sourceLocation)
	{
		this.sourceLocation = sourceLocation;
	}

	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.sunlightDate",
			label="Sunlight Date (GMT)",
			tooltip="",
			enabled=true)
	public long getSunlightDate()
	{
		return sunlightDate;
	}

	public void setSunlightDate(long sunlightDate)
	{
		this.sunlightDate = sunlightDate;
	}

	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.sunlightTime",
			label="Sunlight Time (GMT)",
			tooltip="",
			enabled=true)
	public long getSunlightTime()
	{
		return sunlightTime;
	}

	public void setSunlightTime(long sunlightTime)
	{
		this.sunlightTime = sunlightTime;
	}
	
	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.recalcLightForEachPoint",
			label="Recalc Light For Each Point",
			tooltip="",
			enabled=true)
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
	public double getShadowIntensity()
	{
		return shadowIntensity;
	}

	public void setShadowIntensity(double shadowIntensity)
	{
		this.shadowIntensity = shadowIntensity;
	}
	
	
	
}
