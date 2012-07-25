package us.wthr.jdem846.scripting.scala;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import us.wthr.jdem846.exception.ScriptingException;

public class ScalaCallBack
{
	private Method method = null;
	private Object scalaObject = null;
	
	public ScalaCallBack(Object scalaObject, Method method)
	{
		this.scalaObject = scalaObject;
		this.method = method;
	}
	
	
	public boolean isValid()
	{
		if (method != null && scalaObject != null) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public Object call() throws ScriptingException
	{
		return call(null);
	}
	
	public Object call(Object ... args) throws ScriptingException
	{
		if (!isValid()) {
			return null;
		}
		
		try {
			return this.method.invoke(this.scalaObject, args);
		} catch (Exception ex) {
			throw new ScriptingException("Error invoking callback: " + ex.getMessage(), ex);
		} 

	}
	
}
