package us.wthr.jdem846.model;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.canvas.util.ColorUtil;

public class BasicModelPoint implements ModelPoint
{
	protected double elevation = DemConstants.ELEV_NO_DATA;
	protected int rgba;

	public BasicModelPoint()
	{
		
	}
	
	
	public void dispose()
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
