package us.wthr.jdem846.exception;

@SuppressWarnings("serial")
public class ImageException extends Exception
{
	
	public ImageException()
	{
		super();
	}
	
	
	public ImageException(String message)
	{
		super(message);
	}
	
	public ImageException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
}
