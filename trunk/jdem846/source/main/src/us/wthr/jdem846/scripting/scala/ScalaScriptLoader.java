package us.wthr.jdem846.scripting.scala;

import us.wthr.jdem846.exception.ScriptCompilationFailedException;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.scripting.ScriptProxy;

public class ScalaScriptLoader
{
	private static Log log = Logging.getLog(ScalaScriptLoader.class);
	
	public static ScriptProxy parseScript(String scriptContent) throws ScriptingException, ScriptCompilationFailedException
	{
		log.info("Compiling Scala script...");
		
		EmbeddedScalaInterpreter interpreter = new EmbeddedScalaInterpreter();
		
		
		Class cls = null;
		
		try {
			cls = (Class) interpreter.build(scriptContent, "JDemScript");
		} catch (Exception ex) {
			throw new ScriptCompilationFailedException(null, "Failed to compile scala script: " + ex.getMessage(), ex);
		}
		
		
		Object obj = null;
		
		try {
			obj = cls.newInstance();
		} catch (Exception ex) {
			throw new ScriptingException("Exception thrown when creating instance of script object: " + ex.getMessage(), ex);
		} 
		
		
		if (obj == null) {
			throw new ScriptingException("Scala script object is null!");
		}
		
		ScalaScriptProxy scriptProxy = new ScalaScriptProxy(obj);
		return scriptProxy;
		
	}
	
	
}
