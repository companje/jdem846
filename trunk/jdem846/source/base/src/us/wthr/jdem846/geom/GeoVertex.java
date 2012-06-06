package us.wthr.jdem846.geom;

public class GeoVertex extends Vertex
{
	
	public double latitude;
	public double longitude;
	public double elevation;
	
	public GeoVertex(double x, double y, double z, int[] rgba, double latitude, double longitude, double elevation)
	{
		super(x, y, z, rgba);
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = elevation;
	}

	public GeoVertex(double x, double y, double z, double latitude, double longitude, double elevation)
	{
		super(x, y, z);
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = elevation;
	}

	public GeoVertex(double x, double y, double latitude, double longitude, double elevation)
	{
		super(x, y);
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = elevation;
	}

	public GeoVertex(GeoVertex copy)
	{
		super(copy);
		this.latitude = copy.latitude;
		this.longitude = copy.longitude;
		this.elevation = copy.elevation;
	}
	
	
	
	
}
