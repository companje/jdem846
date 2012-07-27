package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.lighting.LightSourceSpecifyTypeEnum;
import us.wthr.jdem846.model.AzimuthElevationAngles;
import us.wthr.jdem846.model.LightingDate;
import us.wthr.jdem846.model.LightingTime;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.Order;
import us.wthr.jdem846.model.annotations.ProcessOption;
import us.wthr.jdem846.model.annotations.ValueBounds;

public class HillshadingOptionModel implements OptionModel
{
	
	private boolean lightingEnabled = true;
	//private String sourceType = LightSourceSpecifyTypeEnum.BY_AZIMUTH_AND_ELEVATION.optionValue();
	//private AzimuthElevationAngles sourceLocation = new AzimuthElevationAngles(315.0, 25.0); 
	private LightingDate sunlightDate = new LightingDate(System.currentTimeMillis());
	private LightingTime sunlightTime = new LightingTime(System.currentTimeMillis());
	//private boolean recalcLightForEachPoint = true;
	//private double lightZenith = 90.0;
	//private double darkZenith = 108.0;
	//private double lightMultiple = 1.0;
	private double lightIntensity = 0.75;
	private double darkIntensity = 1.0;
	
	private boolean advancedLightingControl = false;
	private double emmisive = 0.0;
	private double ambient = 0.4;
	private double diffuse = 0.7;
	private double specular = 0.6;
	
	private int spotExponent = 1;
	private boolean rayTraceShadows = false;
	private double shadowIntensity = 0.4;
	
	private boolean useDistanceAttenuation = true;
	private double attenuationRadius = 2000;

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

	
	/*
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
	*/
	
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
	
	/*
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
	@ValueBounds(minimum=0,
			stepSize=0.1)
	public double getLightMultiple()
	{
		return lightMultiple;
	}

	public void setLightMultiple(double lightMultiple)
	{
		this.lightMultiple = lightMultiple;
	}
	*/
	

	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.lightIntensity",
			label="Light Intensity",
			tooltip="",
			enabled=true)
	@Order(90)
	@ValueBounds(minimum=0,
			maximum=1.0,
			stepSize=0.05)
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
	@ValueBounds(minimum=0,
			maximum=1.0,
			stepSize=0.05)
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
	@ValueBounds(minimum=1,
			maximum=128)
	public int getSpotExponent()
	{
		return spotExponent;
	}

	public void setSpotExponent(int spotExponent)
	{
		this.spotExponent = spotExponent;
	}

	
	
	
	
	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.advancedLightingControl",
			label="Advanced Lighting",
			tooltip="",
			enabled=true)
	@Order(140)
	public boolean getAdvancedLightingControl()
	{
		return advancedLightingControl;
	}

	public void setAdvancedLightingControl(boolean advancedLightingControl)
	{
		this.advancedLightingControl = advancedLightingControl;
	}

	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.emmisive",
			label="Emmisive",
			tooltip="Light emmitted or given off by the surface",
			enabled=true)
	@Order(150)
	@ValueBounds(minimum=0,
			maximum=10.0,
			stepSize=0.1)
	public double getEmmisive()
	{
		return emmisive;
	}

	public void setEmmisive(double emmisive)
	{
		this.emmisive = emmisive;
	}
	
	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.ambient",
			label="Ambient",
			tooltip="Fixed-intensity light that affects all surfaces equally",
			enabled=true)
	@Order(160)
	@ValueBounds(minimum=0,
			maximum=10.0,
			stepSize=0.1)
	public double getAmbient()
	{
		return ambient;
	}

	public void setAmbient(double ambient)
	{
		this.ambient = ambient;
	}
	
	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.diffuse",
			label="Diffuse",
			tooltip="Directed light reflected off a surface equally in all directions",
			enabled=true)
	@Order(170)
	@ValueBounds(minimum=0,
			maximum=10.0,
			stepSize=0.1)
	public double getDiffuse()
	{
		return diffuse;
	}

	public void setDiffuse(double diffuse)
	{
		this.diffuse = diffuse;
	}
	
	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.specular",
			label="Specular",
			tooltip="Light scattered from the surface predominantly around the mirror direction (Shininess).",
			enabled=true)
	@Order(180)
	@ValueBounds(minimum=0,
			maximum=10.0,
			stepSize=0.1)
	public double getSpecular()
	{
		return specular;
	}

	public void setSpecular(double specular)
	{
		this.specular = specular;
	}

	
	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.rayTraceShadows",
			label="Ray Trace Shadows",
			tooltip="",
			enabled=true)
	@Order(190)
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
	@Order(200)
	@ValueBounds(minimum=0,
			maximum=1.0,
			stepSize=0.1)
	public double getShadowIntensity()
	{
		return shadowIntensity;
	}

	public void setShadowIntensity(double shadowIntensity)
	{
		this.shadowIntensity = shadowIntensity;
	}
	
	
	
	
	
	
	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.useDistanceAttenuation",
			label="Use Distance Attenuation",
			tooltip="",
			enabled=true)
	@Order(210)
	public boolean getUseDistanceAttenuation()
	{
		return useDistanceAttenuation;
	}

	public void setUseDistanceAttenuation(boolean useDistanceAttenuation)
	{
		this.useDistanceAttenuation = useDistanceAttenuation;
	}

	@ProcessOption(id="us.wthr.jdem846.model.HillshadingOptionModel.attenuationRadius",
			label="Attenuation Radius",
			tooltip="",
			enabled=true)
	@ValueBounds(minimum=0,
			maximum=1000000000,
			stepSize=500)
	@Order(220)
	public double getAttenuationRadius()
	{
		return attenuationRadius;
	}

	public void setAttenuationRadius(double attenuationRadius)
	{
		this.attenuationRadius = attenuationRadius;
	}

	public HillshadingOptionModel copy()
	{
		HillshadingOptionModel copy = new HillshadingOptionModel();
		
		copy.lightingEnabled = this.lightingEnabled;
		//copy.sourceType = this.sourceType;
		//copy.sourceLocation = this.sourceLocation.copy();
		copy.sunlightDate = this.sunlightDate.copy();
		copy.sunlightTime = this.sunlightTime.copy();
		//copy.recalcLightForEachPoint = this.recalcLightForEachPoint;
		//copy.lightZenith = this.lightZenith;
		//copy.darkZenith = this.darkZenith;
		//copy.lightMultiple = this.lightMultiple;
		copy.lightIntensity = this.lightIntensity;
		copy.darkIntensity = this.darkIntensity;
		copy.spotExponent = this.spotExponent;
		copy.rayTraceShadows = this.rayTraceShadows;
		copy.shadowIntensity = this.shadowIntensity;
		copy.useDistanceAttenuation = this.useDistanceAttenuation;
		copy.attenuationRadius = this.attenuationRadius;
		
		return copy;
	}
}
