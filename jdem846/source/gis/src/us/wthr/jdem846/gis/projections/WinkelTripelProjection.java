package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.gfx.NumberUtil;

/** Implements the Winkel Tripel map projection (Winkel III), a modified azimuthal map projection.
 * 
 * @author Kevin M. Gill
 * @see http://en.wikipedia.org/wiki/Winkel_Tripel_projection
 */
public class WinkelTripelProjection extends AbstractBaseProjection
{
	private static Log log = Logging.getLog(WinkelTripelProjection.class);


	
	public WinkelTripelProjection()
	{
		
	}
	
	public WinkelTripelProjection(double north, double south, double east, double west, double width, double height)
	{
		super(north, south, east, west, width, height);
	}
	

	@Override
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point)
	{


		double lpphi = Math.toRadians(latitude);
		double lplam = Math.toRadians(longitude);

		double c = 0.5 * lplam;
		double cosO1 = 0.99993827224000145098735895662767;
		double d = Math.acos(Math.cos(lpphi) * Math.cos(c));
		
		double sinca = (d == 0) ? 0 : (Math.sin(d) / d);
		
		double x = 0;
		double y = 0;
		
		if (d != 0) {
			x = (2.0 * Math.cos(lpphi) * Math.sin(c)) / sinca;
			y = Math.sin(lpphi) / sinca;
		}
		
		x = (x + lplam * cosO1) / 2.0;
		y = (y + lpphi) / 2.0;
		

		point.column = longitudeToColumn(Math.toDegrees(x));
		point.row = latitudeToRow(Math.toDegrees(y));

	}

	
	
}
