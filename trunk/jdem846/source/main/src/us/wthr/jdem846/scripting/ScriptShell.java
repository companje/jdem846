package us.wthr.jdem846.scripting;

import us.wthr.jdem846.exception.ScriptCompilationFailedException;
import us.wthr.jdem846.exception.ScriptingException;

public interface ScriptShell
{
	
	public void evaluate(String code)  throws  ScriptingException, ScriptCompilationFailedException;
	
}
