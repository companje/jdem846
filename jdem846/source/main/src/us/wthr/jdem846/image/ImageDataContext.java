package us.wthr.jdem846.image;

import java.util.ArrayList;
import java.util.List;

import us.wthr.jdem846.DataContext;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;

public class ImageDataContext implements DataContext
{
	
	private static Log log = Logging.getLog(ImageDataContext.class);
	
	private boolean isDisposed = false;
	
	private double east = 180.0;
	private double west = -180.0;
	private double north = 90.0;
	private double south = -90.0;
	
	private List<SimpleGeoImage> imageList = new ArrayList<SimpleGeoImage>();
	
	public ImageDataContext()
	{
		
	}

	@Override
	public void prepare() throws DataSourceException
	{
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
			}
			
		} else {
			east = 180.0;
			west = -180.0;
			north = 90.0;
			south = -90.0;
		}
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
		
		for (SimpleGeoImage image : imageList) {
			if (image.contains(latitude, longitude)) {
				try {
					image.getColor(latitude, longitude, rgba);
				} catch (DataSourceException ex) {
					throw new DataSourceException("Error retrieving color values: " + ex.getMessage(), ex);
				}
				return true;
			}
		}
		
		return false;
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
	
}
