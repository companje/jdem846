package us.wthr.jdem846.render;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class InterruptibleProcess
{
	private static Log log = Logging.getLog(InterruptibleProcess.class);
	
	private ProcessInterruptListener processInterruptListener = null;
	private boolean cancel = false;
	private boolean paused = false;
	
	protected void setProcessInterruptListener(ProcessInterruptListener listener)
	{
		processInterruptListener = listener;
	}
	
	protected void fireProcessCancelListener()
	{
		if (processInterruptListener != null) {
			processInterruptListener.onProcessCancelled();
		}
	}
	
	protected void fireProcessPausedListener()
	{
		if (processInterruptListener != null) {
			processInterruptListener.onProcessPaused();
		}
	}
	
	protected void fireProcessResumedListener()
	{
		if (processInterruptListener != null) {
			processInterruptListener.onProcessResumed();
		}
	}
	
	/** Requests that a rendering process is stopped.
	 * 
	 */
	public void cancel()
	{
		if (isPaused())
			resume();
		this.cancel = true;
		fireProcessCancelListener();
	}
	
	/** Determines whether the rendering process has been requested to stop. This does not necessarily mean
	 * that the process <i>has</i> stopped as engine implementations need not check this value that often or
	 * at all.
	 * 
	 * @return Whether the rendering process has been requested to stop.
	 */
	public boolean isCancelled()
	{
		return cancel;
	}
	
	
	public void pause()
	{
		paused = true;
		fireProcessPausedListener();
	}
	
	public void resume()
	{
		paused = false;
		fireProcessResumedListener();
	}
	
	public boolean isPaused()
	{
		return paused;
	}
	
	protected void checkPause()
	{
		while(isPaused()) {
			//Thread.yield();
			try {
				Thread.sleep(250);
			} catch (InterruptedException ex) {
				log.warn("Interruption to pause in thread: " + ex.getMessage(), ex);
			}
		}
	}
	
}
