package us.wthr.jdem846ui.views.gradient;

public class StopIndicatorConfig
{
	private int halfHeight = 4;
	private int width = 10;
	private int leftX = 1;
	
	public StopIndicatorConfig()
	{
		
	}
	
	public StopIndicatorConfig(int halfHeight, int width, int leftX)
	{
		this.halfHeight = halfHeight;
		this.width = width;
		this.leftX = leftX;
	}

	public int getHalfHeight()
	{
		return halfHeight;
	}

	public void setHalfHeight(int halfHeight)
	{
		this.halfHeight = halfHeight;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getLeftX()
	{
		return leftX;
	}

	public void setLeftX(int leftX)
	{
		this.leftX = leftX;
	}
	
	
	
}
