package us.wthr.jdem846.geom;


import us.wthr.jdem846.geom.util.BarycentricTrianglePointTest;
import us.wthr.jdem846.geom.util.PointTest;
import us.wthr.jdem846.geom.util.RgbaTriangleInterpolator;
import us.wthr.jdem846.geom.util.SameSideTrianglePointTest;
import us.wthr.jdem846.geom.util.SignTrianglePointTest;
import us.wthr.jdem846.geom.util.TriangleInterpolator;
import us.wthr.jdem846.math.MathExt;

public class Triangle extends RenderableShape
{
	
	public Vertex p0;
	public Vertex p1;
	public Vertex p2;
	
	private TriangleInterpolator zInterpolator = new TriangleInterpolator();
	private RgbaTriangleInterpolator rgbaInterpolator = new RgbaTriangleInterpolator();

	//private static PointTest pointTest = new SameSideTrianglePointTest();
	//private static PointTest pointTest = new BarycentricTrianglePointTest();
	private static PointTest pointTest = new SignTrianglePointTest();
	
	public Triangle()
	{

	}
	
	public Triangle(Vertex p0, Vertex p1, Vertex p2)
	{
		this.p0 = new Vertex(p0);
		this.p1 = new Vertex(p1);
		this.p2 = new Vertex(p2);

		initialize();
	}
	
	public void initialize()
	{

		zInterpolator.set(p0.x(), p0.y(), p0.z(),
							p1.x(), p1.y(), p1.z(),
							p2.x(), p2.y(), p2.z());
		
		rgbaInterpolator.set(p0.x(), p0.y(), p0.rgba,
							p1.x(), p1.y(), p1.rgba,
							p2.x(), p2.y(), p2.rgba);
		

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
		double maxX = MathExt.max(p0.x(), p1.x(), p2.x());
		double minX = MathExt.min(p0.x(), p1.x(), p2.x());
		double maxY = MathExt.max(p0.y(), p1.y(), p2.y());
		double minY = MathExt.min(p0.y(), p1.y(), p2.y());
		
		Bounds bounds = new Bounds(minX, minY, (maxX - minX), (maxY - minY));
		return bounds;
		
	}
	
	public void getInterpolatedColor(double x, double y, int[] color)
	{
		rgbaInterpolator.getInterpolatedColor(x, y, color);
	}
	
	

	
	@Override
	public double getInterpolatedZ(double x, double y)
	{
		return zInterpolator.getInterpolatedValue(x, y);
	}

	
	
	
}
