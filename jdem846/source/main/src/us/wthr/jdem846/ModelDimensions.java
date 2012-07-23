package us.wthr.jdem846;

import us.wthr.jdem846.canvas.CanvasProjection;
import us.wthr.jdem846.canvas.CanvasProjection3d;
import us.wthr.jdem846.canvas.CanvasProjectionGlobe;
import us.wthr.jdem846.canvas.CanvasProjectionTypeEnum;
import us.wthr.jdem846.canvas.LatLonResolution;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;

public abstract class ModelDimensions
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(ModelDimensions.class);
	
	public double north;
	public double south;
	public double east;
	public double west;
	
	public int dataRows;
	public int dataColumns;
	public double latitudeResolution;
	public double longitudeResolution;
	
	public int outputWidth;
	public int outputHeight;
	
	public double outputLongitudeResolutionTrue;
	public double outputLatitudeResolutionTrue;
	
	public double outputLongitudeResolution;
	public double outputLatitudeResolution;
	
	
	public ModelDimensions()
	{
		
	}
	
	public abstract ModelDimensions copy();
	

	
	public double getMetersResolution(double meanRadius)
	{
		
		double lat = (getNorth() - getSouth()) / 2.0;
		double lon = (getEast() - getWest()) / 2.0;
		return getMetersResolution(meanRadius, lat, lon, getLatitudeResolution(), getLongitudeResolution());

	}
	
	public double getMetersOutputResolution(double meanRadius)
	{
		
		double lat = (getNorth() - getSouth()) / 2.0;
		double lon = (getEast() - getWest()) / 2.0;
		return getMetersResolution(meanRadius, lat, lon, getOutputLatitudeResolution(), getOutputLongitudeResolution());

	}
	
	public double getMetersTrueOutputResolution(double meanRadius)
	{
		
		double lat = (getNorth() - getSouth()) / 2.0;
		double lon = (getEast() - getWest()) / 2.0;
		return getMetersResolution(meanRadius, lat, lon, getOutputLatitudeResolutionTrue(), getOutputLongitudeResolutionTrue());

	}
	
	public double getMetersResolution(double meanRadius, double latitude, double longitude)
	{
		return ModelDimensions.getMetersResolution(meanRadius, latitude, longitude, getLatitudeResolution(), getLongitudeResolution());
	}
	
	public static double getMetersResolution(double meanRadius, double latitude, double longitude, double latitudeResolution, double longitudeResolution)
	{
		double lat1 = latitude;
		double lon1 = longitude;
		double lat2 = lat1 + latitudeResolution;
		double lon2 = lon1 + longitudeResolution;
		double R = meanRadius;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		

		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon/2) * Math.sin(dLon/2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = R * c * 1000;
		return d;
	}


	public double getNorth()
	{
		return north;
	}


	public double getSouth()
	{
		return south;
	}


	public double getEast()
	{
		return east;
	}


	public double getWest()
	{
		return west;
	}


	public int getDataRows()
	{
		return dataRows;
	}


	public int getDataColumns()
	{
		return dataColumns;
	}


	public double getLatitudeResolution()
	{
		return latitudeResolution;
	}


	public double getLongitudeResolution()
	{
		return longitudeResolution;
	}


	public int getOutputWidth()
	{
		return outputWidth;
	}


	public int getOutputHeight()
	{
		return outputHeight;
	}


	public double getOutputLongitudeResolution()
	{
		return outputLongitudeResolution;
	}


	public double getOutputLatitudeResolution()
	{
		return outputLatitudeResolution;
	}

	public double getOutputLongitudeResolutionTrue()
	{
		return outputLongitudeResolutionTrue;
	}

	public void setOutputLongitudeResolutionTrue(
			double outputLongitudeResolutionTrue)
	{
		this.outputLongitudeResolutionTrue = outputLongitudeResolutionTrue;
	}

	public double getOutputLatitudeResolutionTrue()
	{
		return outputLatitudeResolutionTrue;
	}

	public void setOutputLatitudeResolutionTrue(double outputLatitudeResolutionTrue)
	{
		this.outputLatitudeResolutionTrue = outputLatitudeResolutionTrue;
	}
	
	
	
}
