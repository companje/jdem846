package us.wthr.jdem846.articles;


import us.wthr.jdem846.math.Spheres;

public class RayTracing
{
	protected static final double ELEV_NO_DATA = -9999.99;
	
	private RasterDataFetchHandler rasterDataFetchHandler;

	private double longitudeResolution;
	private double latitudeResolution;
	private double radiusInterval;
	
	private double metersResolution;
	
	private double northLimit;
	private double southLimit;
	private double eastLimit;
	private double westLimit;
	
	private double maxElevationValue;
	
	private double[] points = new double[3];
	

	public RayTracing(
					double latitudeResolution, 
					double longitudeResolution, 
					double metersResolution, 
					double northLimit,
					double southLimit,
					double eastLimit,
					double westLimit,
					double maxElevationValue,
					RasterDataFetchHandler rasterDataFetchHandler)
	{
		setRasterDataFetchHandler(rasterDataFetchHandler);
		setLatitudeResolution(latitudeResolution);
		setLongitudeResolution(longitudeResolution);
		setMetersResolution(metersResolution);
		setNorthLimit(northLimit);
		setSouthLimit(southLimit);
		setEastLimit(eastLimit);
		setWestLimit(westLimit);
		setMaxElevationValue(maxElevationValue);
		
		// Calculate a default radius interval as the
		// hypotenuse of a right triangle with the latitude
		// and longitude resolutions as the legs.
		setRadiusInterval(Math.sqrt(Math.pow(getLatitudeResolution(), 2) + Math.pow(getLongitudeResolution(), 2)));
	}
	
	
	/** Ray trace from coordinate to the source of light. If there is another
	 * higher elevation that blocks the way, then return true. Return false if
	 * the trace either not blocked or it exceeds the maximum data elevation and 
	 * it can then be assumed to not be blocked. Points with no data are skipped
	 * and the loop continues on. Note: This initial implementation
	 * assumes a flat Earth and is therefore not technically accurate.
	 * 
	 * @param centerLatitude Geographic latitude of the point being tested for shadow
	 * @param centerLongitude Geographic longitude of the point being tested for shadow
	 * @param centerElevation Elevation of the test point.
	 * @return True if the ray's path is blocked, otherwise returns false.
	 * @throws RayTracingException Thrown if an error is detected when fetching an elevation along the ray path.
	 */
	public boolean isRayBlocked(double remoteElevationAngle, double remoteAzimuth, double centerLatitude, double centerLongitude, double centerElevation) throws RayTracingException
	{
		boolean isBlocked = false;
		
		// Variables for use during each pass
		double radius = radiusInterval;

		while (true) {
			
			// Fetch points in space following the path of the azimuth and elevation angles
			// at the current radius.
			Spheres.getPoint3D(remoteAzimuth, remoteElevationAngle, radius, points);
			
			// Latitude/Longitude pair for the path at the current radius
			double latitude = centerLatitude + points[0];
			double longitude = centerLongitude - points[2];
			
			// Check if the latitude/longitude point is outside of the dataset.
			// If we get to this point, we assume that the ray is not blocked because
			// we are unable to prove otherwise.
			if (latitude > northLimit ||
					latitude < southLimit ||
					longitude > eastLimit ||
					longitude < westLimit) {
				isBlocked = false;
				break;
			}
			
			// Calculate the elevation of the path at the current radius.
			double resolution = (points[1] / radiusInterval);
			double rayElevation = centerElevation + (resolution * metersResolution);
			
			// Fetch the elevation value
			double pointElevation = 0;
			try {
				pointElevation = rasterDataFetchHandler.getRasterData(latitude, longitude);
			} catch (Exception ex) {
				throw new RayTracingException("Failed to get elevation for point: " + ex.getMessage(), ex);
			}
			
			// Increment for the next pass radius
			radius += radiusInterval;
			
			// If the elevation at the current point is invalid, we skip it and continue. Given this condition
			// we assume the path to not be blocked since we cannot prove otherwise.
			if (pointElevation == ELEV_NO_DATA) {
				continue;
			}
			
			// If the elevation at the current point exceeds the elevation of the ray path
			// then the ray is blocked. 
			if (pointElevation > rayElevation) {
				isBlocked = true;
				break;
			}
			
			// If the elevation of the ray path at the current radius exceeds the maximum dataset
			// elevation then we can safely assume that the ray is not blocked.
			if (rayElevation > maxElevationValue) {
				isBlocked = false;
				break;
			}
			
		}
		
		
		return isBlocked;
		
	}

	public double getMaxElevationValue()
	{
		return maxElevationValue;
	}

	public void setMaxElevationValue(double maxElevationValue)
	{
		this.maxElevationValue = maxElevationValue;
	}

	public double getNorthLimit()
	{
		return northLimit;
	}

	public void setNorthLimit(double northLimit)
	{
		this.northLimit = northLimit;
	}

	public double getSouthLimit()
	{
		return southLimit;
	}

	public void setSouthLimit(double southLimit)
	{
		this.southLimit = southLimit;
	}

	public double getEastLimit()
	{
		return eastLimit;
	}

	public void setEastLimit(double eastLimit)
	{
		this.eastLimit = eastLimit;
	}

	public double getWestLimit()
	{
		return westLimit;
	}

	public void setWestLimit(double westLimit)
	{
		this.westLimit = westLimit;
	}

	public double getLongitudeResolution()
	{
		return longitudeResolution;
	}

	public void setLongitudeResolution(double longitudeResolution)
	{
		this.longitudeResolution = longitudeResolution;
	}

	public double getLatitudeResolution()
	{
		return latitudeResolution;
	}

	public void setLatitudeResolution(double latitudeResolution)
	{
		this.latitudeResolution = latitudeResolution;
	}

	public double getMetersResolution()
	{
		return metersResolution;
	}

	public void setMetersResolution(double metersResolution)
	{
		this.metersResolution = metersResolution;
	}

	public double getRadiusInterval()
	{
		return radiusInterval;
	}

	public void setRadiusInterval(double radiusInterval)
	{
		this.radiusInterval = radiusInterval;
	}

	public RasterDataFetchHandler getRasterDataFetchHandler()
	{
		return rasterDataFetchHandler;
	}

	public void setRasterDataFetchHandler(
			RasterDataFetchHandler rasterDataFetchHandler)
	{
		this.rasterDataFetchHandler = rasterDataFetchHandler;
	}

	
}
