package us.wthr.jdem846.geom;

public class Vertex implements Comparable<Vertex>
{
	public static final int X = 0;
	public static final int Y = 1;
	public static final int Z = 2;
	

	public double[] xyz = new double[3];
	public int[] rgba = null;
	
	public Vertex(Vertex copy)
	{
		this(copy.xyz, copy.rgba);
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
		this.xyz[X] = x;
		this.xyz[Y] = y;
		this.xyz[Z] = z;

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
		Double y0 = (Double) this.xyz[Y];
		Double y1 = (Double) other.xyz[Y];
		return y0.compareTo(y1);
	}
	
	public int compareToX(Vertex other)
	{
		Double x0 = (Double) this.xyz[X];
		Double x1 = (Double) other.xyz[X];
		return x0.compareTo(x1);
	}
	
	
	public void x(double x)
	{
		this.xyz[X] = x;
	}
	
	public double x()
	{
		return this.xyz[X];
	}
	
	
	public void y(double y)
	{
		this.xyz[Y] = y;
	}
	
	public double y()
	{
		return this.xyz[Y];
	}
	
	
	public void z(double z)
	{
		this.xyz[Z] = z;
	}
	
	public double z()
	{
		return this.xyz[Z];
	}
	
}
