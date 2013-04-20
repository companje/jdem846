package us.wthr.planets;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import us.wthr.jdem846.graphics.ExamineView;
import us.wthr.jdem846.graphics.IColor;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Matrix;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.math.Vectors;

public class AtmosphereHalo implements Renderable
{

	private double radius = .5;

	
	private double elevationMinimum = 0;
	private double elevationMaximum = 10;
	
	private HaloColoring coloring;
	
	private Vector atmosphereVector = new Vector();
	private Vector atmosphereNormal = new Vector();
	
	public AtmosphereHalo()
	{
		this(new HaloColoring());
	}
	
	public AtmosphereHalo(HaloColoring coloring)
	{
		this.coloring = coloring;
	}
	
	
	public void render(GL2 gl, GLU glu, ExamineView view)
	{	
		gl.glDisable(GL2.GL_FOG);
		gl.glDisable(GL2.GL_LIGHTING);
		
		gl.glPushMatrix();
		renderHalo(gl, glu, view, -0.2, coloring.getColorLower(), 0.01, coloring.getColorUpper());
		renderHalo(gl, glu, view, 0.01, coloring.getColorUpper(), 0.025, coloring.getColorFaded());
		gl.glPopMatrix();
	}
	
	protected void renderHalo(GL2 gl, GLU glu, ExamineView view, double elevationMinimum, IColor colorMinimum, double elevationMaximum, IColor colorMaximum)
	{
		Vector spherePosition = new Vector(0, 0, -radius);
		Vector focalPoint = new Vector(0, 0, 0);
		
		
		spherePosition.rotate(-view.getPitch(), Vectors.X_AXIS);
		spherePosition.inverse();
		
		gl.glPushMatrix();
		
		gl.glLoadIdentity();
		glu.gluLookAt(0, 0, view.getDistance(), 0, 0, 0, 0, 1, 0);

		//double distance = MathExt.sqrt(MathExt.sqr(spherePosition.y) + MathExt.sqr(view.getDistance() + spherePosition.z));
		double distance = view.getEyeDistanceToCenter();
		
		double near = distance - radius;
		double far = farClipDistance(near);
		double distanceToCenter = distance;
		double trans = distanceToCenter - far;
		double horizonHeightFromPlane = MathExt.sqrt(MathExt.sqr(radius) - MathExt.sqr(trans));
		//double distanceToSurfaceHorizon = MathExt.sqrt(MathExt.sqr(far) + MathExt.sqr(horizonHeightFromPlane));
	   
		double scale = (horizonHeightFromPlane / radius);// * view.getScale();
		
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glDisable(GL2.GL_COLOR_MATERIAL);

		gl.glTranslated(0.0, 0.0, trans);
		gl.glScaled(scale, scale, scale);

		gl.glTranslated(-spherePosition.x, -spherePosition.y, -spherePosition.z);
		gl.glTranslated(0, 0, radius * scale);

		gl.glRotated(90.0, 1.0, 0.0, 0.0);
		
		//double distanceToCenterAngled = MathExt.sqrt(MathExt.sqr(spherePosition.y) + MathExt.sqr(view.getDistance() + spherePosition.z));
		double a = MathExt.degrees(MathExt.asin(spherePosition.y / distance));

		gl.glRotated(-a, 1.0, 0.0, 0.0);

		gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		
		//double latitude = 0.0;
		for (double longitude = 0.0; longitude <= 360.0; longitude+=1.0) {
			double latitude = 0.0;
		//for (double latitude = 90; latitude >= -90; latitude -= 1.0) {
			renderAtmosphereVertex(gl, glu, latitude, longitude, elevationMinimum, colorMinimum);
			renderAtmosphereVertex(gl, glu, latitude, longitude, elevationMaximum, colorMaximum);
		}
		gl.glEnd();
		gl.glPopMatrix();
	}
	
	
	public void renderAtmosphereVertex(GL2 gl, GLU glu, double latitude, double longitude, double elevation, IColor color)
	{
		Spheres.getPoint3D(longitude, latitude, radius + elevation, atmosphereVector);
		
		//view.getNormal(latitude, longitude, atmosphereNormal, false)
		//Vectors.rotate(viewAngle.getRotateX(), viewAngle.getRotateY(), viewAngle.getRotateZ(), atmosphereNormal);
	   
		//Vectors.rotate(90, 0, 0, atmosphereNormal);
		//Vectors.rotate(0, 180, 0, atmosphereNormal);
		//Vectors.rotate(0, 0, 90, atmosphereNormal);
		//renderer.normal(atmosphereNormal)
		
		float f[] = {0, 0, 0, 0};
		color.toArray(f);
		
		gl.glColor4fv(f, 0);
		gl.glVertex3d(atmosphereVector.x, atmosphereVector.y, atmosphereVector.z);
	       
	}
	
	public void setBillboard(GL2 gl, GLU glu, Vector cam, Vector objPos)
	{

		Vector objToCam = new Vector();
		Vector lookAt = new Vector();
		Vector objToCamProj = new Vector();
		Vector upAux = new Vector();
		Matrix modelView = new Matrix();
		double angleCosine;

		objToCamProj.x = cam.x - objPos.x;
		objToCamProj.y = 0;
		objToCamProj.z = cam.z - objPos.z;

		lookAt.x = 0;
		lookAt.y = 0;
		lookAt.z = 1;

		objToCamProj.normalize();
		upAux = lookAt.crossProduct(objToCamProj);

		angleCosine = lookAt.dotProduct(objToCamProj);

		if ((angleCosine < 0.99990) && (angleCosine > -0.99990)) {
			gl.glRotated(MathExt.degrees(MathExt.acos(angleCosine)), upAux.x, upAux.y, upAux.z);
		}

		objToCam = cam.subtract(objPos);
		objToCam.normalize();

		angleCosine = objToCamProj.dotProduct(objToCam);
		if ((angleCosine < 0.99990) && (angleCosine > -0.9999)) {
			gl.glRotated(MathExt.degrees(MathExt.acos(angleCosine)), (objToCam.y < 0) ? 1 : -1, 0, 0);
		}

	}

	private double radius()
	{
		return radius;
	}


	
	private double farClipDistance(double distanceFromSurface)
	{
		double r = radius();
		double e = distanceFromSurface;
		double f = MathExt.sqrt(e * (2 * r + e));
		return f;
	}
	


}
