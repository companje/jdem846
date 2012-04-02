package us.wthr.jdem846.render.render3;

import us.wthr.jdem846.render.util.ColorUtil;

public class ModelPoint
{
	
	private double elevation;
	private double[] normal = new double[3];
	private double dotProduct;
	private int rgba;
	
	public ModelPoint()
	{
		
	}

	public double getElevation()
	{
		return elevation;
	}

	public void setElevation(double elevation)
	{
		this.elevation = elevation;
	}

	public void getNormal(double[] fill)
	{
		fill[0] = normal[0];
		fill[1] = normal[1];
		fill[2] = normal[2];
	}
	
	public double[] getNormal()
	{
		return normal;
	}

	public void setNormal(double[] normal)
	{
		this.normal[0] = normal[0];
		this.normal[1] = normal[1];
		this.normal[2] = normal[2];
	}

	public double getDotProduct()
	{
		return dotProduct;
	}

	public void setDotProduct(double dotProduct)
	{
		this.dotProduct = dotProduct;
	}

	
	public void getRgba(int[] fill) 
	{
		ColorUtil.intToRGBA(rgba, fill);
	}
	
	public int getRgba()
	{
		return rgba;
	}

	public void setRgba(int rgba)
	{
		this.rgba = rgba;
	}
	
	public void setRgba(int[] rgba)
	{
		this.rgba = ColorUtil.rgbaToInt(rgba);
	}
	
	public void setRgba(int r, int g, int b, int a)
	{
		this.rgba = ColorUtil.rgbaToInt(r, g, b, a);
	}
	
	
	
}
