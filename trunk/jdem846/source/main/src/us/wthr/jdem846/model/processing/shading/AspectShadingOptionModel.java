package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.Order;
import us.wthr.jdem846.model.annotations.ProcessOption;
import us.wthr.jdem846.model.annotations.ValueBounds;

public class AspectShadingOptionModel implements OptionModel
{
	
	private double lightMultiple = 1.0;
	private double lightIntensity = 0.50;
	private double darkIntensity = 0.75;
	private int spotExponent = 1;
	
	public AspectShadingOptionModel()
	{
		
	}
	

	@ProcessOption(id="us.wthr.jdem846.model.AspectShadingOptionModel.lightMultiple",
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

	@ProcessOption(id="us.wthr.jdem846.model.AspectShadingOptionModel.lightIntensity",
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
	
	@ProcessOption(id="us.wthr.jdem846.model.AspectShadingOptionModel.darkIntensity",
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

	@ProcessOption(id="us.wthr.jdem846.model.AspectShadingOptionModel.spotExponent",
			label="Spot Exponent",
			tooltip="",
			enabled=true)
	@Order(110)
	@ValueBounds(minimum=1,
			maximum=5)
	public int getSpotExponent()
	{
		return spotExponent;
	}

	public void setSpotExponent(int spotExponent)
	{
		this.spotExponent = spotExponent;
	}
	
	
	public AspectShadingOptionModel copy()
	{
		AspectShadingOptionModel copy = new AspectShadingOptionModel();
		copy.lightMultiple = this.lightMultiple;
		copy.lightIntensity = this.lightIntensity;
		copy.darkIntensity = this.darkIntensity;
		copy.spotExponent = this.spotExponent;
		return copy;
	}
	
}
