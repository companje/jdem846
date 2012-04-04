package us.wthr.jdem846.gis.exceptions;

@SuppressWarnings("serial")
public class ParseException extends Exception
{
	
	public ParseException(String message)
	{
		super(message);
	}
	
	public ParseException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
	
}
