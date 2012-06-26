package us.wthr.jdem846.globe;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.geom.TriangleStrip;

public class LayerRenderThread extends Thread
{
	TriangleStrip strip;
	RenderMethod renderMethod;
	
	ModelContext modelContext;
	
	double north; 
	double south;
	double east;
	double west;
	double latitudeResolution;
	double longitudeResolution;
	
	StripRenderQueue stripQueue;
	
	private boolean completed = false;
	
	public LayerRenderThread(RenderMethod renderMethod, 
								double north, 
								double south,
								double east,
								double west,
								double latitudeResolution,
								double longitudeResolution,
								ModelContext modelContext,
								StripRenderQueue stripQueue)
	{
		this.renderMethod = renderMethod;
		this.strip = new TriangleStrip();
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		this.modelContext = modelContext;
		this.stripQueue = stripQueue;
	}
	
	public void setStripRenderQueue(StripRenderQueue stripQueue)
	{
		this.stripQueue = stripQueue;
	}
	
	public void run()
	{
		double maxLon = east;
        double minLat = south;
        
        ModelCanvas modelCanvas = null;
		try {
			modelCanvas = modelContext.getModelCanvas();
		} catch (ModelContextException ex) {
			ex.printStackTrace();
			return;
		}

        for (double lat = north; lat > minLat; lat-=latitudeResolution) {
        	
        	strip = new TriangleStrip();
        	
            for (double lon = west; lon < maxLon; lon+=longitudeResolution) {
            	renderMethod.renderPoint(lat, lon, strip);
            	renderMethod.renderPoint(lat-latitudeResolution, lon, strip);      
            }
            
            if (stripQueue != null) {
            	stripQueue.add(strip);
            } else {
	            synchronized(modelCanvas) {
	            	modelCanvas.fillShape(strip);
	            }
	            strip.reset();
            }
        }
        
        completed = true;
	}
	
	public boolean isCompleted()
	{
		return completed;
	}
}
