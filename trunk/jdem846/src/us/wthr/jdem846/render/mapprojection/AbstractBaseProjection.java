package us.wthr.jdem846.render.mapprojection;

import us.wthr.jdem846.ModelContext;

public abstract class AbstractBaseProjection implements MapProjection
{
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	private double width; 
	private double height;
	
	public AbstractBaseProjection()
	{
		
	}
	
	public AbstractBaseProjection(double north, double south, double east, double west, double width, double height)
	{
		setUp(north, south, east, west, width, height);
	}

	public void setUp(ModelContext modelContext)
	{
		setUp(modelContext.getNorth(), 
				modelContext.getSouth(),
				modelContext.getEast(),
				modelContext.getWest(),
				modelContext.getModelDimensions().getOutputWidth(),
				modelContext.getModelDimensions().getOutputHeight());
	}
	
	public void setUp(double north, double south, double east, double west, double width, double height)
	{
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.width = width;
		this.height = height;
	}
	
	
	public double latitudeToRow(double latitude)
	{
		double range = north - south;
		double pos = range - (north - latitude);
		double row = (1.0 - (pos / range)) * (double)height;
		return row;
	}
	
	public double longitudeToColumn(double longitude)
	{
		double range = east - west;
		double pos = range - (longitude - west);
		double col = (1.0 - (pos / range)) * (double) width;
		return col;
	}

	protected double getNorth()
	{
		return north;
	}

	protected double getSouth()
	{
		return south;
	}

	protected double getEast()
	{
		return east;
	}

	protected double getWest()
	{
		return west;
	}

	protected double getWidth()
	{
		return width;
	}

	protected double getHeight()
	{
		return height;
	}
	
	
	
}
