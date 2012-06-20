package us.wthr.jdem846.scripting.groovy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.codehaus.groovy.runtime.metaclass.ClosureMetaClass;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ModelProcessContainer;
import us.wthr.jdem846.model.processing.util.LightingValues;
import us.wthr.jdem846.scripting.ScriptProxy;

public class GroovyScriptProxy implements ScriptProxy
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(GroovyScriptProxy.class);
	
	
	private GroovyObject groovyObject;

	
	
	private CallBack initializeCallBack;
	private CallBack destroyCallBack;
	private CallBack onModelBeforeCallBack;
	private CallBack onModelAfterCallBack;
	private CallBack onProcessBeforeCallBack;
	private CallBack onProcessAfterCallBack;
	private CallBack onGetElevationBeforeCallBack;
	private CallBack onGetElevationAfterCallBack;
	private CallBack onGetPointColorCallBack;
	private CallBack onLightLevelsCallBack;
	
	private boolean hasModelContext = false;
	private boolean hasLog = false;
	
	
	public GroovyScriptProxy(GroovyObject groovyObject) throws ScriptingException
	{
		this.groovyObject = groovyObject;
		
		initializeCallBack = new CallBack(groovyObject, "getInitialize");
		destroyCallBack = new CallBack(groovyObject, "getDestroy");
		onModelBeforeCallBack = new CallBack(groovyObject, "getOnModelBefore");
		onModelAfterCallBack = new CallBack(groovyObject, "getOnModelAfter");
		onProcessBeforeCallBack = new CallBack(groovyObject, "getOnProcessBefore");
		onProcessAfterCallBack = new CallBack(groovyObject, "getOnProcessAfter");
		onGetElevationBeforeCallBack = new CallBack(groovyObject, "getOnGetElevationBefore");
		onGetElevationAfterCallBack = new CallBack(groovyObject, "getOnGetElevationAfter");
		onGetPointColorCallBack = new CallBack(groovyObject, "getOnGetPointColor");
		onLightLevelsCallBack = new CallBack(groovyObject, "getOnLightLevels");

		
		hasModelContext = CallBack.hasMethod(groovyObject, "getModelContext");
		hasLog = CallBack.hasMethod(groovyObject, "getLog");
		
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
	
	protected Object invokeMethod(String method, Object...args)
	{
		return groovyObject.invokeMethod(method, args);
	}
	

	

}
