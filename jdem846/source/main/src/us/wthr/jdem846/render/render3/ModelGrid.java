package us.wthr.jdem846.render.render3;

import us.wthr.jdem846.math.MathExt;

public class ModelGrid 
{
	
	private double north;
	private double south;
	private double east;
	private double west;
	private double latitudeResolution;
	private double longitudeResolution;
	
	private int width;
	private int height;
	
	private int gridLength;
	
	private ModelPoint[] grid;
	
	private boolean isDisposed = false;
	
	public ModelGrid(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution)
	{
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		
		this.height = (int) MathExt.ceil((north - south) / latitudeResolution);
		this.width = (int) MathExt.ceil((east - west) / longitudeResolution);
		
		gridLength = height * width;
		grid = new ModelPoint[gridLength];
		
		for (int i = 0; i < gridLength; i++) {
			grid[i] = new ModelPoint();
		}
		
		reset();
	}
	
	public void dispose()
	{
		if (isDisposed())
			return;
		
		grid = null;
	}
	
	public boolean isDisposed()
	{
		return isDisposed;
	}
	
	public void reset()
	{
		
	}
	
	public ModelPoint get(double latitude, double longitude)
	{
		int index = getIndex(latitude, longitude);
		
		if (index >= 0 && index < this.gridLength) {
			return grid[index];
		} else {
			// TODO: Throw
			return null;
		}
	}
	
	protected int getIndex(double latitude, double longitude)
	{
		int column = (int) Math.floor((longitude - west) / longitudeResolution);
		int row = (int) Math.floor((north - latitude) / latitudeResolution);
		
		if (column < 0 || column >= width) {
			return -1;
		}
		
		if (row < 0 || row >= height) {
			return -1;
		}
		
		
		int index = row * width + column;
		return index;
	}
	
}
