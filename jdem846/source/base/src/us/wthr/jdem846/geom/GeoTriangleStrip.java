package us.wthr.jdem846.geom;

public class GeoTriangleStrip extends TriangleStrip
{
	
	@Override
	protected Triangle createDefaultTriangle()
	{
		return new GeoTriangle();
	}
}
