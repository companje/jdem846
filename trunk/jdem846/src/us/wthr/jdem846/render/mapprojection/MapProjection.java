package us.wthr.jdem846.render.mapprojection;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Specifies a map projection class for plotting coordinates onto a mapping canvas.
 * 
 * @author Kevin M. Gill
 *
 */
public interface MapProjection
{
	
	/** Requests a canvas row & column from the specified latitude, longitude, and, optionally, elevation.
	 * The MapPoint parameter is to allow object reuse.
	 * 
	 * @param latitude Latitude coordinate
	 * @param longitude Longitude coordinate
	 * @param elevation Elevation at these coordinates. Only used by projection algorithms that support it.
	 * @param point Reusable map point object that will be populated with the canvas row & column.
	 */
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point);


}
