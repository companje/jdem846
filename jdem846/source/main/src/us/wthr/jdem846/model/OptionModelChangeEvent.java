package us.wthr.jdem846.model;

public class OptionModelChangeEvent
{
	
	private String propertyName;
	private String propertyId;
	
	private Object oldValue;
	private Object newValue;
	
	private boolean runValidation = true;
	
	public OptionModelChangeEvent(String propertyName, String propertyId, Object oldValue, Object newValue, boolean runValidation)
	{
		this.propertyId = propertyId;
		this.propertyName = propertyName;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.runValidation = runValidation;
	}

	public String getPropertyName()
	{
		return propertyName;
	}

	public String getPropertyId()
	{
		return propertyId;
	}

	public Object getOldValue()
	{
		return oldValue;
	}

	public Object getNewValue()
	{
		return newValue;
	}
	
	public boolean runValidation()
	{
		return runValidation;
	}
	
}
