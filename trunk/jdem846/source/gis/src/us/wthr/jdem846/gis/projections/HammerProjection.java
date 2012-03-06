package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.gis.Location;
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
	public void setUp(Location northWest, Location northEast, Location southWest, Location southEast, double width, double height)
	{
		super.setUp(northWest, northEast, southWest, southEast, width, height);
		
		
		//equator = (getNorth() + getSouth()) / 2.0;
		
	}
	
	//@Override
	//public void getPoint(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	//{
		
		//double east = getEast();
		//double west = getWest();
		//double north = getNorth();
		//double south = getSouth();
		
		
		//double a_longitude = (((longitude - west) / (east - west)) * 360.0) - 180.0;
		//double a_latitude = ((1.0 - (north - latitude) / (north - south)) * 180.0) - 90.0;
		
		
		//double a_longitude = (360.0 * ((longitude - west) / (east - west))) + 180.0;
		//double a_latitude = (180.0 * ((north - latitude) / (north - south))) + 90.0;
		/*
		double a_longitude = longitude - getMeridian();
		double a_latitude = latitude;// - equator;
		
		double r_latitude = Math.toRadians(a_latitude);
		double r_longitude = Math.toRadians(a_longitude);
		*/
	
	@Override
	public void project(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{
		
		double s = Math.sqrt(1.0 + Math.cos(latitude) * Math.cos(longitude / 2.0));
		double x = 2.0 * Math.sqrt(2.0) * Math.cos(latitude) * Math.sin(longitude / 2.0) / s;
		double y = Math.sqrt(2.0) * Math.sin(latitude) / s;
			    
		point.column = x;
		point.row = y;
		
		
		
	}
	
}
