package us.wthr.jdem846.model;

import java.text.NumberFormat;
import java.util.Map;

public class AzimuthElevationAngles
{
	
	private double azimuthAngle;
	private double elevationAngle;
	
	public AzimuthElevationAngles()
	{
		
	}
	
	public AzimuthElevationAngles(double azimuthAngle, double elevationAngle)
	{
		setAzimuthAngle(azimuthAngle);
		setElevationAngle(elevationAngle);
	}

	public double getAzimuthAngle()
	{
		return azimuthAngle;
	}

	public void setAzimuthAngle(double azimuthAngle)
	{
		this.azimuthAngle = azimuthAngle;
	}

	public double getElevationAngle()
	{
		return elevationAngle;
	}

	public void setElevationAngle(double elevationAngle)
	{
		this.elevationAngle = elevationAngle;
	}
	
	
	
	public static AzimuthElevationAngles fromString(String s)
	{
		Map<String, double[]> values = SimpleNumberListMapSerializer.parseDoubleListString(s);
		
		double[] azimuth = values.get("azimuth");
		double[] elevation = values.get("elevation");
		
		AzimuthElevationAngles angles = new AzimuthElevationAngles();
		angles.setAzimuthAngle(azimuth[0]);
		angles.setElevationAngle(elevation[0]);
		
		return angles;
	}
	
	public String toString()
	{
		String s = "azimuth:[" + getAzimuthAngle() + "];elevation:[" + getElevationAngle() + "]";
		return s;
	}
	
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof AzimuthElevationAngles) {
			AzimuthElevationAngles other = (AzimuthElevationAngles) obj;
			if (other.azimuthAngle == this.azimuthAngle 
					&& other.elevationAngle == this.elevationAngle) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
}
