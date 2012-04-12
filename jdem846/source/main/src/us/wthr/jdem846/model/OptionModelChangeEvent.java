package us.wthr.jdem846.model;

public class OptionModelChangeEvent
{
	
	private String propertyName;
	private String propertyId;
	
	private Object oldValue;
	private Object newValue;
	
	public OptionModelChangeEvent(String propertyName, String propertyId, Object oldValue, Object newValue)
	{
		this.propertyId = propertyId;
		this.propertyName = propertyName;
		this.oldValue = oldValue;
		this.newValue = newValue;
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
	
	
}
