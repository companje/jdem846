package us.wthr.jdem846.scripting.groovy;

import groovy.lang.GroovyObject;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.model.ModelProcessContainer;
import us.wthr.jdem846.scripting.ScriptProxy;

public class GroovyScriptProxy implements ScriptProxy
{
	
	private GroovyObject groovyObject;
	
	public GroovyScriptProxy(GroovyObject groovyObject)
	{
		this.groovyObject = groovyObject;
	}

	@Override
	public void initialize(ModelContext modelContext)
	{
		invokeMethod("initialize", modelContext);
	}

	@Override
	public void destroy(ModelContext modelContext)
	{
		invokeMethod("destroy", modelContext);
	}

	@Override
	public void onModelBefore(ModelContext modelContext)
	{
		invokeMethod("onModelBefore", modelContext);
	}

	@Override
	public void onModelAfter(ModelContext modelContext)
	{
		invokeMethod("onModelAfter", modelContext);
	}

	@Override
	public void onProcessBefore(ModelContext modelContext, ModelProcessContainer modelProcessContainer)
	{
		invokeMethod("onProcessBefore", modelContext, modelProcessContainer);
	}

	@Override
	public void onProcessAfter(ModelContext modelContext, ModelProcessContainer modelProcessContainer)
	{
		invokeMethod("onProcessAfter", modelContext, modelProcessContainer);
	}

	@Override
	public Object onGetElevationBefore(ModelContext modelContext, double latitude, double longitude)
	{
		Object result = invokeMethod("onGetElevationBefore", modelContext, latitude, longitude);
		return result;
	}

	@Override
	public Object onGetElevationAfter(ModelContext modelContext, double latitude, double longitude, double elevation)
	{
		Object result = invokeMethod("onGetElevationAfter", modelContext, latitude, longitude, elevation);
		return result;
	}
	
	@Override
	public void onGetPointColor(ModelContext modelContext, double latitude, double longitude, double elevation, double elevationMinimum, double elevationMaximum, int[] color)
	{
		invokeMethod("onGetPointColor", modelContext, latitude, longitude, elevation, elevationMinimum, elevationMaximum, color);
	}
	
	
	protected Object invokeMethod(String method, Object...args)
	{
		return groovyObject.invokeMethod(method, args);
	}
}
