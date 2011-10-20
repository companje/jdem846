package us.wthr.jdem846.scripting.groovy;

import groovy.lang.GroovyObject;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.render.DemCanvas;
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
	public void on2DModelBefore(ModelContext modelContext, DemCanvas tileCanvas)
	{
		invokeMethod("on2DModelBefore", modelContext, tileCanvas);
	}

	@Override
	public void on2DModelAfter(ModelContext modelContext, DemCanvas tileCanvas)
	{
		invokeMethod("on2DModelAfter", modelContext, tileCanvas);
	}

	@Override
	public void onTileBefore(ModelContext modelContext, DemCanvas tileCanvas)
	{
		invokeMethod("onTileBefore", modelContext, tileCanvas);
	}

	@Override
	public void onTileAfter(ModelContext modelContext, DemCanvas tileCanvas)
	{
		invokeMethod("onTileAfter", modelContext, tileCanvas);
	}

	@Override
	public Object onGetElevationBefore(ModelContext modelContext, int column, int row)
	{
		Object result = invokeMethod("onGetElevationBefore", modelContext, column, row);
		return result;
	}

	@Override
	public Object onGetElevationAfter(ModelContext modelContext, int column, int row, double elevation)
	{
		Object result = invokeMethod("onGetElevationAfter", modelContext, column, row, elevation);
		return result;
	}
	
	
	protected Object invokeMethod(String method, Object...args)
	{
		return groovyObject.invokeMethod(method, args);
	}
}
