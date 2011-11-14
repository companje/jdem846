package us.wthr.jdem846.render.mapprojection;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Implements a simple map projection to generate row/column coordinates that, for a global
 * dataset, would produce a rectangle that has a width:height ratio of 2:1. This will
 * serve as the default projection.
 * 
 * @author Kevin M. Gill
 * @see http://en.wikipedia.org/wiki/Equirectangular_projection
 */
public class EquirectangularProjection implements MapProjection
{
	
	private static Log log = Logging.getLog(EquirectangularProjection.class);
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	private double width; // Should be 2x height for a global-sized raster
	private double height;
	
	public EquirectangularProjection()
	{
		
	}
	
	public EquirectangularProjection(double north, double south, double east, double west, double width, double height)
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
	
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point)
	{
		point.row = latitudeToRow(latitude);
		point.column = longitudeToColumn(longitude);
	}
	
	public double latitudeToRow(double latitude)
	{
		double range = getNorth() - getSouth();
		double pos = range - (getNorth() - latitude);
		double row = (1.0 - (pos / range)) * (double)getHeight();
		return row;
	}
	
	public double longitudeToColumn(double longitude)
	{
		double range = getEast() - getWest();
		double pos = range - (longitude - getWest());
		double row = (1.0 - (pos / range)) * (double) getWidth();
		return row;
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
