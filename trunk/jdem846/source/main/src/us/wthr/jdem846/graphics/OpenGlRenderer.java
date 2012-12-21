package us.wthr.jdem846.graphics;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;

import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846.exception.GraphicsRenderException;
import us.wthr.jdem846.graphics.framebuffer.FrameBuffer;
import us.wthr.jdem846.graphics.framebuffer.FrameBufferFactory;
import us.wthr.jdem846.graphics.framebuffer.FrameBufferModeEnum;
import us.wthr.jdem846.math.Vector;

/** An OpenGL render context. Note: I cannot claim to be an OpenGL expert, so this won't be absolute magic here...
 * 
 * @author Kevin M. Gill <kmsmgill@gmail.com>
 *
 */
public class OpenGlRenderer extends BaseRenderer implements IRenderer
{
	protected GLProfile glProfile;
	protected GLCapabilities glCapabilities;
	protected GLContext glContext;
    protected GLU glu = new GLU();
	protected GLAutoDrawable drawable;
	
	protected boolean enableFrameBuffer;
	protected OpenGlFrameBuffer frameBuffer;
	
	protected int texture = 0;
	
	public OpenGlRenderer()
	{
		
	}
	
	protected void setGLAutoDrawable(GLAutoDrawable drawable)
	{
		this.drawable = drawable;
	}
	
	@Override
	public void initialize(int width, int height)
	{
		glProfile = GLProfile.getDefault();
		GLDrawableFactory fac = GLDrawableFactory.getFactory(glProfile);
		glCapabilities = new GLCapabilities(glProfile);
		glCapabilities.setDoubleBuffered(false);
		
		drawable = fac.createOffscreenAutoDrawable(null, glCapabilities, null, width, height, GLContext.getCurrent());

		
		if (this.enableFrameBuffer) {
			this.frameBuffer = new OpenGlFrameBuffer(width, height, 4);
			this.frameBuffer.initialize();
			this.frameBuffer.bindForRender();
		}
		
		drawable.getGL().getGL2().glClampColor(GL2.GL_CLAMP_READ_COLOR, GL.GL_FALSE);
		drawable.getGL().getGL2().glClampColor(GL2.GL_CLAMP_VERTEX_COLOR, GL.GL_FALSE);
		drawable.getGL().getGL2().glClampColor(GL2.GL_CLAMP_FRAGMENT_COLOR, GL.GL_FALSE);
		
		drawable.getGL().glEnable(GL.GL_TEXTURE_2D);
		drawable.getGL().glEnable(GL.GL_MULTISAMPLE);
		
		drawable.getGL().getGL2().glShadeModel(GL2.GL_SMOOTH);
		
		drawable.getGL().glEnable(GL2.GL_POLYGON_SMOOTH);
		drawable.getGL().glEnable(GL.GL_DEPTH_TEST);
		drawable.getGL().glEnable(GL.GL_LEQUAL);
		
		drawable.getGL().glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		drawable.getGL().glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
		
		drawable.getGL().glBlendFunc(GL.GL_ONE, GL.GL_ZERO);
		
		
	}
	
	@Override
	public void pushMatrix()
	{
		drawable.getGL().getGL2().glPushMatrix();
	}

	@Override
	public void popMatrix()
	{
		drawable.getGL().getGL2().glPopMatrix();
	}

	@Override
	public void setFrameBuffer(FrameBuffer frameBuffer)
	{
		
	}

	@Override
	public void viewPort(int x, int y, int width, int height, FrameBufferModeEnum bufferMode)
	{
		drawable.getGL().getGL2().glViewport(x, y, width, height);
	}

	@Override
	public void matrixMode(MatrixModeEnum mode) throws GraphicsRenderException
	{
		if (mode == MatrixModeEnum.MODELVIEW) {
			drawable.getGL().getGL2().glMatrixMode(GL2.GL_MODELVIEW);
		} else if (mode == MatrixModeEnum.PROJECTION) {
			drawable.getGL().getGL2().glMatrixMode(GL2.GL_PROJECTION);
		}
	}

	@Override
	public void loadIdentity()
	{
		drawable.getGL().getGL2().glLoadIdentity();
	}

	@Override
	public void ortho(double left, double right, double bottom, double top, double nearval, double farval)
	{
		drawable.getGL().getGL2().glOrtho(left, right, bottom, top, nearval, farval);
	}

	@Override
	public void perspective(double fov, double aspect, double near, double far)
	{
		glu.gluPerspective(fov, aspect, near, far);
	}

	@Override
	public void lookAt(double eyeX, double eyeY, double eyeZ, double centerX, double centerY, double centerZ, double upX, double upY, double upZ)
	{
		glu.gluLookAt(eyeX
					, eyeY
					, eyeZ
					, centerX
					, centerY
					, centerZ
					, upX
					, upY
					, upZ);
		
	}

	@Override
	public void bindTexture(int[] tex, int width, int height)
	{
		this.unbindTexture();
		
		int[] textures = {0};
		drawable.getGL().glGenTextures(1, textures, 0);
		this.texture = textures[0];
		
		drawable.getGL().glBindTexture(GL.GL_TEXTURE_2D, texture);
		
		IntBuffer intBuffer = IntBuffer.wrap(tex);
		drawable.getGL().glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB8, width, height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, intBuffer);
		drawable.getGL().glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		drawable.getGL().glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	}

	@Override
	public void unbindTexture()
	{
		drawable.getGL().getGL2().glBindTexture(GL.GL_TEXTURE_2D, 0);
		this.texture = 0;
	}

	@Override
	public boolean isTextureBound()
	{
		return (texture != 0);
	}

	@Override
	public void clear(int backgroundColor)
	{
		int[] rgba = {0, 0, 0, 0};
		ColorUtil.intToRGBA(backgroundColor, rgba);
		drawable.getGL().glClearColor((float) rgba[0] / 255.0f
									, (float) rgba[1] / 255.0f
									, (float) rgba[2] / 255.0f
									, (float) rgba[3] / 255.0f);
		drawable.getGL().glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void rotate(double angle, AxisEnum axis)
	{
		if (axis == AxisEnum.X_AXIS) {
			drawable.getGL().getGL2().glRotated(angle, 1.0, 0.0, 0.0);
		} else if (axis == AxisEnum.Y_AXIS) {
			drawable.getGL().getGL2().glRotated(angle, 0.0, 1.0, 0.0);
		} else if (axis == AxisEnum.Z_AXIS) {
			drawable.getGL().getGL2().glRotated(angle, 0.0, 0.0, 1.0);
		}
	}

	@Override
	public void translate(double x, double y, double z)
	{
		drawable.getGL().getGL2().glTranslated(x, y, z);
	}

	@Override
	public void scale(double x, double y, double z)
	{
		drawable.getGL().getGL2().glScaled(x, y, z);
	}

	@Override
	public void begin(PrimitiveModeEnum mode)
	{
		if (mode == PrimitiveModeEnum.TRIANGLE_STRIP) {
			drawable.getGL().getGL2().glBegin(GL2.GL_TRIANGLE_STRIP);
		}
	}

	@Override
	public void end()
	{
		drawable.getGL().getGL2().glEnd();
	}

	@Override
	public void color(int color)
	{
		int[] rgba = {0, 0, 0, 0};
		ColorUtil.intToRGBA(color, rgba);
		drawable.getGL().getGL2().glColor4d((double) rgba[0] / 255.0
											, (double) rgba[1] / 255.0
											, (double) rgba[2] / 255.0
											, (double) rgba[3] / 255.0);
	}

	@Override
	public void texCoord(double left, double front)
	{
		drawable.getGL().getGL2().glTexCoord2d(left, front);
	}

	@Override
	public void vertex(double x, double y, double z)
	{
		drawable.getGL().getGL2().glVertex3d(x, y, z);
	}

	@Override
	public boolean project(Vector v)
	{
		return true;
	}

	@Override
	public void finalize()
	{
		drawable.getGL().glFlush();
		drawable.getGL().glFinish();
		
		if (frameBuffer != null) {
			frameBuffer.transferToResolveTargetBuffer();
		}
		
	}
	
	@Override
	public FrameBuffer getFrameBuffer()
	{
		
		if (this.frameBuffer != null) {
			this.frameBuffer.bindForCapture();
		}
		
		int[] vals = new int[4];
		drawable.getGL().glGetIntegerv(GL.GL_VIEWPORT, vals, 0);
		
		int width = vals[2];
		int height = vals[3];
		
		int length = width * height;
		int bufferLength = length * 4;
		
		byte[] pixelBuffer = new byte[bufferLength];
		ByteBuffer byteBuffer = ByteBuffer.wrap(pixelBuffer);
		
		drawable.getGL().glPixelStorei(GL.GL_PACK_ALIGNMENT, 4);
		drawable.getGL().glReadPixels(0, 0, width, height, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, byteBuffer);
		
		
		FrameBuffer frameBuffer = FrameBufferFactory.createFrameBufferInstance(width, height, FrameBufferModeEnum.STANDARD);
		
		return frameBuffer;
	}

}
