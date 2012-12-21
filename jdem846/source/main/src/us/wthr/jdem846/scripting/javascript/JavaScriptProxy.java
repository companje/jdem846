package us.wthr.jdem846.scripting.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.graphics.IRenderer;
import us.wthr.jdem846.graphics.View;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.processing.util.LightingValues;
import us.wthr.jdem846.scripting.ScriptProxy;

public class JavaScriptProxy implements ScriptProxy
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(JavaScriptProxy.class);
	
	private Context cx;
	private Scriptable scope;
	
	private Function initializeFunction = null;
	private Function destroyFunction = null;
	private Function onProcessBeforeFunction = null;
	private Function onProcessAfterFunction = null;
	private Function onGetElevationBeforeFunction = null;
	private Function onGetElevationAfterFunction = null;
	private Function preRenderFunction = null;
	private Function postRenderFunction = null;
	private Function onGetPointColorFunction = null;
	private Function onLightLevelsFunction = null;
	

	
	public JavaScriptProxy(Scriptable scope) throws ScriptingException
	{
		this.cx = null;
		this.scope = scope;
		
		initializeFunction = getFunction("initialize");
		destroyFunction = getFunction("destroy");
		
		onProcessBeforeFunction = getFunction("onProcessBefore");
		onProcessAfterFunction = getFunction("onProcessAfter");
		onGetElevationBeforeFunction = getFunction("onGetElevationBefore");
		onGetElevationAfterFunction = getFunction("onGetElevationAfter");
		preRenderFunction = getFunction("preRender");
		postRenderFunction = getFunction("postRender");
		onGetPointColorFunction = getFunction("onGetPointColor");
		onLightLevelsFunction = getFunction("onLightLevels");
		
		setLog(Logging.getLog(JavaScriptProxy.class));
	}
	
	
	protected Context getContext() throws ScriptingException
	{
		if (cx == null) {
			if ((cx = Context.getCurrentContext()) == null) {
				cx = Context.enter();
			}
		} else {
			if (!cx.equals(Context.getCurrentContext())) {
				cx = Context.enter();
			}
		}

		return cx;
	}
	
	protected Function getFunction(String name) throws ScriptingException
	{
		Object function = scope.get(name, scope);
		if (!(function instanceof Function) || function == null) {
			throw new ScriptingException("Invalid function name or function not found");
		} else {
			Function f = (Function) function;
			return f;
		}
	}
	
	@Override
	public void setProperty(String name, Object value) throws ScriptingException
	{
		ScriptableObject.putProperty(scope, name, wrapJavaObject(value));
	}
	
	protected Object wrapJavaObject(Object value) throws ScriptingException
	{
		getContext();
		Object wrappedObject = Context.javaToJS(value, scope);
		return wrappedObject;
	}
	
	protected void setLog(Log log) throws ScriptingException
	{
		setProperty("log", log);
	}
	
	@Override
	public void setModelContext(ModelContext modelContext) throws ScriptingException
	{
		setProperty("modelContext", modelContext);
	}

	@Override
	public void initialize() throws ScriptingException
	{
		Object functionArgs[] = {};
		this.initializeFunction.call(getContext(), scope, scope, functionArgs);
	}

	@Override
	public void destroy() throws ScriptingException
	{
		Object functionArgs[] = {};
		this.destroyFunction.call(getContext(), scope, scope, functionArgs);
	}

	@Override
	public void onProcessBefore() throws ScriptingException
	{
		Object functionArgs[] = {};
		this.onProcessBeforeFunction.call(getContext(), scope, scope, functionArgs);
	}

	@Override
	public void onProcessAfter() throws ScriptingException
	{
		Object functionArgs[] = {};
		this.onProcessAfterFunction.call(getContext(), scope, scope, functionArgs);
	}

	@Override
	public Object onGetElevationBefore(double latitude, double longitude) throws ScriptingException
	{
		Object functionArgs[] = {latitude, longitude};
		return this.onGetElevationBeforeFunction.call(getContext(), scope, scope, functionArgs);
	}

	@Override
	public Object onGetElevationAfter(double latitude, double longitude, double elevation) throws ScriptingException
	{
		Object functionArgs[] = {latitude, longitude, elevation};
		return this.onGetElevationAfterFunction.call(getContext(), scope, scope, functionArgs);
	}

	@Override
	public void preRender(IRenderer renderer, View view) throws ScriptingException
	{
		Object functionArgs[] = {wrapJavaObject(renderer), wrapJavaObject(view)};
		this.preRenderFunction.call(getContext(), scope, scope, functionArgs);
	}

	@Override
	public void postRender(IRenderer renderer, View view) throws ScriptingException
	{
		Object functionArgs[] = {wrapJavaObject(renderer), wrapJavaObject(view)};
		this.postRenderFunction.call(getContext(), scope, scope, functionArgs);
	}

	@Override
	public void onGetPointColor(double latitude, double longitude, double elevation, double elevationMinimum, double elevationMaximum, int[] color) throws ScriptingException
	{
		Object functionArgs[] = {latitude, longitude, elevation, elevationMinimum, elevationMaximum, color};
		this.onGetPointColorFunction.call(getContext(), scope, scope, functionArgs);
	}

	@Override
	public void onLightLevels(double latitude, double longitude, double elevation, LightingValues lightingValues) throws ScriptingException
	{
		Object functionArgs[] = {latitude, longitude, elevation, wrapJavaObject(lightingValues)};
		this.onLightLevelsFunction.call(getContext(), scope, scope, functionArgs);
	}

}
