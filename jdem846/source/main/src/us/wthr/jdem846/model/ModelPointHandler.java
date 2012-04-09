package us.wthr.jdem846.model;

import us.wthr.jdem846.exception.RenderEngineException;

public interface ModelPointHandler
{
	public void onCycleStart() throws RenderEngineException;
	public void onModelLatitudeStart(double latitude) throws RenderEngineException;
	public void onModelPoint(double latitude, double longitude) throws RenderEngineException;
	public void onModelLatitudeEnd(double latitude) throws RenderEngineException;
	public void onCycleEnd() throws RenderEngineException;
}
