package us.wthr.jdem846.exception;

@SuppressWarnings("serial")
public class ScriptingException extends Exception
{
	
	public ScriptingException()
	{
		
	}
	
	public ScriptingException(String message)
	{
		super(message);
	}
	
	public ScriptingException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
}
