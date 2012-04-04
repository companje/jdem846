package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


public class AitoffProjection extends AbstractBaseProjection
{
	
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(AitoffProjection.class);


	public AitoffProjection()
	{
		
	}
	
	public AitoffProjection(double north, double south, double east, double west, double width, double height)
	{
		super(north, south, east, west, width, height);
	}

	@Override
	public void project(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{
		if (latitude == 0.0 && longitude == 0.0) {
			point.column = getWidth() / 2.0;
			point.row = getHeight() / 2.0;
			return;
		}
		
		double a = Math.acos(Math.cos(latitude) * Math.cos(longitude / 2.0));
		double sinca = (a == 0) ? 0 : (Math.sin(a) / a);
		
		point.column = 2.0 * Math.cos(latitude) * Math.sin(longitude / 2.0) / sinca;
		point.row = Math.sin(latitude) / sinca;
	}
	
	/*
	@Override
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{
		if (latitude == 0.0 && longitude == 0.0) {
			point.column = getWidth() / 2.0;
			point.row = getHeight() / 2.0;
			return;
		}
		
		
		
		latitude = Math.toRadians(latitude);
		longitude = Math.toRadians(longitude);
		
		//double o1 = Math.acos(Math.toRadians(2 / Math.PI));
		double a = Math.acos(Math.cos(latitude) * Math.cos(longitude / 2.0));
		
		
		double sinca = (a == 0) ? 0 : (Math.sin(a) / a);
		
		double x = 2.0 * Math.cos(latitude) * Math.sin(longitude / 2.0) / sinca;
		double y = Math.sin(latitude) / sinca;
		
		x = Math.toDegrees(x);
		y = Math.toDegrees(y);
		
		point.column = x;
		point.row = y;
		
	}
	*/
	

	
	
	
}
