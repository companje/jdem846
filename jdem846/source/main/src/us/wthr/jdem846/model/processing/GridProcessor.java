package us.wthr.jdem846.model.processing;

import us.wthr.jdem846.exception.RenderEngineException;


public abstract class GridProcessor extends GridWorker
{
	
	
	public abstract void onLatitudeStart(double latitude) throws RenderEngineException;
	public abstract void onLatitudeEnd(double latitude) throws RenderEngineException;

	public WorkerTypeEnum getWorkerType()
	{
		return WorkerTypeEnum.PROCESSOR;
	}
	
}
