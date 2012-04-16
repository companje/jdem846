package us.wthr.jdem846.model;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;
import us.wthr.jdem846.model.processing.ModelProcessRegistry;
import us.wthr.jdem846.model.processing.ProcessInstance;

public class ModelProcessManifest
{
	private static Log log = Logging.getLog(ModelProcessManifest.class);
	
	private GlobalOptionModel globalOptionModel;
	private List<ModelProcessContainer> processList = new LinkedList<ModelProcessContainer>();
	
	public ModelProcessManifest()
	{
		
	}
	
	public ModelProcessManifest(GlobalOptionModel globalOptionModel)
	{
		this.globalOptionModel = globalOptionModel;
	}
	
	
	public void addProcessor(String processId)
	{
		ProcessInstance processInstance = ModelProcessRegistry.getInstance(processId);
		Class<?> clazz = (Class<?>) processInstance.getProcessorClass();
		
		
	}
	
	public void addProcessor(String processId, OptionModel optionModel)
	{
		ProcessInstance processInstance = ModelProcessRegistry.getInstance(processId);
		Class<?> clazz = (Class<?>) processInstance.getProcessorClass();
		
		
		AbstractGridProcessor gridProcessor = null;
		try {
			gridProcessor = (AbstractGridProcessor) clazz.newInstance();
		} catch (Exception ex) {
			// TODO: Throw up
			log.error("Error creating processor instance: " + ex.getMessage(), ex);
			return;
		}
		
		addProcessor(gridProcessor, optionModel);
		
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

	public GlobalOptionModel getGlobalOptionModel()
	{
		return globalOptionModel;
	}

	public void setGlobalOptionModel(GlobalOptionModel globalOptionModel)
	{
		this.globalOptionModel = globalOptionModel;
	}
	
	
	
}
