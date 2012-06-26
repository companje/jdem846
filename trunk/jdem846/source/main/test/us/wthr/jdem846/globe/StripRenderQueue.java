package us.wthr.jdem846.globe;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;

import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.geom.TriangleStrip;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class StripRenderQueue extends Thread
{
	
	private static Log log = Logging.getLog(StripRenderQueue.class);
	
	Queue<TriangleStrip> stripQueue = new ConcurrentLinkedQueue<TriangleStrip>();
	ModelCanvas modelCanvas;
	
	private int pollDelayMillis = 25;
	
	private boolean stop = false;
	private boolean completed = false;
	
	public StripRenderQueue(ModelCanvas modelCanvas)
	{
		this.modelCanvas = modelCanvas;
	}
	
	
	public void add(TriangleStrip strip)
	{
		try {
			stripQueue.add(strip);
		} catch (IllegalStateException ex) {
			log.error("Illegal state exception thrown adding triangle strip to queue (queue currently has " + stripQueue.size() + " elements): " + ex.getMessage(), ex);
		}
	}
	
	
	
	public void run()
	{
		
		long stripCount = 0;
		
		while(!stop || stripQueue.size() > 0) {
			
			TriangleStrip strip = stripQueue.poll();
			
			if (strip != null) {
				modelCanvas.fillShape(strip);
				strip.reset();
				stripCount++;
			}
			
			
			try {
				Thread.sleep(pollDelayMillis);
			} catch (InterruptedException ex) {
				log.info("Log poll delay interrupted: " + ex.getMessage(), ex);
			}
			
			
		}
		
		
		completed = true;
		log.info("Strip render queue completed.");
		log.info("Rendered " + stripCount + " triangle strips");
	}

	
	public boolean isCompleted()
	{
		return completed;
	}
	
	public void stopRendering()
	{
		stop = true;
	}
	
}
