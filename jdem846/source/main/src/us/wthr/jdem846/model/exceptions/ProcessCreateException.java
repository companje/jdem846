package us.wthr.jdem846.model.exceptions;

@SuppressWarnings("serial")
public class ProcessCreateException extends Exception
{
	
	private String processId;
	
	public ProcessCreateException()
	{
		
	}
	
	public ProcessCreateException(String message)
	{
		super(message);
	}
	
	public ProcessCreateException(String message, String processId)
	{
		super(message);
		this.processId = processId;
	}
	
	public ProcessCreateException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
	public ProcessCreateException(String message, Throwable thrown, String processId)
	{
		super(message, thrown);
		this.processId = processId;
	}
	
	public String getProcessId()
	{
		return processId;
	}
	
}
