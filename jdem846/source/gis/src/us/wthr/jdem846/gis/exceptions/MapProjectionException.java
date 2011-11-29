package us.wthr.jdem846.gis.exceptions;

@SuppressWarnings("serial")
public class MapProjectionException extends Exception
{
	
	public MapProjectionException(String message)
	{
		super(message);
	}
	
	public MapProjectionException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
	
}
