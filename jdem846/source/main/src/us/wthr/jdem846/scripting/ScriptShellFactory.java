package us.wthr.jdem846.scripting;

import us.wthr.jdem846.exception.ScriptCompilationFailedException;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.scripting.groovy.GroovyScriptShell;

public class ScriptShellFactory
{
	private static Log log = Logging.getLog(ScriptShellFactory.class);
	
	public static ScriptShell getScriptShell(ScriptLanguageEnum language) throws ScriptingException
	{
		ScriptShell shell = null;
		
		if (language == ScriptLanguageEnum.GROOVY) {
			shell = new GroovyScriptShell();
		} else {
			throw new ScriptingException("Unsupported language: " + language.text());
		}
		
		return shell;
	}
	
}
