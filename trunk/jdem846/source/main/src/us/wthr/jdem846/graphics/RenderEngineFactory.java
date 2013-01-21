package us.wthr.jdem846.graphics;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.graphics.opengl.OpenGlRenderer;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class RenderEngineFactory
{
	private static Log log = Logging.getLog(RenderEngineFactory.class);
	
	public static IRenderer createRenderer()
	{
		IRenderer renderer = null;
		
		String renderEngine = JDem846Properties.getProperty("us.wthr.jdem846.rendering.renderEngine"); 
		if (renderEngine == null) {
			renderEngine = "software";
		}
		
		if (renderEngine.equalsIgnoreCase("software")) {
			log.info("Initializing software renderer");
			renderer = new GraphicsRenderer();
		} else if (renderEngine.equalsIgnoreCase("opengl")) {
			log.info("Initializing OpenGL renderer");
			renderer = new OpenGlRenderer();
		} else {
			// Throw!!
		}
		
		return renderer;
	}
	
	
}
