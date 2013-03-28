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
	
	private Closure<?> closure = null;
	private Method method = null;
	private CallBackType callbackType;
	private GroovyObject groovyObject;
	
	
	
	public CallBack(GroovyObject groovyObject, String methodName) throws ScriptingException
	{
		this(groovyObject, methodName, getCallBackType(groovyObject, methodName));

	}
	
	public CallBack(GroovyObject groovyObject, String methodName, CallBackType callbackType) throws ScriptingException
	{
		
		this.groovyObject = groovyObject;
		this.callbackType = callbackType;
		
		if (callbackType == CallBackType.Closure) {
			closure = getClosure(groovyObject, methodName);
			log.info("CLOSURE: " + methodName + ", " + closure);
		} else if (callbackType == CallBackType.Method) {
			method = getMethod(groovyObject, methodName);
			log.info("METHOD: " + methodName + ", " + method);
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
		return call((Object[])null);
	}
	
	public Object call(Object ... args) throws ScriptingException
	{
		if (!isValid()) {
			return null;
		}
		
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
	
	protected static CallBackType getCallBackType(GroovyObject groovyObject, String methodName) throws ScriptingException
	{	
		
		if (hasMethod(groovyObject, methodName)) {
			return CallBackType.Method;
		} else if (hasMethod(groovyObject, methodNameToCallbackGetter(methodName))) {
			return CallBackType.Closure;
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
	
	protected static Closure<?> getClosure(GroovyObject groovyObject, String methodName) throws ScriptingException
	{
		String closureName = methodNameToCallbackGetter(methodName);
		Method m = null;
		Closure<?> c = null;
		
		for (Method method : groovyObject.getClass().getMethods()) {
			if (method.getName().equals(closureName)) {
				m = method;
				break;
			}
		}
		
		if (m != null) {
			try {
				Object o = m.invoke(groovyObject);
				c = (Closure<?>) o;
			} catch (Exception ex) {
				throw new ScriptingException("Error fetching closure " + methodName + ": " + ex.getMessage(), ex);
			}
		}
		
		return c;
	}
	

	protected static String methodNameToCallbackGetter(String methodName)
	{
		
		String s = "get" + methodName.toUpperCase().charAt(0) + methodName.substring(1);
		log.info("Method Name " + methodName + " to closure getter: " + s);
		return s;
	}
}
