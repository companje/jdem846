package us.wthr.jdem846.jogl.view;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import us.wthr.jdem846.graphics.Color;
import us.wthr.jdem846.graphics.ExamineView;
import us.wthr.jdem846.graphics.IColor;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Matrix;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vector;

public class AtmosphereHalo
{

	private double radius = .5;

	
	private double elevationMinimum = 0;
	private double elevationMaximum = 10;
	
	private IColor colorLower = new Color("688AB0FF");
	private IColor colorUpper = new Color("688AB0FF");
	private IColor colorFaded = new Color("688AB000");
	
	private IColor emissive = new Color("#000000FF");
	private IColor ambient = new Color("#0F161EFF");
	private IColor diffuse = new Color("#688AB0FF");
	private IColor specular = new Color("#0000000");
	private double shininess = 5.0;
	
	private Vector atmosphereVector = new Vector();
	private Vector atmosphereNormal = new Vector();
	
	public AtmosphereHalo()
	{
		
	}
	
	
	public void render(GL2 gl, GLU glu, ExamineView view)
	{	
		renderHalo(gl, glu, view, 0, colorLower, 0.015, colorUpper);
		renderHalo(gl, glu, view, 0.015, colorUpper, 0.025, colorFaded);
		
	}
	
	protected void renderHalo(GL2 gl, GLU glu, ExamineView view, double elevationMinimum, IColor colorMinimum, double elevationMaximum, IColor colorMaximum)
	{
		Vector position = new Vector(0, 0, view.getDistance() - radius);
		Vector focalPoint = new Vector(0, 0, 0);
		
		gl.glPushMatrix();
		
		gl.glLoadIdentity();
		glu.gluLookAt(0, 0, view.getDistance() - radius, 0, 0, 0, 0, 1, 0);
		
		gl.glMultMatrixd(view.getUnrotatedModelView().matrix, 0);
		
		setBillboard(gl, glu, position, focalPoint);
		//renderer.setBillboard(viewer.getPosition(), viewer.getFocalPoint())
		
		double near = view.getDistance() - radius;
		double far = farClipDistance(near);
		double distanceToCenter = view.getDistance();
		double trans = distanceToCenter - far;
		double horizonHeightFromPlane = MathExt.sqrt(MathExt.sqr(radius) - MathExt.sqr(trans));
		double distanceToSurfaceHorizon = MathExt.sqrt(MathExt.sqr(far) + MathExt.sqr(horizonHeightFromPlane));
	   
		double scale = (horizonHeightFromPlane / radius) * view.getScale();
		
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glDisable(GL2.GL_COLOR_MATERIAL);
		
		gl.glTranslated(0.0, 0.0, trans);
		gl.glScaled(scale, scale, scale);
		gl.glRotated(90, 1, 0, 0);

		gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		
		double latitude = 0.0;
		for (double angle = 0.0; angle <= 360.0; angle+=1.0) {
			renderAtmosphereVertex(gl, glu, latitude, angle, elevationMinimum, colorMinimum);
			renderAtmosphereVertex(gl, glu, latitude, angle, elevationMaximum, colorMaximum);
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
