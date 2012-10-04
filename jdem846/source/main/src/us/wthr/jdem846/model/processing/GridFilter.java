package us.wthr.jdem846.model.processing;

public abstract class GridFilter extends GridWorker
{
	public WorkerTypeEnum getWorkerType()
	{
		return WorkerTypeEnum.FILTER;
	}
}
