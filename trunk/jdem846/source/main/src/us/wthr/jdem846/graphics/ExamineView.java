package us.wthr.jdem846.graphics;

import java.util.ArrayList;
import java.util.List;

import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Matrix;
import us.wthr.jdem846.math.Quaternion;
import us.wthr.jdem846.math.Vector;

//https://kenai.com/projects/jogl/sources/jogl-demos-git/content/src/gleem/ExaminerViewer.java?rev=2d5f1650af2b64f7a8d25842b2b95192b3e4a4e9
public class ExamineView
{
	private Quaternion orientation;
	private Matrix modelView;
	private Matrix unrotatedModelView;
	
	private double maxDistance = 10;
	private double minDistance = .5;
	
	private double distance = 2;
	private double scale = 1.0;
	private double pitch = 0.0;
	private double yaw = 0.0;
	private double roll = 0.0;

	private double minScale = 0.0001;
	private double maxScale = 10000.0;
	
	private double minPitch = 0;
	private double maxPitch = 90;
	
	private double modelRadius = .5;
	
	private List<ModelViewChangeListener> modelViewChangeListeners = new ArrayList<ModelViewChangeListener>();
	
	public ExamineView()
	{
		orientation = new Quaternion();
		modelView = new Matrix(true);
		unrotatedModelView = new Matrix(true);
	}

	public void rotate(double rotateX, double rotateY)
	{
		double xRads = MathExt.radians(rotateX);//Math.PI * -1.0f * rotateX / 1000.0f;
		double yRads = MathExt.radians(rotateY);//Math.PI * -1.0f * rotateY / 1000.0f;
		
		Vector xAxis = new Vector(Vector.X_AXIS_VECTOR);
		Vector yAxis = new Vector(Vector.Y_AXIS_VECTOR);

		xAxis.rotate(0, 0, getRoll());
		yAxis.rotate(0, 0, getRoll());
		
		Quaternion xRot = new Quaternion(xAxis, xRads);
		Quaternion yRot = new Quaternion(yAxis, yRads);
		Quaternion newRot = yRot.times(xRot);
		orientation = orientation.times(newRot);

		recalcModelView();
	}

	public void recalcModelView()
	{
		recalcModelView(modelView, true);
		recalcModelView(unrotatedModelView, false);
		
		fireModelViewChangeListeners();
	}
	
	private void recalcModelView(Matrix modelView, boolean rotated)
	{
		modelView.loadIdentity();
		
		modelView.translate(0, 0, modelRadius);
		modelView.rotate(pitch, 1, 0, 0);
		modelView.rotate(yaw, 0, 1, 0);
		modelView.rotate(roll, 0, 0, 1);
		modelView.translate(0, 0, -modelRadius);
		
		if (rotated) {
			Matrix m = new Matrix(true);
			orientation.toMatrix(m);
			modelView.multiply(m);
		}
		
		modelView.scale(scale, scale, scale);
	}
	

	
	public Quaternion getOrientation()
	{
		return orientation;
	}

	public void setOrientation(Quaternion orientation)
	{
		this.orientation = orientation;
	}

	public Matrix getModelView()
	{
		return modelView;
	}
	
	public Matrix getUnrotatedModelView()
	{
		return unrotatedModelView;
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
		if (pitch < minPitch)
			pitch = minPitch;
		if (pitch > maxPitch)
			pitch = maxPitch;
		
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
		if (scale < minScale) {
			scale = minScale;
			recalcModelView();
		}
	}

	public double getMaxScale()
	{
		return maxScale;
	}

	public void setMaxScale(double maxScale)
	{
		this.maxScale = maxScale;
		if (scale > maxScale) {
			scale = maxScale;
			recalcModelView();
		}
	}

	public double getDistance()
	{
		return distance;
	}

	public void setDistance(double distance)
	{
		if (distance > maxDistance) {
			distance = maxDistance;
		}
		if (distance < minDistance) {
			distance = minDistance;
		}
		this.distance = distance;
		recalcModelView();
	}


	public double getMaxDistance()
	{
		return maxDistance;
	}

	public void setMaxDistance(double maxDistance)
	{
		this.maxDistance = maxDistance;
		if (distance > maxDistance) {
			distance = maxDistance;
			recalcModelView();
		}
	}

	public double getMinDistance()
	{
		return minDistance;
	}

	public void setMinDistance(double minDistance)
	{
		this.minDistance = minDistance;
		if (distance < minDistance) {
			distance = minDistance;
			recalcModelView();
		}
	}

	public double getModelRadius()
	{
		return modelRadius;
	}

	public void setModelRadius(double modelRadius)
	{
		this.modelRadius = modelRadius;
	}
	
	
	protected void fireModelViewChangeListeners()
	{
		for (ModelViewChangeListener l : this.modelViewChangeListeners) {
			l.onModelViewChanged(modelView);
		}
	}
	
	public void addModelViewChangeListener(ModelViewChangeListener l)
	{
		this.modelViewChangeListeners.add(l);
	}
	
	public boolean removeModelViewChangeListener(ModelViewChangeListener l)
	{
		return this.modelViewChangeListeners.remove(l);
	}
	
	
	public interface ModelViewChangeListener {
		public void onModelViewChanged(Matrix modelView);
	}
	
	public void fromString(String s)
	{
		
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
}
