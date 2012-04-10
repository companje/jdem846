package us.wthr.jdem846.model.exceptions;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class InvalidProcessOptionException extends Exception
{
	
	private Method method;
	
	public InvalidProcessOptionException()
	{
		super();
	}
	
	public InvalidProcessOptionException(String message)
	{
		super(message);
	}
	
	public InvalidProcessOptionException(String message, Method method)
	{
		super(message);
		this.method = method;
	}
	
	public InvalidProcessOptionException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
	public InvalidProcessOptionException(String message, Throwable thrown, Method method)
	{
		super(message, thrown);
		this.method = method;
	}
	
	public Method getMethod()
	{
		return method;
	}
	
}
