package us.wthr.jdem846ui.observers;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModelChangeEvent;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.PropertyValidationResult;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.model.exceptions.OptionValidationException;
import us.wthr.jdem846ui.project.ProjectContext;

public class OptionValidationChangeObserver extends ProjectChangeObserver 
{
	
	private static Log log = Logging.getLog(OptionValidationChangeObserver.class);
	private static OptionValidationChangeObserver INSTANCE;
	
	private List<OptionValidationExceptionListener> exceptionListeners = new LinkedList<OptionValidationExceptionListener>();
	private List<OptionValidationResultsListener> resultsListeners = new LinkedList<OptionValidationResultsListener>();
	
	public OptionValidationChangeObserver()
	{
		super();
		OptionValidationChangeObserver.INSTANCE = this;
	}

	@Override
	public void onDataAdded()
	{
		validateOptionModels();
	}

	@Override
	public void onDataRemoved()
	{
		validateOptionModels();
	}

	@Override
	public void onOptionChanged(OptionModelChangeEvent e)
	{
		validateOptionModels();
	}
	
	
	protected void validateOptionModels()
	{
		List<OptionModelContainer> containers = ProjectContext.getInstance().getDefaultOptionModelContainerList();
		ProjectContext.getInstance().setIgnoreOptionChanges(true);
		
		List<PropertyValidationResult> results = new LinkedList<PropertyValidationResult>();
		List<OptionValidationException> validationExceptions = new LinkedList<OptionValidationException>();
		
		for (OptionModelContainer container : containers) {
			if (container != null && container.getOptionModel() != null) {
				log.info("Performing validation on container for " + container.getOptionModel().getClass().getName());
				
				try {
					results.addAll(container.validateOptions(ProjectContext.getInstance().getModelContext()));
				} catch (ModelContainerException ex) {
					fireOptionValidationExceptionListeners(ex);
					log.error("Model container error during validation: " + ex.getMessage(), ex);
				}
			}
			
		}
		
		ProjectContext.getInstance().setIgnoreOptionChanges(false);
		fireOptionValidationResultsListeners(results);
	}
	
	public void addOptionValidationExceptionListener(OptionValidationExceptionListener l)
	{
		this.exceptionListeners.add(l);
	}
	
	public boolean removeOptionValidationExceptionListener(OptionValidationExceptionListener l)
	{
		return this.exceptionListeners.remove(l);
	}
	
	protected void fireOptionValidationExceptionListeners(ModelContainerException ex)
	{
		for (OptionValidationExceptionListener l : this.exceptionListeners) {
			l.onOptionValidationException(ex);
		}
	}
	
	public void addOptionValidationResultsListener(OptionValidationResultsListener l)
	{
		this.resultsListeners.add(l);
	}
	
	public boolean removeOptionValidationResultsListener(OptionValidationResultsListener l)
	{
		return this.resultsListeners.remove(l);
	}
	
	protected void fireOptionValidationResultsListeners(List<PropertyValidationResult> results)
	{
		for (OptionValidationResultsListener l : this.resultsListeners) {
			l.onOptionValidationResults(results);
		}
	}
	
	public static OptionValidationChangeObserver getInstance()
	{
		return OptionValidationChangeObserver.INSTANCE;
	}
	
}
