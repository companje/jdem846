package us.wthr.jdem846.project.context;

@SuppressWarnings("serial")
public class ProjectException extends Exception
{
	
	public ProjectException(String message)
	{
		super(message);
	}
	
	public ProjectException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
}
