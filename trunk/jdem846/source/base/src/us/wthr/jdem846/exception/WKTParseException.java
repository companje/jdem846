package us.wthr.jdem846.exception;

@SuppressWarnings("serial")
public class WKTParseException extends Exception
{
	
	public WKTParseException(String message)
	{
		super(message);
	}
	
	public WKTParseException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
	
	
}
