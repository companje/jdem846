package us.wthr.jdem846.scripting.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

import us.wthr.jdem846.exception.ScriptCompilationFailedException;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.scripting.ScriptShell;

public class GroovyScriptShell implements ScriptShell
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(GroovyScriptShell.class);
	
	private CompilerConfiguration compiler;
	private GroovyShell shell;
	
	public GroovyScriptShell()
	{
		compiler = new CompilerConfiguration();
		shell = new GroovyShell(Thread.currentThread().getContextClassLoader(), new Binding(), compiler);
	}
	
	
	@Override
	public void evaluate(String code) throws  ScriptingException, ScriptCompilationFailedException
	{
		try {
			shell.evaluate(code);	
		} catch (CompilationFailedException ex) {
			throw new ScriptCompilationFailedException(ex.getUnit(), "Failed to compile Groovy script: " + ex.getMessage(), ex);
		} catch (Exception ex) {
			throw new ScriptingException("Exception in script: " + ex.getMessage(), ex);
		}
	}

}
