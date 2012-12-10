package us.wthr.jdem846.model;

import us.wthr.jdem846.ModelContext;

public interface IOptionEnabler {
	
	public boolean isOptionEnabled(ModelContext modelContext, OptionModel optionModel, String propertyId, Object value);
	
}
