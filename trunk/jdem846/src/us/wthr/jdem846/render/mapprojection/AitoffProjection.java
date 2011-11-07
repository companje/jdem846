package us.wthr.jdem846.render.mapprojection;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.NumberUtil;

public class AitoffProjection implements MapProjection
{
	
	private static Log log = Logging.getLog(AitoffProjection.class);
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	private double width; 
	private double height;
	
	public AitoffProjection(double north, double south, double east, double west, double width, double height)
	{
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.width = width;
		this.height = height;
	}

	@Override
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point)
	{
		if (latitude == 0.0 && longitude == 0.0) {
			point.column = getWidth() / 2.0;
			point.row = getHeight() / 2.0;
			return;
		}
		
		latitude = Math.toRadians(latitude);
		longitude = Math.toRadians(longitude);
		
		double a = Math.acos(Math.cos(latitude) * Math.cos(longitude / 2.0));
		double sinca = (a == 0) ? 0 : (Math.sin(a) / a);
		
		double x = 2.0 * Math.cos(latitude) * Math.sin(longitude / 2.0) / sinca;
		double y = Math.sin(latitude) / sinca;
		
		x = Math.toDegrees(x);
		y = Math.toDegrees(y);
		
		point.column = longitudeToColumn(x);
		point.row = latitudeToRow(y);
		
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
		double col = (1.0 - (pos / range)) * (double) getWidth();
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
