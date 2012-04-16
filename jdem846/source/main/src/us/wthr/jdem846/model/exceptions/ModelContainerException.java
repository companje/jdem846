package us.wthr.jdem846.model.exceptions;

@SuppressWarnings("serial")
public class ModelContainerException extends Exception
{
	
	public ModelContainerException()
	{
		
	}
	
	public ModelContainerException(String message)
	{
		super(message);
	}
	
	public ModelContainerException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
}
