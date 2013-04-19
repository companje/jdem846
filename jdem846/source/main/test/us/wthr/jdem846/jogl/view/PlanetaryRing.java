package us.wthr.jdem846.jogl.view;

import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;

import us.wthr.jdem846.graphics.ExamineView;
import us.wthr.jdem846.math.Plane;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vector;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class PlanetaryRing implements Renderable
{
	private String texturePath = "t00fri_gh_saturnrings.png";

	private Texture texture;
	private Vector lightPosition = new Vector(10000, 0, 0);

	private Plane ringPlane = new Plane(new Vector(0, 0, -10000.0), new Vector(-10000, 0, 10000.0), new Vector(10000, 0, 10000));
	private double innerRingRadius = 0.618072609;
	private double outterRingRadius = 1.163303909;

	public PlanetaryRing(GLProfile glProfile) throws Exception
	{
		try {
			InputStream stream = getClass().getResourceAsStream(texturePath);

			String extension = texturePath.substring(texturePath.lastIndexOf(".") + 1);

			TextureData data = TextureIO.newTextureData(glProfile, stream, true, extension);
			texture = TextureIO.newTexture(data);
			System.err.println("Loaded Texture " + texturePath);
		} catch (IOException exc) {
			exc.printStackTrace();
			throw new Exception("Error loading texture " + texturePath);
		}
	}

	@Override
	public void render(GL2 gl, GLU glu, ExamineView view)
	{
		gl.glPushMatrix();

		gl.glDisable(GL2.GL_FOG);
		gl.glDisable(GL2.GL_LIGHTING);

		texture.enable(gl);
		texture.bind(gl);
		gl.glRotated(90, 1, 0, 0);
		gl.glBegin(GL2.GL_TRIANGLE_STRIP);

		Vector vec = new Vector();

		for (double i = -180.0; i <= 180.0; i++) {
			Spheres.getPoint3D(i, 0.0, innerRingRadius, vec);
			if (isPointInShadow(vec)) {
				gl.glColor4f(0.25f, 0.25f, 0.25f, 1.0f);
			} else {
				gl.glColor3f(1.0f, 1.0f, 1.0f);
			}

			gl.glTexCoord2d(0.0f, 0.0f);
			gl.glVertex3d(vec.x, vec.y, vec.z);

			Spheres.getPoint3D(i, 0.0, outterRingRadius, vec);
			if (isPointInShadow(vec)) {
				gl.glColor4f(0.25f, 0.25f, 0.25f, 1.0f);
			} else {
				gl.glColor3f(1.0f, 1.0f, 1.0f);
			}
			gl.glTexCoord2d(1.0f, 0.0f);
			gl.glVertex3d(vec.x, vec.y, vec.z);
		}
		gl.glEnd();
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);

		gl.glPopMatrix();

	}

	protected boolean isPointInShadow(Vector point)
	{
		double dot = point.dotProduct(lightPosition);
		return (dot <= -0.9);

	}

	protected double isVertexInRingShadow(double latitude, double longitude, double elevation, double radius)
	{
		Vector point = new Vector();
		Spheres.getPoint3D(longitude, latitude, radius, point);

		Vector direction = point.getDirectionTo(lightPosition);
		double intersectDistance = point.intersectDistance(ringPlane, direction) * radius;

		Vector intersect = point.intersectPoint(direction, intersectDistance);

		if (intersect != null && intersectDistance > 0) {

			double intersectRadius = intersect.length();

			if (intersectRadius >= innerRingRadius && intersectRadius <= outterRingRadius) {

				double f = (intersectRadius - innerRingRadius) / (outterRingRadius - innerRingRadius);
				double a = getRingAlpha(f) / 1.5;
				double af = (a / 255.0);
				return af;
			} else {
				return 0;
			}
		}

		return 0;
	}

	double getRingAlpha(double f)
	{
		return 0.75;
		/*
		 * double x = texture.getWidth() * f; texture.get c =
		 * texture.getColor(x) if (c != null) { var a = c.getAlpha(); a =
		 * ColorUtil.clamp(a); return a } else { return 255 }
		 */
	}
}
