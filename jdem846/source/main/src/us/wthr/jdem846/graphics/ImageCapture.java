package us.wthr.jdem846.graphics;

import us.wthr.jdem846.buffers.BufferFactory;
import us.wthr.jdem846.buffers.IIntBuffer;

public class ImageCapture
{
	
	//protected int[] buffer;
	protected IIntBuffer buffer;
	protected int width;
	protected int height;
	protected int backgroundColor;

	
	public ImageCapture(int width, int height, int backgroundColor)
	{
		this.width = width;
		this.height = height;
		this.backgroundColor = backgroundColor;
		
		int capacity = width * height;
		buffer = BufferFactory.allocateIntBuffer(capacity);
		for (int i = 0; i < capacity; i++) {
			buffer.put(i, backgroundColor);
		}

	}
	
	public void dispose()
	{
		if (buffer != null) {
			buffer.dispose();
		}
	}
	
	
	public void set(int x, int y, int rgba)
	{
		if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
			return;
		}
		
		int index = (y * this.width) + x;
		buffer.put(index, rgba);
	}
	
	
	public int get(int x, int y)
	{
		if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
			return backgroundColor;
		}
		
		int index = (y * this.width) + x;
		return buffer.get(index);
	}
	
	
	public int getWidth()
	{
		return width;
	}
	
	
	public int getHeight()
	{
		return height;
	}
	
}
