package us.wthr.jdem846.render.mapprojection;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.NumberUtil;

/** Implements the Winkel Tripel map projection (Winkel III), a modified azimuthal map projection.
 * 
 * @author Kevin M. Gill
 * @see http://en.wikipedia.org/wiki/Winkel_Tripel_projection
 */
public class WinkelTripelProjection implements MapProjection
{
	private static Log log = Logging.getLog(WinkelTripelProjection.class);

	private EquirectangularProjection equirectangular;
	private AitoffProjection aitoff;
	
	MapPoint equirectangularPoint = new MapPoint();
	MapPoint aitoffPoint = new MapPoint();

	public WinkelTripelProjection()
	{
		
	}
	
	public WinkelTripelProjection(double north, double south, double east, double west, double width, double height)
	{
		setUp(north, south, east, west, width, height);
	}
	
	public void setUp(double north, double south, double east, double west, double width, double height)
	{
		equirectangular = new EquirectangularProjection(north, south, east, west, width, height);
		aitoff = new AitoffProjection(north, south, east, west, width, height);
	}
	
	@Override
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point)
	{
		equirectangular.getPoint(latitude, longitude, elevation, equirectangularPoint);
		aitoff.getPoint(latitude, longitude, elevation, aitoffPoint);
		
		point.column = (equirectangularPoint.column + aitoffPoint.column) / 2.0;
		point.row = (equirectangularPoint.row + aitoffPoint.row) / 2.0;
		
		//point.row = equirectangularPoint.row;
		//point.column = equirectangularPoint.column;
		
	}
	
	
	
}
