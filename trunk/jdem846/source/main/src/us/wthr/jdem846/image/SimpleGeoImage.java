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
import us.wthr.jdem846.graphics.Texture;
import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.util.ColorUtil;

public class SimpleGeoImage implements InputSourceData, ISimpleGeoImage
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(SimpleGeoImage.class);

	private String imageFile;

	private IIntBuffer rasterBuffer = null;

	private IImageDefinition imageDefinition;
	
	private MapProjection mapProjection;
	private CanvasProjection canvasProjection;

	private CoordinateSpaceAdjuster coordinateSpaceAdjuster;

	private MapPoint mapPoint = new MapPoint();

	private boolean hasAlphaChannel = false;

	public SimpleGeoImage()
	{
		this.imageDefinition = new ImageDefinition();
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
		
		this.imageDefinition = new ImageDefinition();
		imageDefinition.setImageHeight(dimensions.height);
		imageDefinition.setImageWidth(dimensions.width);
		imageDefinition.setNorth(north);
		imageDefinition.setSouth(south);
		imageDefinition.setEast(east);
		imageDefinition.setWest(west);

		update();
	}

	public void update()
	{
		if (imageDefinition.getLatitudeResolution() == 0) {
			imageDefinition.determineLatitudeResolution();
		}
		
		if (imageDefinition.getLongitudeResolution() == 0) {
			imageDefinition.determineLongitudeResolution();
		}

		mapProjection = new EquirectangularProjection(imageDefinition.getNorth()
													, imageDefinition.getSouth()
													, imageDefinition.getEast()
													, imageDefinition.getWest()
													, imageDefinition.getImageWidth()
													, imageDefinition.getImageHeight());
		
		canvasProjection = new CanvasProjection(mapProjection
												, imageDefinition.getNorth()
												, imageDefinition.getSouth()
												, imageDefinition.getEast()
												, imageDefinition.getWest()
												, imageDefinition.getImageWidth()
												, imageDefinition.getImageHeight());

		coordinateSpaceAdjuster = new CoordinateSpaceAdjuster(imageDefinition.getNorth()
												, imageDefinition.getSouth()
												, imageDefinition.getEast()
												, imageDefinition.getWest());

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
	
	
	public int getColor(double latitude, double longitude) throws DataSourceException
	{
		int[] rgba = {0, 0, 0, 0};
		if (getColor(latitude, longitude, rgba)) {
			return ColorUtil.rgbaToInt(rgba);
		} else {
			return 0x0;
		}
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
		return getColor(latitude, longitude, imageDefinition.getLatitudeResolution(), imageDefinition.getLongitudeResolution(), rgba, nearestNeighbor);
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
			effectiveLatitudeResolution = imageDefinition.getLatitudeResolution();
		}

		if (effectiveLongitudeResolution == DemConstants.ELEV_UNDETERMINED) {
			effectiveLongitudeResolution = imageDefinition.getLongitudeResolution();
		}

		int[] rgbaBuffer0 = new int[4];

		if (latitude >= imageDefinition.getSouth() && latitude <= imageDefinition.getNorth() && longitude >= imageDefinition.getWest() && longitude <= imageDefinition.getEast()) {

			if (nearestNeighbor) {
				getColorNearestNeighbor(latitude, longitude, rgba);
			} else {

				double north = latitude + (effectiveLatitudeResolution / 2.0);
				double south = latitude - (effectiveLatitudeResolution / 2.0);

				double west = longitude - (effectiveLongitudeResolution / 2.0);
				double east = longitude + (effectiveLongitudeResolution / 2.0);

				resetRgbaBuffer(rgba);

				double samples = 0;

				double rows = (north - south) / imageDefinition.getLatitudeResolution();
				double columns = (east - west) / imageDefinition.getLongitudeResolution();

				if (rows < 1 && columns < 1) {
					getColorBilinear(latitude, longitude, rgba);
				} else {

					for (double x = west; x <= east; x += imageDefinition.getLongitudeResolution()) {
						for (double y = north; y >= south; y -= imageDefinition.getLatitudeResolution()) {
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
		if (latitude >= imageDefinition.getSouth() && latitude <= imageDefinition.getNorth() && longitude >= imageDefinition.getWest() && longitude <= imageDefinition.getEast()) {
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
		
		//if (x < 0) {
		//	ColorUtil.intToRGBA(ColorUtil.RED, rgba);
		//	return true;
			//x = getWidth() - 1 - (0 - x);
		//}
		
		//if (x >= getWidth()) {
		//	ColorUtil.intToRGBA(ColorUtil.RED, rgba);
		//	return true;
			//x = 0 + (x - getWidth() - 1);
		//}
		
		
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			
			return false; // Throw?
		}

		long index = (y * imageDefinition.getImageWidth()) + x;
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
		return imageDefinition.getNorth();
	}

	@Override
	public void setNorth(double north)
	{
		imageDefinition.setNorth(north);
		update();
	}

	@Override
	public double getSouth()
	{
		return imageDefinition.getSouth();
	}
	
	@Override
	public void setSouth(double south)
	{
		imageDefinition.setSouth(south);
		update();
	}

	@Override
	public double getEast()
	{
		return imageDefinition.getEast();
	}

	@Override
	public void setEast(double east)
	{
		imageDefinition.setEast(east);
		update();
	}

	@Override
	public double getWest()
	{
		return imageDefinition.getWest();
	}

	@Override
	public void setWest(double west)
	{
		imageDefinition.setWest(west);
		update();
	}

	@Override
	public int getHeight()
	{
		return imageDefinition.getImageHeight();
	}

	@Override
	public int getWidth()
	{
		return imageDefinition.getImageWidth();
	}

	@Override
	public double getLatitudeResolution()
	{
		return imageDefinition.getLatitudeResolution();
	}

	@Override
	public double getLongitudeResolution()
	{
		return imageDefinition.getLongitudeResolution();
	}
	
	public Texture getAsTexture()
	{
		Texture tex = new Texture(imageDefinition.getImageWidth()
								, imageDefinition.getImageHeight()
								, imageDefinition.getNorth()
								, imageDefinition.getSouth()
								, imageDefinition.getEast()
								, imageDefinition.getWest()
								, rasterBuffer);
		return tex;
	}

	
	public IImageDefinition getImageDefinition()
	{
		return imageDefinition;
	}
	
	/**
	 * Creates copy.
	 * 
	 * @return
	 */
	public SimpleGeoImage copy() throws DataSourceException
	{
		SimpleGeoImage copy = new SimpleGeoImage(imageFile
												, imageDefinition.getNorth()
												, imageDefinition.getSouth()
												, imageDefinition.getEast()
												, imageDefinition.getWest());

		if (this.isLoaded()) {
			copy.rasterBuffer = this.rasterBuffer;
			//copy.image = this.image;
			//copy.raster = this.raster;
			copy.hasAlphaChannel = this.hasAlphaChannel;
		}

		return copy;
	}

}
