package us.wthr.jdem846.jogl.view;

public class ViewPoint
{
	private double pitch = 0;
	private double roll = 0;
	private double yaw = 0;
	
	private double x = 0;
	private double y = 0;
	private double z = 20;
	
	public ViewPoint()
	{
		
	}

	public double getPitch()
	{
		return pitch;
	}

	public void setPitch(double pitch)
	{
		this.pitch = pitch;
	}

	public double getRoll()
	{
		return roll;
	}

	public void setRoll(double roll)
	{
		this.roll = roll;
	}

	public double getYaw()
	{
		return yaw;
	}

	public void setYaw(double yaw)
	{
		this.yaw = yaw;
	}

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public double getZ()
	{
		return z;
	}

	public void setZ(double z)
	{
		this.z = z;
	}
	
	
	
	
	
}
