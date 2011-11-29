package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.ModelContext;
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
	
	private static Log log = Logging.getLog(EquirectangularProjection.class);

	
	public EquirectangularProjection()
	{
		
	}
	
	public EquirectangularProjection(double north, double south, double east, double west, double width, double height)
	{
		super(north, south, east, west, width, height);
	}
	

	
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point)
	{
		point.row = latitudeToRow(latitude);
		point.column = longitudeToColumn(longitude);
	}
	
	

}
