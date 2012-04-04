package us.wthr.jdem846.rasterdata;


public class RasterDataLatLongBox
{
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	private double width;
	private double height;
	
	//private Rectangle2D.Double rectangle = null;
	
	public RasterDataLatLongBox(double north, double south, double east, double west)
	{
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		
		// TODO: Too simplistic
		width = (east - west);
		height = (north - south);
		
		
		//rectangle = new Rectangle2D.Double(west, north, width, height);
		/*
		box = new Path2D.Double();
		box.moveTo(west, north);
		box.lineTo(west, south);
		box.lineTo(east, south);
		box.lineTo(east, north);
		box.closePath();
		*/
	}
	
	public boolean intersects(RasterDataLatLongBox other)
	{
		if (this.contains(other.getNorth(), other.getWest())
				|| this.contains(other.getSouth(), other.getWest())
				|| this.contains(other.getSouth(), other.getEast())
				|| this.contains(other.getNorth(), other.getEast())
				|| other.contains(this.getNorth(), this.getWest())
				|| other.contains(this.getSouth(), this.getWest())
				|| other.contains(this.getSouth(), this.getEast())
				|| other.contains(this.getNorth(), this.getEast())) {
			return true;
		} else {
			return false;
		}
		
		//return (rectangle.intersects(other.rectangle) || other.rectangle.intersects(rectangle));	
		/*
		return rectangle.contains(other.getLeftX(), other.getTopY())
				|| rectangle.contains(other.getLeftX(), other.getBottomY())
				|| rectangle.contains(other.getRightX(), other.getTopY())
				|| rectangle.contains(other.getRightX(), other.getBottomY())
				|| bounds.contains(getLeftX(), getTopY())
				|| bounds.contains(getLeftX(), getBottomY())
				|| bounds.contains(getRightX(), getTopY())
				|| bounds.contains(getRightX(), getBottomY());
				*/
		//return box.intersects(other.getNorth(), other.getWest(), other.getWidth(), other.getHeight());
	}

	
	public boolean contains(double latitude, double longitude)
	{
		if (latitude >= getSouth() 
				&& latitude <= getNorth()
				&& longitude >= getWest()
				&& longitude <= getEast()) {
			return true;
		} else {
			return false;
		}
	}
	
	public double getWidth()
	{
		
		return width;
	}
	
	public double getHeight()
	{
		return height;
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
	
	
	public RasterDataLatLongBox copy()
	{
		return new RasterDataLatLongBox(north, south, east, west);
	}
	
	
}
