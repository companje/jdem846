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
	
	public ModelPointCycler(ModelContext modelContext, ModelGridDimensions modelDimensions)
	{
		latitudeResolution = modelDimensions.getTextureLatitudeResolution();
		longitudeResolution = modelDimensions.getTextureLongitudeResolution();
		
		north = modelContext.getModelProcessManifest().getGlobalOptionModel().getNorthLimit();
		south = modelContext.getModelProcessManifest().getGlobalOptionModel().getSouthLimit();
		east = modelContext.getModelProcessManifest().getGlobalOptionModel().getEastLimit();
		west = modelContext.getModelProcessManifest().getGlobalOptionModel().getWestLimit();
		
		
		
	}
	
	

	public void forEachModelPoint(ModelPointHandler pointHandler) throws RenderEngineException
	{
		double maxLon = east;
		double minLat = south;
		
		int pointsProcessed = 0;
		
		pointHandler.onCycleStart();
		
		for (double lat = north; lat > minLat; lat-=latitudeResolution) {

			pointHandler.onModelLatitudeStart(lat);
			

			for (double lon = west; lon < maxLon; lon+=longitudeResolution) {
				
				//if (lon > east) {
				//	break;
				//}
				//	lon = east;
				
				
				pointHandler.onModelPoint(lat, lon);
				
				pointsProcessed++;
				
				this.checkPause();
				if (this.isCancelled()) {
					log.info("Model point cycler cancelled after " + pointsProcessed + " points were processed.");
					return;
				}
				
				//if (lon == east)
				//	break;
			}
			
			
			pointHandler.onModelLatitudeEnd(lat);
		}
		
		pointHandler.onCycleEnd();
		
		log.info("Processed " + pointsProcessed + " points");
	}
	
}
