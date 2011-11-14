package us.wthr.jdem846.render;

public class CancellableProcess
{
	
	private ProcessCancelListener processCancelListener = null;
	private boolean cancel = false;
	
	
	protected void setProcessCancelListener(ProcessCancelListener listener)
	{
		processCancelListener = listener;
	}
	
	protected void fireProcessCancelListener()
	{
		if (processCancelListener != null) {
			processCancelListener.onProcessCancelled();
		}
	}
	
	/** Requests that a rendering process is stopped.
	 * 
	 */
	public void cancel()
	{
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
	
}
