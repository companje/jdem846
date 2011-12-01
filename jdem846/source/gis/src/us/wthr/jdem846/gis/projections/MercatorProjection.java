package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


/** http://wiki.openstreetmap.org/wiki/Mercator
 * http://svn.osgeo.org/metacrs/proj/trunk/proj/src/PJ_merc.c
 * @author Kevin M. Gill
 *
 */
public class MercatorProjection extends AbstractBaseProjection
{
	private static Log log = Logging.getLog(MercatorProjection.class);
	
	private double minLatitude = -85;
	private double maxLatitude = 85;
	
	public MercatorProjection()
	{
		
		
		
	}
	
	public MercatorProjection(double north, double south, double east, double west, double width, double height)
	{
		super(north, south, east, west, width, height);
		setNorth(latitudeToY(Math.toRadians((north > maxLatitude) ? maxLatitude : north)));
		setSouth(latitudeToY(Math.toRadians((south < minLatitude) ? minLatitude : south)));
	}
	

	
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{

		double phi = Math.toRadians(latitude);
		point.column = longitudeToColumn(longitude);
		
		if (Math.abs(Math.abs(phi) - HALFPI) <= EPS10) {
			if (latitude > 0)
				point.row = -1;
			else
				point.row = getHeight() + 1;
			return;
		}

		point.row =  latitudeToRow(latitudeToY(phi));

	}
	
	
	protected double latitudeToY(double phi)
	{
		return Math.toDegrees(getScaleFactor() * Math.log(Math.tan(FORTPI + .5 * phi)));
	}

}
