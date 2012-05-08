package us.wthr.jdem846.model.exceptions;

@SuppressWarnings("serial")
public class ContextPrepareException extends Exception
{
	
	public ContextPrepareException()
	{
		
	}
	
	public ContextPrepareException(String message)
	{
		super(message);
	}
	
	public ContextPrepareException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
}
