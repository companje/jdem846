package us.wthr.jdem846.model;

import java.awt.Color;
import java.util.Map;

import us.wthr.jdem846.canvas.util.ColorUtil;

public class RgbaColor
{
	
	private int[] rgba = new int[4];
	
	public RgbaColor()
	{
		rgba[0] = 0x0;
		rgba[1] = 0x0;
		rgba[2] = 0x0;
		rgba[3] = 0x0;
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
		rgba[0] = r;
		rgba[1] = g;
		rgba[2] = b;
		rgba[3] = a;
	}
	
	public void setRgba(int rgba)
	{
		ColorUtil.intToRGBA(rgba, this.rgba);
	}
	
	public void setRgba(int[] rgba)
	{
		setRgba(rgba[0], rgba[1], rgba[2], rgba[3]);
	}
	
	
	public int getRgba()
	{
		return ColorUtil.rgbaToInt(rgba);
	}
	
	public void getRgba(int[] fill)
	{
		fill[0] = rgba[0];
		fill[1] = rgba[1];
		fill[2] = rgba[2];
		fill[3] = rgba[3];
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
		String s = "rgba:[" + rgba[0] + "," + 
						rgba[1] + "," +
						rgba[2] + "," +
						rgba[3] + "]";
		return s;
	}
	
	public Color toAwtColor()
	{
		return new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
	}
	
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof RgbaColor) {
			RgbaColor other = (RgbaColor) obj;
			if (other.rgba[0] == this.rgba[0]
					&& other.rgba[1] == this.rgba[1]
					&& other.rgba[2] == this.rgba[2]
					&& other.rgba[3] == this.rgba[3]) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
