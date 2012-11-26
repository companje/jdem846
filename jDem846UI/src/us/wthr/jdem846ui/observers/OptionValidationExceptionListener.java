package us.wthr.jdem846ui.observers;

import us.wthr.jdem846.model.exceptions.ModelContainerException;

public interface OptionValidationExceptionListener {
	public void onOptionValidationException(ModelContainerException ex);
}
