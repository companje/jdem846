package us.wthr.jdem846.geom;

import java.awt.geom.Path2D;

import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.geom.util.BarycentricTrianglePointTest;
import us.wthr.jdem846.geom.util.PointTest;
import us.wthr.jdem846.geom.util.SameSideTrianglePointTest;
import us.wthr.jdem846.math.MathExt;

public class Triangle extends RenderableShape
{
	
	public Vertex p0;
	public Vertex p1;
	public Vertex p2;
	

	private double[] a;
	private double[] b;
	private double[] c;
	
	private double aZ;
	private double bZ;
	private double cZ;
	
	private static PointTest pointTest = new SameSideTrianglePointTest();
	
	//private Path2D.Double polygon;
	
	public Triangle(Vertex p0, Vertex p1, Vertex p2)
	{
		this.p0 = new Vertex(p0);
		this.p1 = new Vertex(p1);
		this.p2 = new Vertex(p2);
		
		/*
		polygon = new Path2D.Double();
		polygon.moveTo(p0.x, p0.y);
		polygon.lineTo(p1.x, p1.y);
		polygon.lineTo(p2.x, p2.y);
		polygon.closePath();
		*/
		
		double det = p0.x*p1.y-p1.x*p0.y+p1.x*p2.y-p2.x*p1.y+p2.x*p0.y-p0.x*p2.y;
		
		aZ = ((p1.y-p2.y)*p0.z+(p2.y-p0.y)*p1.z+(p0.y-p1.y)*p2.z) / det;
		bZ = ((p2.x-p1.x)*p0.z+(p0.x-p2.x)*p1.z+(p1.x-p0.x)*p2.z) / det;
		cZ = ((p1.x*p2.y-p2.x*p1.y)*p0.z+(p2.x*p0.y-p0.x*p2.y)*p1.z+(p0.x*p1.y-p1.x*p0.y)*p2.z) / det;
		
		a = new double[4];
		b = new double[4];
		c = new double[4];
		for (int i = 0; i < 4; i++) {
			a[i] = ((p1.y-p2.y)*(double)p0.rgba[i]+(p2.y-p0.y)*(double)p1.rgba[i]+(p0.y-p1.y)*(double)p2.rgba[i]) / det;
			b[i] = ((p2.x-p1.x)*(double)p0.rgba[i]+(p0.x-p2.x)*(double)p1.rgba[i]+(p1.x-p0.x)*(double)p2.rgba[i]) / det;
			c[i] = ((p1.x*p2.y-p2.x*p1.y)*(double)p0.rgba[i]+(p2.x*p0.y-p0.x*p2.y)*(double)p1.rgba[i]+(p0.x*p1.y-p1.x*p0.y)*(double)p2.rgba[i]) / det;
		}
		
		//pointTest = new TrianglePointTest();
	}
	
	public boolean contains(double x, double y)
	{
		//return polygon.contains(x, y);
		//return polygon.intersects(x, y, 1, 1);
		return pointTest.contains(p0, p1, p2, x, y, getInterpolatedZ(x, y));
	}

	
	
	
	@Override
	public Bounds getBounds()
	{
		double maxX = MathExt.max(p0.x, p1.x, p2.x);
		double minX = MathExt.min(p0.x, p1.x, p2.x);
		double maxY = MathExt.max(p0.y, p1.y, p2.y);
		double minY = MathExt.min(p0.y, p1.y, p2.y);
		
		Bounds bounds = new Bounds(minX, minY, (maxX - minX), (maxY - minY));
		return bounds;
		
	}
	
	public void getInterpolatedColor(double x, double y, int[] color)
	{
		color[0] = (int) MathExt.round(a[0] * x + b[0] * y + c[0]);
		color[1] = (int) MathExt.round(a[1] * x + b[1] * y + c[1]);
		color[2] = (int) MathExt.round(a[2] * x + b[2] * y + c[2]);
		color[3] = (int) MathExt.round(a[3] * x + b[3] * y + c[3]);
	}
	
	

	
	@Override
	public double getInterpolatedZ(double x, double y)
	{
		return aZ*x+bZ*y+cZ;
	}

	
	
	
}
