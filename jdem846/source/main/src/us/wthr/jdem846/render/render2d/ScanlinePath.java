package us.wthr.jdem846.render.render2d;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ScanlinePath
{
	private static Log log = Logging.getLog(ScanlinePath.class);
	
	private double leftX;
	private double rightX;
	private double y;
	private double z;
	private int rgba;
	
	public ScanlinePath(double leftX, double rightX, double y, double z, int rgba)
	{
		this.leftX = leftX;
		this.rightX = rightX;
		this.y = y;
		this.z = z;
		this.rgba = rgba;
	}

	public double getLeftX()
	{
		return leftX;
	}

	public double getRightX()
	{
		return rightX;
	}

	public double getY()
	{
		return y;
	}

	public double getZ()
	{
		return z;
	}

	public int getRgba()
	{
		return rgba;
	}
	
	
	
	
}
