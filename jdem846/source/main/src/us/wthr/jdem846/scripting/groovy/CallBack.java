package us.wthr.jdem846.scripting.groovy;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;

import java.lang.reflect.Method;

import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class CallBack
{
	private static Log log = Logging.getLog(CallBack.class);
	
	private enum CallBackType {
		NA,
		Method,
		Closure
	}
	
	private Closure closure = null;
	private Method method = null;
	private CallBackType callbackType;
	private GroovyObject groovyObject;
	
	
	
	public CallBack(GroovyObject groovyObject, String methodName) throws ScriptingException
	{
		this(groovyObject, getCallBack(groovyObject, methodName));
	}
	
	public CallBack(GroovyObject groovyObject, Object callback) throws ScriptingException
	{
		this(groovyObject, callback, getCallBackType(callback));
	}
	
	public CallBack(GroovyObject groovyObject, Object callback, CallBackType callbackType) throws ScriptingException
	{
		this.groovyObject = groovyObject;
		this.callbackType = callbackType;
		if (callbackType == CallBackType.Closure) {
			closure = (Closure) callback;
		} else if (callbackType == CallBackType.Method) {
			method = (Method) callback;
		}
	}

	public boolean isValid()
	{
		if (closure != null && callbackType == CallBackType.Closure) {
			return true;
		} else if (method != null && callbackType == CallBackType.Method) {
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
		try {
			if (callbackType == CallBackType.Closure) {
				return closure.call(args);
			} else if (callbackType == CallBackType.Method) {
				return method.invoke(groovyObject, args);
			} else {
				return null;
			}
		} catch (Exception ex) {
			throw new ScriptingException("Error invoking callback: " + ex.getMessage(), ex);
		}
	}
	
	protected static CallBackType getCallBackType(Object m) throws ScriptingException
	{		
		if (m == null) {
			return CallBackType.NA;
		}
		
		if (m instanceof Closure) {
			return CallBackType.Closure;
		} else if (m instanceof Method) {
			return CallBackType.Method;
		} else {
			return CallBackType.NA;
		}
		
		
	}
	
	
	protected static boolean hasMethod(GroovyObject groovyObject, String methodName)
	{
		if (getMethod(groovyObject, methodName) == null) {
			return false;
		} else {
			return true;
		}
	}
	
	protected static Method getMethod(GroovyObject groovyObject, String methodName)
	{
		for (Method method : groovyObject.getClass().getMethods()) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		return null;
	}
	
	protected static Object getCallBack(GroovyObject groovyObject, String methodName) throws ScriptingException
	{
		if (!hasMethod(groovyObject, methodName)) {
			return null;
		}
		Method get = getMethod(groovyObject, methodName);
		
		if (get == null) {
			return null;
		}
		
		Object m;
		try {
			m = get.invoke(groovyObject);
		} catch (Exception ex) {
			throw new ScriptingException("Error fetching method from script: " + ex.getMessage(), ex);
		} 
		
		return m;
	}
}
