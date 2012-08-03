package us.wthr.jdem846.canvas;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;

public abstract class AbstractBuffer
{
	private static Log log = Logging.getLog(AbstractBuffer.class);
	
	private int width;
	private int height;
	
	private int subpixelWidth;
	
	private int bufferLength;
	
	public AbstractBuffer(int width, int height, int subpixelWidth)
	{
		this.width = width;
		this.height = height;
		this.subpixelWidth = subpixelWidth;
		
		
		bufferLength = width * height * (int) MathExt.sqr(subpixelWidth);
		log.debug("Abstract Buffer: " + bufferLength);
	}
	
	public abstract void reset();
	
	
	protected int getIndex(double X, double Y)
	{
		
		if (X < 0 || X >= this.width || Y < 0 || Y >= this.height)
			return -1;
		
		double f = 1.0 / this.subpixelWidth;
		double x = MathExt.round(X / f) * f;
		double y = MathExt.round(Y / f) * f;
		
		//x = MathExt.floor(x / f) * f;
		//y = MathExt.floor(y / f) * f;
		
		int _x = (int) MathExt.floor(x);
		int _y = (int) MathExt.floor(y);
		
		int _xSub = (int) ((x - (double)_x) / f);
		int _ySub = (int) ((y - (double)_y) / f);
		
		int index = ((_y * this.width) * this.subpixelWidth) + _ySub + (_x * this.subpixelWidth + _xSub);
		return index;
	}
	
	

	public int getBufferLength()
	{
		return bufferLength;
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
