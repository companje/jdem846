package us.wthr.jdem846.gis.projections;

import java.awt.geom.Point2D;

import us.wthr.jdem846.gis.Location;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;


/** http://wiki.openstreetmap.org/wiki/Mercator
 * http://svn.osgeo.org/metacrs/proj/trunk/proj/src/PJ_merc.c
 * @author Kevin M. Gill
 *
 */
public class MercatorProjection extends AbstractBaseProjection
{
	private static Log log = Logging.getLog(MercatorProjection.class);
	
	private double minLatitude = MathExt.radians(-85);
	private double maxLatitude = MathExt.radians(85);
	

	private boolean spheriod = true;
	
	public MercatorProjection()
	{
		
		
		
	}
	
	public MercatorProjection(double north, double south, double east, double west, double width, double height)
	{
		super(north, south, east, west, width, height);
	}
	

	@Override
	public void project(double phi, double lam, double elevation, MapPoint point) throws MapProjectionException
	{
		double e = (getPlanet() == null) ? 0.01671123 : getPlanet().getEccentricity();
		
		point.column = getScaleFactor() * lam;
		
		if (phi < minLatitude) {
			point.row = minLatitude;
		} else if (phi > maxLatitude) {
			point.row = maxLatitude;
		} else if (spheriod) {
			if (Math.abs(Math.abs(phi) - HALFPI) <= EPS10) {
				throw new MapProjectionException("Invalid coordinates for Mercator projection");
			}
			
			point.row = getScaleFactor() * MathExt.log(MathExt.tan(FORTPI + 0.5 * phi));
		} else { // Ellipsoid
			if (Math.abs(Math.abs(phi) - HALFPI) <= EPS10) {
				throw new MapProjectionException("Invalid coordinates for Mercator projection");
			}
			
			point.column = getScaleFactor() * lam;
			point.row = -getScaleFactor() * MathExt.log(this.tsfn(phi, MathExt.sin(phi), e));
		}
		
	}

	public boolean isSpheriod()
	{
		return spheriod;
	}

	public void setSpheriod(boolean spheriod)
	{
		this.spheriod = spheriod;
	}
	
	

}
