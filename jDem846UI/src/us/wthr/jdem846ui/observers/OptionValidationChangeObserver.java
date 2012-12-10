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

	static {
		OptionValidationChangeObserver.INSTANCE = new OptionValidationChangeObserver();
	}

	protected OptionValidationChangeObserver()
	{
		super();
	}

	@Override
	public void onDataAdded()
	{
		validateOptionModels(null);
	}

	@Override
	public void onDataRemoved()
	{
		validateOptionModels(null);
	}

	@Override
	public void onOptionChanged(OptionModelChangeEvent e)
	{
		validateOptionModels(e);
	}

	protected void validateOptionModels(OptionModelChangeEvent e)
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
					fireOptionValidationExceptionListeners(ex, e);
					log.error("Model container error during validation: " + ex.getMessage(), ex);
				}
			}

		}

		ProjectContext.getInstance().setIgnoreOptionChanges(false);
		fireOptionValidationResultsListeners(results, e);
	}

	public void addOptionValidationExceptionListener(OptionValidationExceptionListener l)
	{
		this.exceptionListeners.add(l);
	}

	public boolean removeOptionValidationExceptionListener(OptionValidationExceptionListener l)
	{
		return this.exceptionListeners.remove(l);
	}

	protected void fireOptionValidationExceptionListeners(ModelContainerException ex, OptionModelChangeEvent e)
	{
		for (OptionValidationExceptionListener l : this.exceptionListeners) {
			l.onOptionValidationException(ex, e);
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

	protected void fireOptionValidationResultsListeners(List<PropertyValidationResult> results, OptionModelChangeEvent e)
	{
		for (OptionValidationResultsListener l : this.resultsListeners) {
			l.onOptionValidationResults(results, e);
		}
	}

	public static OptionValidationChangeObserver getInstance()
	{
		return OptionValidationChangeObserver.INSTANCE;
	}

}
