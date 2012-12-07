package us.wthr.jdem846.model.processing;

public abstract class GridProcessor extends GridWorker implements IGridProcessor
{

	@Override
	public WorkerTypeEnum getWorkerType()
	{
		return WorkerTypeEnum.PROCESSOR;
	}

}
