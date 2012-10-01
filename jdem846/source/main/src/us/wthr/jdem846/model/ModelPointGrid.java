package us.wthr.jdem846.model;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;

public abstract class ModelPointGrid
{
	private static Log log = Logging.getLog(ModelPointGrid.class);
	
	protected double north;
	protected double south;
	protected double east;
	protected double west;
	protected double latitudeResolution;
	protected double longitudeResolution;
	
	protected int width;
	protected int height;
	
	protected long gridLength;

	private ElevationHistogramModel elevationHistogramModel;
	
	public ModelPointGrid(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution, double minimum, double maximum)
	{
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		
		double _height = (this.north - this.south) / latitudeResolution;
		double _width = (this.east - this.west) / longitudeResolution;
		
		this.height = (int) MathExt.ceil(_height);
		this.width = (int) MathExt.ceil(_width);
		
		gridLength = (long)height * (long)width;
		
		elevationHistogramModel = new ElevationHistogramModel(500, minimum, maximum);
		
	}
	
	public abstract int[] getModelTexture();
	
	public abstract void dispose();
	public abstract boolean isDisposed();
	public abstract void reset();
	
	public abstract ModelPoint get(double latitude, double longitude);
	
	
	protected int getIndex(double latitude, double longitude)
	{
		int column = (int) Math.round((longitude - west) / longitudeResolution);
		int row = (int) Math.round((north - latitude) / latitudeResolution);
		
		if (column < 0 || column >= width) {
			return -1;
		}
		
		if (row < 0 || row >= height) {
			return -1;
		}
		
		
		int index = row * width + column;
		return index;
	}
	
	public double getElevation(double latitude, double longitude)
	{
		return getElevation(latitude, longitude, false);
	}
	
	public double getElevation(double latitude, double longitude, boolean basic)
	{
		ModelPoint mp = get(latitude, longitude);
		if (mp != null) {
			return mp.getElevation();
		} else {
			return DemConstants.ELEV_NO_DATA;
		}
	}
	
	
	public void setElevation(double latitude, double longitude, double elevation)
	{
		ModelPoint mp = get(latitude, longitude);
		if (mp != null) {
			mp.setElevation(elevation);
			getElevationHistogramModel().add(elevation);
		}
	}
	
	public void getRgba(double latitude, double longitude, int[] fill) 
	{
		ModelPoint mp = get(latitude, longitude);
		if (mp != null) {
			mp.getRgba(fill);
		}
	}
	
	public int getRgba(double latitude, double longitude)
	{
		ModelPoint mp = get(latitude, longitude);
		if (mp != null) {
			return mp.getRgba();
		} else {
			return 0x0;
		}
	}

	public void setRgba(double latitude, double longitude, int rgba)
	{
		ModelPoint mp = get(latitude, longitude);
		if (mp != null) {
			mp.setRgba(rgba);
		}
	}
	

	public void setRgba(double latitude, double longitude, int[] rgba)
	{
		ModelPoint mp = get(latitude, longitude);
		if (mp != null) {
			mp.setRgba(rgba);
		}
	}

	public ElevationHistogramModel getElevationHistogramModel()
	{
		return elevationHistogramModel;
	}

	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}

}
