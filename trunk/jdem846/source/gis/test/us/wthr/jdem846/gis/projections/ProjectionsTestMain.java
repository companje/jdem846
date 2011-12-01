package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ProjectionsTestMain extends AbstractTestMain
{
private static Log log = null;
	
	public static void main(String[] args)
	{
		try {
			initialize(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		double north =90;
		double south = -90;
		double east = 180;
		double west = -180;
		
		//MapProjection projection = new MercatorProjection(north, south, 180, -180, 600, 600);
		MapProjection projection = new TransverseMercatorProjection(north, south, 180, -180, 600, 600);
		log = Logging.getLog(ProjectionsTestMain.class);
		MapPoint point = new MapPoint();
		
		
		for (double lat = north; lat >= south; lat-=1.0) {
			for (double lon = west; lon <= east; lon+=1.0) {
				try {
					projection.getPoint(lat, lon, 0, point);
				} catch (MapProjectionException ex) {
					log.error("Failed to project coordinates " + lat + "/" + lon + ": " + ex.getMessage(), ex);
					return;
				}
				log.info("TEST** Lat/Lon: " + lat + "/" + lon + ", x/y: " + point.column + "/" + point.row);
			}
		}
	}
}
