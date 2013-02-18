package us.wthr.jdem846.graphics.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.gl2.GLUgl2;

public interface OpenGlRenderContext
{
	public GLProfile getGlProfile();
	public void dispose();
	public void makeGlContextCurrent();
	public GL getGL();
	public GL2 getGL2();
	public GLContext getGLContext();
	public GLAutoDrawable getDrawable();
	public GLU getGLU();
	public GLUgl2 getGLUgl2();
}
