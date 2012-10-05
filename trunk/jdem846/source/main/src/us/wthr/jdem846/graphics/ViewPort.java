package us.wthr.jdem846.graphics;

public class ViewPort
{
	public int x = 0;
	public int y = 0;
	public int width = 1000;
	public int height = 1000;
	
	public ViewPort(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
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
