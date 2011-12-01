package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class HammerProjection extends AbstractBaseProjection
{
	private static Log log = Logging.getLog(HammerProjection.class);
	
	
	public HammerProjection()
	{
		
	}
	
	public HammerProjection(double north, double south, double east, double west, double width, double height)
	{
		super(north, south, east, west, width, height);
	}

	
	
	@Override
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{
		latitude = Math.toRadians(latitude);
		longitude = Math.toRadians(longitude);
		
		double s = Math.sqrt(1.0 + Math.cos(latitude) * Math.cos(longitude / 2.0));
		double x = 2.0 * Math.sqrt(2.0) * Math.cos(latitude) * Math.sin(longitude / 2.0) / s;
		double y = Math.sqrt(2.0) * Math.sin(latitude) / s;
			    
			    
		x = Math.toDegrees(x);
		y = Math.toDegrees(y);
		
		point.column = longitudeToColumn(x);
		point.row = latitudeToRow(y);
	}
	
}
