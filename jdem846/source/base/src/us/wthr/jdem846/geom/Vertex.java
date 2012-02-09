package us.wthr.jdem846.geom;

import java.util.Comparator;

public class Vertex implements Comparable<Vertex>
{
	public double x;
	public double y;
	public double z;
	
	public int[] rgba = null;
	
	public Vertex(Vertex copy)
	{
		this(copy.x, copy.y, copy.z, copy.rgba);
	}
	
	public Vertex(double x, double y)
	{
		this(x, y, 0, null);
	}
	
	public Vertex(double x, double y, double z)
	{
		this(x, y, z, null);
	}
	
	public Vertex(double x, double y, double z, int[] rgba)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		
		if (rgba != null && rgba.length >= 4) {
			this.rgba = new int[4];
			this.rgba[0] = rgba[0];
			this.rgba[1] = rgba[1];
			this.rgba[2] = rgba[2];
			this.rgba[3] = rgba[3];
		}
	}

	@Override
	public int compareTo(Vertex other)
	{
		return compareToY(other);
	}

	public int compareToY(Vertex other)
	{
		Double y0 = (Double) this.y;
		Double y1 = (Double) other.y;
		return y0.compareTo(y1);
	}
	
	public int compareToX(Vertex other)
	{
		Double x0 = (Double) this.x;
		Double x1 = (Double) other.x;
		return x0.compareTo(x1);
	}
	
}
