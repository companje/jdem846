package us.wthr.jdem846.model;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.model.exceptions.OptionValidationException;

public interface OptionValidator
{
	/**
	 * 
	 * @param modelContext
	 * @param optionModel
	 * @param propertyId
	 * @param value
	 * @return True if another value in the option model was modified requiring a UI update.
	 * @throws OptionValidationException
	 */
	public boolean validate(ModelContext modelContext, OptionModel optionModel, String propertyId, Object value) throws OptionValidationException;
}
