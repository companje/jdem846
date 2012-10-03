package us.wthr.jdem846.scripting.scala;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.graphics.GraphicsRenderer;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ModelProcessContainer;
import us.wthr.jdem846.model.processing.util.LightingValues;
import us.wthr.jdem846.scripting.ScriptProxy;
import us.wthr.jdem846.scripting.groovy.CallBack;


public class ScalaScriptProxy implements ScriptProxy
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(ScalaScriptProxy.class);
	
	private Object scalaObject;
	
	
	private ScalaCallBack initializeCallBack;
	private ScalaCallBack destroyCallBack;
	private ScalaCallBack onModelBeforeCallBack;
	private ScalaCallBack onModelAfterCallBack;
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
		onModelBeforeCallBack = new ScalaCallBack(scalaObject, getMethod("onModelBefore"));
		onModelAfterCallBack = new ScalaCallBack(scalaObject, getMethod("onModelAfter"));
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
		/*
		if (hasLog) {
			Field f = null;
			
			try {
				f = getField("log");
				
				if (f.getType() != us.wthr.jdem846.logging.Log.class) {
					throw new ScriptingException("Field 'log' is of incorrect type");
				}
				
				f.set(this.scalaObject, log);
				
			} catch (Exception ex) {
				log.warn("Failed to set log: " + ex.getMessage(), ex);
			} 

		}
		*/
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
		/*
		if (hasModelContext) {
			Field f = null;
			
			try {
				f = getField("modelContext");
				
				if (f.getType() != us.wthr.jdem846.ModelContext.class) {
					throw new ScriptingException("Field 'modelContext' is of incorrect type");
				}
				
				f.set(this.scalaObject, modelContext);
				
			} catch (Exception ex) {
				log.warn("Failed to set model context: " + ex.getMessage(), ex);
			} 

		}
		*/
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
	public void onModelBefore() throws ScriptingException
	{
		onModelBeforeCallBack.call();
	}

	@Override
	public void onModelAfter() throws ScriptingException
	{
		onModelAfterCallBack.call();
	}

	@Override
	public void onProcessBefore(ModelProcessContainer modelProcessContainer) throws ScriptingException
	{
		onProcessBeforeCallBack.call(modelProcessContainer);
	}

	@Override
	public void onProcessAfter(ModelProcessContainer modelProcessContainer) throws ScriptingException
	{
		onProcessAfterCallBack.call(modelProcessContainer);
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
	public void onLightLevels(double latitude, double longitude, LightingValues lightingValues) throws ScriptingException
	{
		onLightLevelsCallBack.call(latitude, longitude, lightingValues);
	}
	
	public void preRender(GraphicsRenderer renderer) throws ScriptingException
	{
		preRenderCallBack.call(renderer);
	}
	
	public void postRender(GraphicsRenderer renderer) throws ScriptingException
	{
		postRenderCallBack.call(renderer);
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
