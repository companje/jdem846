package us.wthr.jdem846.graphics;

import us.wthr.jdem846.util.ColorUtil;

public class MutableColor extends Color
{

	public MutableColor(double r, double g, double b, double a)
	{
		super(r, g, b, a);
	}

	public MutableColor(double r, double g, double b)
	{
		super(r, g, b);
	}

	public MutableColor(float r, float g, float b, float a)
	{
		super(r, g, b, a);
	}

	public MutableColor(float r, float g, float b)
	{
		super(r, g, b);
	}

	public MutableColor(int r, int g, int b, int a)
	{
		super(r, g, b, a);
	}

	public MutableColor(int r, int g, int b)
	{
		super(r, g, b);
	}

	public MutableColor(int c)
	{
		super(c);
	}

	public MutableColor(int[] a, int offset)
	{
		super(a, offset);
	}

	public MutableColor(int[] a)
	{
		super(a);
	}

	public MutableColor(String hex)
	{
		super(hex);
	}
	
	
	public void fromInt(int c)
	{
		ColorUtil.intToRGBA(c, rgba);
		clampValues();
	}
	
	public void setRed(int red)
	{
		this.rgba[0] = red;
		clampValues();
	}
	
	public void setGreen(int green)
	{
		this.rgba[1] = green;
		clampValues();
	}
	
	public void setBlue(int blue)
	{
		this.rgba[2] = blue;
		clampValues();
	}
	
	public void setAlpha(int alpha)
	{
		this.rgba[3] = alpha;
		clampValues();
	}
	
}
