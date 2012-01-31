package us.wthr.jdem846.geom;


import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.render.gfx.NumberUtil;


/**
 * http://www.sunshine2k.de/stuff/Java/Polygon/Filling/FillPolygon.htm
 * @author Kevin M. Gill
 * 
 */
public class Edge implements Comparable<Edge>
{
	public Vertex p0;
	public Vertex p1;
	
	public double m;
	public double curX;
	public double curZ;
	
	public Edge(Edge other)
	{
		this(other.p0.x, other.p0.y, other.p0.z, other.p1.x, other.p1.y, other.p1.z);
	}
	
	
	public Edge(double x0, double y0, double x1, double y1)
	{
		this(new Vertex(x0, y0), new Vertex(x1, y1));
	}
	
	public Edge(double x0, double y0, double z0, double x1, double y1, double z1)
	{
		this(new Vertex(x0, y0, z0), new Vertex(x1, y1, z1));
	}
	
	public Edge(Vertex t0, Vertex t1)
	{
		if (t0.compareTo(t1) < 0) {
			this.p0 = t0;
			this.p1 = t1;
		} else {
			this.p0 = t1;
			this.p1 = t0;
		}
		
		m = (double)((Double)p0.y - (Double)p1.y) / (double)((Double)p0.x - (Double)p1.x);

	}
	
	
	

	
	
	public void activate()
	{
		curX = p0.x;
		curZ = p0.z;
	}
	
	public void update()
    {
        curX += (1.0 / m);
        
        double xFrac = (curX - p0.x) / (p1.x - p0.x);
        curZ = getInterpolatedZ(xFrac);
        
    }

    public void deactivate()
    {
		curX = p1.x;
		curZ = p1.z;
    }

	@Override
	public int compareTo(Edge o)
	{
		return this.p0.compareTo(o.p0);
	}

    public Bounds getBounds()
    {
    	double minY = MathExt.min(p0.y, p1.y);
    	double maxY = MathExt.max(p0.y, p1.y);
    	double minX = MathExt.min(p0.x, p1.x);
    	double maxX = MathExt.max(p0.x, p1.x);
    	return new Bounds(minX, minY, (maxX - minX), (maxY - minY));
    }
    
    public double getInterpolatedZ(double frac)
    {
    	double z0 = p0.z;
		double z1 = p1.z;
		
    	double value = 0;
		
		if (!NumberUtil.isValidNumber(frac)) {
			value = p0.z;
		} else {
			value = (z1 - z0)*frac + z0;
		}

		return value;
    }
    
    
 
    
}
