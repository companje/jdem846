package us.wthr.jdem846ui.observers;

import java.util.List;

import us.wthr.jdem846.model.OptionModelChangeEvent;
import us.wthr.jdem846.model.PropertyValidationResult;

public interface OptionValidationResultsListener
{
	public void onOptionValidationResults(List<PropertyValidationResult> results, OptionModelChangeEvent originatingEvent);
}
