package us.wthr.jdem846.scripting.javascript;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.graphics.GraphicsRenderer;
import us.wthr.jdem846.graphics.View;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.processing.util.LightingValues;
import us.wthr.jdem846.scripting.ScriptProxy;

public class JavaScriptProxy implements ScriptProxy
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(JavaScriptProxy.class);
	
	@Override
	public void setProperty(String name, Object value) throws ScriptingException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setModelContext(ModelContext modelContext)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() throws ScriptingException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() throws ScriptingException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProcessBefore() throws ScriptingException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProcessAfter() throws ScriptingException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object onGetElevationBefore(double latitude, double longitude) throws ScriptingException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onGetElevationAfter(double latitude, double longitude, double elevation) throws ScriptingException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void preRender(GraphicsRenderer renderer, View view) throws ScriptingException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postRender(GraphicsRenderer renderer, View view) throws ScriptingException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPointColor(double latitude, double longitude, double elevation, double elevationMinimum, double elevationMaximum, int[] color) throws ScriptingException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLightLevels(double latitude, double longitude, double elevation, LightingValues lightingValues) throws ScriptingException
	{
		// TODO Auto-generated method stub
		
	}

}
