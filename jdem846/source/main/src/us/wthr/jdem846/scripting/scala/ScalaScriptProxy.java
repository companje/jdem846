package us.wthr.jdem846.scripting.scala;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.graphics.IRenderer;
import us.wthr.jdem846.graphics.View;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.processing.util.LightingValues;
import us.wthr.jdem846.scripting.ScriptProxy;


public class ScalaScriptProxy implements ScriptProxy
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(ScalaScriptProxy.class);
	
	private Object scalaObject;
	
	
	private ScalaCallBack initializeCallBack;
	private ScalaCallBack destroyCallBack;
	private ScalaCallBack onProcessBeforeCallBack;
	private ScalaCallBack onProcessAfterCallBack;
	private ScalaCallBack onGetElevationBeforeCallBack;
	private ScalaCallBack onGetElevationAfterCallBack;
	private ScalaCallBack onGetPointColorCallBack;
	private ScalaCallBack onLightLevelsCallBack;
	private ScalaCallBack preRenderCallBack;
	private ScalaCallBack postRenderCallBack;
	
	private ScalaCallBack setModelContextCallBack;
	private ScalaCallBack setLogCallBack;
	
	private boolean hasModelContext = false;
	private boolean hasLog = false;
	
	
	public ScalaScriptProxy(Object scalaObject)
	{
		this.scalaObject = scalaObject;
		
		
		initializeCallBack = new ScalaCallBack(scalaObject, getMethod("initialize"));
		destroyCallBack = new ScalaCallBack(scalaObject, getMethod("destroy"));
		onProcessBeforeCallBack = new ScalaCallBack(scalaObject, getMethod("onProcessBefore"));
		onProcessAfterCallBack = new ScalaCallBack(scalaObject, getMethod("onProcessAfter"));
		onGetElevationBeforeCallBack = new ScalaCallBack(scalaObject, getMethod("onGetElevationBefore"));
		onGetElevationAfterCallBack = new ScalaCallBack(scalaObject, getMethod("onGetElevationAfter"));
		onGetPointColorCallBack = new ScalaCallBack(scalaObject, getMethod("onGetPointColor"));
		onLightLevelsCallBack = new ScalaCallBack(scalaObject, getMethod("onLightLevels"));
		preRenderCallBack = new ScalaCallBack(scalaObject, getMethod("preRender"));
		postRenderCallBack = new ScalaCallBack(scalaObject, getMethod("postRender"));
		
		setModelContextCallBack = new ScalaCallBack(scalaObject, getMethod("setModelContext"));
		setLogCallBack = new ScalaCallBack(scalaObject, getMethod("setLog"));
		
		this.hasModelContext = hasField("modelContext");
		
		
		this.hasLog = hasField("log");
		setLog(Logging.getLog(scalaObject.getClass()));
		
	}
	
	public void setProperty(String name, Object value) throws ScriptingException
	{
		String methodName = name + "_$eq";
		if (hasMethod(methodName)) {
			log.info("Setting property value for field '" + name + "'");
			invokeMethod(methodName, value);
		}
	}
	

	protected void setLog(Log log)
	{
		if (setLogCallBack != null) {
			try {
				setLogCallBack.call(log);
			} catch (ScriptingException ex) {
				log.warn("Error calling method 'setLog': " + ex.getMessage(), ex);
			}
		} else {
			log.warn("Script does not have a 'setLog()' method");
		}

	}
	
	@Override
	public void setModelContext(ModelContext modelContext)
	{
		if (setModelContextCallBack != null) {
			try {
				setModelContextCallBack.call(modelContext);
			} catch (ScriptingException ex) {
				log.warn("Error calling method 'setModelContext': " + ex.getMessage(), ex);
			}
		} else {
			log.warn("Script does not have the method 'setModelContext(ModelContext)'");
		}

	}
	
	@Override
	public void initialize() throws ScriptingException
	{
		initializeCallBack.call();
	}

	@Override
	public void destroy() throws ScriptingException
	{
		destroyCallBack.call();
	}


	@Override
	public void onProcessBefore() throws ScriptingException
	{
		onProcessBeforeCallBack.call();
	}

	@Override
	public void onProcessAfter() throws ScriptingException
	{
		onProcessAfterCallBack.call();
	}

	@Override
	public Object onGetElevationBefore(double latitude, double longitude) throws ScriptingException
	{
		return onGetElevationBeforeCallBack.call(latitude, longitude);
	}

	@Override
	public Object onGetElevationAfter(double latitude, double longitude, double elevation) throws ScriptingException
	{
		return onGetElevationAfterCallBack.call(latitude, longitude, elevation);
	}
	
	@Override
	public void onGetPointColor(double latitude, double longitude, double elevation, double elevationMinimum, double elevationMaximum, int[] color) throws ScriptingException
	{
		onGetPointColorCallBack.call(latitude, longitude, elevation, elevationMinimum, elevationMaximum, color);
	}
	
	@Override
	public void onLightLevels(double latitude, double longitude, double elevation, LightingValues lightingValues) throws ScriptingException
	{
		onLightLevelsCallBack.call(latitude, longitude, elevation, lightingValues);
	}
	
	@Override
	public void preRender(IRenderer renderer, View view) throws ScriptingException
	{
		preRenderCallBack.call(renderer, view);
	}
	
	@Override
	public void postRender(IRenderer renderer, View view) throws ScriptingException
	{
		postRenderCallBack.call(renderer, view);
	}
	
	protected boolean hasField(String fieldName)
	{
		
		try {
			Field f = getField(fieldName);
			return (f != null);
		} catch (ScriptingException ex) {
			return false;
		}
		
	}
	
	protected Field getField(String fieldName) throws ScriptingException
	{
		Field field = null;
		try {
			field = this.scalaObject.getClass().getField(fieldName);
		} catch (Exception ex) {
			throw new ScriptingException("Field '" + fieldName + "' not found: " + ex.getMessage(), ex);
		} 
		return field;
	}
	
	protected boolean hasMethod(String methodName)
	{
		if (getMethod(methodName) != null) {
			return true;
		} else {
			return false;
		}
	}
	
	protected Method getMethod(String methodName)
	{
		for (Method method : this.scalaObject.getClass().getMethods()) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		return null;
	}
	
	protected Object invokeMethod(String methodName, Object...args) throws ScriptingException
	{
		Method method = this.getMethod(methodName);
		if (method != null) {
			try {
				return method.invoke(this.scalaObject, args);
			} catch (Exception ex) {
				throw new ScriptingException("Error invoking method '" + methodName + "': " + ex.getMessage(), ex);
			}
		} else {
			throw new ScriptingException("Method '" + methodName + "' not found");
		}
		
	}
}
