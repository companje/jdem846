package us.wthr.jdem846.render;

import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.render.util.ColorUtil;

public class PixelBuffer extends AbstractBuffer
{

	private int[] buffer;
	
	
	public PixelBuffer(int width, int height, int subpixelWidth)
	{
		super(width, height, subpixelWidth);
		buffer = new int[getBufferLength()];
		
		reset();
	}
	
	public void reset()
	{
		if (buffer == null) {
			return;
		}
		
		for (int i = 0; i < getBufferLength(); i++) {
			buffer[i] = 0x0;
		}
	}
	

	
	public void set(double x, double y, int rgba)
	{
		int index = this.getIndex(x, y);
		
		if (index >= 0 && index < getBufferLength()) {
			buffer[index] = rgba;
		} else {
			// TODO: Throw
		}
	}
	
	public int get(double x, double y)
	{
		int index = this.getIndex(x, y);
		
		if (index >= 0 && index < getBufferLength()) {
			return buffer[index];
		} else {
			return 0x0;
			// TODO: Throw
		}
	}
	
	
	

	
}
