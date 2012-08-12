package us.wthr.jdem846.model.processing;

import us.wthr.jdem846.exception.RenderEngineException;

public interface GridPointFilter {
	
	public void onModelPoint(double latitude, double longitude) throws RenderEngineException;
}
