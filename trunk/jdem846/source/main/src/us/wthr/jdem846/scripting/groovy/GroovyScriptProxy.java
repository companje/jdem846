package us.wthr.jdem846.scripting.groovy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.codehaus.groovy.runtime.metaclass.ClosureMetaClass;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.graphics.GraphicsRenderer;
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
	private CallBack onProcessBeforeCallBack;
	private CallBack onProcessAfterCallBack;
	private CallBack onGetElevationBeforeCallBack;
	private CallBack onGetElevationAfterCallBack;
	private CallBack onGetPointColorCallBack;
	private CallBack onLightLevelsCallBack;
	private CallBack preRenderCallBack;
	private CallBack postRenderCallBack;
	
	private boolean hasModelContext = false;
	private boolean hasLog = false;
	
	
	public GroovyScriptProxy(GroovyObject groovyObject) throws ScriptingException
	{
		this.groovyObject = groovyObject;
		
		initializeCallBack = new CallBack(groovyObject, "initialize");
		destroyCallBack = new CallBack(groovyObject, "destroy");
		onProcessBeforeCallBack = new CallBack(groovyObject, "onProcessBefore");
		onProcessAfterCallBack = new CallBack(groovyObject, "onProcessAfter");
		onGetElevationBeforeCallBack = new CallBack(groovyObject, "onGetElevationBefore");
		onGetElevationAfterCallBack = new CallBack(groovyObject, "onGetElevationAfter");
		onGetPointColorCallBack = new CallBack(groovyObject, "onGetPointColor");
		onLightLevelsCallBack = new CallBack(groovyObject, "onLightLevels");
		preRenderCallBack = new CallBack(groovyObject, "preRender");
		postRenderCallBack = new CallBack(groovyObject, "postRender");
		
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
	public void onLightLevels(double latitude, double longitude, LightingValues lightingValues) throws ScriptingException
	{
		onLightLevelsCallBack.call(latitude, longitude, lightingValues);
	}
	
	@Override
	public void preRender(GraphicsRenderer renderer) throws ScriptingException
	{
		preRenderCallBack.call(renderer);
	}
	
	@Override
	public void postRender(GraphicsRenderer renderer) throws ScriptingException
	{
		postRenderCallBack.call(renderer);
	}
	
	protected Object invokeMethod(String method, Object...args)
	{
		return groovyObject.invokeMethod(method, args);
	}
	

	

}
