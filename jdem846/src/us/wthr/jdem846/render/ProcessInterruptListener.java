package us.wthr.jdem846.render;

public interface ProcessInterruptListener
{
	public void onProcessCancelled();
	public void onProcessPaused();
	public void onProcessResumed();
}
