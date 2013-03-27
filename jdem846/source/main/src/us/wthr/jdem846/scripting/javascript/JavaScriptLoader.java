package us.wthr.jdem846.scripting.javascript;


import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;

import us.wthr.jdem846.Activator;
import us.wthr.jdem846.exception.ScriptCompilationFailedException;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.scripting.ScriptProxy;

public class JavaScriptLoader
{
	public static ScriptProxy parseScript(String scriptContent) throws ScriptingException, ScriptCompilationFailedException
	{
		
		Context cx = Context.enter();
		cx.setApplicationClassLoader(Activator.class.getClassLoader());
		Scriptable scope = new ImporterTopLevel(cx);
		Object result = cx.evaluateString(scope, scriptContent, "<cmd>", 1, null);
		return new JavaScriptProxy(scope);
	}
}
