package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Implements the Mollweide pseudocylindrical map projection.
 * 
 * @author Kevin M. Gill
 * @see http://en.wikipedia.org/wiki/Mollweide_projection
 */
public class MollweideProjection extends AbstractBaseProjection
{
	private static Log log = Logging.getLog(MollweideProjection.class);
	

	
	public MollweideProjection()
	{
		
	}
	
	public MollweideProjection(double north, double south, double east, double west, double width, double height)
	{
		super(north, south, east, west, width, height);
	}

	@Override
	public void project(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{
		double x1 = latitude;
		double x2 = 0;
		double theta = 0;
		
		if (latitude == Math.PI / 2.0) {
			theta = Math.PI / 2.0;
		} else if (latitude == -Math.PI / 2.0) {
			theta = -Math.PI / 2.0;
		} else {
			while(true) {
				x2 = x1 - ((2.0 * x1 + Math.sin(2.0 * x1) - Math.PI * Math.sin(latitude)) / (2.0 + 2.0 * Math.cos(2.0 * x1)));
				
				if (Math.abs(x2 - x1) < 0.001) {
					break;
				} else {
					x1 = x2;
				}
			}
			theta = x2;
		}
		
		double x = ((2 * Math.sqrt(2)) / Math.PI) * longitude * Math.cos(theta);
		double y = Math.sqrt(2) * Math.sin(theta);

		point.column = x;
		point.row = y;
	}
	
	/*
	@Override
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{
		latitude = Math.toRadians(latitude);
		longitude = Math.toRadians(longitude);
		
		double x1 = latitude;
		double x2 = 0;
		double theta = 0;
		
		if (latitude == Math.PI / 2.0) {
			theta = Math.PI / 2.0;
		} else if (latitude == -Math.PI / 2.0) {
			theta = -Math.PI / 2.0;
		} else {
			while(true) {
				x2 = x1 - ((2.0 * x1 + Math.sin(2.0 * x1) - Math.PI * Math.sin(latitude)) / (2.0 + 2.0 * Math.cos(2.0 * x1)));
				
				if (Math.abs(x2 - x1) < 0.001) {
					break;
				} else {
					x1 = x2;
				}
			}
			theta = x2;
		}
		
		double x = ((2 * Math.sqrt(2)) / Math.PI) * longitude * Math.cos(theta);
		double y = Math.sqrt(2) * Math.sin(theta);
		
		
		x = Math.toDegrees(x);
		y = Math.toDegrees(y);
		
		point.column = x;
		point.row = y;
	}
	*/
	

}
