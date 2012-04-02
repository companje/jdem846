package us.wthr.jdem846.render.render3;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ModelPointCycler
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
	
	

	public void forEachModelPoint(boolean cached, ModelPointHandler pointHandler)
	{
		double maxLon = east + longitudeResolution;
		double minLat = south + latitudeResolution;
		
		int pointsProcessed = 0;
		
		for (double lat = north; lat > minLat; lat-=latitudeResolution) {

			pointHandler.onModelLatitudeStart(lat);
			
			for (double lon = west; lon <= maxLon; lon+=longitudeResolution) {
				
				if (lon > east)
					lon = east;
				
				
				pointHandler.onModelPoint(lat, lon);
				
				pointsProcessed++;
				
				if (lon == east)
					break;
			}
			
			
			pointHandler.onModelLatitudeEnd(lat);
		}
		
		log.info("Processed " + pointsProcessed + " points");
	}
	
}
