package us.wthr.jdem846.model.exceptions;

@SuppressWarnings("serial")
public class OptionValidationException extends Exception
{
	
	private String propertyId;
	private Object value;
	
	public OptionValidationException(String message)
	{
		super(message);
	}
	
	public OptionValidationException(String message, String propertyId, Object value)
	{
		super(message);
		this.propertyId = propertyId;
		this.value = value;
	}
	
	public OptionValidationException(String message, Throwable thrown)
	{
		super(message, thrown);
	}

	public OptionValidationException(String message, Throwable thrown, String propertyId, Object value)
	{
		super(message, thrown);
		this.propertyId = propertyId;
		this.value = value;
	}
	
	public String getPropertyId()
	{
		return propertyId;
	}

	public Object getValue()
	{
		return value;
	}
	
	
	
}
