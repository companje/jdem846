package us.wthr.jdem846.exception;

@SuppressWarnings("serial")
public class DataSourceException extends Exception
{
	
	public DataSourceException(String message)
	{
		super(message);
	}
	
	public DataSourceException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
}
