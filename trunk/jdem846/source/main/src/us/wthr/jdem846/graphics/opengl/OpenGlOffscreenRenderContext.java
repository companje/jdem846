package us.wthr.jdem846.graphics.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.gl2.GLUgl2;

import us.wthr.jdem846.JDem846Properties;

public class OpenGlOffscreenRenderContext implements OpenGlRenderContext
{
	
	protected GLProfile glProfile;
	protected GLCapabilities glCapabilities;
	protected GLContext glContext;
	protected GLU glu = new GLU();
	protected GLUgl2 glugl2 = new GLUgl2();
	protected GLAutoDrawable drawable;
	
	
	public OpenGlOffscreenRenderContext(int width, int height)
	{
		
		glProfile = GLProfile.getDefault();
		GLDrawableFactory fac = GLDrawableFactory.getFactory(glProfile);
		glCapabilities = new GLCapabilities(glProfile);
		glCapabilities.setDoubleBuffered(false);
		glCapabilities.setOnscreen(false);
		glCapabilities.setPBuffer(true);
		
		boolean multisampling = JDem846Properties.getBooleanProperty("us.wthr.jdem846.rendering.opengl.multisampling.enabled");
		int samples = JDem846Properties.getIntProperty("us.wthr.jdem846.rendering.opengl.multisampling.samples");
		
		glCapabilities.setSampleBuffers(multisampling);
		glCapabilities.setNumSamples(samples);
		
		drawable = fac.createOffscreenAutoDrawable(null, glCapabilities, null, width, height, null);
		glContext = drawable.getContext();
		
	}
	
	public GLProfile getGlProfile()
	{
		return glProfile;
	}
	
	public void dispose()
	{
		drawable.destroy();
		glContext.destroy();
		
		
	}
	
	public void makeGlContextCurrent()
	{
		if (drawable.getNativeSurface().isSurfaceLockedByOtherThread()) {
			throw new RuntimeException("Surface is locked by another thread.");
		}
		this.glContext.makeCurrent();
	}
	
	public GL getGL()
	{
		return glContext.getGL();
	}
	
	public GL2 getGL2()
	{
		return getGL().getGL2();
	}
	
	public GLContext getGLContext()
	{
		return glContext;
	}
	
	public GLAutoDrawable getDrawable()
	{
		return drawable;
	}
	
	public GLU getGLU()
	{
		return glu;
	}
	
	public GLUgl2 getGLUgl2()
	{
		return glugl2;
	}
}
