package us.wthr.jdem846.scripting;

import us.wthr.jdem846.Context;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.exceptions.ContextPrepareException;

public class ScriptingContext implements Context
{
	
	private static Log log = Logging.getLog(ScriptingContext.class);
	
	private ScriptProxy scriptProxy = null;
	private String userScript = null;
	private ScriptLanguageEnum scriptLanguage = ScriptLanguageEnum.JAVASCRIPT;
	
	
	private boolean disposed = false;
	
	public ScriptingContext()
	{
		
	}


	@Override
	public void prepare() throws ContextPrepareException
	{
		
		if (userScript != null) {
			try {
				scriptProxy = ScriptProxyFactory.createScriptProxy(scriptLanguage, userScript);
			} catch (Exception ex) {
				log.warn("Error compiling script: " + ex.getMessage(), ex);
				throw new ContextPrepareException("Error compiling script: " + ex.getMessage(), ex);
			}
		}
	}

	
	
	public ScriptProxy getScriptProxy()
	{
		return scriptProxy;
	}


	public void setScriptProxy(ScriptProxy scriptProxy)
	{
		this.scriptProxy = scriptProxy;
	}


	public String getUserScript()
	{
		return userScript;
	}


	public void setUserScript(String userScript)
	{
		this.userScript = userScript;
	}


	public ScriptLanguageEnum getScriptLanguage()
	{
		return scriptLanguage;
	}


	public void setScriptLanguage(ScriptLanguageEnum scriptLanguage)
	{
		this.scriptLanguage = scriptLanguage;
	}


	@Override
	public void dispose() throws DataSourceException
	{
		
		
		disposed = true;
	}

	
	@Override
	public boolean isDisposed()
	{
		return disposed;
	}


	@Override
	public ScriptingContext copy() throws DataSourceException
	{
		ScriptingContext copy = new ScriptingContext();
		copy.scriptLanguage = this.scriptLanguage;
		copy.userScript = this.userScript;
		copy.scriptProxy = this.scriptProxy;
		return copy;
	}
	
	
	
	
}
