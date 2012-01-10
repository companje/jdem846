package us.wthr.jdem846.render;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import us.wthr.jdem846.math.MathExt;

public class Quadrangle3d
{
	private double[] p0;
	private double[] p1;
	private double[] p2;
	private double[] p3;
	
	private Path2D.Double path;
	private boolean dirty = true;
	
	public Quadrangle3d()
	{
		p0 = new double[3];
		p1 = new double[3];
		p2 = new double[3];
		p3 = new double[3];
		
		path = new Path2D.Double();
	}
	
	public void set(int point, double x, double y, double z)
	{
		switch(point) {
		case 0:
			set0(x, y, z);
			break;
		case 1:
			set1(x, y, z);
			break;
		case 2:
			set2(x, y, z);
			break;
		case 3:
			set3(x, y, z);
			break;
		default:
			throw new IllegalArgumentException("Invalid point index: " + point);
		}
	}
	
	
	public void get(int point, double[] xyz)
	{
		switch(point) {
		case 0:
			get0(xyz);
			break;
		case 1:
			get1(xyz);
			break;
		case 2:
			get2(xyz);
			break;
		case 3:
			get3(xyz);
			break;
		default:
			throw new IllegalArgumentException("Invalid point index: " + point);
		}
	}
	
	public void get0(double[] xyz)
	{
		xyz[0] = p0[0];
		xyz[1] = p0[1];
		xyz[2] = p0[2];
	}
	
	public void set0(double x, double y, double z)
	{
		p0[0] = x;
		p0[1] = y;
		p0[2] = z;
		setDirty(true);
	}
	
	public void get1(double[] xyz)
	{
		xyz[0] = p1[0];
		xyz[1] = p1[1];
		xyz[2] = p1[2];
	}
	
	public void set1(double x, double y, double z)
	{
		p1[0] = x;
		p1[1] = y;
		p1[2] = z;
		setDirty(true);
	}
	
	public void get2(double[] xyz)
	{
		xyz[0] = p2[0];
		xyz[1] = p2[1];
		xyz[2] = p2[2];
	}
	
	public void set2(double x, double y, double z)
	{
		p2[0] = x;
		p2[1] = y;
		p2[2] = z;
		setDirty(true);
	}
	
	public void get3(double[] xyz)
	{
		xyz[0] = p3[0];
		xyz[1] = p3[1];
		xyz[2] = p3[2];
	}
	
	public void set3(double x, double y, double z)
	{
		p3[0] = x;
		p3[1] = y;
		p3[2] = z;
		setDirty(true);
	}
	
	public Rectangle2D.Double getBounds2D()
	{
		double maxX = MathExt.max(p0[0], p1[0], p2[0], p3[0]);
		double minX = MathExt.min(p0[0], p1[0], p2[0], p3[0]);
		
		double maxY = MathExt.max(p0[1], p1[1], p2[1], p3[1]);
		double minY = MathExt.min(p0[1], p1[1], p2[1], p3[1]);
		
		double width = maxX - minX;
		double height = maxY - minY;
		
		return new Rectangle2D.Double(minX, minY, width, height);
		
	}
	
	public double interpolateZ(double xFrac, double yFrac)
	{
		
		double s00 = p0[2];
		double s01 = p1[2];
		double s10 = p2[2];
		double s11 = p3[2];
		
		return MathExt.interpolate(s00, s01, s10, s11, xFrac, yFrac);
	}
	
	public Path2D.Double getPath()
	{
		if (isDirty()) {
			path.reset();
			path.moveTo(p0[0], p0[1]);
			path.lineTo(p1[0], p1[1]);
			path.lineTo(p2[0], p2[1]);
			path.lineTo(p3[0], p3[1]);
			path.closePath();
			setDirty(false);
		}
		
		return path;
	}
	
	public boolean contains(double x, double y)
	{
		Path2D.Double path = getPath();
		return path.contains(x, y);
	}
	
	public boolean contains(double x, double y, double w, double h)
	{
		Path2D.Double path = getPath();
		return path.contains(x, y, w, h);
	}
	
	public boolean contains(Point2D p)
	{
		Path2D.Double path = getPath();
		return path.contains(p);
	}
	
	public boolean contains(Rectangle2D r)
	{
		Path2D.Double path = getPath();
		return path.contains(r);
	}
	
	
	public boolean intersects(double x, double y, double w, double h)
	{
		Path2D.Double path = getPath();
		return path.intersects(x, y, w, h);
	}
	
	
	private void setDirty(boolean dirty)
	{
		this.dirty = dirty;
	}
	
	private boolean isDirty()
	{
		return dirty;
	}
}
