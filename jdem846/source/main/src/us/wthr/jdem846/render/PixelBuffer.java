package us.wthr.jdem846.render;

import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.render.util.ColorUtil;

public class PixelBuffer
{
	
	private int width;
	private int height;
	
	private int subpixelWidth;
	
	private int bufferLength;
	private int[] buffer;
	
	
	public PixelBuffer(int width, int height, int subpixelWidth)
	{
		this.width = width;
		this.height = height;
		this.subpixelWidth = subpixelWidth;
		
		
		bufferLength = width * height * (int) MathExt.sqr(subpixelWidth);
		buffer = new int[bufferLength];
		
		reset();
	}
	
	public void reset()
	{
		if (buffer == null) {
			return;
		}
		
		for (int i = 0; i < bufferLength; i++) {
			buffer[i] = 0x0;
		}
	}
	

	
	public void set(double x, double y, int rgba)
	{
		double f = 1.0 / this.subpixelWidth;
		x = MathExt.round(x / f) * f;
		y = MathExt.round(y / f) * f;
		
		int _x = (int) MathExt.floor(x);
		int _y = (int) MathExt.floor(y);
		
		int _xSub = (int) ((x - (double)_x) / f);
		int _ySub = (int) ((y - (double)_y) / f);
		
		int index = ((_y * this.width) * this.subpixelWidth) + _ySub + (_x * this.subpixelWidth + _xSub);
		
		if (index >= 0 && index < this.bufferLength) {
			buffer[index] = rgba;
		} else {
			// TODO: Throw
		}
	}
	
	public int get(double x, double y)
	{
		double f = 1.0 / this.subpixelWidth;
		x = MathExt.round(x / f) * f;
		y = MathExt.round(y / f) * f;
		
		int _x = (int) MathExt.floor(x);
		int _y = (int) MathExt.floor(y);
		
		int _xSub = (int) ((x - (double)_x) / f);
		int _ySub = (int) ((y - (double)_y) / f);
		
		int index = ((_y * this.width) * this.subpixelWidth) + _ySub + (_x * this.subpixelWidth + _xSub);
		
		if (index >= 0 && index < this.bufferLength) {
			return buffer[index];
		} else {
			return 0x0;
			// TODO: Throw
		}
	}
	
	
	
	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int getSubpixelWidth()
	{
		return subpixelWidth;
	}

	
}
