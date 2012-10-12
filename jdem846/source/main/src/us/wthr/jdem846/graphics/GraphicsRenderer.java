package us.wthr.jdem846.graphics;

import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846.exception.GraphicsRenderException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Matrix;
import us.wthr.jdem846.math.MatrixStack;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.math.Vectors;

public class GraphicsRenderer
{
	private static Log log = Logging.getLog(GraphicsRenderer.class);
	
	public static final int RENDER_MAXIMUM_WIDTH = 5000;
	public static final int RENDER_MAXIMUM_HEIGHT = 5000;
	
	
	protected int currentColor = 0x0;
	protected Texture currentTexture = null;
	protected RenderCodesEnum error = RenderCodesEnum.RENDER_NO_ERROR;
	
	protected ViewPort viewPort = null;
	protected FrameBuffer frameBuffer = null;
	
	protected boolean inPrimitiveDraw = false;
	protected PrimitiveModeEnum primitiveMode;
	protected PrimitiveDraw primitiveDrawer = null;
	
	protected double near = 0;
	protected double far = 0;
	
	protected MatrixStack modelViewStack = null;
	protected MatrixStack projectionStack = null;
	protected MatrixStack currentMatrixStack = null;
	
	public GraphicsRenderer()
	{
		modelViewStack = new MatrixStack(true);
		projectionStack = new MatrixStack(true);
	}
	
	
	public void multMatrix(Matrix tgt, Matrix with)
	{
		if (tgt != null && with != null) {
			tgt.multiply(with);
		}
	}
	
	public void multMatrix(Matrix m)
	{
		if (this.currentMatrixStack != null && this.currentMatrixStack.depth() > 0 && m != null) {
			this.multMatrix(this.currentMatrixStack.top(), m);
		}
	}
	
	
	public RenderCodesEnum getError()
	{
		return this.error;
	}
	
	
	public void pushMatrix()
	{
		if (this.currentMatrixStack != null) {
			this.currentMatrixStack.push();
		}
	}
	
	public void popMatrix()
	{
		if (this.currentMatrixStack != null) {
			this.currentMatrixStack.pop();
		}
	}
	
	
	public void viewPort(int x, int y, int width, int height)
	{

		if (width <= 0 || width > GraphicsRenderer.RENDER_MAXIMUM_WIDTH || height <= 0 || height > GraphicsRenderer.RENDER_MAXIMUM_HEIGHT) {
			this.error = RenderCodesEnum.RENDER_ERR_INVALID_DIMENSIONS;
			return;
		}
		this.viewPort = new ViewPort(x, y, width, height);
		this.frameBuffer = new FrameBuffer(width, height);
	
	}
	
	public void matrixMode(MatrixModeEnum mode) throws GraphicsRenderException
	{
		
		if (mode == MatrixModeEnum.PROJECTION) {
			this.currentMatrixStack = this.projectionStack;
		} else if (mode == MatrixModeEnum.MODELVIEW) {
			this.currentMatrixStack = this.modelViewStack;
		} else {
			this.currentMatrixStack = null;
			this.error = RenderCodesEnum.RENDER_ERR_INVALID_ENUM;
			throw new GraphicsRenderException("Invalid or unsupported matrix mode specified", RenderCodesEnum.RENDER_ERR_INVALID_ENUM);
		}
			
	}
	
	public void loadIdentity()
	{
		if (this.currentMatrixStack != null && this.currentMatrixStack.depth() > 0) {
			this.currentMatrixStack.top().loadIdentity();
		}
	}
	
	// http://cgit.freedesktop.org/mesa/mesa/tree/src/mesa/math/m_matrix.c
	public void ortho(double left, double right, double bottom, double top, double nearval, double farval)
	{
		Matrix m = new Matrix(true);
		
		m.set(0, 0, 2.0 / (right - left));
		//m.set(0, 1, 0.0);
		//m.set(0, 2, 0.0);
		m.set(0, 3, -(right + left) / (right - left));

		//m.set(1, 0, 0.0);
		m.set(1, 1, 2.0 / (top - bottom));
		//m.set(1, 2, 0.0);
		m.set(1, 3, -(top+bottom) / (top - bottom));

		//m.set(2, 0, 0.0);
		//m.set(2, 1, 0.0);
		m.set(2, 2, -2.0 / (farval - nearval));
		m.set(2, 3, -(farval + nearval) / (farval - nearval));

		//m.set(3, 0, 0.0);
		//m.set(3, 1, 0.0);
		//m.set(3, 2, 0.0);
		m.set(3, 3, 1.0);
		
		this.multMatrix(m);
	}
	
	//http://cgit.freedesktop.org/mesa/mesa/tree/src/glu/sgi/libutil/project.c?h=7.8
	public void perspective(double fov, double aspect, double near, double far)
	{
		this.near = near;
		this.far = far;
		
		Matrix m = new Matrix(true);
		
		double _fov = MathExt.radians(fov);
		
		double deltaZ = far - near;
		double s = MathExt.sin(_fov);
		
		if ((deltaZ == 0) || (s == 0) || (aspect == 0)) {
			this.error = RenderCodesEnum.RENDER_ERR_INVALID_PARAMETERS;
			return;
		}
		
		double c = MathExt.cos(_fov) / s;

		
		m.set(0, 0, (c / aspect));
		m.set(1, 1, c);
		m.set(2, 2, -(far + near) / deltaZ);
		m.set(3, 2, -1.0);
		m.set(2, 3, -2.0 * near * far / deltaZ);
		m.set(3, 3, 0);

		this.multMatrix(m);

	}
	
	
	public void lookAt(double eyeX, double eyeY, double eyeZ
						, double centerX, double centerY, double centerZ
						, double upX, double upY, double upZ)
	{
		Vector forward = new Vector();
		Vector side = new Vector();
		Vector up = new Vector();
		
		Matrix m = new Matrix(true);
		
		forward.x = centerX - eyeX;
		forward.y = centerY - eyeY;
		forward.z = centerZ - eyeZ;
		
		up.x = upX;
		up.y = upY;
		up.z = upZ;
		
		Vectors.normalize(forward);
		Vectors.crossProduct(forward, up, side);
		Vectors.normalize(side);
		
		Vectors.crossProduct(side, forward, up);
		//Vectors.normalize(up);
		
		
		m.set(0, 0, side.x);
		m.set(0, 1, side.y);
		m.set(0, 2, side.z);

		m.set(1, 0, up.x);
		m.set(1, 1, up.y);
		m.set(1, 2, up.z);

		m.set(2, 0, -forward.x);
		m.set(2, 1, -forward.y);
		m.set(2, 2, -forward.z);
		
		
		/*
		m.set(0, 0, side.x);
		m.set(1, 0, side.y);
		m.set(2, 0, side.z);

		m.set(0, 1, up.x);
		m.set(1, 1, up.y);
		m.set(2, 1, up.z);

		m.set(0, 2, forward.x);
		m.set(1, 2, forward.y);
		m.set(2, 2, forward.z);
		*/
		this.multMatrix(m);
		this.translate(eyeX, eyeY, eyeZ);
		//this.translate(-eyeX, -eyeY, -eyeZ);
		
	}
	
	public void bindTexture(int[] tex, int width, int height)
	{
		if (tex == null) {
			this.error = RenderCodesEnum.RENDER_ERR_INVALID_TEXTURE;
			return;
		}
		
		if (width <= 0 || height <= 0) {
			this.error = RenderCodesEnum.RENDER_ERR_INVALID_DIMENSIONS;
			return;
		}
		
		this.currentTexture = new Texture(width, height, tex);
		
		if (this.primitiveDrawer != null) {
			this.primitiveDrawer.setTexture(this.currentTexture);
		}
		
		
	}
	
	public void unbindTexture()
	{
		this.currentTexture = null;
		if (this.primitiveDrawer != null) {
			this.primitiveDrawer.setTexture(null);
		}
	}
	
	public boolean isTextureBound()
	{
		return (this.currentTexture != null);
	}
	
	public void clear(int backgroundColor)
	{
		if (this.frameBuffer != null) {
			this.frameBuffer.reset(true, backgroundColor);
		} else {
			this.error = RenderCodesEnum.RENDER_ERR_NO_FRAME_BUFFER_DEFINED;
		}
	}
	
	
	public void rotate(double angle, AxisEnum axis)
	{
		Matrix m = new Matrix(true);
		
		double _a = MathExt.radians(angle);
		double c = MathExt.cos(_a);
		double s = MathExt.sin(_a);
		
		
		if (axis == AxisEnum.X_AXIS) {
			m.set(1, 1, c);
			m.set(2, 2, c);
			
			m.set(2, 1, -s);
			m.set(1, 2, s);
		} else if (axis == AxisEnum.Y_AXIS) {
			m.set(0, 0, c);
			m.set(2, 2, c);
			
			m.set(2, 0, s);
			m.set(0, 2, -s);
		} else if (axis == AxisEnum.Z_AXIS) {
			m.set(0, 0, c);
			m.set(1, 1, c);
			
			m.set(0, 1, s);
			m.set(1, 0, -s);
		} else {
			this.error = RenderCodesEnum.RENDER_ERR_INVALID_ENUM;
			return;
		}
		
		this.multMatrix(this.modelViewStack.top(), m);
	}
	
	
	public void translate(double x, double y, double z)
	{
		if (this.currentMatrixStack != null && this.currentMatrixStack.top() != null) {
			this.currentMatrixStack.top().translate(x, y, z);
		}
	}
	
	
	public void scale(double x, double y, double z)
	{
		if (this.currentMatrixStack != null && this.currentMatrixStack.top() != null) {
			this.currentMatrixStack.top().scale(x, y, z);
		}
	}
	
	public void begin(PrimitiveModeEnum mode)
	{
		if (this.inPrimitiveDraw == true) {
			this.error = RenderCodesEnum.RENDER_ERR_INVALID_OPERATION;
		} else {
			
			if (mode == PrimitiveModeEnum.TRIANGLE_STRIP) {
				this.primitiveDrawer = new TriangleStripDraw(this.frameBuffer);
				this.primitiveDrawer.setColor(currentColor);
				this.primitiveDrawer.setTexture(currentTexture);
			} else {
				this.error = RenderCodesEnum.RENDER_ERR_INVALID_ENUM;
			}
			
			this.inPrimitiveDraw = true;
			this.primitiveMode = mode;
			
		}

	}
	
	public void end()
	{
		this.primitiveDrawer = null;
		this.inPrimitiveDraw = false;
	}
	
	
	public void color(int[] color)
	{
		color(ColorUtil.rgbaToInt(color));
	}
	
	public void color(int color)
	{
		this.currentColor = color;
		if (this.primitiveDrawer != null) {
			this.primitiveDrawer.setColor(color);
		}
	}
	
	public void texCoord(double left, double front)
	{
		if (this.currentTexture != null) {
			this.currentTexture.left = left;
			this.currentTexture.front = front;
		}
	}
	
	public void vertex(Vector v)
	{
		vertex(v.x, v.y, v.z);
	}
	
	private Vector v0 = new Vector();
	public void vertex(double x, double y, double z)
	{
		if (this.primitiveDrawer == null) {
			this.error = RenderCodesEnum.RENDER_ERR_INVALID_OPERATION;
		} else {
			v0.x = x;
			v0.y = y;
			v0.z = z;
			v0.w = 1.0;
			if (this.project(v0)) {
				this.primitiveDrawer.vertex(v0.x, v0.y, v0.z);
			}
			
		}
	}
	
	
	public boolean project(Vector v)
	{

		Vector in = new Vector(v);
		in.w = 1.0;
		
		Vector out = new Vector();
		
		
		this.modelViewStack.top().multiply(in, out);
		this.projectionStack.top().multiply(out, in);
		
		if (in.w == 0.0) {
			return false;
		}
		
		in.x /= in.w;
		in.y /= in.w;
		in.z /= in.w;
		
		in.x = in.x * 0.5 + 0.5;
		in.y = in.y * 0.5 + 0.5;
		in.z = in.z * 0.5 + 0.5;
		

		
		in.x = in.x * (double)this.viewPort.getWidth() + this.viewPort.getX();
		in.y = in.y * (double)this.viewPort.getHeight() + this.viewPort.getY();
		
		v.x = in.x;
		v.y = in.y;
		v.z = in.z;
		
		
		
		//if (-v.z > this.far || -v.z < this.near) {
			//return false;
		//}
		
		return true;
		
	}
	
	public FrameBuffer getFrameBuffer()
	{
		return frameBuffer;
	}
	
}
