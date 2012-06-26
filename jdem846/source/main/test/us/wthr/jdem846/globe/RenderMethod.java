package us.wthr.jdem846.globe;

import us.wthr.jdem846.geom.TriangleStrip;

public interface RenderMethod
{
	public void renderPoint(double lat, double lon, TriangleStrip strip);
}
