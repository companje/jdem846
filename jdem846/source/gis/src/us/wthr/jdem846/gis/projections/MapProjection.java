package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Specifies a map projection class for plotting coordinates onto a mapping canvas.
 * 
 * @author Kevin M. Gill
 *
 */
public interface MapProjection
{
	
	/** Initializes the map projection class
	 * 
	 * @param north
	 * @param south
	 * @param east
	 * @param west
	 * @param width
	 * @param height
	 */
	public void setUp(double north, double south, double east, double west, double width, double height);
	
	/** Initializes the map projection class with the properties in the model context object.
	 * 
	 * @param modelContext
	 */
	public void setUp(ModelContext modelContext);
	
	/** Requests a canvas row & column from the specified latitude, longitude, and, optionally, elevation.
	 * The MapPoint parameter is to allow object reuse.
	 * 
	 * @param latitude Latitude coordinate
	 * @param longitude Longitude coordinate
	 * @param elevation Elevation at these coordinates. Only used by projection algorithms that support it.
	 * @param point Reusable map point object that will be populated with the canvas row & column.
	 */
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException;


}
