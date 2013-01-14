package us.wthr.jdem846.exception;

@SuppressWarnings("serial")
public class BufferException extends RuntimeException 
{
	
	public BufferException(String message)
	{
		super(message);
	}
	
	public BufferException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
}
