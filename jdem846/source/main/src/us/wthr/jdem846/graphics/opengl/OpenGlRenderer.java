package us.wthr.jdem846.graphics.opengl;

import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.exception.GraphicsRenderException;
import us.wthr.jdem846.graphics.AxisEnum;
import us.wthr.jdem846.graphics.BaseRenderer;
import us.wthr.jdem846.graphics.IRenderer;
import us.wthr.jdem846.graphics.ImageCapture;
import us.wthr.jdem846.graphics.MatrixModeEnum;
import us.wthr.jdem846.graphics.PrimitiveModeEnum;
import us.wthr.jdem846.graphics.Texture;
import us.wthr.jdem846.graphics.TextureMapConfiguration;
import us.wthr.jdem846.graphics.TextureMapConfiguration.InterpolationTypeEnum;
import us.wthr.jdem846.graphics.TextureMapConfiguration.TextureWrapTypeEnum;
import us.wthr.jdem846.graphics.framebuffer.FrameBuffer;
import us.wthr.jdem846.graphics.framebuffer.FrameBufferFactory;
import us.wthr.jdem846.graphics.framebuffer.FrameBufferModeEnum;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
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
	
	protected LightingConfig lightingConfig;
	
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
		//openGl.getGL().glEnable(GL2.GL_POLYGON_SMOOTH);

		openGl.getGL().glEnable(GL.GL_DEPTH_TEST);
		//openGl.getGL().glDepthFunc(GL.GL_ALWAYS);
		openGl.getGL().glDepthFunc(GL.GL_LEQUAL);

		openGl.getGL().glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		openGl.getGL().glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
		openGl.getGL().glHint(GL2.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
		
		
		openGl.getGL().glEnable(GL.GL_CULL_FACE);
		openGl.getGL().glCullFace(GL.GL_BACK);
		
		//openGl.getGL().glHint(GL2.GL_CLIP_VOLUME_CLIPPING_HINT_EXT, GL2.GL_FASTEST);
		
		openGl.getGL().glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		openGl.getGL2().glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

		 
		// openGl.getGL().glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
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

	public void enableLighting()
	{
		if (this.lightingConfig != null) {
			setLighting(lightingConfig.lightPosition
						, lightingConfig.emission
						, lightingConfig.ambient
						, lightingConfig.diffuse
						, lightingConfig.specular
						, lightingConfig.shininess);
		}
	}
	
	@Override
	public void setLighting(Vector lightPosition, double emission, double ambient, double diffuse, double specular, double shininess)
	{
		// PoC Testing: OpenGL Lighting:
		
		this.lightingConfig = new LightingConfig(lightPosition, emission, ambient, diffuse, specular, shininess);
		
		
		float lightPos[] = { (float) lightPosition.x, (float) lightPosition.y, (float) lightPosition.z, 1.0f };

		//float lightPos[] = { 0.0f, 3.0f, 2.0f, 0.0f };

		
		
		//openGl.getGL2().glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, lightPos, 0);
		openGl.getGL2().glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
		
		float ambientl[] =
		    { 0.0f, 0.0f, 0.0f, 1.0f };
		    float diffusel[] =
		    { 1.0f, 1.0f, 1.0f, 1.0f };
		    float specularl[] =
		    { 1.0f, 1.0f, 1.0f, 1.0f };
		float lmodel_ambient[] = { (float)0.8, (float)0.8, (float)0.8, 1.0f };
		float local_view[] = { 0.0f };
		
		openGl.getGL2().glShadeModel(GL2.GL_SMOOTH);
		openGl.getGL2().glColorMaterial ( GL.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE ) ;
		openGl.getGL2().glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambientl, 0);
		openGl.getGL2().glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffusel, 0);
		//openGl.getGL2().glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specularl, 0);


		openGl.getGL2().glLightModeli( GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SEPARATE_SPECULAR_COLOR );
		openGl.getGL2().glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
		openGl.getGL2().glLightModelfv(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, local_view, 0);
		
		openGl.getGL2().glEnable(GLLightingFunc.GL_LIGHTING);
		openGl.getGL2().glEnable(GLLightingFunc.GL_LIGHT0);
		
		float ambientLight[] = { (float)ambient, (float)ambient, (float)ambient, 1.0f };
		float diffuseLight[] = { (float)diffuse, (float)diffuse, (float)diffuse, 1.0f };
		float specularLight[] = { (float)specular, (float)specular, (float)specular, 1.0f };
		float emissionLight[] = { (float)emission, (float)emission, (float)emission, 0.0f };
		float shininessLight[] = { (float)shininess };
		
		openGl.getGL2().glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, ambientLight, 0);
		openGl.getGL2().glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, diffuseLight, 0);
		openGl.getGL2().glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, specularLight, 0);
		openGl.getGL2().glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, shininessLight, 0);
		openGl.getGL2().glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, emissionLight, 0);
		
		//openGl.getGL2().glEnable(GL2.GL_COLOR_MATERIAL);
		//openGl.getGL2().glEnable(GL2.GL_RESCALE_NORMAL);
		//openGl.getGL2().glEnable(GL2.GL_NORMALIZE);
		
	}
	
	public void disableLighting()
	{
		openGl.getGL2().glDisable(GLLightingFunc.GL_LIGHTING);
		openGl.getGL2().glDisable(GLLightingFunc.GL_LIGHT0);
	}
	
	public void normal(Vector normal)
	{
		double[] dv = { normal.x, normal.y, normal.z };
		openGl.getGL2().glNormal3dv(dv, 0);
	}

	protected int getMaximumSystemTextureSize()
	{
		return JDem846Properties.getIntProperty("us.wthr.jdem846.rendering.maxTextureSize");
	}

	protected int getMaximumTextureSize()
	{
		int[] list = { 0 };
		openGl.getGL().glGetIntegerv(GL.GL_MAX_TEXTURE_SIZE, list, 0);
		return list[0];
	}

	public int getMaximumTextureWidth()
	{
		return (int) MathExt.min(getMaximumSystemTextureSize(), getMaximumTextureSize());
	}

	public int getMaximumTextureHeight()
	{
		return (int) MathExt.min(getMaximumSystemTextureSize(), getMaximumTextureSize());
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
	public boolean bindTexture(Texture tex, TextureMapConfiguration configuration)
	{
		if (configuration == null) {
			configuration = new TextureMapConfiguration();
		}
		
		if (tex.getHeight() <= 0 || tex.getHeight() >= this.getMaximumTextureHeight()) {
			return false;
		}
		
		if (tex.getWidth() <= 0 || tex.getWidth() >= this.getMaximumTextureWidth()) {
			return false;
		}
		
		this.unbindTexture();
		
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition prior to binding of texture");
			return false;
		}

		int[] textures = { 0 };
		openGl.getGL2().glGenTextures(1, textures, 0);
		this.texture = textures[0];

		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following generation of texture");
			return false;
		}
		
		openGl.getGL2().glBindTexture(GL.GL_TEXTURE_2D, texture);
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following bind of texture");
			return false;
		}

		// ByteBuffer byteBuffer = ByteBuffer.allocate(tex.length * 4);
		// IntBuffer intBuffer = byteBuffer.asIntBuffer();
		// intBuffer.put(tex);

		//openGl.getGL2().glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

		
		
		openGl.getGL2().glTexImage2D(GL.GL_TEXTURE_2D, 0, 4, tex.getWidth(), tex.getHeight(), 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, tex.getAsByteBuffer());
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following tex image 2d");
			return false;
		}
		
		if (configuration.getCreateMipMaps()) {
			openGl.getGL2().glGenerateMipmap(GL2.GL_TEXTURE_2D);
			if (!this.checkGlContextSane()) {
				log.error("GL Context in error condition following generate mipmaps");
			}
		}
	
		
		if (configuration.getInterpolationType() == InterpolationTypeEnum.LINEAR) {
			openGl.getGL2().glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
			openGl.getGL2().glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		} else if (configuration.getInterpolationType() == InterpolationTypeEnum.NEAREST) {
			openGl.getGL2().glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
			openGl.getGL2().glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		}
		
		if (configuration.getTextureWrapType() == TextureWrapTypeEnum.REPEAT) {
			openGl.getGL2().glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT );
			openGl.getGL2().glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT );
		} else if (configuration.getTextureWrapType() == TextureWrapTypeEnum.CLAMP) {
			openGl.getGL2().glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP );
			openGl.getGL2().glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP );
		} else if (configuration.getTextureWrapType() == TextureWrapTypeEnum.CLAMP_TO_EDGE) {
			openGl.getGL2().glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE );
			openGl.getGL2().glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE );
		}
		

		if (configuration.getCreateMipMaps()) {
			if (configuration.getInterpolationType() == InterpolationTypeEnum.LINEAR) {
				openGl.getGL2().glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
				openGl.getGL2().glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
			} else if (configuration.getInterpolationType() == InterpolationTypeEnum.NEAREST) {
				openGl.getGL2().glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST_MIPMAP_NEAREST);
				openGl.getGL2().glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST_MIPMAP_NEAREST);
			}
			
			openGl.getGL2().glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_GENERATE_MIPMAP, GL2.GL_TRUE);    //The flag is set to TRUE
		}
		

		
				
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following tex parameters");
		}
		
		return true;
	}

	@Override
	public void unbindTexture()
	{
		openGl.getGL2().glBindTexture(GL.GL_TEXTURE_2D, 0);
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following unbinding texture");
		}
		if (texture != 0) {
			int[] textures = {texture};
			openGl.getGL2().glDeleteTextures(1, textures, 0);
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
	public ImageCapture captureImage()
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

		ByteBuffer byteBuffer = ByteBuffer.allocate(bufferLength);

		openGl.getGL().glPixelStorei(GL.GL_PACK_ALIGNMENT, 4);
		openGl.getGL().glReadPixels(0, 0, width, height, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, byteBuffer);

		ImageCapture image = new ImageCapture(width, height, 0x0);

		int i = 0; // Index into target int[]
		for (int row = 0; row < height; row++) {

			for (int col = 0; col < width; col++) {

				i = ((height - row - 1) * width * 4) + (col * 4);

				int iR = byteBuffer.get(i);
				int iG = byteBuffer.get(i + 1);
				int iB = byteBuffer.get(i + 2);
				int iA = byteBuffer.get(i + 3);

				int pixelInt = ColorUtil.rgbaToInt(iR, iG, iB, iA);

				image.set(col, row, pixelInt);
			}
		}

		return image;
	}

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

		ByteBuffer byteBuffer = ByteBuffer.allocate(bufferLength);

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

				int pixelInt = ColorUtil.rgbaToInt(iR, iG, iB, iA);

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

	@Override
	public void dispose()
	{
		openGl.dispose();
	}
	
	class LightingConfig 
	{
		Vector lightPosition;
		double emission;
		double ambient;
		double diffuse;
		double specular;
		double shininess;
		
		public LightingConfig(Vector lightPosition, double emission, double ambient, double diffuse, double specular, double shininess)
		{
			this.lightPosition = lightPosition;
			this.emission = emission;
			this.ambient = ambient;
			this.diffuse = diffuse;
			this.specular = specular;
			this.shininess = shininess;
		}
	}
}
