package us.wthr.jdem846.gis;

import us.wthr.jdem846.math.MathExt;

public class CoordinateBounds
{
	private double north;
	private double south;
	private double east;
	private double west;
	
	private boolean bounded;
	
	public CoordinateBounds(double north, double south, double east, double west)
	{
		this(north, south, east, west, false);
	}
	
	public CoordinateBounds(double north, double south, double east, double west, boolean bounded)
	{
		this.bounded = bounded;
	}

	public double getNorth()
	{
		return (bounded) ? boundNorth(north) : north;
	}

	public void setNorth(double north)
	{
		this.north = north;
	}

	public double getSouth()
	{
		return (bounded) ? boundSouth(south) : south;
	}

	public void setSouth(double south)
	{
		this.south = south;
	}

	public double getEast()
	{
		return (bounded) ? boundEast(east) : east;
	}

	public void setEast(double east)
	{
		this.east = east;
	}

	public double getWest()
	{
		return (bounded) ? boundWest(west) : west;
	}

	public void setWest(double west)
	{
		this.west = west;
	}

	public boolean isBounded()
	{
		return bounded;
	}

	public void setBounded(boolean bounded)
	{
		this.bounded = bounded;
	}
	
	
	public static double boundNorth(double north)
	{
		return boundLatitude(north);
	}
	
	public static double boundSouth(double south)
	{
		return boundLatitude(south);
	}
	
	public static double boundEast(double east)
	{
		return boundLongitude(east);
	}
	
	public static double boundWest(double west)
	{
		return boundLongitude(west);
	}
	
	public static double boundLongitude(double lon)
	{
		lon = MathExt.min(lon, 180.0);
		lon = MathExt.max(lon, -180.0);
		return lon;
	}
	
	public static double boundLatitude(double lat)
	{
		lat = MathExt.min(lat, 90.0);
		lat = MathExt.max(lat, -90.0);
		return lat;
	}
}
