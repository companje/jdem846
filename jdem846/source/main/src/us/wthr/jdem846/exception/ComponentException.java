package us.wthr.jdem846.exception;

@SuppressWarnings("serial")
public class ComponentException extends Exception
{
	
	public ComponentException(String message)
	{
		super(message);
	}
	
	public ComponentException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
}
