package us.wthr.jdem846.model;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.model.processing.AbstractGridProcessor;

public class ModelProcessList
{
	
	private List<ModelProcessContainer> processList = new LinkedList<ModelProcessContainer>();

	public ModelProcessList()
	{
		
	}
	
	public void addProcessor(AbstractGridProcessor gridProcessor, OptionModel optionModel)
	{
		addProcessContainer(new ModelProcessContainer(gridProcessor, optionModel));
	}
	
	public void addProcessContainer(ModelProcessContainer processContainer)
	{
		processList.add(processContainer);
	}
	
	public List<ModelProcessContainer> getProcessList()
	{
		return processList;
	}
	
	public int getProcessListSize()
	{
		return processList.size();
	}
	
	public ModelProcessContainer getProcessContainerByIndex(int index)
	{
		return processList.get(index);
	}
	
}
