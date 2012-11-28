package us.wthr.jdem846ui.tasks;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class RenderTaskLauncher {
	private static Log log = Logging.getLog(RenderTaskLauncher.class);
	
	private static RenderTaskLauncher INSTANCE;
	
	private List<RenderCompletionListener> completionListeners = new LinkedList<RenderCompletionListener>();
	
	
	static {
		RenderTaskLauncher.INSTANCE = new RenderTaskLauncher();
	}
	
	protected RenderTaskLauncher()
	{	
		
	}
	
	public void launchRenderTask()
	{
		log.info("Launching render task...");
		
		RenderTask renderTask = new RenderTask(completionListeners);
		renderTask.schedule();
		
		log.info("Render task scheduled for execution");
	}
	
	
	public void addRenderCompletionListener(RenderCompletionListener l)
	{
		this.completionListeners.add(l);
	}
	
	public boolean removeRenderCompletionListener(RenderCompletionListener l)
	{
		return this.completionListeners.remove(l);
	}
	
	
	
	
	
	public static RenderTaskLauncher getInstance()
	{
		return RenderTaskLauncher.INSTANCE;
	}
}
