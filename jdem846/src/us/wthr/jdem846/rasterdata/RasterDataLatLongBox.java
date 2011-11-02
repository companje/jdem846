package us.wthr.jdem846.rasterdata;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public class RasterDataLatLongBox
{
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	private Path2D.Double box = null;
	
	public RasterDataLatLongBox(double north, double south, double east, double west)
	{
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		
		box = new Path2D.Double();
		box.moveTo(west, north);
		box.lineTo(west, south);
		box.lineTo(east, south);
		box.lineTo(east, north);
		box.closePath();
	}
	
	public boolean intersects(RasterDataLatLongBox other)
	{
		return box.intersects(other.getNorth(), other.getWest(), other.getWidth(), other.getHeight());
	}

	
	public double getWidth()
	{
		// TODO: Too simplistic
		return (east - west);
	}
	
	public double getHeight()
	{
		// TODO: Too simplistic
		return (north - south);
	}

	public double getNorth()
	{
		return north;
	}


	public double getSouth()
	{
		return south;
	}


	public double getEast()
	{
		return east;
	}


	public double getWest()
	{
		return west;
	}
	
	
	
	
	
}
