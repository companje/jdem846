package us.wthr.jdem846.model;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.canvas.util.ColorUtil;

public class BasicModelPoint implements ModelPoint
{
	private double elevation = DemConstants.ELEV_NO_DATA;
	private double[] normal = new double[3];
	private int rgba;

	public BasicModelPoint()
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

	public void setElevation(float elevation)
	{
		this.elevation = elevation;
	}

	public void getNormal(double[] fill)
	{
		fill[0] = normal[0];
		fill[1] = normal[1];
		fill[2] = normal[2];
	}

	
	public void setNormal(double[] normal)
	{
		this.normal[0] = (float) normal[0];
		this.normal[1] = (float) normal[1];
		this.normal[2] = (float) normal[2];
	}
	


	
	public void getRgba(int[] fill) 
	{
		ColorUtil.intToRGBA(getRgba(), fill);
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
		this.setRgba(ColorUtil.rgbaToInt(rgba));
	}
	
	
	public void setRgba(int r, int g, int b, int a)
	{
		this.setRgba(ColorUtil.rgbaToInt(r, g, b, a));
	}
	
}
