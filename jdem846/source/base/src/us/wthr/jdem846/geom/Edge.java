package us.wthr.jdem846.geom;

import java.awt.Point;


/**
 * http://www.sunshine2k.de/stuff/Java/Polygon/Filling/FillPolygon.htm
 * @author Kevin M. Gill
 * 
 */
public class Edge<T> implements Comparable<Edge<T>>
{
	public Vertex<T> p0;
	public Vertex<T> p1;
	
	public double m;
	public double curX;
	
	public Edge(Vertex<T> p0, Vertex<T> p1)
	{
		this(p0.x, p0.y, p1.x, p1.y);
	}
	
	public Edge(T x0, T y0, T x1, T y1)
	{
		Vertex<T> t0 = new Vertex<T>(x0, y0);
		Vertex<T> t1 = new Vertex<T>(x1, y1);
		
		if (t0.compareTo(t1) < 0) {
			this.p0 = t0;
			this.p1 = t1;
		} else {
			this.p0 = t1;
			this.p1 = t0;
		}
		
		if (p0.y instanceof Double) {
			m = (double)((Double)p0.y - (Double)p1.y) / (double)((Double)p0.x - (Double)p1.x);
		} else if (p0.y instanceof Integer) {
			m = (double)((Integer)p0.y - (Integer)p1.y) / (double)((Integer)p0.x - (Integer)p1.x);
		}
	}
	

	
	
	public void activate()
	{
		if (p0.x instanceof Double) {
			curX = (Double) p0.x;
		} else {
			curX = (Integer) p0.x;
		}
	}
	
	public void update()
    {
        curX += (1.0 / m);
    }

    public void deactivate()
    {
        if (p1.x instanceof Double) {
			curX = (Double) p1.x;
		} else {
			curX = (Integer) p1.x;
		}
    }

	@Override
	public int compareTo(Edge<T> o)
	{
		return this.p0.compareTo(o.p0);
	}

    
    
}
