package us.wthr.jdem846.render;

import java.awt.Color;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;

public class ModelDimensions2D
{
	private static Log log = Logging.getLog(ModelDimensions2D.class);
	
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
	public double outputLongitudeResolution;
	public double outputLatitudeResolution;
	
	
	protected ModelDimensions2D()
	{
		
	}

	
	public ModelDimensions2D(ModelContext modelContext)
	{
		init(modelContext);
	}
	
	
	public void init(ModelContext modelContext)
	{
		RasterDataContext rasterDataContext = modelContext.getRasterDataContext();
		ImageDataContext imageDataContext = modelContext.getImageDataContext();
		ModelOptions modelOptions = modelContext.getModelOptions();
		
		/*
		north = modelContext.getNorth();
		south = modelContext.getSouth();
		east = modelContext.getEast();
		west = modelContext.getWest();
		*/
		north = -90;
		south = 90;
		east = -180;
		west = 180;
		
		latitudeResolution = Double.MAX_VALUE;
		longitudeResolution = Double.MAX_VALUE;
		
		if (rasterDataContext != null) {
			for (RasterData rasterData : rasterDataContext.getRasterDataList()) {
				latitudeResolution = MathExt.min(latitudeResolution, rasterData.getLatitudeResolution());
				longitudeResolution = MathExt.min(longitudeResolution, rasterData.getLongitudeResolution());
				
				north = MathExt.max(north, rasterData.getNorth());
				south = MathExt.min(south, rasterData.getSouth());
				east = MathExt.max(east, rasterData.getEast());
				west = MathExt.min(west, rasterData.getWest());
			}
		}
		
		if (imageDataContext != null) {
			for (SimpleGeoImage image : imageDataContext.getImageList()) {
				latitudeResolution = MathExt.min(latitudeResolution, image.getLatitudeResolution());
				longitudeResolution = MathExt.min(longitudeResolution, image.getLongitudeResolution());
			
				north = MathExt.max(north, image.getNorth());
				south = MathExt.min(south, image.getSouth());
				east = MathExt.max(east, image.getEast());
				west = MathExt.min(west, image.getWest());
			}
		}
		
		if (north > 90) {
			north = 90;
		}
		
		if (south < -90) {
			south = -90;
		}
		
		if (east > 180) {
			east = 180;
		}
		
		if (west < -180) {
			west = -180;
		}
		
		
		//double scale = modelContext.getModelOptions().getProjection().getZoom();
		
		dataRows = (int) MathExt.round((north - south) / latitudeResolution);
		dataColumns = (int) MathExt.round((east - west) / longitudeResolution);
		
		outputHeight = modelOptions.getHeight();
		outputWidth = modelOptions.getWidth();
		
		double xdimRatio = (double)outputWidth / (double)dataColumns;
		double ydimRatio = (double)outputHeight / (double)dataRows;
		
		outputLongitudeResolution = longitudeResolution / xdimRatio;
		outputLatitudeResolution = latitudeResolution / ydimRatio;

		
	}
	
	public static ModelDimensions2D getModelDimensions(ModelContext modelContext)
	{
		
		ModelDimensions2D modelDimensions = new ModelDimensions2D(modelContext);
		return modelDimensions;
	}
	
	public double getMetersResolution(double meanRadius)
	{
		
		double lat = (getNorth() - getSouth()) / 2.0;
		double lon = (getEast() - getWest()) / 2.0;
		return getMetersResolution(meanRadius, lat, lon, getLatitudeResolution(), getLongitudeResolution());

	}
	
	public double getMetersResolution(double meanRadius, double latitude, double longitude)
	{
		return ModelDimensions2D.getMetersResolution(meanRadius, latitude, longitude, getLatitudeResolution(), getLongitudeResolution());
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
	
	
	public ModelDimensions2D copy()
	{
		ModelDimensions2D copy = new ModelDimensions2D();
		copy.dataColumns = this.dataColumns;
		copy.dataRows = this.dataRows;
		copy.north = this.north;
		copy.south = this.south;
		copy.east = this.east;
		copy.west = this.west;
		copy.latitudeResolution = this.latitudeResolution;
		copy.longitudeResolution = this.longitudeResolution;
		copy.outputHeight = this.outputHeight;
		copy.outputWidth = this.outputWidth;
		copy.outputLatitudeResolution = this.outputLatitudeResolution;
		copy.outputLongitudeResolution = this.outputLongitudeResolution;

		return copy;
	}
	
}
