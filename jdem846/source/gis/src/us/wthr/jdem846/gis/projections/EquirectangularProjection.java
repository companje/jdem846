package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.gis.Location;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Implements a simple map projection to generate row/column coordinates that, for a global
 * dataset, would produce a rectangle that has a width:height ratio of 2:1. This will
 * serve as the default projection.
 * 
 * @author Kevin M. Gill
 * @see http://en.wikipedia.org/wiki/Equirectangular_projection
 */
public class EquirectangularProjection extends AbstractBaseProjection
{
	
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(EquirectangularProjection.class);

	
	public EquirectangularProjection()
	{
		
	}
	
	public EquirectangularProjection(double north, double south, double east, double west, double width, double height)
	{
		super(north, south, east, west, width, height);
	}
	
	public EquirectangularProjection(double northWestLatitude,
			double northWestLongitude,
			double northEastLatitude,
			double northEastLongitude,
			double southWestLatitude,
			double southWestLongitude,
			double southEastLatitude,
			double southEastLongitude, 
			double width, 
			double height)
	{
		super(northWestLatitude,
				northWestLongitude,
				northEastLatitude,
				northEastLongitude,
				southWestLatitude,
				southWestLongitude,
				southEastLatitude,
				southEastLongitude, 
				width, 
				height);
	}
	
	public EquirectangularProjection(Location northWest, Location northEast, Location southWest, Location southEast, double width, double height)
	{
		super(northWest, northEast, southWest, southEast, width, height);
	}

	@Override
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{
		point.row = latitude;
		point.column = longitude;
	}
	
	@Override
	public void project(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{
		// Does nothing.
	}
	

}
