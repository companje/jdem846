package us.wthr.jdem846.graphics.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.gl2.GLUgl2;

public class OpenGlOnscreenRenderContext implements OpenGlRenderContext
{
	protected GLProfile glProfile;
	protected GLCapabilities glCapabilities;
	protected GLContext glContext;
	protected GLU glu = new GLU();
	protected GLUgl2 glugl2 = new GLUgl2();
	
	
	protected static GLAutoDrawable drawable;
	
	
	public OpenGlOnscreenRenderContext()
	{
		
	}
	
	
	public static void setGlAutoDrawable(GLAutoDrawable drawable)
	{
		OpenGlOnscreenRenderContext.drawable = drawable;
	}
	
	
	@Override
	public GLProfile getGlProfile()
	{
		return drawable.getGLProfile();
	}

	@Override
	public void dispose()
	{
		
	}

	@Override
	public void makeGlContextCurrent()
	{
		this.getGLContext().makeCurrent();
	}

	@Override
	public GL getGL()
	{
		return drawable.getGL();
	}

	@Override
	public GL2 getGL2()
	{
		return getGL().getGL2();
	}

	@Override
	public GLContext getGLContext()
	{
		return drawable.getContext();
	}

	@Override
	public GLAutoDrawable getDrawable()
	{
		return drawable;
	}

	@Override
	public GLU getGLU()
	{
		return glu;
	}

	@Override
	public GLUgl2 getGLUgl2()
	{
		return glugl2;
	}

}
