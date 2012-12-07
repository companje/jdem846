package us.wthr.jdem846.model.processing;

import us.wthr.jdem846.exception.RenderEngineException;

public interface IGridProcessor extends IGridWorker
{

	public void onLatitudeStart(double latitude) throws RenderEngineException;

	public void onLatitudeEnd(double latitude) throws RenderEngineException;

}
