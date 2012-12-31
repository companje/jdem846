package us.wthr.jdem846.graphics;

public class ImageCapture
{
	
	protected int[] buffer;
	protected int width;
	protected int height;
	protected int backgroundColor;
	
	public ImageCapture(int width, int height)
	{
		this(width, height, 0x0);
	}
	
	public ImageCapture(int width, int height, int backgroundColor)
	{
		this.width = width;
		this.height = height;
		this.backgroundColor = backgroundColor;
		
		this.buffer = new int[width * height];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = backgroundColor;
		}
		
	}
	
	
	public void set(int x, int y, int rgba)
	{
		if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
			return;
		}
		
		this.buffer[(y * this.width) + x] = rgba;
		
	}
	
	
	public int get(int x, int y)
	{
		if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
			return backgroundColor;
		}
		
		return this.buffer[(y * this.width) + x];
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
