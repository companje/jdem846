package us.wthr.jdem846.model.exceptions;

@SuppressWarnings("serial")
public class ProcessContainerException extends Exception
{
	
	public ProcessContainerException(String message)
	{
		super(message);
	}
	
	public ProcessContainerException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
}
