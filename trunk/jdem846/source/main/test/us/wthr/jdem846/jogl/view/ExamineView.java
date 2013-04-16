package us.wthr.jdem846.jogl.view;

import us.wthr.jdem846.math.Matrix;
import us.wthr.jdem846.math.Vector;

//https://kenai.com/projects/jogl/sources/jogl-demos-git/content/src/gleem/ExaminerViewer.java?rev=2d5f1650af2b64f7a8d25842b2b95192b3e4a4e9
public class ExamineView
{
	private Quaternion orientation;
	private Matrix modelView;

	private double distance = 20;
	private double scale = 1.0;
	private double pitch = 0.0;
	private double yaw = 0.0;
	private double roll = 0.0;

	private double minScale = 0.0001;
	private double maxScale = 10000.0;
	
	private double modelRadius = 5;
	
	public ExamineView()
	{
		orientation = new Quaternion();
		modelView = new Matrix(true);
	}

	public void rotate(double rotateX, double rotateY)
	{
		double xRads = Math.PI * -1.0f * rotateX / 1000.0f;
		double yRads = Math.PI * -1.0f * rotateY / 1000.0f;

		Quaternion xRot = new Quaternion(Vector.X_AXIS, xRads);
		Quaternion yRot = new Quaternion(Vector.Y_AXIS, yRads);
		Quaternion newRot = yRot.times(xRot);
		orientation = orientation.times(newRot);

		recalcModelView();
	}

	public void recalcModelView()
	{
		modelView.loadIdentity();
		
		modelView.translate(0, 0, modelRadius);
		modelView.rotate(pitch, 1, 0, 0);
		modelView.rotate(yaw, 0, 1, 0);
		modelView.rotate(roll, 0, 0, 1);
		modelView.translate(0, 0, -modelRadius);
		
		Matrix m = new Matrix(true);
		orientation.toMatrix(m);
		modelView.multiply(m);
		modelView.scale(scale, scale, scale);
		
	}

	public Matrix getModelView()
	{
		return modelView;
	}

	public void scale(double scale)
	{
		if (scale > maxScale)
			scale = maxScale;
		if (scale < minScale)
			scale = minScale;
		this.scale = scale;
		recalcModelView();
	}

	public double getScale()
	{
		return scale;
	}

	public double getPitch()
	{
		return pitch;

	}

	public void setPitch(double pitch)
	{
		this.pitch = pitch;
		recalcModelView();
	}

	public double getYaw()
	{
		return yaw;
	}

	public void setYaw(double yaw)
	{
		this.yaw = yaw;
		recalcModelView();
	}

	public double getRoll()
	{
		return roll;
	}

	public void setRoll(double roll)
	{
		this.roll = roll;
		recalcModelView();
	}

	public double getMinScale()
	{
		return minScale;
	}

	public void setMinScale(double minScale)
	{
		this.minScale = minScale;
	}

	public double getMaxScale()
	{
		return maxScale;
	}

	public void setMaxScale(double maxScale)
	{
		this.maxScale = maxScale;
	}

	public double getDistance()
	{
		return distance;
	}

	public void setDistance(double distance)
	{
		this.distance = distance;
	}

	public double getModelRadius()
	{
		return modelRadius;
	}

	public void setModelRadius(double modelRadius)
	{
		this.modelRadius = modelRadius;
	}
	
	
	
}
