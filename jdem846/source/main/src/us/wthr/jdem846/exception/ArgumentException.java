package us.wthr.jdem846.exception;

@SuppressWarnings("serial")
public class ArgumentException extends Exception
{
	
	public ArgumentException()
	{
		
	}
	
	public ArgumentException(String message)
	{
		super(message);
	}
	
	public ArgumentException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
	
	
}
