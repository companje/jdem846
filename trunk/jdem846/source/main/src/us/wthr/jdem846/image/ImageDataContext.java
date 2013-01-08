package us.wthr.jdem846.image;

import java.util.ArrayList;
import java.util.List;

import us.wthr.jdem846.DataContext;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.exceptions.ContextPrepareException;
import us.wthr.jdem846.util.ColorUtil;

public class ImageDataContext implements DataContext
{
	
	private static Log log = Logging.getLog(ImageDataContext.class);
	
	private boolean isDisposed = false;
	
	private double east = 180.0;
	private double west = -180.0;
	private double north = 90.0;
	private double south = -90.0;
	
	private double latitudeResolution = DemConstants.ELEV_NO_DATA;
	private double longitudeResolution = DemConstants.ELEV_NO_DATA;
	
	private int columns;
	private int rows;
	
	//private int[] rgbaBufferA = new int[4];
	
	private List<SimpleGeoImage> imageList = new ArrayList<SimpleGeoImage>();
	
	public ImageDataContext()
	{
		
	}

	@Override
	public void prepare() throws ContextPrepareException
	{
		
		latitudeResolution = Double.MAX_VALUE;
		longitudeResolution = Double.MAX_VALUE;
		
		if (getImageListSize() > 0) {
			east = -180.0;
			west = 180.0;
			north = -90.0;
			south = 90.0;
			
			for (SimpleGeoImage image : imageList) {
				east = MathExt.max(east, image.getEast());
				west = MathExt.min(west, image.getWest());
				north = MathExt.max(north, image.getNorth());
				south = MathExt.min(south, image.getSouth());
				
				latitudeResolution = MathExt.min(image.getLatitudeResolution(), latitudeResolution);
				longitudeResolution = MathExt.min(image.getLongitudeResolution(), longitudeResolution);
			}
			
		} else {
			east = 180.0;
			west = -180.0;
			north = 90.0;
			south = -90.0;
		}
		
		columns = (int) MathExt.round((east - west) / longitudeResolution);
		rows = (int) MathExt.round((north - south) / latitudeResolution);
		
	}
	
	public void loadImageData() throws DataSourceException
	{
		for (SimpleGeoImage image : imageList) {
			if (!image.isLoaded()) {
				image.load();
			}
		}
	}
	
	public void unloadImageData() throws DataSourceException
	{
		for (SimpleGeoImage image : imageList) {
			if (image.isLoaded()) {
				image.unload();
			}
		}
	}
	
	public boolean getColor(double latitude, double longitude, int[] rgba) throws DataSourceException
	{
		return getColor(latitude, longitude, rgba, false);
	}
	
	public boolean getColor(double latitude, double longitude, int[] rgba, boolean nearestNeighbor) throws DataSourceException
	{
		return getColor(latitude, longitude, DemConstants.ELEV_UNDETERMINED, DemConstants.ELEV_UNDETERMINED, rgba, nearestNeighbor);
	}
	
	public boolean getColor(double latitude, double longitude, double latitudeResolution, double longitudeResolution, int[] rgba) throws DataSourceException
	{
		return getColor(latitude, longitude, latitudeResolution, longitudeResolution, rgba, false);
	}
	
	public boolean getColor(double latitude, double longitude, double latitudeResolution, double longitudeResolution, int[] rgba, boolean nearestNeighbor) throws DataSourceException
	{
		boolean pixelLoaded = false;
		int i = 0;
		
		int[] rgbaBufferA = {0, 0, 0, 0};//new int[4];
		
		for (SimpleGeoImage image : imageList) {
			if (image.contains(latitude, longitude)) {
				try {
					image.getColor(latitude, longitude, latitudeResolution, longitudeResolution, rgbaBufferA, nearestNeighbor);
				} catch (DataSourceException ex) {
					throw new DataSourceException("Error retrieving color values: " + ex.getMessage(), ex);
				}
				
				pixelLoaded = true;
				
				if (i == 0) {
					rgba[0] = rgbaBufferA[0];
					rgba[1] = rgbaBufferA[1];
					rgba[2] = rgbaBufferA[2];
					rgba[3] = rgbaBufferA[3];
				} else {
					
					double r = 1.0 - ((double)rgbaBufferA[3] / 255.0);
					int a = Math.max(rgbaBufferA[3], rgba[3]);
					ColorUtil.interpolateColor(rgbaBufferA, rgba, rgba, r);
					rgba[3] = a;
					
				}
				
				i++;
			}
			
			if (pixelLoaded && rgba[3] == 255) {
				//break;
			}
		}
		
		return pixelLoaded;
	}
	
	
	public void addImage(SimpleGeoImage image)
	{
		imageList.add(image);
	}
	
	public SimpleGeoImage removeImage(int index)
	{
		return imageList.remove(index);
	}
	
	public boolean removeImage(SimpleGeoImage image)
	{
		return imageList.remove(image);
	}
	
	public List<SimpleGeoImage> getImageList()
	{
		return imageList;
	}
	
	public int getImageListSize()
	{
		return imageList.size();
	}
	
	
	@Override
	public void dispose() throws DataSourceException
	{
		log.info("Disposing image data context");
		
		if (isDisposed()) {
			throw new DataSourceException("Already disposed");
		}
		
		
		// TODO: Dispose of stuff
		
		this.unloadImageData();
		imageList.clear();
		
		isDisposed = true;
	}

	@Override
	public boolean isDisposed()
	{
		return isDisposed;
	}

	@Override
	public ImageDataContext copy() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Image context disposed. Cannot create copy");
		}
		
		ImageDataContext copy = new ImageDataContext();
		copy.north = this.north;
		copy.south = this.south;
		copy.east = this.east;
		copy.west = this.west;
		
		for (SimpleGeoImage image : imageList) {
			copy.addImage(image.copy());
		}
		
		return copy;
	}

	@Override
	public double getNorth()
	{
		return north;
	}

	@Override
	public double getSouth()
	{
		return south;
	}

	@Override
	public double getEast()
	{
		return east;
	}

	@Override
	public double getWest()
	{
		return west;
	}

	public double getLatitudeResolution()
	{
		return latitudeResolution;
	}

	public double getLongitudeResolution() 
	{
		return longitudeResolution;
	}

	public int getColumns()
	{
		return columns;
	}

	public int getRows()
	{
		return rows;
	}
	
	
	
}
