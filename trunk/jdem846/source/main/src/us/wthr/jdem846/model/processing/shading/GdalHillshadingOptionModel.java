package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.Order;
import us.wthr.jdem846.model.annotations.ProcessOption;
import us.wthr.jdem846.model.annotations.ValueBounds;

public class GdalHillshadingOptionModel implements OptionModel
{

	private double azimuth = 315.0;
	private double altitude = 45.0;
	private boolean preserveColor = false;

	private double lightIntensity = 0.75;
	private double darkIntensity = 1.0;

	public GdalHillshadingOptionModel()
	{

	}

	@ProcessOption(id = "us.wthr.jdem846.model.GdalHillshadingOptionModel.azimuth", label = "Azimuth", tooltip = "", visible = true)
	@Order(0)
	public double getAzimuth()
	{
		return azimuth;
	}

	public void setAzimuth(double azimuth)
	{
		this.azimuth = azimuth;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.GdalHillshadingOptionModel.altitude", label = "Altitude", tooltip = "", visible = true)
	@Order(10)
	public double getAltitude()
	{
		return altitude;
	}

	public void setAltitude(double altitude)
	{
		this.altitude = altitude;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.GdalHillshadingOptionModel.preserveColor", label = "Preserve Model Color", tooltip = "", visible = true)
	@Order(20)
	public boolean getPreserveColor()
	{
		return preserveColor;
	}

	public void setPreserveColor(boolean preserveColor)
	{
		this.preserveColor = preserveColor;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.GdalHillshadingOptionModel.lightIntensity", label = "Light Intensity", tooltip = "", visible = true)
	@Order(30)
	@ValueBounds(minimum = 0, maximum = 1.0, stepSize = 0.05)
	public double getLightIntensity()
	{
		return lightIntensity;
	}

	public void setLightIntensity(double lightIntensity)
	{
		this.lightIntensity = lightIntensity;
	}

	@ProcessOption(id = "us.wthr.jdem846.model.GdalHillshadingOptionModel.darkIntensity", label = "Dark Intensity", tooltip = "", visible = true)
	@Order(40)
	@ValueBounds(minimum = 0, maximum = 1.0, stepSize = 0.05)
	public double getDarkIntensity()
	{
		return darkIntensity;
	}

	public void setDarkIntensity(double darkIntensity)
	{
		this.darkIntensity = darkIntensity;
	}

	@Override
	public OptionModel copy()
	{
		GdalHillshadingOptionModel copy = new GdalHillshadingOptionModel();
		copy.altitude = this.altitude;
		copy.azimuth = this.azimuth;
		copy.preserveColor = this.preserveColor;
		return copy;
	}

}
