package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.RayTracingException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;

public class RayTracing
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(RayTracing.class);
	
	
	private RasterDataFetchHandler rasterDataFetchHandler;

	private double longitudeResolution;
	private double latitudeResolution;
	private double radiusInterval;
	
	private double metersResolution;
	
	private double northLimit;
	private double southLimit;
	private double eastLimit;
	private double westLimit;
	
	private double elevationMultiple;
	private double minDataValue;
	private double maxDataValueTrue;
	private double maxDataValue;
	
	private double[] points;
	
	public RayTracing(
			ModelContext modelContext,
			RasterDataFetchHandler rasterDataFetchHandler)
		{
			this(
				modelContext.getRasterDataContext().getEffectiveLatitudeResolution(),
				modelContext.getRasterDataContext().getEffectiveLongitudeResolution(),
				modelContext.getRasterDataContext().getMetersResolution(),
				modelContext.getNorth(),
				modelContext.getSouth(),
				modelContext.getEast(),
				modelContext.getWest(),
				modelContext.getRasterDataContext().getDataMinimumValue(),
				modelContext.getRasterDataContext().getDataMaximumValue(),
				modelContext.getModelProcessManifest().getGlobalOptionModel().getElevationMultiple(),
				rasterDataFetchHandler
				);
		}
	
	public RayTracing(
					double latitudeResolution, 
					double longitudeResolution, 
					double metersResolution, 
					double northLimit,
					double southLimit,
					double eastLimit,
					double westLimit,
					double minDataValue,
					double maxDataValue,
					double elevationMultiple,
					RasterDataFetchHandler rasterDataFetchHandler)
	{
		this.rasterDataFetchHandler = rasterDataFetchHandler;
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		this.metersResolution = metersResolution;
		this.northLimit = northLimit;
		this.southLimit = southLimit;
		this.eastLimit = eastLimit;
		this.westLimit = westLimit;
		this.elevationMultiple = elevationMultiple;
		this.minDataValue = minDataValue;
		this.maxDataValueTrue = maxDataValue;
		this.maxDataValue = maxDataValue * elevationMultiple;
		
		this.points = new double[3];
		
		radiusInterval = MathExt.sqrt(MathExt.sqr(latitudeResolution) + MathExt.sqr(longitudeResolution));
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
	public double isRayBlocked(double remoteElevationAngle, double remoteAzimuth, double centerLatitude, double centerLongitude, double centerElevation) throws RayTracingException
	{
		//boolean isBlocked = false;
		
		double isBlocked = 0.0;
		
		
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
				//isBlocked = 0.0;
				break;
			}
			
			// Calculate the elevation of the path at the current radius.
			double resolution = (points[1] / radiusInterval);
			double _rayElevation = centerElevation + (resolution * metersResolution);
			double rayElevation = _rayElevation;//getElevationMultiplied(_rayElevation);
			
			
			// Fetch the elevation value
			double pointElevation = 0;
			try {
				pointElevation = rasterDataFetchHandler.getRasterData(latitude, longitude);
			} catch (Exception ex) {
				throw new RayTracingException("Failed to get elevation for point: " + ex.getMessage(), ex);
			}
			//pointElevation = getElevationMultiplied(pointElevation);
			
			// Increment for the next pass radius
			radius += radiusInterval;
			
			// If the elevation at the current point is invalid, we skip it and continue. Given this condition
			// we assume the path to not be blocked since we cannot prove otherwise.
			if (pointElevation == DemConstants.ELEV_NO_DATA) {
				continue;
			}
			
			// If the elevation at the current point exceeds the elevation of the ray path
			// then the ray is blocked. 
			if (pointElevation > rayElevation) {
				isBlocked = 1.0;
				break;
				// TODO: Find a good method for edge detecting the shadows...
				/*
				double d = pointElevation - rayElevation;
				if (d >= 10.0) {
					isBlocked = 1.0;
					break;
				} else {
					isBlocked = d / 10.0;
					break;
				}
				*/
				
				//break;
			}
			
			// If the elevation of the ray path at the current radius exceeds the maximum dataset
			// elevation then we can safely assume that the ray is not blocked.
			if (rayElevation > this.maxDataValue) {
				//isBlocked = 0.0;
				break;
			}
			
		}
		

		return isBlocked;
		
	}
	
	
	protected double getElevationMultiplied(double elevation)
	{

		double ratio = (elevation - minDataValue) / (maxDataValueTrue - minDataValue);
		elevation = minDataValue + (maxDataValue - minDataValue) * ratio;
		
		return elevation;
	}
	
	public double getMaxDataValue()
	{
		return maxDataValue;
	}

	public void setMaxDataValue(double maxDataValue)
	{
		this.maxDataValue = maxDataValue;
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


	public RasterDataFetchHandler getRasterDataFetchHandler()
	{
		return rasterDataFetchHandler;
	}

	public void setRasterDataFetchHandler(
			RasterDataFetchHandler rasterDataFetchHandler)
	{
		this.rasterDataFetchHandler = rasterDataFetchHandler;
	}
	
	public interface RasterDataFetchHandler 
	{
		public double getRasterData(double latitude, double longitude) throws Exception;
	}
	
	
}
