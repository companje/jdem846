package us.wthr.jdem846.model.processing.coloring;

public class AspectColorCategory
{
	
	private String direction;
	private double start;
	private double end;
	private int[] rgba = new int[4];
	
	public AspectColorCategory(String direction, double start, double end, int[] rgba)
	{
		this.direction = direction;
		this.start = start;
		this.end = end;
		this.setRgba(rgba);
	}

	public String getDirection()
	{
		return direction;
	}

	public void setDirection(String direction)
	{
		this.direction = direction;
	}

	

	public double getStart()
	{
		return start;
	}

	public void setStart(double start)
	{
		this.start = start;
	}

	public double getEnd()
	{
		return end;
	}

	public void setEnd(double end)
	{
		this.end = end;
	}

	public int[] getRgba()
	{
		return rgba;
	}
	
	public void getRgba(int[] fill)
	{
		fill[0] = rgba[0];
		fill[1] = rgba[1];
		fill[2] = rgba[2];
		fill[3] = rgba[3];
	}
	
	public void setRgba(int[] rgba)
	{
		this.rgba[0] = rgba[0];
		this.rgba[1] = rgba[1];
		this.rgba[2] = rgba[2];
		this.rgba[3] = rgba[3];
	}
	
	
}
