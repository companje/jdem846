package us.wthr.jdem846.rasterdata;

import java.awt.Rectangle;


public class RasterDataRowColumnBox
{
	
	private Rectangle rectangle;
	
	public RasterDataRowColumnBox(int x, int y, int width, int height)
	{
		rectangle = new Rectangle(x, y, width, height);
	}
	
	
	public boolean contains(int x, int y)
	{
		return rectangle.contains(x, y);
	}
	
	public boolean contains(RasterDataRowColumnBox bounds)
	{
		return rectangle.contains(bounds.rectangle);
	}
	
	public boolean overlaps(RasterDataRowColumnBox bounds)
	{
		return rectangle.contains(bounds.getLeftX(), bounds.getTopY())
					|| rectangle.contains(bounds.getLeftX(), bounds.getBottomY())
					|| rectangle.contains(bounds.getRightX(), bounds.getTopY())
					|| rectangle.contains(bounds.getRightX(), bounds.getBottomY())
					|| bounds.contains(getLeftX(), getTopY())
					|| bounds.contains(getLeftX(), getBottomY())
					|| bounds.contains(getRightX(), getTopY())
					|| bounds.contains(getRightX(), getBottomY());
	}
	
	
	public int getLeftX()
	{
		return rectangle.x;
	}
	
	public int getRightX()
	{
		return rectangle.x + rectangle.width;
	}
	
	
	public int getTopY()
	{
		return rectangle.y;
	}
	
	public int getBottomY()
	{
		return rectangle.y + rectangle.height;
	}
	
	public int getWidth()
	{
		return rectangle.width;
	}
	
	public int getHeight()
	{
		return rectangle.height;
	}
	
	public RasterDataRowColumnBox copy()
	{
		RasterDataRowColumnBox copy = new RasterDataRowColumnBox(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		return copy;
	}
	
}
