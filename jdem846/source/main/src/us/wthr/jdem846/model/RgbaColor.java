package us.wthr.jdem846.model;


import java.util.Map;

import us.wthr.jdem846.graphics.Color;
import us.wthr.jdem846.graphics.Colors;
import us.wthr.jdem846.graphics.IColor;

public class RgbaColor
{
	private IColor color;

	public RgbaColor()
	{
		color = new Color(Colors.TRANSPARENT.getRed()
							, Colors.TRANSPARENT.getGreen()
							, Colors.TRANSPARENT.getBlue()
							, Colors.TRANSPARENT.getAlpha()
							);
	}
	
	public RgbaColor(IColor c)
	{
		color = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}
	
	public RgbaColor(int r, int g, int b, int a)
	{
		setRgba(r, g, b, a);
	}
	
	public RgbaColor(int rgba)
	{
		setRgba(rgba);
	}
	
	public RgbaColor(int[] rgba)
	{
		setRgba(rgba);
	}
	
	public void setRgba(int r, int g, int b, int a)
	{
		color = new Color(r, g, b, a);
	}
	
	public void setRgba(int rgba)
	{
		color = new Color(rgba);
	}
	
	public void setRgba(int[] rgba)
	{
		setRgba(rgba[0], rgba[1], rgba[2], rgba[3]);
	}
	
	
	public int getRgba()
	{
		return color.asInt();
	}
	
	public void getRgba(int[] fill)
	{
		color.toArray(fill);
	}
	
	
	public static RgbaColor fromString(String s)
	{
		Map<String, int[]> values = SimpleNumberListMapSerializer.parseIntegerListString(s);
		
		int[] rgba = values.get("rgba");
		
		RgbaColor color = new RgbaColor(rgba[0], rgba[1], rgba[2], rgba[3]);
		
		return color;
	}
	
	public String toString()
	{
		String s = "rgba:[" + color.getRed() + "," + 
							color.getGreen() + "," +
							color.getBlue() + "," +
							color.getAlpha() + "]";
		return s;
	}
	
	public IColor toColor()
	{
		return color;
	}
	
	public java.awt.Color toAwtColor()
	{
		return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof RgbaColor) {
			RgbaColor other = (RgbaColor) obj;
			return other.color.equals(this.color);
		} else {
			return false;
		}
	}
	
	
	public RgbaColor copy()
	{
		RgbaColor copy = new RgbaColor(this.color);
		return copy;
	}
}
