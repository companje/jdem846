package us.wthr.jdem846.model;

import java.util.Map;

import us.wthr.jdem846.math.Matrix;
import us.wthr.jdem846.math.Quaternion;

public class ViewerPosition
{
	private Quaternion orientation = new Quaternion();
	private double distance = 2;
	private double scale = 1.0;
	private double pitch = 0.0;
	private double yaw = 0.0;
	private double roll = 0.0;

	public ViewerPosition()
	{
		
	}
	
	public ViewerPosition(ViewerPosition other)
	{
		this.pitch = other.pitch;
		this.roll = other.roll;
		this.yaw = other.yaw;
		this.distance = other.distance;
		this.scale = other.scale;
		this.orientation = new Quaternion(other.orientation);
		
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


	public Quaternion getOrientation()
	{
		return orientation;
	}

	public void setOrientation(Quaternion orientation)
	{
		this.orientation = orientation;
	}

	public double getDistance()
	{
		return distance;
	}

	public void setDistance(double distance)
	{
		this.distance = distance;
	}

	public double getScale()
	{
		return scale;
	}

	public void setScale(double scale)
	{
		this.scale = scale;
	}

	public Matrix toMatrix()
	{
		return toMatrix(true);
	}
	
	public Matrix toMatrix(boolean rotated)
	{
		Matrix modelView = new Matrix(true);

		modelView.translate(0, 0, 0.5);
		modelView.rotate(pitch, 1, 0, 0);
		modelView.rotate(yaw, 0, 1, 0);
		modelView.rotate(roll, 0, 0, 1);
		modelView.translate(0, 0, -0.5);
		
		if (rotated) {
			Matrix m = new Matrix(true);
			orientation.toMatrix(m);
			modelView.multiply(m);
		}
		
		modelView.scale(scale, scale, scale);
		return modelView;
	}
	
	public static ViewerPosition fromString(String s)
	{
		Map<String, double[]> values = SimpleNumberListMapSerializer.parseDoubleListString(s);
		
		double[] quarternion = values.get("quarternion");
		double[] pitch = values.get("pitch");
		double[] roll = values.get("roll");
		double[] yaw = values.get("yaw");
		double[] distance = values.get("distance");
		double[] scale = values.get("scale");
		
		ViewerPosition viewer = new ViewerPosition();
		viewer.setPitch(pitch[0]);
		viewer.setRoll(roll[0]);
		viewer.setYaw(yaw[0]);
		viewer.setDistance(distance[0]);
		viewer.setScale(scale[0]);
		
		Quaternion orientation = new Quaternion();
		orientation.set(quarternion[0], quarternion[1], quarternion[2], quarternion[3]);
		viewer.setOrientation(orientation);
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
				"distance:[" + 
				getDistance() + "];" +
				"scale:[" + 
				getScale() + "];" +
				"quarternion:[" + 
				orientation.getQ(0) + "," +
				orientation.getQ(1) + "," +
				orientation.getQ(2) + "," +
				orientation.getQ(3) + "];";
		return s;
			
	}
	
	
	public ViewerPosition copy()
	{
		ViewerPosition clone = new ViewerPosition(this);
		return clone;
	}
	
	
}
