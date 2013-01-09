package us.wthr.jdem846.graphics.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.exception.GraphicsRenderException;
import us.wthr.jdem846.graphics.AxisEnum;
import us.wthr.jdem846.graphics.BaseRenderer;
import us.wthr.jdem846.graphics.IRenderer;
import us.wthr.jdem846.graphics.MatrixModeEnum;
import us.wthr.jdem846.graphics.PrimitiveModeEnum;
import us.wthr.jdem846.graphics.framebuffer.FrameBuffer;
import us.wthr.jdem846.graphics.framebuffer.FrameBufferFactory;
import us.wthr.jdem846.graphics.framebuffer.FrameBufferModeEnum;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.util.ColorUtil;

/**
 * An OpenGL render context. Note: I cannot claim to be an OpenGL expert, so
 * this won't be absolute magic here...
 * 
 * @author Kevin M. Gill <kmsmgill@gmail.com>
 * 
 */
public class OpenGlRenderer extends BaseRenderer implements IRenderer
{
	private static Log log = Logging.getLog(OpenGlRenderer.class);

	protected OpenGlOffscreenRenderContext openGl;
	protected boolean enableFrameBuffer = false;
	protected OpenGlFrameBuffer frameBuffer;

	protected int texture = 0;

	public OpenGlRenderer()
	{

	}

	@Override
	public void initialize(int width, int height)
	{
		openGl = new OpenGlOffscreenRenderContext(width, height);
		openGl.makeGlContextCurrent();

		
		boolean multisampling = JDem846Properties.getBooleanProperty("us.wthr.jdem846.rendering.opengl.multisampling.enabled");
		int samples = JDem846Properties.getIntProperty("us.wthr.jdem846.rendering.opengl.multisampling.samples");


		openGl.getGL().glEnable(GL.GL_BLEND);
		openGl.getGL().glEnable(GL.GL_TEXTURE_2D);
		
		if (multisampling) {
			openGl.getGL().glEnable(GL.GL_MULTISAMPLE);
		}
		
		openGl.getGL2().glShadeModel(GL2.GL_SMOOTH);
		openGl.getGL().glEnable(GL2.GL_POLYGON_SMOOTH);

		openGl.getGL().glEnable(GL.GL_DEPTH_TEST);
		openGl.getGL().glDepthFunc(GL.GL_LEQUAL);

		openGl.getGL().glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		openGl.getGL().glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
		openGl.getGL().glHint(GL2.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);

		openGl.getGL().glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following renderer initialization");
		}

		if (this.enableFrameBuffer) {
			this.frameBuffer = new OpenGlFrameBuffer(openGl, width, height, samples);
			this.frameBuffer.initialize();
			this.frameBuffer.bindForRender();
		}

		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition prior to gl environment initialization");
		}

	}

	@Override
	public void pushMatrix()
	{
		openGl.getGL2().glPushMatrix();
	}

	@Override
	public void popMatrix()
	{
		openGl.getGL2().glPopMatrix();
	}

	@Override
	public void setFrameBuffer(FrameBuffer frameBuffer)
	{

	}

	@Override
	public void viewPort(int x, int y, int width, int height, FrameBufferModeEnum bufferMode)
	{
		openGl.getGL2().glViewport(x, y, width, height);
	}

	@Override
	public void matrixMode(MatrixModeEnum mode) throws GraphicsRenderException
	{
		if (mode == MatrixModeEnum.MODELVIEW) {
			openGl.getGL2().glMatrixMode(GL2.GL_MODELVIEW);
		} else if (mode == MatrixModeEnum.PROJECTION) {
			openGl.getGL2().glMatrixMode(GL2.GL_PROJECTION);
		}
	}

	@Override
	public void loadIdentity()
	{
		openGl.getGL2().glLoadIdentity();
	}

	@Override
	public void ortho(double left, double right, double bottom, double top, double nearval, double farval)
	{
		openGl.getGL2().glOrtho(left, right, bottom, top, nearval, farval);
	}

	@Override
	public void perspective(double fov, double aspect, double near, double far)
	{
		openGl.getGLU().gluPerspective(fov, aspect, near, far);
	}

	@Override
	public void lookAt(double eyeX, double eyeY, double eyeZ, double centerX, double centerY, double centerZ, double upX, double upY, double upZ)
	{
		openGl.getGLU().gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);

	}

	@Override
	public void bindTexture(int[] tex, int width, int height)
	{
		this.unbindTexture();

		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition prior to binding of texture");
		}

		int[] textures = { 0 };
		openGl.getGL2().glGenTextures(1, textures, 0);
		this.texture = textures[0];

		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following generation of texture");
		}

		openGl.getGL2().glBindTexture(GL.GL_TEXTURE_2D, texture);
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following bind of texture");
		}

		ByteBuffer byteBuffer = ByteBuffer.allocate(tex.length * 4);

		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(tex);

		openGl.getGL2().glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);


		openGl.getGL2().glTexImage2D(GL.GL_TEXTURE_2D, 0, 4, width, height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, byteBuffer);
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following tex image 2d");
		}

		openGl.getGL2().glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		openGl.getGL2().glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following tex parameters");
		}
	}

	@Override
	public void unbindTexture()
	{
		openGl.getGL2().glBindTexture(GL.GL_TEXTURE_2D, 0);
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following unbinding texture");
		}

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
		int[] rgba = { 0, 0, 0, 0 };
		ColorUtil.intToRGBA(backgroundColor, rgba);
		openGl.getGL().glClearDepth(1.0f);
		openGl.getGL().glClearColor((float) rgba[0] / 255.0f, (float) rgba[1] / 255.0f, (float) rgba[2] / 255.0f, (float) rgba[3] / 255.0f);
		openGl.getGL().glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void rotate(double angle, AxisEnum axis)
	{
		if (axis == AxisEnum.X_AXIS) {
			openGl.getGL2().glRotated(angle, 1.0, 0.0, 0.0);
		} else if (axis == AxisEnum.Y_AXIS) {
			openGl.getGL2().glRotated(angle, 0.0, 1.0, 0.0);
		} else if (axis == AxisEnum.Z_AXIS) {
			openGl.getGL2().glRotated(angle, 0.0, 0.0, 1.0);
		}
	}

	@Override
	public void translate(double x, double y, double z)
	{
		openGl.getGL2().glTranslated(x, y, z);
	}

	@Override
	public void scale(double x, double y, double z)
	{
		openGl.getGL2().glScaled(x, y, z);
	}

	@Override
	public void begin(PrimitiveModeEnum mode)
	{
		if (mode == PrimitiveModeEnum.TRIANGLE_STRIP) {
			openGl.getGL2().glBegin(GL2.GL_TRIANGLE_STRIP);
		}
	}

	@Override
	public void end()
	{
		openGl.getGL2().glEnd();
	}

	@Override
	public void color(int color)
	{
		int[] rgba = { 0, 0, 0, 0 };
		ColorUtil.intToRGBA(color, rgba);
		openGl.getGL2().glColor4d((double) rgba[0] / 255.0, (double) rgba[1] / 255.0, (double) rgba[2] / 255.0, (double) rgba[3] / 255.0);
	}

	@Override
	public void texCoord(double left, double front)
	{
		openGl.getGL2().glTexCoord2d(left, front);
	}

	@Override
	public void vertex(double x, double y, double z)
	{
		openGl.getGL2().glVertex3d(x, y, z);
	}

	@Override
	public boolean project(Vector v)
	{
		return true;
	}

	@Override
	public void finish()
	{
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition prior to renderer finish operation");
		}

		// openGl.getGL().glFlush();
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following gl flush");
		}

		// openGl.getGL().glFinish();
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following gl finish");
		}

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
		openGl.getGL().glGetIntegerv(GL.GL_VIEWPORT, vals, 0);

		int width = vals[2];
		int height = vals[3];

		int length = width * height;
		int bufferLength = length * 4;

		// byte[] pixelBuffer = new byte[bufferLength];
		ByteBuffer byteBuffer = ByteBuffer.allocate(bufferLength);
		// ByteBuffer byteBuffer = ByteBuffer.wrap(pixelBuffer);

		// openGl.getGL2().glReadBuffer(GL.GL_FRONT);
		openGl.getGL().glPixelStorei(GL.GL_PACK_ALIGNMENT, 4);
		openGl.getGL().glReadPixels(0, 0, width, height, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, byteBuffer);

		FrameBuffer frameBuffer = FrameBufferFactory.createFrameBufferInstance(width, height, FrameBufferModeEnum.STANDARD);

		int i = 0; // Index into target int[]
		for (int row = 0; row < height; row++) {

			for (int col = 0; col < width; col++) {

				i = ((height - row - 1) * width * 4) + (col * 4);

				int iR = byteBuffer.get(i);
				int iG = byteBuffer.get(i + 1);
				int iB = byteBuffer.get(i + 2);
				int iA = byteBuffer.get(i + 3);
				if (iG != -1 && iG != 0) {
					int fdjuhfusd = 0;
				}

				int pixelInt = ColorUtil.rgbaToInt(iR, iG, iB, iA);
				// int pixelInt =
				// ((iR & 0x000000FF) << 24) |
				// ((iG & 0x000000FF) << 16) | (iB & 0x000000FF << 8) | (iA &
				// 0x000000FF);

				frameBuffer.set(col, row, 0.0, pixelInt);
			}
		}

		return frameBuffer;
	}

	public boolean checkGlContextSane()
	{
		int error = 0;
		if ((error = openGl.getGL().glGetError()) != GL.GL_NO_ERROR) {
			log.error("GL Context is in error #" + error);
			return false;
		} else {
			return true;
		}
	}
	//
	// //http://www.felixgers.de/teaching/jogl/imagingProg.html
	// private IntBuffer transformPixelsRGBBuffer2ARGB(ByteBuffer byteBufferRGB,
	// int width, int height)
	// {
	// int[] pixelInts = new int[width * height];
	//
	// // Convert RGB bytes to ARGB ints with no transparency.
	// // Flip image vertically by reading the
	// // rows of pixels in the byte buffer in reverse
	// // - (0,0) is at bottom left in OpenGL.
	// //
	// // Points to first byte (red) in each row.
	// int p = width * height * 3;
	// int q; // Index into ByteBuffer
	// int i = 0; // Index into target int[]
	// int w3 = width * 3; // Number of bytes in each row
	// for (int row = 0; row < height; row++) {
	// p -= w3;
	// q = p;
	// for (int col = 0; col < width; col++) {
	// int iR = byteBufferRGB.get(q++);
	// int iG = byteBufferRGB.get(q++);
	// int iB = byteBufferRGB.get(q++);
	// pixelInts[i++] = 0xFF000000 | ((iR & 0x000000FF) << 16) | ((iG &
	// 0x000000FF) << 8) | (iB & 0x000000FF);
	// }
	// }
	//
	//
	// IntBuffer transformedBuffer = IntBuffer.wrap(pixelInts);
	// return transformedBuffer;
	// }

}
