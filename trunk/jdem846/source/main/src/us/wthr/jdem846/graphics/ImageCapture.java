package us.wthr.jdem846.graphics;

public class ImageCapture
{
	
	protected int[] buffer;
	protected int width;
	protected int height;
	
	
	public ImageCapture(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		this.buffer = new int[width * height];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = 0x0;
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
			return 0x0;
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
