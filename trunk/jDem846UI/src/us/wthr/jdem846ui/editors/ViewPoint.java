package us.wthr.jdem846ui.editors;

import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.math.Vectors;
import us.wthr.jdem846.model.ViewPerspective;

public class ViewPoint
{
	private double pitch = 0;
	private double roll = 0;
	private double yaw = 0;
	
	private double x = 0;
	private double y = 0;
	private double z = 0;
	
	private Vector focalPoint = new Vector(0, 0, -1);
	
	
	public ViewPoint()
	{
		
	}

	public void applyPointDeltas(Vector moveVector)
	{
		Vectors.rotateY(getYaw(), moveVector);
        setZ(getZ() + moveVector.z);
        setX(getX() + moveVector.x);
	}
	
	public void applyAngleDeltas(double pitchDelta, double rollDelta, double yawDelta)
	{
		setPitch(getPitch() + pitchDelta);
		setRoll(getRoll() + rollDelta);
		setYaw(getYaw() + yawDelta);
		
        Vectors.rotate(-pitchDelta, -yawDelta, -rollDelta, this.focalPoint);
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


	public Vector getFocalPoint()
	{
		return focalPoint;
	}


	public void setFocalPoint(Vector focalPoint)
	{
		this.focalPoint = focalPoint;
	}
	
	
	
	
	public ViewPerspective toViewPerspective()
	{
		ViewPerspective view = new ViewPerspective();
		view.setRotateX(pitch);
		view.setRotateY(yaw);
		view.setRotateZ(roll);
		view.setShiftX(x);
		view.setShiftY(y);
		view.setShiftZ(z);
		view.setZoom(1.0);
		
		return view;
	}
	
	
}
