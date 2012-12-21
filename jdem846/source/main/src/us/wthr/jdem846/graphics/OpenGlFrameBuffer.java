package us.wthr.jdem846.graphics;

//import javax.media.opengl.GL;

public class OpenGlFrameBuffer
{
		
	private int frameBuffer;
	private int colorBuffer;
	private int depthBuffer;
	private int offscreenTexture;
	private int resolveTarger;
	private int width;
	private int height;
	private int samples = 1;
	private boolean initialized = false;
	
	
	public OpenGlFrameBuffer(int width, int height, int samples)
	{
		this.width = width;
		this.height = height;
		this.samples = samples;
	}
	
	public boolean isFrameBufferSupported()
	{
		return false;
	}
	
	public boolean isMultisampleSupported()
	{
		return false;
	}
	
	public int getMaxSamples()
	{
		return 0;
	}
	
	
	public int getActualSamples()
	{
		return 0;
	}
	
	public int getActualWidth()
	{
		return 0;
	}
	
	public int getActualHeight()
	{
		return 0;
	}
	
	public boolean initialize()
	{
		return false;
	}
	
	public boolean bindForRender()
	{
		return false;
	}
	
	public boolean transferToResolveTargetBuffer()
	{
		return false;
	}
	
	public boolean bindForCapture()
	{
		return false;
	}
	
	public void unbind()
	{
		
	}
	
	public boolean destroy()
	{
		return false;
	}
	
	public void checkGlError(boolean exitOnError)
	{
		
	}
	
	public int checkFrameBufferStatus()
	{
		return 0;
	}
	
	public boolean checkFrameBufferReady()
	{
		return false;
	}
	
	public boolean initialized()
	{
		return this.initialized;
	}
	
	
	
	
}
