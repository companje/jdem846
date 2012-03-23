package us.wthr.jdem846.exception;

@SuppressWarnings("serial")
public class ModelContextException extends Exception
{
	
	public ModelContextException(String message)
	{
		super(message);
	}
	
	public ModelContextException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
}
