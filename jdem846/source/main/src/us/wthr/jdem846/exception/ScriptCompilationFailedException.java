package us.wthr.jdem846.exception;

import org.codehaus.groovy.control.ProcessingUnit;

@SuppressWarnings("serial")
public class ScriptCompilationFailedException extends Exception
{
	
	private ProcessingUnit unit;
	
	public ScriptCompilationFailedException(ProcessingUnit unit, String message, Throwable thrown)
	{
		super(message, thrown);
		this.unit = unit;
	}
	
	public ProcessingUnit getUnit()
	{
		return unit;
	}
	
}
