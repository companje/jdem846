package us.wthr.jdem846.geom;

import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.util.ColorUtil;

public class Vertex implements Comparable<Vertex>
{

	public Vector vector = new Vector();
	public int rgba = 0x0;
	
	public Vertex()
	{
		
	}
	
	public Vertex(Vertex copy)
	{
		this(copy.vector, copy.rgba);
	}
	
	public Vertex(double x, double y)
	{
		this(x, y, 0, null);
	}
	
	public Vertex(double x, double y, double z)
	{
		this(x, y, z, null);
	}
	
	public Vertex(double[] xyz, int[] rgba)
	{
		this(xyz[0], xyz[1], xyz[2], rgba);
	}
	
	public Vertex(double x, double y, double z, int[] rgba)
	{
		set(x, y, z, ColorUtil.rgbaToInt(rgba));
	}
	
	
	public Vertex(Vector v, int rgba)
	{
		set(v.x, v.y, v.z, rgba);
	}
	
	public Vertex(Vector v)
	{
		set(v.x, v.y, v.z, 0x0);
	}
	
	
	public void set(Vector v, int rgba)
	{
		set(v.x, v.y, v.z, rgba);
	}
	
	public void set(double x, double y, double z, int[] rgba)
	{
		set(x, y, z, ColorUtil.rgbaToInt(rgba));
	}
	
	public void set(double x, double y, double z, int rgba)
	{
		if (this.vector == null) {
			this.vector = new Vector(x, y, z);
		} else {
			this.vector.x = x;
			this.vector.y = y;
			this.vector.z = z;
		}
		this.rgba = rgba;

	}

	@Override
	public int compareTo(Vertex other)
	{
		return compareToY(other);
	}

	public int compareToY(Vertex other)
	{
		Double y0 = (Double) this.vector.y;
		Double y1 = (Double) other.vector.y;
		return y0.compareTo(y1);
	}
	
	public int compareToX(Vertex other)
	{
		Double x0 = (Double) this.vector.x;
		Double x1 = (Double) other.vector.x;
		return x0.compareTo(x1);
	}
	
	
	public void x(double x)
	{
		this.vector.x = x;
	}
	
	public double x()
	{
		return this.vector.x;
	}
	
	
	public void y(double y)
	{
		this.vector.y = y;
	}
	
	public double y()
	{
		return this.vector.y;
	}
	
	
	public void z(double z)
	{
		this.vector.z = z;
	}
	
	public double z()
	{
		return this.vector.z;
	}
	
}
