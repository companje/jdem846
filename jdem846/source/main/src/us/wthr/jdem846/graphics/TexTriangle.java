package us.wthr.jdem846.graphics;

import us.wthr.jdem846.geom.Triangle;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.geom.util.TriangleInterpolator;

public class TexTriangle extends Triangle
{
	protected TriangleInterpolator leftInterpolator = new TriangleInterpolator();
	protected TriangleInterpolator frontInterpolator = new TriangleInterpolator();
	
	public TexTriangle()
	{
		super();
	}
	
	public TexTriangle(TexVertex p0, TexVertex p1, TexVertex p2)
	{
		super(p0, p1, p2);
	}

	@Override
	public void setVerteces(Vertex p0, Vertex p1, Vertex p2)
	{
		setVerteces((TexVertex)p0, (TexVertex)p1, (TexVertex)p2);
	}
	
	
	public void setVerteces(TexVertex p0, TexVertex p1, TexVertex p2)
	{
		super.setVerteces(p0, p1, p2);
		this.leftInterpolator.set(p0.vector.x, p0.vector.y, p0.left
									, p1.vector.x, p1.vector.y, p1.left
									, p2.vector.x, p2.vector.y, p2.left);
		
		this.frontInterpolator.set(p0.vector.x, p0.vector.y, p0.front
								, p1.vector.x, p1.vector.y, p1.front
								, p2.vector.x, p2.vector.y, p2.front);
		
	}
	
	public double getInterpolatedLeft(double x, double y)
	{
		return this.leftInterpolator.getInterpolatedValue(x, y);
	}
	
	public double getInterpolatedFront(double x, double y)
	{
		return this.frontInterpolator.getInterpolatedValue(x, y);
	}
	
	
}
