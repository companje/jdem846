package us.wthr.jdem846.render.render3;

import us.wthr.jdem846.exception.RenderEngineException;

public interface GridProcessor
{
	public boolean isProcessing();
	public void prepare() throws RenderEngineException;
	public void process() throws RenderEngineException;
	
}
