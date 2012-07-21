package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.Order;
import us.wthr.jdem846.model.annotations.ProcessOption;

public class GdalHillshadingOptionModel implements OptionModel {

	private double azimuth = 315.0;
	private double altitude = 45.0;
	private boolean preserveColor = false;
	
	
	public GdalHillshadingOptionModel()
	{
		
	}
	
	@ProcessOption(id="us.wthr.jdem846.model.GdalHillshadingOptionModel.azimuth",
			label="Azimuth",
			tooltip="",
			enabled=true)
	@Order(0)
	public double getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(double azimuth) {
		this.azimuth = azimuth;
	}




	@ProcessOption(id="us.wthr.jdem846.model.GdalHillshadingOptionModel.altitude",
			label="Altitude",
			tooltip="",
			enabled=true)
	@Order(10)
	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}




	@ProcessOption(id="us.wthr.jdem846.model.GdalHillshadingOptionModel.preserveColor",
			label="Preserve Model Color",
			tooltip="",
			enabled=true)
	@Order(20)
	public boolean getPreserveColor() {
		return preserveColor;
	}

	public void setPreserveColor(boolean preserveColor) {
		this.preserveColor = preserveColor;
	}





	@Override
	public OptionModel copy() {
		GdalHillshadingOptionModel copy = new GdalHillshadingOptionModel();
		copy.altitude = this.altitude;
		copy.azimuth = this.azimuth;
		copy.preserveColor = this.preserveColor;
		return copy;
	}

}
