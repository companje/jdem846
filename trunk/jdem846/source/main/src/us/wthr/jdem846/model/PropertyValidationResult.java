package us.wthr.jdem846.model;

import us.wthr.jdem846.model.exceptions.OptionValidationException;

public class PropertyValidationResult
{
	
	private String id;
	private OptionValidationException exception;
	private boolean refreshUI;
	
	public PropertyValidationResult(String id, boolean refreshUI)
	{
		this(id, null, refreshUI);
	}
	
	public PropertyValidationResult(String id, OptionValidationException exception, boolean refreshUI)
	{
		this.id = id;
		this.exception = exception;
		this.refreshUI = refreshUI;
	}

	public String getId()
	{
		return id;
	}

	public OptionValidationException getException()
	{
		return exception;
	}

	public boolean getRefreshUI()
	{
		return refreshUI;
	}
	
	
	
	
}
