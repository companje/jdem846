package us.wthr.jdem846.gis.elevation;

public class ElevationSample extends ElevationMinMax
{
	
	private double latitude;
	private double longitude;
	
	


	public ElevationSample()
	{
		
	}
	
	public ElevationSample(double latitude, 
							double longitude,
							double minimumElevation,
							double maximumElevation,
							double meanElevation,
							double medianElevation)
	{
		super(minimumElevation, maximumElevation, meanElevation, medianElevation);
		
		this.latitude = latitude;
		this.longitude = longitude;

	}
	
	
	public boolean contains(double latitude, double longitude, double latitudeResolution, double longitudeResolution)
	{
		
		if (latitude <= this.latitude 
				&& latitude >= this.latitude + latitudeResolution
				&& longitude >= this.longitude 
				&& longitude <= this.longitude + longitudeResolution)
			return true;
		else
			return false;
	}
	
	public double getLatitude()
	{
		return latitude;
	}


	public double getLongitude()
	{
		return longitude;
	}


	
	
}
