package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;

/** http://svn.osgeo.org/metacrs/proj/trunk/proj/src/PJ_tmerc.c
 * NOTE: Not even close to being correct.
 * 
 * @author Kevin M. Gill
 *
 */
public class TransverseMercatorProjection extends AbstractBaseProjection
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(TransverseMercatorProjection.class);
	
	private final static double FC1 = 1.0;
	private final static double FC2 = 0.5;
	private final static double FC3 = 0.16666666666666666666;
	private final static double FC4 = 0.08333333333333333333;
	private final static double FC5 = 0.05;
	private final static double FC6 = 0.03333333333333333333;
	private final static double FC7 = 0.02380952380952380952;
	private final static double FC8 = 0.01785714285714285714;
	
	private double esp;
	private double ml0;
	private double[] en;
	
	private double e = 0;
	private double es = 0;
	
	private boolean spherical = true;
	
	public TransverseMercatorProjection()
	{
		
	}
	
	public TransverseMercatorProjection(double north, double south, double east, double west, double width, double height)
	{
		super(north, south, east, west, width, height);
		init();
	}
	
	protected void init()
	{
		if (spherical) {
			esp = getScaleFactor();
			ml0 = .5 * esp;
		} else {
			en = MathExt.enfn(es);
			ml0 = MathExt.mlfn(0, MathExt.sin(0), MathExt.cos(0), en);
			esp = es / (1. - es);
		}
	}

	
	@Override
	public void project(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{
		double phi = latitude;
		double lam = longitude;
		
		if (lam < -MathExt.HALFPI || lam > MathExt.HALFPI) {
			point.row = 0;
			point.column = 0;
			return;
		}
		
		if (spherical) {
			getPointSpherical(phi, lam, elevation, point);
		} else {
			getPointEllipse(phi, lam, elevation, point);
		}
		
		
	}
	
	/*
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{
		double phi = Math.toRadians(latitude);
		double lam = Math.toRadians(longitude);
		
		if (lam < -HALFPI || lam > HALFPI) {
			point.row = 0;
			point.column = 0;
			return;
		}
		
		if (spherical) {
			getPointSpherical(phi, lam, elevation, point);
		} else {
			getPointEllipse(phi, lam, elevation, point);
		}
		
		
	}
	*/
	
	public void getPointEllipse(double phi, double lam, double elevation, MapPoint point) throws MapProjectionException
	{
		double sinphi = MathExt.sin(phi); 
		double cosphi = MathExt.cos(phi);
		
		double t = MathExt.abs(cosphi) > 1e-10 ? sinphi/cosphi : 0.;
		t *= t;
		
		double al = cosphi * lam;
		double als = al * al;
		al /= MathExt.sqrt(1. - es * sinphi * sinphi);
		
		double n = esp * cosphi * cosphi;
		
		double x = getScaleFactor() * al * (FC1 +
			FC3 * als * (1. - t + n +
			FC5 * als * (5. + t * (t - 18.) + n * (14. - 58. * t)
			+ FC7 * als * (61. + t * ( t * (179. - t) - 479. ) )
			)));
		double y = getScaleFactor() * (MathExt.mlfn(phi, sinphi, cosphi, en) - ml0 +
			sinphi * al * lam * FC2 * ( 1. +
			FC4 * als * (5. - t + n * (9. + 4. * n) +
			FC6 * als * (61. + t * (t - 58.) + n * (270. - 330 * t)
			+ FC8 * als * (1385. + t * ( t * (543. - t) - 3111.) )
			))));
		
		
		//x = Math.toDegrees(x);
		//y = Math.toDegrees(y);
		
		//x = longitudeToColumn(x);
		//y = latitudeToRow(y);
		
		point.column = x;
		point.row = y;
	}

	public void getPointSpherical(double phi, double lam, double elevation, MapPoint point) throws MapProjectionException
	{
	 
		double cosphi = MathExt.cos(phi);
		double b = cosphi * MathExt.sin(lam);
		double aks0 = esp;
		double aks5 = ml0;
		double x, y = 0;
		
		
		
		if (MathExt.abs(MathExt.abs(b) - 1.0) <= MathExt.EPS10) {
			point.row = 0;
			point.column = 0;
			return;
		}
		
		x = aks5 * MathExt.log((1.0 + b) / (1.0 - b));
		y = cosphi * MathExt.cos(lam) / MathExt.sqrt(1.0 - b * b);
		b = MathExt.abs(y);
		if (b >= 1.0) {
			if (b - 1.0 > MathExt.EPS10) {
				// Throw!!!
			} else {
				y = 0.0;
			}
		} else {
			y = MathExt.acos(y);
		}
		
		if (phi < 0) {
			y = -y;
		}
		
		y = aks0 * (y - 0);

		
		//x = Math.toDegrees(x);
		//y = Math.toDegrees(y);
		
		//x = longitudeToColumn(x);
		//y = latitudeToRow(y);
		
		point.column = x;
		point.row = y;
		
		
	}
}
