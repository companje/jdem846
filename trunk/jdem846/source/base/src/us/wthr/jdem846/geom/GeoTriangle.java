package us.wthr.jdem846.geom;

import us.wthr.jdem846.geom.util.TriangleInterpolator;

public class GeoTriangle extends Triangle
{
	
	private TriangleInterpolator latitudeInterpolator;
	private TriangleInterpolator longitudeInterpolator;
	private TriangleInterpolator elevationInterpolator;
	
	
	public GeoTriangle()
	{
		super();
	}

	public GeoTriangle(GeoVertex p0, GeoVertex p1, GeoVertex p2)
	{
		super(p0, p1, p2);
	}
	
	@Override
	public void initialize()
	{
		super.initialize();
		
		
		latitudeInterpolator = new TriangleInterpolator(p0.x, p0.y, ((GeoVertex)p0).latitude,
														p1.x, p1.y, ((GeoVertex)p1).latitude,
														p2.x, p2.y, ((GeoVertex)p2).latitude);
		
		longitudeInterpolator = new TriangleInterpolator(p0.x, p0.y, ((GeoVertex)p0).longitude,
														 p1.x, p1.y, ((GeoVertex)p1).longitude,
														 p2.x, p2.y, ((GeoVertex)p2).longitude);
		
		elevationInterpolator = new TriangleInterpolator(p0.x, p0.y, ((GeoVertex)p0).elevation,
														 p1.x, p1.y, ((GeoVertex)p1).elevation,
														 p2.x, p2.y, ((GeoVertex)p2).elevation);
		
	}
	
	
	public double getInterpolatedLatitude(double x, double y)
	{
		return latitudeInterpolator.getInterpolatedValue(x, y);
	}
	
	public double getInterpolatedLongitude(double x, double y)
	{
		return longitudeInterpolator.getInterpolatedValue(x, y);
	}
	
	public double getInterpolatedElevation(double x, double y)
	{
		return elevationInterpolator.getInterpolatedValue(x, y);
	}
	
	
}
