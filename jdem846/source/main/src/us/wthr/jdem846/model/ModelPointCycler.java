package us.wthr.jdem846.model;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.InterruptibleProcess;

public class ModelPointCycler extends InterruptibleProcess 
{
	private static Log log = Logging.getLog(ModelPointCycler.class);
	
	private double latitudeResolution;
	private double longitudeResolution;
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	public ModelPointCycler(ModelContext modelContext)
	{
		latitudeResolution = modelContext.getModelDimensions().getOutputLatitudeResolution();
		longitudeResolution = modelContext.getModelDimensions().getOutputLongitudeResolution();
		
		north = modelContext.getNorth();
		south = modelContext.getSouth();
		east = modelContext.getEast();
		west = modelContext.getWest();
		
		
		
	}
	
	

	public void forEachModelPoint(ModelPointHandler pointHandler) throws RenderEngineException
	{
		double maxLon = east;
		double minLat = south + latitudeResolution;
		
		int pointsProcessed = 0;
		
		for (double lat = north; lat > minLat; lat-=latitudeResolution) {

			pointHandler.onModelLatitudeStart(lat);
			

			for (double lon = west; lon <= maxLon; lon+=longitudeResolution) {
				
				if (lon > east)
					lon = east;
				
				
				pointHandler.onModelPoint(lat, lon);
				
				pointsProcessed++;
				
				this.checkPause();
				if (this.isCancelled()) {
					log.info("Model point cycler cancelled after " + pointsProcessed + " points were processed.");
					return;
				}
				
				if (lon == east)
					break;
			}
			
			
			pointHandler.onModelLatitudeEnd(lat);
		}

		log.info("Processed " + pointsProcessed + " points");
	}
	
}
