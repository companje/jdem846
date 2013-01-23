package us.wthr.jdem846.graphics;

import us.wthr.jdem846.exception.GraphicsRenderException;
import us.wthr.jdem846.graphics.framebuffer.FrameBuffer;
import us.wthr.jdem846.graphics.framebuffer.FrameBufferModeEnum;
import us.wthr.jdem846.math.Vector;

public interface IRenderer
{

	public RenderCodesEnum getError();

	public void initialize(int width, int height);
	
	public void setLighting(Vector position, double emission, double ambiant, double diffuse, double specular, double shininess);
	public void disableLighting();
	public void enableLighting();
	
	public void normal(Vector normal);
	
	public void pushMatrix();

	public void popMatrix();

	public void setFrameBuffer(FrameBuffer frameBuffer);
	
	
	public int getMaximumTextureWidth();
	public int getMaximumTextureHeight();
	
	public void viewPort(int x, int y, int width, int height);

	public void viewPort(int x, int y, int width, int height, FrameBufferModeEnum bufferMode);

	public void matrixMode(MatrixModeEnum mode) throws GraphicsRenderException;

	public void loadIdentity();

	public void ortho(double left, double right, double bottom, double top, double nearval, double farval);

	public void perspective(double fov, double aspect, double near, double far);

	public void lookAt(double eyeX, double eyeY, double eyeZ, double centerX, double centerY, double centerZ, double upX, double upY, double upZ);

	public void bindTexture(Texture texture);

	public void unbindTexture();

	public boolean isTextureBound();

	public void clear(int backgroundColor);

	public void rotate(double angle, AxisEnum axis);

	public void translate(double x, double y, double z);

	public void scale(double x, double y, double z);

	public void begin(PrimitiveModeEnum mode);

	public void end();

	public void color(int[] color);

	public void color(int color);

	public void texCoord(double left, double front);

	public void vertex(Vector v);

	public void vertex(double x, double y, double z);

	public boolean project(Vector v);

	public ImageCapture captureImage();
	
	
	
	public void finish();
	
	public void dispose();
}
