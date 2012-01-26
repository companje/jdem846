package us.wthr.jdem846.render;

import java.awt.Point;

/**
 * http://www.sunshine2k.de/stuff/Java/Polygon/Filling/FillPolygon.htm
 * @author Kevin M. Gill
 * 
 */
public class Edge
{
	private Point p0;
	private Point p1;
	
	private double m;
	private double curX;
	
	public Edge(Point p0, Point p1)
	{
		this.p0 = new Point(p0);
		this.p1 = new Point(p1);
		
		m = (double)((double)(p0.y - p1.y) / (double)(p0.x - p1.x));
	}
	
	public void activate()
	{
		curX = p0.x;
	}
	
	public void update()
    {
        curX += (1.0 / m);
    }

    public void deactivate()
    {
        curX = p1.x;
    }

	public Point getP0()
	{
		return p0;
	}

	public void setP0(Point p0)
	{
		this.p0 = p0;
	}

	public Point getP1()
	{
		return p1;
	}

	public void setP1(Point p1)
	{
		this.p1 = p1;
	}

	public double getM()
	{
		return m;
	}

	public void setM(double m)
	{
		this.m = m;
	}

	public double getCurX()
	{
		return curX;
	}

	public void setCurX(double curX)
	{
		this.curX = curX;
	}
    
    
}
