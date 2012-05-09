package us.wthr.jdem846.model;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.model.exceptions.ProcessContainerException;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;
import us.wthr.jdem846.model.processing.ModelProcessRegistry;
import us.wthr.jdem846.model.processing.ProcessInstance;

public class ModelProcessManifest
{
	private static Log log = Logging.getLog(ModelProcessManifest.class);
	
	
	private OptionModelContainer globalOptionModelContainer;
	
	private List<ModelProcessContainer> processList = new LinkedList<ModelProcessContainer>();
	
	public ModelProcessManifest() throws ProcessContainerException
	{
		this(new GlobalOptionModel());
	}
	
	public ModelProcessManifest(GlobalOptionModel globalOptionModel) throws ProcessContainerException
	{
		if (globalOptionModel != null) {
			setGlobalOptionModel(globalOptionModel);
		}
	}
	
	public ModelProcessManifest(OptionModelContainer globalOptionModelContainer)
	{
		this.globalOptionModelContainer = globalOptionModelContainer;
	}
	
	public void addProcessor(String processId)
	{
		ProcessInstance processInstance = ModelProcessRegistry.getInstance(processId);
		Class<?> clazz = (Class<?>) processInstance.getProcessorClass();
		
		
	}
	
	public void addProcessor(String processId, OptionModel optionModel) throws ProcessContainerException
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
	
	public void addProcessor(AbstractGridProcessor gridProcessor, OptionModel optionModel) throws ProcessContainerException
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
		return (GlobalOptionModel) this.globalOptionModelContainer.getOptionModel();
	}

	public void setGlobalOptionModel(GlobalOptionModel globalOptionModel) throws ProcessContainerException
	{
		try {
			globalOptionModelContainer = new OptionModelContainer(globalOptionModel);
		} catch (InvalidProcessOptionException ex) {
			throw new ProcessContainerException("Error creating default container for global option model: " + ex.getMessage(), ex);
		}
	}
	
	
	protected OptionModelContainer getOptionModelContainerThatContainsPropertyName(String name)
	{
		if (globalOptionModelContainer != null && globalOptionModelContainer.hasPropertyByName(name)) {
			return globalOptionModelContainer;
		}
		
		for (ModelProcessContainer processContainer : this.processList) {
			OptionModelContainer optionModelContainer = processContainer.getOptionModelContainer();
			if (optionModelContainer != null && optionModelContainer.hasPropertyByName(name)) {
				return optionModelContainer;
			}
			
		}
		
		return null;
	}
	
	protected OptionModelContainer getOptionModelContainerThatContainsPropertyId(String id)
	{
		if (globalOptionModelContainer != null && globalOptionModelContainer.hasPropertyById(id)) {
			return globalOptionModelContainer;
		}
		
		for (ModelProcessContainer processContainer : this.processList) {
			OptionModelContainer optionModelContainer = processContainer.getOptionModelContainer();
			if (optionModelContainer != null && optionModelContainer.hasPropertyById(id)) {
				return optionModelContainer;
			}
			
		}
		
		return null;
	}
	
	public boolean setPropertyById(String id, Object value) throws ModelContainerException
	{
		OptionModelContainer optionModelContainer = getOptionModelContainerThatContainsPropertyId(id);
		if (optionModelContainer != null && optionModelContainer.hasPropertyById(id)) {
			optionModelContainer.setPropertyValueById(id, value);
			return true;
		} else {
			return false;
		}
	}
	
	
	public Object getPropertyById(String id) throws ModelContainerException
	{
		OptionModelContainer optionModelContainer = getOptionModelContainerThatContainsPropertyId(id);
		if (optionModelContainer != null && optionModelContainer.hasPropertyById(id)) {
			return optionModelContainer.getPropertyValueById(id);
		} else {
			return null;
		}
	}
	
	public boolean setPropertyByName(String name, Object value) throws ModelContainerException
	{
		OptionModelContainer optionModelContainer = getOptionModelContainerThatContainsPropertyName(name);
		if (optionModelContainer != null && optionModelContainer.hasPropertyByName(name)) {
			optionModelContainer.setPropertyValueByName(name, value);
			return true;
		} else {
			return false;
		}
	}
	
	
	public Object getPropertyByName(String name) throws ModelContainerException
	{
		OptionModelContainer optionModelContainer = getOptionModelContainerThatContainsPropertyName(name);
		if (optionModelContainer != null && optionModelContainer.hasPropertyByName(name)) {
			return optionModelContainer.getPropertyValueByName(name);
		} else {
			return null;
		}
	}
	
	
	public ModelProcessManifest copy() throws ProcessContainerException
	{

		ModelProcessManifest copy = null;
		
		
		copy = new ModelProcessManifest((GlobalOptionModel)null);
		try {
			copy.globalOptionModelContainer = new OptionModelContainer(this.globalOptionModelContainer.getOptionModel());
		} catch (InvalidProcessOptionException ex) {
			throw new ProcessContainerException("Error creating copy of option model container: " + ex.getMessage(), ex);
		}
		
		
		for (ModelProcessContainer processContainer : this.processList) {
			copy.addProcessContainer(new ModelProcessContainer(processContainer.getGridProcessor(), processContainer.getOptionModel()));
		}
		
		return copy;
		
	}
	
	
}
