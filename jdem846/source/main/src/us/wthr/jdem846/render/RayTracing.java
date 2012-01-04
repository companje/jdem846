package us.wthr.jdem846.render;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.RayTracingException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.Spheres;

public class RayTracing
{
	private static Log log = Logging.getLog(RayTracing.class);
	
	
	private RasterDataFetchHandler rasterDataFetchHandler;
	private double remoteAzimuth;
	private double remoteElevationAngle;
	
	private double longitudeResolution;
	private double latitudeResolution;
	
	private double metersResolution;
	
	private double northLimit;
	private double southLimit;
	private double eastLimit;
	private double westLimit;
	
	private double maxDataValue;
	
	public RayTracing(double remoteAzimuth, 
			double remoteElevationAngle, 
			ModelContext modelContext,
			RasterDataFetchHandler rasterDataFetchHandler)
		{
			this(remoteAzimuth,
				remoteElevationAngle,
				modelContext.getRasterDataContext().getLatitudeResolution(),
				modelContext.getRasterDataContext().getLongitudeResolution(),
				modelContext.getRasterDataContext().getMetersResolution(),
				modelContext.getNorth(),
				modelContext.getSouth(),
				modelContext.getEast(),
				modelContext.getWest(),
				modelContext.getRasterDataContext().getDataMaximumValue(),
				rasterDataFetchHandler
				);
		

		
		}
	
	public RayTracing(double remoteAzimuth, 
					double remoteElevationAngle, 
					double latitudeResolution, 
					double longitudeResolution, 
					double metersResolution, 
					double northLimit,
					double southLimit,
					double eastLimit,
					double westLimit,
					double maxDataValue,
					RasterDataFetchHandler rasterDataFetchHandler)
	{
		this.rasterDataFetchHandler = rasterDataFetchHandler;
		this.remoteAzimuth = remoteAzimuth;
		this.remoteElevationAngle = remoteElevationAngle;
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		this.metersResolution = metersResolution;
		this.northLimit = northLimit;
		this.southLimit = southLimit;
		this.eastLimit = eastLimit;
		this.westLimit = westLimit;
		this.maxDataValue = maxDataValue;
		
	}
	
	
	/** Ray trace from coordinate to the source of light. If there is another
	 * higher elevation that blocks the way, then return true. Return false if
	 * the trace either not blocked or it exceeds the maximum data elevation and 
	 * it can then be assumed to not be blocked. Points with no data are skipped
	 * and the loop continues on. Note: This initial implementation
	 * assumes a flat Earth and is therefore not technically accurate.
	 * 
	 * @param centerLatitude
	 * @param centerLongitude
	 * @param centerElevation
	 * @return
	 * @throws RayTracingException
	 */
	public boolean isRayBlocked(double centerLatitude, double centerLongitude, double centerElevation) throws RayTracingException
	{
		double[] points = {0.0, 0.0, 0.0};
		
		double radius = longitudeResolution;
		while (true) {
			Spheres.getPoint3D(this.remoteAzimuth, this.remoteElevationAngle, radius, points);
			
			double latitude = centerLatitude + points[0];
			double longitude = centerLongitude - points[2];
			
			
			if (latitude > northLimit ||
					latitude < southLimit ||
					longitude > eastLimit ||
					longitude < westLimit) {
				return false;
			}
			
			double resolution = (points[1] / longitudeResolution);
			double rayElevation = centerElevation + (resolution * metersResolution);
			double pointElevation = 0;
			//log.info("Radius: " + radius + ", X/Y/Z: " + points[0] + "/" + points[1] + "/" + points[2] + ", Center Elevation: " + centerElevation + ", Ray Elevation: " + rayElevation);;
			try {
				pointElevation = rasterDataFetchHandler.getRasterData(latitude, longitude);
			} catch (Exception ex) {
				throw new RayTracingException("Failed to get elevation for point: " + ex.getMessage(), ex);
			}
			
			if (pointElevation == DemConstants.ELEV_NO_DATA) {
				continue;
			}
			
			if (pointElevation > rayElevation) {
				return true;
			}
			
			if (rayElevation > this.maxDataValue) {
				return false;
			}
			

			
			radius += longitudeResolution;
		}
		
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

	public RasterDataFetchHandler getRasterDataFetchHandler()
	{
		return rasterDataFetchHandler;
	}

	public void setRasterDataFetchHandler(
			RasterDataFetchHandler rasterDataFetchHandler)
	{
		this.rasterDataFetchHandler = rasterDataFetchHandler;
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

	public double getRemoteAzimuth()
	{
		return remoteAzimuth;
	}

	public void setRemoteAzimuth(double remoteAzimuth)
	{
		this.remoteAzimuth = remoteAzimuth;
	}

	public double getRemoteElevationAngle()
	{
		return remoteElevationAngle;
	}

	public void setRemoteElevationAngle(double remoteElevationAngle)
	{
		this.remoteElevationAngle = remoteElevationAngle;
	}
	
	
	public interface RasterDataFetchHandler 
	{
		public double getRasterData(double latitude, double longitude) throws Exception;
	}
	
	
}
