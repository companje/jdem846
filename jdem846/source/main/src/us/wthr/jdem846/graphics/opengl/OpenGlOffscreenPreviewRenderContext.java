package us.wthr.jdem846.graphics.opengl;

public class OpenGlOffscreenPreviewRenderContext extends OpenGlOffscreenRenderContext
{
	
	
	private static OpenGlOffscreenPreviewRenderContext INSTANCE;
	
	static {
		OpenGlOffscreenPreviewRenderContext.INSTANCE = new OpenGlOffscreenPreviewRenderContext();
	}
	
	
	public OpenGlOffscreenPreviewRenderContext()
	{
		super(1000, 1000);
	}
	
	
	public static OpenGlOffscreenPreviewRenderContext getInstance()
	{
		return OpenGlOffscreenPreviewRenderContext.INSTANCE;
	}


	@Override
	public void dispose()
	{
		drawable.getNativeSurface().unlockSurface();
	}
	
	
	
	
}
