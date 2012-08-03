package us.wthr.jdem846.image;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.gis.CoordinateSpaceAdjuster;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.EquirectangularProjection;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.canvas.CanvasProjection;
import us.wthr.jdem846.color.ColorAdjustments;

public class SimpleGeoImage 
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(SimpleGeoImage.class);
	
	private String imageFile;
	
	private BufferedImage image = null;
	private Raster raster = null;
	
	private int height;
	private int width;
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	private double longitudeResolution;
	private double latitudeResolution;
	
	private MapProjection mapProjection;
	private CanvasProjection canvasProjection;
	
	private CoordinateSpaceAdjuster coordinateSpaceAdjuster;
	
	private MapPoint mapPoint = new MapPoint();
	
	private int[] rgbaBuffer0 = new int[4];
	
	private int[] rgbaBuffer00 = new int[4];
	private int[] rgbaBuffer01 = new int[4];
	private int[] rgbaBuffer10 = new int[4];
	private int[] rgbaBuffer11 = new int[4];
	
	private boolean hasAlphaChannel = false;
	
	public SimpleGeoImage()
	{
		
	}
	

	
	public SimpleGeoImage(String imagePath, double north, double south, double east, double west) throws DataSourceException
	{
		imageFile = imagePath;
		
		Dimension dimensions = null;
		
		try {
			dimensions = fetchImageDimensions();
		} catch (IOException ex) {
			throw new DataSourceException("Error determining image dimensions: " + ex.getMessage(), ex);
		}
		
		height = dimensions.height;
		width = dimensions.width;
		
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		
		
		update();
	}
	
	public void update()
	{
		this.latitudeResolution = (north - south) / height;
		this.longitudeResolution = (east - west) / width;
		
		
		mapProjection = new EquirectangularProjection(north, south, east, west, width, height);
		canvasProjection = new CanvasProjection(mapProjection, north, south, east, west, width, height);
		
		coordinateSpaceAdjuster = new CoordinateSpaceAdjuster(north, south, east, west);
		
	}
	
	public boolean isLoaded()
	{
		return (image != null);
	}
	
	public void load() throws DataSourceException
	{
		if (isLoaded()) {
			throw new DataSourceException("Image data already loaded");
		}
		
		try {
			image = (BufferedImage) ImageIcons.loadImage(this.imageFile);
		} catch (IOException ex) {
			throw new DataSourceException("Failed to load image: " + ex.getMessage(), ex);
		}
		
		raster = image.getRaster();
		
		if (image.getAlphaRaster() != null) {
			hasAlphaChannel = true;
		} else {
			hasAlphaChannel = false;
		}
	}
	
	public void unload() throws DataSourceException
	{
		if (!isLoaded()) {
			throw new DataSourceException("Image data not loaded");
		}
		
		image = null;
		raster = null;
	}
	
	public boolean contains(double latitude, double longitude)
	{
		return coordinateSpaceAdjuster.contains(latitude, longitude);
	}
	
	protected Dimension fetchImageDimensions() throws IOException
	{
		ImageInputStream in = ImageIO.createImageInputStream(new File(this.imageFile));
		
		try {
			final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			if (readers.hasNext()) {
				ImageReader reader = readers.next();
				try {
					reader.setInput(in);
					return new Dimension(reader.getWidth(0), reader.getHeight(0));
				} finally {
					reader.dispose();
				}
			}
		} finally {
			if (in != null)
				in.close();
		}
		
		return null;
	}
	
	
	public boolean getColor(double latitude, double longitude, int[] rgba) throws DataSourceException
	{
		if (!isLoaded()) {
			throw new DataSourceException("Image data not loaded");
		}
		return getColor(latitude, longitude, latitudeResolution, longitudeResolution, rgba);
	}
	
	public boolean getColor(double latitude, double longitude, double effectiveLatitudeResolution, double effectiveLongitudeResolution, int[] rgba) throws DataSourceException
	{
		
		double adjLatitude = 0;//coordinateSpaceAdjuster.adjustLatitude(latitude);
		double adjLongitude = 0;//coordinateSpaceAdjuster.adjustLongitude(longitude);
		
		if ((adjLatitude = coordinateSpaceAdjuster.adjustLatitude(latitude)) == DemConstants.ELEV_NO_DATA) {
			return false;
		}
		
		if ((adjLongitude = coordinateSpaceAdjuster.adjustLongitude(longitude)) == DemConstants.ELEV_NO_DATA) {
			return false;
		}
		
		return _getColor(adjLatitude, adjLongitude, effectiveLatitudeResolution, effectiveLongitudeResolution, rgba);
		
	}
	
	
	
	
	
	protected boolean _getColor(double latitude, double longitude, double effectiveLatitudeResolution, double effectiveLongitudeResolution, int[] rgba) throws DataSourceException
	{
		if (!isLoaded()) {
			throw new DataSourceException("Image data not loaded");
		}
		
		if (effectiveLatitudeResolution == DemConstants.ELEV_UNDETERMINED) {
			effectiveLatitudeResolution = latitudeResolution;
		}
		
		if (effectiveLongitudeResolution == DemConstants.ELEV_UNDETERMINED) {
			effectiveLongitudeResolution = longitudeResolution;
		}
		
		if (latitude >= south && latitude <= north && longitude >= west && longitude <= east) {
			
			double north = latitude + (effectiveLatitudeResolution / 2.0);
			double south = latitude - (effectiveLatitudeResolution / 2.0);
			
			double west = longitude - (effectiveLongitudeResolution / 2.0);
			double east = longitude + (effectiveLongitudeResolution / 2.0);
			
			
			resetRgbaBuffer(rgba);
			
			double samples = 0;
			
			double rows = (north - south) / latitudeResolution;
			double columns = (east - west) / longitudeResolution;
			
			if (rows < 1 && columns < 1) {
				
				
				getColorBilinear(latitude, longitude, rgba);
				
				/*
				try {
					canvasProjection.getPoint(latitude, longitude, 0.0, mapPoint);
				} catch (MapProjectionException ex) {
					throw new DataSourceException("Error getting x/y point from coordinates: " + ex.getMessage(), ex);
				}
				
				double x00 = MathExt.floor(mapPoint.column);
				double y00 = MathExt.floor(mapPoint.row);
				

				double x11 = MathExt.ceil(mapPoint.column);
				double y11 = MathExt.ceil(mapPoint.row);
				
				if (x11 == x00)
					x11 += 1;
				if (y11 == y00)
					y11 += 1;
				*/
				
			} else {
			
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
			}
			
			return true;
		} else {
			return false;
		}
	}
	

	
	public boolean getColorBilinear(double latitude, double longitude, int[] rgba) throws DataSourceException
	{
		if (!isLoaded()) {
			throw new DataSourceException("Image data not loaded");
		}
		
		double x00, y00;
		double x01, y01;
		
		try {
			canvasProjection.getPoint(latitude, longitude, 0.0, mapPoint);
		} catch (MapProjectionException ex) {
			throw new DataSourceException("Error getting x/y point from coordinates: " + ex.getMessage(), ex);
		}
		
		x00 = (int) mapPoint.column;
		y00 = (int) mapPoint.row;
		x01 = mapPoint.column;
		y01 = mapPoint.row;
		
		double xFrac = x01 - x00;
		double yFrac = y01 - y00;
		
		getPixel((int) x00, (int) y00, rgbaBuffer00);
		getPixel((int) x00 + 1, (int) y00, rgbaBuffer01);
		getPixel((int) x00, (int) y00 + 1, rgbaBuffer10);
		getPixel((int) x00 + 1, (int) y00 + 1, rgbaBuffer11);
		
		
		ColorAdjustments.interpolateColor(rgbaBuffer00
											, rgbaBuffer01
											, rgbaBuffer10
											, rgbaBuffer11
											, rgba
											, xFrac, yFrac);

		return true;
	}
	
	public boolean getColorNearestNeighbor(double latitude, double longitude, int[] rgba) throws DataSourceException
	{
		if (!isLoaded()) {
			throw new DataSourceException("Image data not loaded");
		}
		if (latitude >= south && latitude <= north && longitude >= west && longitude <= east) {
			try {
				canvasProjection.getPoint(latitude, longitude, 0.0, mapPoint);
			} catch (MapProjectionException ex) {
				throw new DataSourceException("Error getting x/y point from coordinates: " + ex.getMessage(), ex);
			}
			
			getPixel((int) Math.round(mapPoint.column), (int) Math.round(mapPoint.row), rgba);
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean getPixel(int x, int y, int[] rgba) throws DataSourceException
	{
		if (!isLoaded()) {
			throw new DataSourceException("Image data not loaded");
		}
		
		if (x < 0 || x >= raster.getWidth() || y < 0 || y >= raster.getHeight()) {
			return false;
		}
		
		raster.getPixel(x, y, rgba);
		
		if (!hasAlphaChannel) {
			rgba[3] = 0xFF;
		}
		
		return true;
	}
	
	
	private void resetRgbaBuffer(int[] rgba)
	{
		rgba[0] = rgba[1] = rgba[2] = rgba[3] = 0x0;
	}

	

	public String getImageFile()
	{
		return imageFile;
	}

	public double getNorth()
	{
		return north;
	}



	public void setNorth(double north)
	{
		this.north = north;
		update();
	}



	public double getSouth()
	{
		return south;
	}



	public void setSouth(double south)
	{
		this.south = south;
		update();
	}



	public double getEast()
	{
		return east;
	}



	public void setEast(double east)
	{
		this.east = east;
		update();
	}



	public double getWest()
	{
		return west;
	}



	public void setWest(double west)
	{
		this.west = west;
		update();
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public double getLatitudeResolution()
	{
		return latitudeResolution;
	}

	public double getLongitudeResolution()
	{
		return longitudeResolution;
	}
	
	/** Creates copy.
	 * 
	 * @return
	 */
	public SimpleGeoImage copy() throws DataSourceException
	{
		SimpleGeoImage copy = new SimpleGeoImage(imageFile, north, south, east, west);
		
		if (this.isLoaded()) {
			copy.image = this.image;
			copy.raster = this.raster;
		}
		
		return copy;
	}

}
