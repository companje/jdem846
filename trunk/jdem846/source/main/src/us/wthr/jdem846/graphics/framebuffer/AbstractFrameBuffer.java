package us.wthr.jdem846.graphics.framebuffer;

import us.wthr.jdem846.math.MathExt;

public abstract class AbstractFrameBuffer implements FrameBuffer
{
	protected int width;
	protected int height;
	protected int bufferLength;
	
	public AbstractFrameBuffer(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.bufferLength = width * height;
	}
	
	
	protected int index(double x, double y)
	{
		return index((int) MathExt.floor(x), (int) MathExt.floor(y));
	}
	
	protected int index(int x, int y)
	{
		if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
			return -1;
		}
		
		int i = (y * width) + x;
		return i;
	}
	
	
	@Override
	public void reset()
	{
		reset(false, -1);
	}
	
	@Override
	public int getWidth()
	{
		return width;
	}
	
	@Override
	public int getHeight()
	{
		return height;
	}
	
}
