package us.wthr.jdem846.geom;


import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.math.MathExt;


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
	
	public int[] curRgba;
	
	public Edge(Edge other)
	{
		this(other.p0.x(), other.p0.y(), other.p0.z(), other.p1.x(), other.p1.y(), other.p1.z());
	}
	
	
	public Edge(double x0, double y0, double x1, double y1)
	{
		this(new Vertex(x0, y0), new Vertex(x1, y1));
	}
	
	public Edge(double x0, double y0, double z0, double x1, double y1, double z1)
	{
		this(new Vertex(x0, y0, z0), new Vertex(x1, y1, z1));
	}
	
	public Edge(double x0, double y0, double z0, int[] rgba0, double x1, double y1, double z1, int[] rgba1)
	{
		this(new Vertex(x0, y0, z0, rgba0), new Vertex(x1, y1, z1, rgba1));
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
		
		m = (double)((Double)p0.y() - (Double)p1.y()) / (double)((Double)p0.x() - (Double)p1.x());
		curRgba = new int[4];
	}
	
	
	

	
	
	public void activate(double y)
	{
		curX = p0.x();
		curZ = p0.z();
		this.getInterpolatedColor(curX, y, curRgba);
	}
	
	public void update(double y)
    {
        curX += (1.0 / m);
        
        double xFrac = (curX - p0.x()) / (p1.x() - p0.x());
        curZ = getInterpolatedZ(xFrac);
        
        this.getInterpolatedColor(curX, y, curRgba);
        
    }

    public void deactivate(double y)
    {
		curX = p1.x();
		curZ = p1.z();
		this.getInterpolatedColor(curX, y, curRgba);
    }

	@Override
	public int compareTo(Edge o)
	{
		return this.p0.compareTo(o.p0);
	}

    public Bounds getBounds()
    {
    	double minY = MathExt.min(p0.y(), p1.y());
    	double maxY = MathExt.max(p0.y(), p1.y());
    	double minX = MathExt.min(p0.x(), p1.x());
    	double maxX = MathExt.max(p0.x(), p1.x());
    	return new Bounds(minX, minY, (maxX - minX), (maxY - minY));
    }
    
    public double getInterpolatedZ(double frac)
    {
    	return (p1.z() * frac) + (p0.x() * (1.0 - frac));
    }
    
    public double getInterpolatedZ(double x, double y)
    {
    	if (MathExt.abs(p1.y() - p0.y()) > MathExt.abs(p1.x() - p0.x())) {
    		return y;
    	} else {
	    	double z = p0.z() + (p1.z() - p0.z()) * (x - p0.x()) / (p1.x() - p0.x());
	    	return z;
    	}
    }
    
    
    public void getInterpolatedColor(double x, double y, int[] rgba)
    {
    	Vertex p0 = (this.p0.compareToX(this.p1) < 0) ? this.p0 : this.p1;
    	Vertex p1 = (this.p0.compareToX(this.p1) < 0) ? this.p1 : this.p0;
    	
    	double xFrac = (x - p0.x()) / (p1.x() - p0.x());
    	if (Double.isNaN(xFrac)) {
    		xFrac = 1.0;
    	}
    	double yFrac = (y - p0.y()) / (p1.y() - p0.y());
    	if (yFrac < 0) {
    		yFrac = -1.0 - yFrac;
    	}
    	yFrac = MathExt.abs(yFrac);
    	if (Double.isNaN(yFrac)) {
    		yFrac = 1.0;
    	}
    	
    	double frac = ((xFrac + yFrac) / 2.0);
    	
    	//getInterpolatedColor(p0.rgba, p1.rgba, frac, rgba);

    }
    
    
    
    public void getInterpolatedColor(int[] rgba0, int[] rgba1, double frac, int[] rgba)
    {
    	// TODO: Reimplement
    	/*
    	if (rgba0 != null && rgba1 != null) {
    		ColorAdjustments.interpolateColor(rgba0, rgba1, rgba, frac);
    	} else if (rgba0 == null && p1.rgba != null) {
    		rgba[0] = rgba1[0];
    		rgba[1] = rgba1[1];
    		rgba[2] = rgba1[2];
    		rgba[3] = rgba1[3];
    	} else if (rgba0 != null && p1.rgba == null) {
    		rgba[0] = rgba0[0];
    		rgba[1] = rgba0[1];
    		rgba[2] = rgba0[2];
    		rgba[3] = rgba0[3];
    	}
    	*/
    	
    }
    
    
    public int getInterpolatedColor(int rgba0, int rgba1, double frac)
    {
    	return ColorAdjustments.interpolateColor(rgba0, rgba1, frac);
    }
    
 
    
}
