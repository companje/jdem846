package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.model.LightingDate;
import us.wthr.jdem846.model.LightingTime;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.Order;
import us.wthr.jdem846.model.annotations.ProcessOption;
import us.wthr.jdem846.model.annotations.ValueBounds;

public class RenderLightingOptionModel implements OptionModel
{
	private boolean lightingEnabled = true;
	
	private boolean flatLighting = false;
	
	private LightingDate sunlightDate = new LightingDate(System.currentTimeMillis());
	private LightingTime sunlightTime = new LightingTime(System.currentTimeMillis());


	private double emmisive = 0.0;
	private double ambient = 0.4;
	private double diffuse = 0.7;
	private double specular = 0.2;

	private int spotExponent = 2;


	public RenderLightingOptionModel()
	{

	}

	@ProcessOption(id = "us.wthr.jdem846.model.RenderLightingOptionModel.lightingEnabled"
				, label = "Lighting Enabled"
				, tooltip = ""
				, visible = true)
	@Order(0)
	public boolean isLightingEnabled()
	{
		return lightingEnabled;
	}

	public void setLightingEnabled(boolean lightingEnabled)
	{
		this.lightingEnabled = lightingEnabled;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.RenderLightingOptionModel.sunlightDate"
				, label = "Sunlight Date (GMT)"
				, tooltip = ""
				, enabler = RenderLightingOptionPropertyEnabler.class
				, visible = true)
	@Order(30)
	public LightingDate getSunlightDate()
	{
		return sunlightDate;
	}

	public void setSunlightDate(LightingDate sunlightDate)
	{
		this.sunlightDate = sunlightDate;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.RenderLightingOptionModel.sunlightTime"
				, label = "Sunlight Time (GMT)"
				, tooltip = ""
				, enabler = RenderLightingOptionPropertyEnabler.class
				, visible = true)
	@Order(40)
	public LightingTime getSunlightTime()
	{
		return sunlightTime;
	}

	public void setSunlightTime(LightingTime sunlightTime)
	{
		this.sunlightTime = sunlightTime;
	}



	@ProcessOption(id = "us.wthr.jdem846.model.RenderLightingOptionModel.emmisive"
				, label = "Emmisive"
				, tooltip = "Light emmitted or given off by the surface"
				, optionGroup = "Advanced Lighting"
				, enabler = RenderLightingOptionPropertyEnabler.class
				, visible = true)
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

	@ProcessOption(id = "us.wthr.jdem846.model.RenderLightingOptionModel.ambient"
				, label = "Ambient"
				, tooltip = "Fixed-intensity light that affects all surfaces equally"
				, optionGroup = "Advanced Lighting"
				, enabler = RenderLightingOptionPropertyEnabler.class
				, visible = true)
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

	@ProcessOption(id = "us.wthr.jdem846.model.RenderLightingOptionModel.diffuse"
				, label = "Diffuse"
				, tooltip = "Directed light reflected off a surface equally in all directions"
				, optionGroup = "Advanced Lighting"
				, enabler = RenderLightingOptionPropertyEnabler.class
				, visible = true)
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

	@ProcessOption(id = "us.wthr.jdem846.model.RenderLightingOptionModel.specular"
				, label = "Specular"
				, tooltip = "Light scattered from the surface predominantly around the mirror direction (Shininess)."
				, optionGroup = "Advanced Lighting"
				, enabler = RenderLightingOptionPropertyEnabler.class
				, visible = true)
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

	@ProcessOption(id = "us.wthr.jdem846.model.RenderLightingOptionModel.spotExponent"
				, label = "Spot Exponent"
				, tooltip = ""
				, optionGroup = "Advanced Lighting"
				, enabler = RenderLightingOptionPropertyEnabler.class
				, visible = true)
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
	
	
	@ProcessOption(id = "us.wthr.jdem846.model.RenderLightingOptionModel.flatLighting"
			, label = "Flat Lighting"
			, tooltip = ""
			, enabler = RenderLightingOptionPropertyEnabler.class
			, visible = true)
	@Order(195)
	public boolean getFlatLighting()
	{
		return flatLighting;
	}

	public void setFlatLighting(boolean flatLighting)
	{
		this.flatLighting = flatLighting;
	}

	public RenderLightingOptionModel copy()
	{
		RenderLightingOptionModel copy = new RenderLightingOptionModel();

		copy.lightingEnabled = this.lightingEnabled;
		copy.sunlightDate = this.sunlightDate.copy();
		copy.sunlightTime = this.sunlightTime.copy();
		copy.spotExponent = this.spotExponent;
		copy.flatLighting = this.flatLighting;
		return copy;
	}
}
