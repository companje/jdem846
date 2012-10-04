package us.wthr.jdem846.globe;


import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.geom.TriangleStrip;
import us.wthr.jdem846.graphics.GraphicsRenderer;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ModelProcessContainer;
import us.wthr.jdem846.model.processing.util.LightingValues;
import us.wthr.jdem846.scripting.ScriptProxy;

public class OutterGlobeScript //implements ScriptProxy
{
	/*
	private static Log log = Logging.getLog(OutterGlobeScript.class);
	
	ModelContext modelContext;
			
	private GlobeScript oceanGlobeScript;
	//private GlobeScript cloudGlobeScript;
	//private GlobeScript lowerAtmosphereGlobeScript;
	//private GlobeScript middleAtmosphereGlobeScript;
	//private GlobeScript upperAtmosphereGlobeScript;
	
	
	public OutterGlobeScript()
	{
		oceanGlobeScript = new GlobeScript(true, true, true, true, true);
		//cloudGlobeScript = new GlobeScript(false, true, false, false, false);
		//lowerAtmosphereGlobeScript = new GlobeScript(false, false, true, false, false);
		//middleAtmosphereGlobeScript = new GlobeScript(false, false, false, true, false);
		//upperAtmosphereGlobeScript = new GlobeScript(false, false, false, false, true);
		
	}


	@Override
	public void setModelContext(ModelContext modelContext)
	{
		this.modelContext = modelContext;
		oceanGlobeScript.setModelContext(modelContext);
		//cloudGlobeScript.setModelContext(modelContext);
		//lowerAtmosphereGlobeScript.setModelContext(modelContext);
		//middleAtmosphereGlobeScript.setModelContext(modelContext);
		//upperAtmosphereGlobeScript.setModelContext(modelContext);
	}


	@Override
	public void initialize() throws ScriptingException
	{
		oceanGlobeScript.initialize();
		//cloudGlobeScript.initialize();
		//lowerAtmosphereGlobeScript.initialize();
		//middleAtmosphereGlobeScript.initialize();
		//upperAtmosphereGlobeScript.initialize();
	}


	@Override
	public void destroy() throws ScriptingException
	{
		oceanGlobeScript.destroy();
		//cloudGlobeScript.destroy();
		//lowerAtmosphereGlobeScript.destroy();
		//middleAtmosphereGlobeScript.destroy();
		//upperAtmosphereGlobeScript.destroy();
	}

	public void preRender(GraphicsRenderer renderer) throws ScriptingException
	{
		
	}
	
	public void postRender(GraphicsRenderer renderer) throws ScriptingException
	{
		
	}
	


	
	public void onProcessAfter(ModelProcessContainer modelProcessContainer)
			throws ScriptingException
	{
		//oceanGlobeScript.onProcessAfter(modelProcessContainer);
		//cloudGlobeScript.onProcessAfter(modelProcessContainer);
		//lowerAtmosphereGlobeScript.onProcessAfter(modelProcessContainer);
		//middleAtmosphereGlobeScript.onProcessAfter(modelProcessContainer);
		//upperAtmosphereGlobeScript.onProcessAfter(modelProcessContainer);
		
		if (!modelProcessContainer.getProcessId().equals("us.wthr.jdem846.model.processing.render.ModelRenderer")) {
            return;
        }
		
		
		StripRenderQueue stripQueue = null;
		try {
			stripQueue = new StripRenderQueue(modelContext.getModelCanvas());
			stripQueue.start();
		} catch (ModelContextException ex) {
			log.warn("Error fetching model canvas: " + ex.getMessage(), ex);
		}
		
		oceanGlobeScript.setStripQueue(stripQueue);
		//cloudGlobeScript.setStripQueue(stripQueue);
		//lowerAtmosphereGlobeScript.setStripQueue(stripQueue);
		//middleAtmosphereGlobeScript.setStripQueue(stripQueue);
		//upperAtmosphereGlobeScript.setStripQueue(stripQueue);
		
		
		oceanGlobeScript.onProcessAfter(modelProcessContainer);
		//cloudGlobeScript.onProcessAfter(modelProcessContainer);
		//lowerAtmosphereGlobeScript.onProcessAfter(modelProcessContainer);
		//middleAtmosphereGlobeScript.onProcessAfter(modelProcessContainer);
		//upperAtmosphereGlobeScript.onProcessAfter(modelProcessContainer);
		

        log.info("Stopping triangle strip queue...");
        stripQueue.stopRendering();
        
        while(!stripQueue.isCompleted()) {
        	
        	try {
				Thread.sleep(500);
				Thread.yield();
			} catch (InterruptedException ez) {
				ez.printStackTrace();
			}
        	
        }
        
        log.info("Render queue completed");
	}


	@Override
	public Object onGetElevationBefore(double latitude, double longitude)
			throws ScriptingException
	{
		return oceanGlobeScript.onGetElevationBefore(latitude, longitude);
	}


	@Override
	public Object onGetElevationAfter(double latitude, double longitude,
			double elevation) throws ScriptingException
	{
		return oceanGlobeScript.onGetElevationAfter(latitude, longitude, elevation);
	}


	@Override
	public void onGetPointColor(double latitude, double longitude,
			double elevation, double elevationMinimum, double elevationMaximum,
			int[] color) throws ScriptingException
	{
		oceanGlobeScript.onGetPointColor(latitude, longitude, elevation, elevationMinimum, elevationMaximum, color);
	}


	@Override
	public void onLightLevels(double latitude, double longitude,
			LightingValues lightingValues) throws ScriptingException
	{
		oceanGlobeScript.onLightLevels(latitude, longitude, lightingValues);
	}
	
	
	class OnProcessAfterThread extends Thread
	{
		
		GlobeScript globeScript;
		ModelProcessContainer modelProcessContainer;
		
		private boolean completed = false;
		
		public OnProcessAfterThread(GlobeScript globeScript, ModelProcessContainer modelProcessContainer)
		{
			this.globeScript = globeScript;
			this.modelProcessContainer = modelProcessContainer;
		}
		
		public void run()
		{
			globeScript.onProcessAfter(modelProcessContainer);
			completed = true;
		}
		
		public boolean isCompleted()
		{
			return completed;
		}
		
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
	
	*/
}
