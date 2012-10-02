package us.wthr.jdem846.graphics;

import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.math.MathExt;

public class Texture {
	public int[] texture = null;
	public int width = 0;
	public int height = 0;
	public double left = 0;
	public double front = 0;
	
	
	public Texture(int width, int height, int[] texture)
	{
		this.width = width;
		this.height = height;
		this.texture = texture;
	}
	
	
	protected int index(double x, double y)
	{
		return index((int) MathExt.round(x), (int) MathExt.round(y));		
	}
	
	protected int index(int x, int y)
	{
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return -1;
		}
		int index = (y * width) + x;
		return index;
	}
	
	public int getColor(double x, double y)
	{
		return getColor((int)MathExt.round(x), (int) MathExt.round(y));
	}
	
	public int getColor(int x, int y)
	{
		int index = index(x, y);
		if (index >= 0 && index < texture.length) {
			return texture[index];
		} else {
			return 0x0;
		}
	}
	
	public int getColorNearest(double left, double front)
	{
		int x = (int) MathExt.round(left * (double)width);
		int y = (int) MathExt.round(front * (double)height);
		
		return getColor(x, y);
	}
	
	public int getColorLinear(double left, double front)
	{
		double x = (left * (double)width);
		double y = (front * (double)height); 
		
		double _x = MathExt.floor(x);
		double _y = MathExt.floor(y);
		
		double xFrac = x - _x;
		double yFrac = y - _y;
		
		boolean b00 = isValidCoordinate(_x + 0, _y + 0);
		boolean b01 = isValidCoordinate(_x + 1, _y + 0);
		boolean b10 = isValidCoordinate(_x + 0, _y + 1);
		boolean b11 = isValidCoordinate(_x + 1, _y + 1);
		
		
		int c00 = (b00) ? getColor(_x + 0, _y + 0) : 0x0;
		int c01 = (b01) ? getColor(_x + 1, _y + 0) : 0x0;
		int c10 = (b10) ? getColor(_x + 0, _y + 1) : 0x0;
		int c11 = (b11) ? getColor(_x + 1, _y + 1) : 0x0;
		
		c00 = getValidColor(c00, b00, c01, b01, c10, b10, c11, b11);
		c01 = getValidColor(c01, b01, c00, b00, c11, b11, c10, b10);
		c10 = getValidColor(c10, b10, c11, b11, c00, b00, c01, b01);
		c11 = getValidColor(c11, b11, c10, b10, c01, b01, c00, b00);
	

		int color =  ColorAdjustments.interpolateColor(c00, c01, c10, c11, xFrac, yFrac);

		return color;
	}
	
	protected int getValidColor(int c00, boolean b00, int c01, boolean b01, int c10, boolean b10, int c11, boolean b11)
	{
		if (b00)
			return c00;
		if (b01)
			return c01;
		if (b10)
			return c10;
		if (b11)
			return c11;
		
		return 0x0;
	}
	
	protected boolean isValidCoordinate(double x, double y)
	{
		if (texture == null) {
			return false;
		}
		
		if (x >= 0 && x < width && y >= 0 && y < height) {
			return true;
		} else {
			return false;
		}
		
	}
	
}
