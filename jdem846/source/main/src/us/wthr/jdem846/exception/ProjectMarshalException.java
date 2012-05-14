package us.wthr.jdem846.exception;

@SuppressWarnings("serial")
public class ProjectMarshalException extends Exception
{
	
	public ProjectMarshalException(String message)
	{
		super(message);
	}
	
	public ProjectMarshalException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
}
