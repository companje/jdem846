package us.wthr.jdem846.model;

import java.util.Map;

import us.wthr.jdem846.math.Vector;

public class ViewerPosition
{
	private double pitch = 0;
	private double roll = 0;
	private double yaw = 0;

	
	private Vector position = new Vector(0, 0, 1);
	private Vector focalPoint = new Vector(0, 0, -1);
	
	public ViewerPosition()
	{
		
	}
	
	public ViewerPosition(ViewerPosition other)
	{
		this.pitch = other.pitch;
		this.roll = other.roll;
		this.yaw = other.yaw;
		
		this.position = new Vector(other.position);
		this.focalPoint = new Vector(other.focalPoint);
	}

	public double getPitch()
	{
		return pitch;
	}

	public void setPitch(double pitch)
	{
		if (pitch > 89) {
			pitch = 89;
		} else if (pitch < -89) {
			pitch = -89;
		}
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

	public Vector getPosition()
	{
		return position;
	}

	public void setPosition(Vector position)
	{
		this.position = position;
	}

	public Vector getFocalPoint()
	{
		return focalPoint;
	}

	public void setFocalPoint(Vector focalPoint)
	{
		this.focalPoint = focalPoint;
	}

	public static ViewerPosition fromString(String s)
	{

		Map<String, double[]> values = SimpleNumberListMapSerializer.parseDoubleListString(s);
		
		double[] position = values.get("position");
		double[] focalPoint = values.get("focalPoint");
		double[] pitch = values.get("pitch");
		double[] roll = values.get("roll");
		double[] yaw = values.get("yaw");
		
		
		ViewerPosition viewer = new ViewerPosition();
		viewer.setPitch(pitch[0]);
		viewer.setRoll(roll[0]);
		viewer.setYaw(yaw[0]);
		
		viewer.getPosition().x = position[0];
		viewer.getPosition().y = position[1];
		viewer.getPosition().z = position[2];
		
		viewer.getFocalPoint().x = focalPoint[0];
		viewer.getFocalPoint().y = focalPoint[1];
		viewer.getFocalPoint().z = focalPoint[2];
		
		return viewer;
	}
	
	public String toString()
	{
		String s = "pitch:[" + 
				getPitch() + "];" +
				"roll:[" + 
				getRoll() + "];" +
				"yaw:[" + 
				getYaw() + "];" +
				"position:[" +
				getPosition().x + "," +
				getPosition().y + "," + 
				getPosition().z + "];" +
				"focalPoint:[" +
				getFocalPoint().x + "," +
				getFocalPoint().y + "," + 
				getFocalPoint().z + "];";
		return s;
			
	}
	
	
	public ViewerPosition copy()
	{
		ViewerPosition clone = new ViewerPosition(this);
		return clone;
	}
	
	
}
