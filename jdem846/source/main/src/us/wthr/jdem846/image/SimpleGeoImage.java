package us.wthr.jdem846.image;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.EquirectangularProjection;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.render.CanvasProjection;

public class SimpleGeoImage 
{
	private static Log log = Logging.getLog(SimpleGeoImage.class);
	
	private BufferedImage image;
	private Raster raster;
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	private double longitudeResolution;
	private double latitudeResolution;
	
	private MapProjection mapProjection;
	private CanvasProjection canvasProjection;
	
	private MapPoint mapPoint = new MapPoint();
	
	private int[] rgbaBuffer0 = new int[4];
	
	private int[] rgbaBuffer00 = new int[4];
	private int[] rgbaBuffer01 = new int[4];
	private int[] rgbaBuffer10 = new int[4];
	private int[] rgbaBuffer11 = new int[4];
	
	public SimpleGeoImage(ModelContext modelContext, String imagePath, double north, double south, double east, double west) throws IOException
	{
		image = (BufferedImage) ImageIcons.loadImage(imagePath);
		raster = image.getRaster();
		
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		
		this.latitudeResolution = (north - south) / image.getHeight();
		this.longitudeResolution = (east - west) / image.getWidth();
		
		mapProjection = new EquirectangularProjection(north, south, east, west, image.getWidth(), image.getHeight());
		canvasProjection = new CanvasProjection(modelContext, mapProjection, north, south, east, west, image.getWidth(), image.getHeight());
		
	}
	
	
	
	public boolean getColor(double latitude, double longitude, int[] rgba) throws ImageException
	{
		return getColor(latitude, longitude, latitudeResolution, longitudeResolution, rgba);
	}
	
	
	public boolean getColor(double latitude, double longitude, double effectiveLatitudeResolution, double effectiveLongitudeResolution, int[] rgba) throws ImageException
	{
		if (latitude >= south && latitude <= north && longitude >= west && longitude <= east) {
			
			double north = latitude + (effectiveLatitudeResolution / 2.0);
			double south = latitude - (effectiveLatitudeResolution / 2.0);
			
			double west = longitude - (effectiveLongitudeResolution / 2.0);
			double east = longitude + (effectiveLongitudeResolution / 2.0);
			
			
			resetRgbaBuffer(rgba);
			
			double samples = 0;
			
			for (double x = west; x <= east; x+=longitudeResolution) {
				for (double y = north; y >= south; y-=latitudeResolution) {
					if (getColorBilinear(y, x, rgbaBuffer0)) {
						rgba[0] += rgbaBuffer0[0];
						rgba[1] += rgbaBuffer0[1];
						rgba[2] += rgbaBuffer0[2];
						rgba[3] += rgbaBuffer0[3];
						samples++;
					}
				}
			}
			
			if (samples > 0) {
				rgba[0] = (int) MathExt.round((double)rgba[0] / samples);
				rgba[1] = (int) MathExt.round((double)rgba[1] / samples);
				rgba[2] = (int) MathExt.round((double)rgba[2] / samples);
				rgba[3] = (int) MathExt.round((double)rgba[3] / samples);
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean getColorBilinear(double latitude, double longitude, int[] rgba) throws ImageException
	{
		double x00, y00;
		double x01, y01;
		
		try {
			canvasProjection.getPoint(latitude, longitude, 0.0, mapPoint);
		} catch (MapProjectionException ex) {
			throw new ImageException("Error getting x/y point from coordinates: " + ex.getMessage(), ex);
		}
		
		x00 = (int) mapPoint.column;
		y00 = (int) mapPoint.row;
		x01 = mapPoint.column;
		y01 = mapPoint.row;
		
		double xFrac = x01 - x00;
		double yFrac = y01 - y00;
		
		raster.getPixel((int) x00, (int) y00, rgbaBuffer00);
		raster.getPixel((int) x00 + 1, (int) y00, rgbaBuffer01);
		raster.getPixel((int) x00, (int) y00 + 1, rgbaBuffer10);
		raster.getPixel((int) x00 + 1, (int) y00 + 1, rgbaBuffer11);
		
		rgba[0] = MathExt.interpolate(rgbaBuffer00[0], rgbaBuffer01[0], rgbaBuffer10[0], rgbaBuffer11[0], xFrac, yFrac);
		rgba[1] = MathExt.interpolate(rgbaBuffer00[1], rgbaBuffer01[1], rgbaBuffer10[1], rgbaBuffer11[1], xFrac, yFrac);
		rgba[2] = MathExt.interpolate(rgbaBuffer00[2], rgbaBuffer01[2], rgbaBuffer10[2], rgbaBuffer11[2], xFrac, yFrac);
		rgba[3] = MathExt.interpolate(rgbaBuffer00[3], rgbaBuffer01[3], rgbaBuffer10[3], rgbaBuffer11[3], xFrac, yFrac);
		
		return true;
	}
	
	public boolean getColorNearestNeighbor(double latitude, double longitude, int[] rgba) throws ImageException
	{
		if (latitude >= south && latitude <= north && longitude >= west && longitude <= east) {
			try {
				canvasProjection.getPoint(latitude, longitude, 0.0, mapPoint);
			} catch (MapProjectionException ex) {
				throw new ImageException("Error getting x/y point from coordinates: " + ex.getMessage(), ex);
			}
			
			getPixel((int) Math.round(mapPoint.column), (int) Math.round(mapPoint.row), rgba);
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean getPixel(int x, int y, int[] rgba)
	{
		if (x < 0 || x >= raster.getWidth() || y < 0 || y >= raster.getHeight()) {
			return false;
		}
		
		raster.getPixel(x, y, rgba);
		
		return true;
	}
	
	
	private void resetRgbaBuffer(int[] rgba)
	{
		rgba[0] = rgba[1] = rgba[2] = rgba[3] = 0x0;
	}
	
	
	

}
