package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.model.LightingDate;
import us.wthr.jdem846.model.LightingTime;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.Order;
import us.wthr.jdem846.model.annotations.ProcessOption;
import us.wthr.jdem846.model.annotations.ValueBounds;

public class HillshadingOptionModel implements OptionModel
{

	private boolean lightingEnabled = true;

	private LightingDate sunlightDate = new LightingDate(System.currentTimeMillis());
	private LightingTime sunlightTime = new LightingTime(System.currentTimeMillis());

	private double lightIntensity = 0.75;
	private double darkIntensity = 1.0;

	private boolean advancedLightingControl = true;
	private double emmisive = 0.0;
	private double ambient = 0.4;
	private double diffuse = 0.7;
	private double specular = 0.6;

	private int spotExponent = 10;
	private boolean rayTraceShadows = false;
	private double shadowIntensity = 0.4;

	private boolean useDistanceAttenuation = true;
	private double attenuationRadius = 2000;

	public HillshadingOptionModel()
	{

	}

	@ProcessOption(id = "us.wthr.jdem846.model.HillshadingOptionModel.lightingEnabled", label = "Lighting Enabled", tooltip = "", visible = true)
	@Order(0)
	public boolean isLightingEnabled()
	{
		return lightingEnabled;
	}

	public void setLightingEnabled(boolean lightingEnabled)
	{
		this.lightingEnabled = lightingEnabled;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.HillshadingOptionModel.sunlightDate", label = "Sunlight Date (GMT)", tooltip = "", enabler = HillshadingOptionPropertyEnabler.class, visible = true)
	@Order(30)
	public LightingDate getSunlightDate()
	{
		return sunlightDate;
	}

	public void setSunlightDate(LightingDate sunlightDate)
	{
		this.sunlightDate = sunlightDate;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.HillshadingOptionModel.sunlightTime", label = "Sunlight Time (GMT)", tooltip = "", enabler = HillshadingOptionPropertyEnabler.class, visible = true)
	@Order(40)
	public LightingTime getSunlightTime()
	{
		return sunlightTime;
	}

	public void setSunlightTime(LightingTime sunlightTime)
	{
		this.sunlightTime = sunlightTime;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.HillshadingOptionModel.lightIntensity", label = "Light Intensity", tooltip = "", enabler = HillshadingOptionPropertyEnabler.class, optionGroup = "Basic Lighting", visible = true)
	@Order(90)
	@ValueBounds(minimum = 0, maximum = 1.0, stepSize = 0.05)
	public double getLightIntensity()
	{
		return lightIntensity;
	}

	public void setLightIntensity(double lightIntensity)
	{
		this.lightIntensity = lightIntensity;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.HillshadingOptionModel.darkIntensity", label = "Dark Intensity", tooltip = "", optionGroup = "Basic Lighting", enabler = HillshadingOptionPropertyEnabler.class, visible = true)
	@Order(100)
	@ValueBounds(minimum = 0, maximum = 1.0, stepSize = 0.05)
	public double getDarkIntensity()
	{
		return darkIntensity;
	}

	public void setDarkIntensity(double darkIntensity)
	{
		this.darkIntensity = darkIntensity;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.HillshadingOptionModel.advancedLightingControl", label = "Advanced Lighting", tooltip = "", optionGroup = "Advanced Lighting", enabler = HillshadingOptionPropertyEnabler.class, visible = true)
	@Order(140)
	public boolean getAdvancedLightingControl()
	{
		return advancedLightingControl;
	}

	public void setAdvancedLightingControl(boolean advancedLightingControl)
	{
		this.advancedLightingControl = advancedLightingControl;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.HillshadingOptionModel.emmisive", label = "Emmisive", tooltip = "Light emmitted or given off by the surface", optionGroup = "Advanced Lighting", enabler = HillshadingOptionPropertyEnabler.class, visible = true)
	@Order(150)
	@ValueBounds(minimum = 0, maximum = 10.0, stepSize = 0.1)
	public double getEmmisive()
	{
		return emmisive;
	}

	public void setEmmisive(double emmisive)
	{
		this.emmisive = emmisive;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.HillshadingOptionModel.ambient", label = "Ambient", tooltip = "Fixed-intensity light that affects all surfaces equally", optionGroup = "Advanced Lighting", enabler = HillshadingOptionPropertyEnabler.class, visible = true)
	@Order(160)
	@ValueBounds(minimum = 0, maximum = 10.0, stepSize = 0.1)
	public double getAmbient()
	{
		return ambient;
	}

	public void setAmbient(double ambient)
	{
		this.ambient = ambient;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.HillshadingOptionModel.diffuse", label = "Diffuse", tooltip = "Directed light reflected off a surface equally in all directions", optionGroup = "Advanced Lighting", enabler = HillshadingOptionPropertyEnabler.class, visible = true)
	@Order(170)
	@ValueBounds(minimum = 0, maximum = 10.0, stepSize = 0.1)
	public double getDiffuse()
	{
		return diffuse;
	}

	public void setDiffuse(double diffuse)
	{
		this.diffuse = diffuse;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.HillshadingOptionModel.specular", label = "Specular", tooltip = "Light scattered from the surface predominantly around the mirror direction (Shininess).", optionGroup = "Advanced Lighting", enabler = HillshadingOptionPropertyEnabler.class, visible = true)
	@Order(180)
	@ValueBounds(minimum = 0, maximum = 10.0, stepSize = 0.1)
	public double getSpecular()
	{
		return specular;
	}

	public void setSpecular(double specular)
	{
		this.specular = specular;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.HillshadingOptionModel.spotExponent", label = "Spot Exponent", tooltip = "", optionGroup = "Advanced Lighting", enabler = HillshadingOptionPropertyEnabler.class, visible = true)
	@Order(185)
	@ValueBounds(minimum = 1, maximum = 128)
	public int getSpotExponent()
	{
		return spotExponent;
	}

	public void setSpotExponent(int spotExponent)
	{
		this.spotExponent = spotExponent;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.HillshadingOptionModel.rayTraceShadows", label = "Ray Trace Shadows", optionGroup = "Ray Tracing", tooltip = "", enabler = HillshadingOptionPropertyEnabler.class, visible = true)
	@Order(190)
	public boolean isRayTraceShadows()
	{
		return rayTraceShadows;
	}

	public void setRayTraceShadows(boolean rayTraceShadows)
	{
		this.rayTraceShadows = rayTraceShadows;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.HillshadingOptionModel.shadowIntensity", label = "Shadow Intensity", optionGroup = "Ray Tracing", tooltip = "", enabler = HillshadingOptionPropertyEnabler.class, visible = true)
	@Order(200)
	@ValueBounds(minimum = 0, maximum = 1.0, stepSize = 0.1)
	public double getShadowIntensity()
	{
		return shadowIntensity;
	}

	public void setShadowIntensity(double shadowIntensity)
	{
		this.shadowIntensity = shadowIntensity;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.HillshadingOptionModel.useDistanceAttenuation", label = "Use Distance Attenuation", tooltip = "", optionGroup = "Ray Tracing", enabler = HillshadingOptionPropertyEnabler.class, visible = false)
	@Order(210)
	public boolean getUseDistanceAttenuation()
	{
		return useDistanceAttenuation;
	}

	public void setUseDistanceAttenuation(boolean useDistanceAttenuation)
	{
		this.useDistanceAttenuation = useDistanceAttenuation;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.HillshadingOptionModel.attenuationRadius", label = "Attenuation Radius", optionGroup = "Ray Tracing", tooltip = "", enabler = HillshadingOptionPropertyEnabler.class, visible = false)
	@ValueBounds(minimum = 0, maximum = 1000000000, stepSize = 500)
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
		copy.sunlightDate = this.sunlightDate.copy();
		copy.sunlightTime = this.sunlightTime.copy();
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
