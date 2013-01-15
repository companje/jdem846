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
import us.wthr.jdem846.buffers.BufferFactory;
import us.wthr.jdem846.buffers.IIntBuffer;
import us.wthr.jdem846.canvas.CanvasProjection;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.gis.CoordinateSpaceAdjuster;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.EquirectangularProjection;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.util.ColorUtil;

public class SimpleGeoImage implements InputSourceData, ISimpleGeoImageDefinition
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(SimpleGeoImage.class);

	private String imageFile;

	//private BufferedImage image = null;
	//private Raster raster = null;
	
	private IIntBuffer rasterBuffer = null;
	
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
		return (rasterBuffer != null);
	}

	public void load() throws DataSourceException
	{
		if (isLoaded()) {
			throw new DataSourceException("Image data already loaded");
		}
		
		BufferedImage image = null;
		try {
			image = (BufferedImage) ImageIcons.loadImage(this.imageFile);
		} catch (IOException ex) {
			throw new DataSourceException("Failed to load image: " + ex.getMessage(), ex);
		}

		Raster raster = image.getRaster();
		
		if (image.getAlphaRaster() != null) {
			hasAlphaChannel = true;
		} else {
			hasAlphaChannel = false;
		}
		
		int[] rgba = {0, 0, 0, 0};
		
		long capacity = raster.getWidth() * raster.getHeight();
		this.rasterBuffer = BufferFactory.allocateIntBuffer(capacity);
		for (int y = 0; y < raster.getHeight(); y++) {
			for (int x = 0; x < raster.getWidth(); x++) {
				
				raster.getPixel(x, y, rgba);
				if (!hasAlphaChannel) {
					rgba[3] = 0xFF;
				}
				
				long index = ((long) y * (long)raster.getWidth()) + ((long)x);
				int c = ColorUtil.rgbaToInt(rgba);
				rasterBuffer.put(index, c);
				
			}
		}
		
		image = null;
		raster = null;	
		
	}

	public void unload() throws DataSourceException
	{
		if (!isLoaded()) {
			throw new DataSourceException("Image data not loaded");
		}
		
		//rasterBuffer.dispose(); // Cannot dispose, the buffer may be shared with another copy
		rasterBuffer = null;
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
		return getColor(latitude, longitude, rgba, false);
	}

	public boolean getColor(double latitude, double longitude, int[] rgba, boolean nearestNeighbor) throws DataSourceException
	{
		if (!isLoaded()) {
			throw new DataSourceException("Image data not loaded");
		}
		return getColor(latitude, longitude, latitudeResolution, longitudeResolution, rgba, nearestNeighbor);
	}

	public boolean getColor(double latitude, double longitude, double effectiveLatitudeResolution, double effectiveLongitudeResolution, int[] rgba) throws DataSourceException
	{
		return getColor(latitude, longitude, effectiveLatitudeResolution, effectiveLongitudeResolution, rgba, false);
	}

	public boolean getColor(double latitude, double longitude, double effectiveLatitudeResolution, double effectiveLongitudeResolution, int[] rgba, boolean nearestNeighbor) throws DataSourceException
	{

		double adjLatitude = 0;// coordinateSpaceAdjuster.adjustLatitude(latitude);
		double adjLongitude = 0;// coordinateSpaceAdjuster.adjustLongitude(longitude);

		if ((adjLatitude = coordinateSpaceAdjuster.adjustLatitude(latitude)) == DemConstants.ELEV_NO_DATA) {
			return false;
		}

		if ((adjLongitude = coordinateSpaceAdjuster.adjustLongitude(longitude)) == DemConstants.ELEV_NO_DATA) {
			return false;
		}

		return _getColor(adjLatitude, adjLongitude, effectiveLatitudeResolution, effectiveLongitudeResolution, rgba, nearestNeighbor);

	}

	protected boolean _getColor(double latitude, double longitude, double effectiveLatitudeResolution, double effectiveLongitudeResolution, int[] rgba) throws DataSourceException
	{
		return _getColor(latitude, longitude, effectiveLatitudeResolution, effectiveLongitudeResolution, rgba, false);
	}

	protected boolean _getColor(double latitude, double longitude, double effectiveLatitudeResolution, double effectiveLongitudeResolution, int[] rgba, boolean nearestNeighbor) throws DataSourceException
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

		int[] rgbaBuffer0 = new int[4];

		if (latitude >= south && latitude <= north && longitude >= west && longitude <= east) {

			if (nearestNeighbor) {
				getColorNearestNeighbor(latitude, longitude, rgba);
			} else {

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
				} else {

					for (double x = west; x <= east; x += longitudeResolution) {
						for (double y = north; y >= south; y -= latitudeResolution) {
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
						rgba[0] = (int) MathExt.round((double) rgba[0] / samples);
						rgba[1] = (int) MathExt.round((double) rgba[1] / samples);
						rgba[2] = (int) MathExt.round((double) rgba[2] / samples);
						rgba[3] = (int) MathExt.round((double) rgba[3] / samples);
					}
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

		int[] rgbaBuffer00 = new int[4];
		int[] rgbaBuffer01 = new int[4];
		int[] rgbaBuffer10 = new int[4];
		int[] rgbaBuffer11 = new int[4];

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

		ColorUtil.interpolateColor(rgbaBuffer00, rgbaBuffer01, rgbaBuffer10, rgbaBuffer11, rgba, xFrac, yFrac);

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

		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			return false; // Throw?
		}

		long index = (y * width) + x;
		if (index < 0 || index >= rasterBuffer.capacity()) {
			return false; // Throw?
		}
		
		int c = rasterBuffer.get(index);
		ColorUtil.intToRGBA(c, rgba);

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

	@Override
	public double getNorth()
	{
		return north;
	}

	@Override
	public void setNorth(double north)
	{
		this.north = north;
		update();
	}

	@Override
	public double getSouth()
	{
		return south;
	}
	
	@Override
	public void setSouth(double south)
	{
		this.south = south;
		update();
	}

	@Override
	public double getEast()
	{
		return east;
	}

	@Override
	public void setEast(double east)
	{
		this.east = east;
		update();
	}

	@Override
	public double getWest()
	{
		return west;
	}

	@Override
	public void setWest(double west)
	{
		this.west = west;
		update();
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public double getLatitudeResolution()
	{
		return latitudeResolution;
	}

	@Override
	public double getLongitudeResolution()
	{
		return longitudeResolution;
	}

	/**
	 * Creates copy.
	 * 
	 * @return
	 */
	public SimpleGeoImage copy() throws DataSourceException
	{
		SimpleGeoImage copy = new SimpleGeoImage(imageFile, north, south, east, west);

		if (this.isLoaded()) {
			copy.rasterBuffer = this.rasterBuffer;
			//copy.image = this.image;
			//copy.raster = this.raster;
			copy.hasAlphaChannel = this.hasAlphaChannel;
		}

		return copy;
	}

}
