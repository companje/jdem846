package us.wthr.jdem846.geom;

public abstract class RenderableShape
{
	
	public abstract Bounds getBounds();
	public abstract double getInterpolatedZ(double x, double y);
}
