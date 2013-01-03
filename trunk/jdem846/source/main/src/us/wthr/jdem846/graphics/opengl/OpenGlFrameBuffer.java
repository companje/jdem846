package us.wthr.jdem846.graphics.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

//import javax.media.opengl.GL;

public class OpenGlFrameBuffer
{
	private static Log log = Logging.getLog(OpenGlFrameBuffer.class);

	protected OpenGlOffscreenRenderContext openGl;

	private int frameBuffer;
	private int colorBuffer;
	private int depthBuffer;
	private int offscreenTexture;
	private int resolveTarget;
	private int width;
	private int height;
	private int samples = 1;
	private boolean initialized = false;

	public OpenGlFrameBuffer(OpenGlOffscreenRenderContext openGl, int width, int height, int samples)
	{
		this.width = width;
		this.height = height;
		this.samples = samples;
		this.openGl = openGl;
	}

	public boolean isFrameBufferSupported()
	{
		return true;
	}

	public boolean isMultisampleSupported()
	{
		return true;
	}

	public int getMaxSamples()
	{
		int list[] = { 0 };
		openGl.getGL().glGetIntegerv(GL2.GL_MAX_SAMPLES, list, 0);
		return list[0];
	}

	public int getMaxRenderBufferSize()
	{
		int list[] = { 0 };
		openGl.getGL().glGetIntegerv(GL2.GL_MAX_RENDERBUFFER_SIZE, list, 0);
		return list[0];
	}

	public int getActualSamples()
	{
		int list[] = { 0 };
		openGl.getGL2().glGetRenderbufferParameteriv(GL.GL_RENDERBUFFER, GL2.GL_RENDERBUFFER_SAMPLES, list, 0);
		return list[0];
	}

	public int getActualWidth()
	{
		int list[] = { 0 };
		openGl.getGL2().glGetRenderbufferParameteriv(GL.GL_RENDERBUFFER, GL2.GL_RENDERBUFFER_WIDTH, list, 0);
		return list[0];
	}

	public int getActualHeight()
	{
		int list[] = { 0 };
		openGl.getGL2().glGetRenderbufferParameteriv(GL.GL_RENDERBUFFER, GL2.GL_RENDERBUFFER_HEIGHT, list, 0);
		return list[0];
	}

	public boolean initialize()
	{
		if (initialized) {
			// throw
			return false;
		}

		openGl.getGL().glActiveTexture(GL.GL_TEXTURE0);
		int[] list = { 0 };

		openGl.getGL().glGenFramebuffers(1, list, 0);
		this.resolveTarget = list[0];
		openGl.getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, resolveTarget);

		openGl.getGL().glEnable(GL2.GL_TEXTURE_RECTANGLE_ARB);

		openGl.getGL().glGenTextures(1, list, 0);
		this.offscreenTexture = list[0];
		openGl.getGL().glBindTexture(GL.GL_TEXTURE_2D, offscreenTexture);

		openGl.getGL().glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following teximage2d");
		}
		
		
		openGl.getGL().glGenerateMipmap(GL.GL_TEXTURE_2D);
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following creation of mipmaps");
		}
		
		openGl.getGL().glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
		openGl.getGL().glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		openGl.getGL().glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
		openGl.getGL().glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

		openGl.getGL().glFramebufferTexture2D(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, GL2.GL_TEXTURE_RECTANGLE_ARB, offscreenTexture, 0);

		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following creation of offscreen texture");
		}
		
		openGl.getGL().glBindTexture(GL.GL_TEXTURE_2D, 0);

		// Create the multisample color render buffer image and attach it to the
		// second FBO.
		openGl.getGL().glGenFramebuffers(1, list, 0);
		this.frameBuffer = list[0];
		openGl.getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, frameBuffer);
		
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following creation of frame buffer");
		}
		
		openGl.getGL().glGenRenderbuffers(1, list, 0);
		this.colorBuffer = list[0];
		openGl.getGL().glBindRenderbuffer(GL.GL_RENDERBUFFER, colorBuffer);

		openGl.getGL2().glRenderbufferStorageMultisample(GL.GL_RENDERBUFFER, samples, GL.GL_RGBA, width, height);

		// int smpls;
		// openGl.getGL().glGetRenderbufferParameteriv(GL.GL_RENDERBUFFER,
		// GL2.GL_RENDERBUFFER_SAMPLES, list, 0);
		// smpls = list[0];
		openGl.getGL().glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, GL.GL_RENDERBUFFER, colorBuffer);
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following creation of color buffer");
		}
		
		
		// Create the multisample depth render buffer image and attach it to the
		// second FBO.
		openGl.getGL().glGenRenderbuffers(1, list, 0);
		this.depthBuffer = list[0];
		openGl.getGL().glBindRenderbuffer(GL.GL_RENDERBUFFER, depthBuffer);
		
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following binding of depth buffer");
		}
		
		openGl.getGL2().glRenderbufferStorageMultisample(GL.GL_RENDERBUFFER, samples, GL2.GL_DEPTH_COMPONENT, width, height);
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following setting of depth buffer multisampling");
		}
		
		// openGl.getGL().glGetRenderbufferParameteriv(GL.GL_RENDERBUFFER,
		// GL2.GL_RENDERBUFFER_SAMPLES, list, 0);
		// smpls = list[0];
		// assert(g_fboSamples >= samples);

		openGl.getGL().glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, GL.GL_RENDERBUFFER, depthBuffer);
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following creation of depth buffer");
		}
		
		// this->checkGlError(true);

		// this->checkFrameBufferReady();

		// status = this->checkFrameBufferStatus();

		if (checkFrameBufferReady()) {
			
			openGl.getGL().glBindTexture(GL.GL_TEXTURE_2D, 0);
			openGl.getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
			
			if (!this.checkGlContextSane()) {
				log.error("GL Context in error condition following framebuffer unbinding");
			}
			
			initialized = true;
			return initialized;
		} else {
			return false;
		}

		// if (status != GL_FRAMEBUFFER_COMPLETE_EXT) {
		// return true;
		// } else {
		// #if __EXCEPTIONS
		// LOG_ERROR << "Failed to initialize framebuffer" << END;
		// exit(1);
		// //throw RenderException("Failed to initialize framebuffer");
		// #else
		// return false;
		// #endif
		// }
	}

	public boolean bindForRender()
	{
		if (!initialized) {
			// throw
			return false;
		}

		openGl.getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, frameBuffer);
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following framebuffer binding");
		}
		
		int buffers[] = { GL.GL_COLOR_ATTACHMENT0 };
		openGl.getGL2().glDrawBuffers(1, buffers, 0);
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following set of draw buffers");
		}

		return true;
	}

	public boolean transferToResolveTargetBuffer()
	{
		if (!initialized) {
			// throw
			return false;
		}
		
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition prior to transfer to resolve target buffer");
		}
		
		openGl.getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		openGl.getGL().glBindFramebuffer(GL2.GL_READ_FRAMEBUFFER, frameBuffer);
		openGl.getGL().glBindFramebuffer(GL2.GL_DRAW_FRAMEBUFFER, resolveTarget);

		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following bind of rear/draw frame buffers");
		}
		
		openGl.getGL2().glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL.GL_COLOR_BUFFER_BIT, GL.GL_NEAREST);
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following blit framebuffer");
		}
		
		openGl.getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following unbind framebuffer");
		}
		
		openGl.getGL().glViewport(0, 0, width, height);

		openGl.getGL2().glMatrixMode(GL2.GL_PROJECTION);
		openGl.getGL2().glPushMatrix();
		openGl.getGL2().glLoadIdentity();

		openGl.getGL2().glMatrixMode(GL2.GL_MODELVIEW);
		openGl.getGL2().glPushMatrix();
		openGl.getGL2().glLoadIdentity();

		openGl.getGL2().glPushAttrib(GL2.GL_CURRENT_BIT);

		openGl.getGL().glDisable(GL2.GL_LIGHTING);
		openGl.getGL2().glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		openGl.getGL2().glBegin(GL2.GL_QUADS);
		openGl.getGL2().glTexCoord2i(0, 0);
		openGl.getGL2().glVertex3f(-1.0f, -1.0f, -1.0f);

		openGl.getGL2().glTexCoord2i(width, 0);
		openGl.getGL2().glVertex3f(1.0f, -1.0f, -1.0f);

		openGl.getGL2().glTexCoord2i(width, height);
		openGl.getGL2().glVertex3f(1.0f, 1.0f, -1.0f);

		openGl.getGL2().glTexCoord2i(0, height);
		openGl.getGL2().glVertex3f(-1.0f, 1.0f, -1.0f);
		openGl.getGL2().glEnd();

		openGl.getGL2().glBindTexture(GL2.GL_TEXTURE_RECTANGLE_ARB, 0);
		openGl.getGL().glDisable(GL2.GL_TEXTURE_RECTANGLE_ARB);

		openGl.getGL2().glPopAttrib();
		openGl.getGL2().glPopMatrix();

		openGl.getGL2().glMatrixMode(GL2.GL_PROJECTION);
		openGl.getGL2().glPopMatrix();

		openGl.getGL2().glMatrixMode(GL2.GL_MODELVIEW);

		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition transfer to resolve target");
		}
		
		
		return true;
	}

	public boolean bindForCapture()
	{
		if (!initialized) {
			// throw
			return false;
		}

		openGl.getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, resolveTarget);
		if (!this.checkGlContextSane()) {
			log.error("GL Context in error condition following bind of resolve target for capture");
		}
		
		return true;
	}

	public void unbind()
	{
		openGl.getGL().glBindTexture(GL.GL_TEXTURE_2D, 0);
	}

	public boolean destroy()
	{
		if (!initialized) {
			// throw
			return false;
		}

		this.unbind();

		int[] list = { 0 };

		list[0] = this.frameBuffer;
		openGl.getGL().glDeleteFramebuffers(1, list, 0);

		list[0] = this.resolveTarget;
		openGl.getGL().glDeleteFramebuffers(1, list, 0);

		list[0] = this.colorBuffer;
		openGl.getGL().glDeleteRenderbuffers(1, list, 0);

		list[0] = this.depthBuffer;
		openGl.getGL().glDeleteRenderbuffers(1, list, 0);

		list[0] = this.offscreenTexture;
		openGl.getGL().glDeleteTextures(1, list, 0);

		this.initialized = false;

		return true;
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

	public int checkFrameBufferStatus()
	{
		return openGl.getGL().glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);
	}

	public boolean checkFrameBufferReady()
	{
		int status = this.checkFrameBufferStatus();

		switch (status) {
		case GL.GL_FRAMEBUFFER_COMPLETE:
			break;
		case GL.GL_FRAMEBUFFER_UNSUPPORTED:
			log.error("Unsupported framebuffer format");
			return false;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
			log.error("Framebuffer incomplete, missing attachment");
			return false;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
			log.error("Framebuffer incomplete, duplicate attachment");
			return false;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
			log.error("Framebuffer incomplete, attached images must have same dimensions");
			return false;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS:
			log.error("Framebuffer incomplete, attached images must have same format");
			return false;
		case GL2.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
			log.error("Framebuffer incomplete, missing draw buffer");
			return false;
		case GL2.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
			log.error("Framebuffer incomplete, missing read buffer");
			return false;
		default:
			log.error("Unrecognized error condition: " + status);
			return false;
		}

		return true;
	}

	public boolean initialized()
	{
		return this.initialized;
	}

}
