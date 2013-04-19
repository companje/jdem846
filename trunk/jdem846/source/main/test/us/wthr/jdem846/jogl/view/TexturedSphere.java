package us.wthr.jdem846.jogl.view;

import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import us.wthr.jdem846.graphics.ExamineView;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class TexturedSphere implements Renderable
{
	
	private Texture texture;
	private double radius;
	private int slices = 64;
	private int stacks = 64;
	private boolean facesOut = true;
	
	private int callList = 0;
	
	public TexturedSphere(GLProfile glProfile, String texturePath, double radius, boolean facesOut) throws Exception
	{
		this.radius = radius;
		this.facesOut = facesOut;
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
	
	
	public void render(GL2 gl, GLU glu, ExamineView view)
	{
		if (callList == 0) {
			callList = gl.glGenLists(1);
			gl.glNewList(callList, GL2.GL_COMPILE);
			
			texture.enable(gl);
			texture.bind(gl);

			GLUquadric quadric = glu.gluNewQuadric();
			glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
			glu.gluQuadricNormals(quadric, GLU.GLU_FLAT);
			glu.gluQuadricOrientation(quadric, (facesOut) ? GLU.GLU_OUTSIDE : GLU.GLU_INSIDE);
			glu.gluQuadricTexture(quadric, true);
			glu.gluSphere(quadric, radius, slices, stacks);
			glu.gluDeleteQuadric(quadric);
			
			gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
			gl.glEndList();
		} else {
			gl.glCallList(callList);
		}
		
	}
	
}
