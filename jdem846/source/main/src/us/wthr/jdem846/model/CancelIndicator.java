package us.wthr.jdem846.model;

public class CancelIndicator
{
	private boolean cancelled = false;
	
	public CancelIndicator()
	{
		
	}
	
	public void cancel()
	{
		synchronized(this) {
			cancelled = true;
		}
	}
	
	public boolean isCancelled()
	{
		synchronized(this) {
			return cancelled;
		}
	}
	
}
