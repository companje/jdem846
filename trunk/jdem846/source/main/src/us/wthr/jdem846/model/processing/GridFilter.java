package us.wthr.jdem846.model.processing;

public abstract class GridFilter extends GridWorker implements IGridFilter
{
	@Override
	public WorkerTypeEnum getWorkerType()
	{
		return WorkerTypeEnum.FILTER;
	}
}
