package us.wthr.jdem846.model;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.canvas.util.ColorUtil;

public class ModelPoint
{
	
	private double elevation = DemConstants.ELEV_NO_DATA;
	private double[] normal = new double[3];
	private double dotProduct;
	private int rgba;
	private int shadedRgba;
	
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

	
	public void getRgba(int[] fill, boolean shaded) 
	{
		ColorUtil.intToRGBA(getRgba(shaded), fill);
	}
	
	public int getRgba(boolean shaded)
	{
		if (shaded) {
			return this.shadedRgba;
		} else {
			return rgba;
		}
	}

	public void setRgba(int rgba, boolean shaded)
	{
		if (shaded) {
			this.shadedRgba = rgba;
		} else {
			this.rgba = rgba;
		}
	}
	
	public void setRgba(int[] rgba, boolean shaded)
	{
		this.setRgba(ColorUtil.rgbaToInt(rgba), shaded);
	}
	
	public void setRgba(int r, int g, int b, int a, boolean shaded)
	{
		this.setRgba(ColorUtil.rgbaToInt(r, g, b, a), shaded);
	}
	
	
	
}
