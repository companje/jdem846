package us.wthr.jdem846ui.daemons;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846ui.observers.PreviewRunTask;

public class PreviewRenderDaemon extends Thread
{
	private static Log log = Logging.getLog(PreviewRenderDaemon.class);
	
	private long pollDelayMillis = 200;
	
	private static Queue<PreviewRunTask> previewRunTaskQueue = new ConcurrentLinkedQueue<PreviewRunTask>();
	
	public PreviewRenderDaemon()
	{
		super("us.wthr.jdem846ui.previewRender");
		this.setDaemon(true);
	}
	
	
	
	public void run()
	{
		
		log.info("Starting preview render thread");
		try {
			while(true) {
				
				if (!previewRunTaskQueue.isEmpty()) {
					PreviewRunTask task = previewRunTaskQueue.poll();
					try {
						log.info("Rendering preview task");
						task.run();
						log.info("Completed preview task");
					} catch (Exception ex) {
						log.warn("Preview render failed: " + ex.getMessage(), ex);
					}
				}
				
				try {
					Thread.sleep(pollDelayMillis);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		} finally {
			log.warn("Preview render thread is exiting");
		}
	}
	
	
	public static void schedulePreview(PreviewRunTask previewRunTask)
	{
		PreviewRenderDaemon.previewRunTaskQueue.add(previewRunTask);
	}
	
}
