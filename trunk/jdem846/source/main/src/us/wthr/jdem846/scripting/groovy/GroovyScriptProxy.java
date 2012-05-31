package us.wthr.jdem846.scripting.groovy;

import java.lang.reflect.Method;

import groovy.lang.GroovyObject;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ModelProcessContainer;
import us.wthr.jdem846.scripting.ScriptProxy;

public class GroovyScriptProxy implements ScriptProxy
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(GroovyScriptProxy.class);
	
	private GroovyObject groovyObject;

	
	private boolean hasInitialize = false;
	private boolean hasOnModelBefore = false;
	private boolean hasOnProcessBefore = false;
	private boolean hasOnProcessAfter = false;
	private boolean hasOnModelAfter = false;
	private boolean hasOnGetElevationBefore = false;
	private boolean hasOnGetElevationAfter = false;
	private boolean hasOnGetPointColor = false;
	private boolean hasDestroy = false;
	
	private boolean hasModelContext = false;
	private boolean hasLog = false;
	
	public GroovyScriptProxy(GroovyObject groovyObject)
	{
		this.groovyObject = groovyObject;
		
		hasInitialize = hasMethod("getInitialize");
		hasOnModelBefore = hasMethod("getOnModelBefore");
		hasOnProcessBefore = hasMethod("getOnProcessBefore");
		hasOnProcessAfter = hasMethod("getOnProcessAfter");
		hasOnModelAfter = hasMethod("getOnModelAfter");
		hasOnGetElevationBefore = hasMethod("getOnGetElevationBefore");
		hasOnGetElevationAfter = hasMethod("getOnGetElevationAfter");
		hasOnGetPointColor = hasMethod("getOnGetPointColor");
		hasDestroy = hasMethod("getDestroy");
		
		hasModelContext = hasMethod("getModelContext");
		hasLog = hasMethod("getLog");
		
		if (hasLog) {
			invokeMethod("setLog", Logging.getLog(groovyObject.getClass()));
		}
	}

	@Override
	public void setModelContext(ModelContext modelContext)
	{
		if (hasModelContext) {
			invokeMethod("setModelContext", modelContext);
		}
	}
	
	@Override
	public void initialize()
	{
		if (hasInitialize) {
			invokeMethod("initialize");
		}
	}

	@Override
	public void destroy()
	{
		if (hasDestroy) {
			invokeMethod("destroy");
		}
	}

	@Override
	public void onModelBefore()
	{
		if (hasOnModelBefore) {
			invokeMethod("onModelBefore");
		}
	}

	@Override
	public void onModelAfter()
	{
		if (hasOnModelAfter) {
			invokeMethod("onModelAfter");
		}
	}

	@Override
	public void onProcessBefore(ModelProcessContainer modelProcessContainer)
	{
		if (hasOnProcessBefore) {
			invokeMethod("onProcessBefore", modelProcessContainer);
		}
	}

	@Override
	public void onProcessAfter(ModelProcessContainer modelProcessContainer)
	{
		if (hasOnProcessAfter) {
			invokeMethod("onProcessAfter", modelProcessContainer);
		}
	}

	@Override
	public Object onGetElevationBefore(double latitude, double longitude)
	{
		
		Object result = null;
		if (hasOnGetElevationBefore) { 
			invokeMethod("onGetElevationBefore", latitude, longitude);
		}
		return result;
	}

	@Override
	public Object onGetElevationAfter(double latitude, double longitude, double elevation)
	{
		Object result = null;
		if (hasOnGetElevationAfter) {
			invokeMethod("onGetElevationAfter", latitude, longitude, elevation);
		}
		return result;
	}
	
	@Override
	public void onGetPointColor(double latitude, double longitude, double elevation, double elevationMinimum, double elevationMaximum, int[] color)
	{
		if (hasOnGetPointColor) {
			invokeMethod("onGetPointColor", latitude, longitude, elevation, elevationMinimum, elevationMaximum, color);
		}
	}
	
	
	protected Object invokeMethod(String method, Object...args)
	{
		return groovyObject.invokeMethod(method, args);
	}
	
	
	protected boolean hasMethod(String methodName)
	{
		if (getMethod(methodName) == null) {
			return false;
		} else {
			return true;
		}
	}
	
	protected Method getMethod(String methodName)
	{
		for (Method method : groovyObject.getClass().getMethods()) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		return null;
	}
}
