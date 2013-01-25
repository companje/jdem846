package us.wthr.jdem846.graphics.opengl;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import us.wthr.jdem846.JDem846Properties;

public class OpenGlContextPool
{
	
	private static final int DEFAULT_MAX_GL_CONTEXTS = 10;
	private static final String MAX_GL_CONTEXTS_PROPERTY = "us.wthr.jdem846.opengl.maxContexts";
	private static final long POLL_DELAY_MILLIS = 300;
	
	private static int contextCreateCount = 0;
	private static Queue<OpenGlOffscreenRenderContext> available = new ConcurrentLinkedQueue<OpenGlOffscreenRenderContext>();
	private static List<OpenGlOffscreenRenderContext> inUse = new ArrayList<OpenGlOffscreenRenderContext>(DEFAULT_MAX_GL_CONTEXTS);
	
	public static OpenGlOffscreenRenderContext getContext(boolean wait, long timeoutMillis)
	{
		OpenGlOffscreenRenderContext context = null;
		
		long start = System.currentTimeMillis();
		
		while((context = getContext()) != null && wait && (System.currentTimeMillis() - start < timeoutMillis)) {
			try {
				Thread.sleep(POLL_DELAY_MILLIS);
			} catch (InterruptedException ex) {
				ex.printStackTrace(); // TODO: throw
			}
		}
		
		return context;
	}
	
	
	public static OpenGlOffscreenRenderContext getContext(boolean wait)
	{
		return getContext(wait, 999999999999l);
	}
	
	public static OpenGlOffscreenRenderContext getContext()
	{
		OpenGlOffscreenRenderContext context = available.poll();
		
		if (context != null) {
			inUse.add(context);
		}
		
		return context;
	}
	
	public static void releaseContext(OpenGlOffscreenRenderContext context)
	{
		
	}
	
	protected OpenGlOffscreenRenderContext createContext()
	{
		if (contextCreateCount >= getMaxGlContexts()) {
			return null;
		}
		
		return null;
	}
	
	
	
	
	
	
	protected int getMaxGlContexts()
	{
		if (JDem846Properties.hasProperty(MAX_GL_CONTEXTS_PROPERTY)) {
			return JDem846Properties.getIntProperty(MAX_GL_CONTEXTS_PROPERTY);
		} else {
			return DEFAULT_MAX_GL_CONTEXTS;
		}
	}
	
}
