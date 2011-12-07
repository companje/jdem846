package us.wthr.jdem846.gis.input.proj4;


/** http://trac.osgeo.org/proj/wiki/GenParms
 * 
 * @author Kevin M. Gill
 *
 */
public class Proj4GridSpecification
{
	
	public int id;
	public double a; 					// Semimajor radius of the ellipsoid axis
	public double alpha; 				// ? Used with Oblique Mercator and possibly a few others
	public String axis; 				// Axis orientation (e,w,n,s,u,d)
	public double b; 					// Semiminor radius of the ellipsoid axis
	public String datum; 				// Datum name
	public String ellps; 				// Ellipsoid name
	public double k;					// Scaling factor (old name)
	public double k_0;					// Scaling factor (new name)
	public double lat_0;				// Latitude of origin
	public double lat_1;				// Latitude of first standard parallel
	public double lat_2;				// Latitude of second standard parallel
	public double lat_ts;				// Latitude of true scale
	public double lon_0;				// Central meridian
	public double lonc;					// ? Longitude used with Oblique Mercator and possibly a few others
	public double lon_wrap;				// Center longitude to use for wrapping
	public String nadgrids;				// Filename of NTv2 grid file to use for datum transforms
	public boolean no_defs = false;		// Don't use the /usr/share/proj/proj_def.dat defaults file
	public boolean over = false;		// Allow longitude output outside -180 to 180 range, disables wrapping
	public String pm;					// Alternate prime meridian (typically city name)
	public String proj;					// Projection name
	public boolean south = false;		// Denotes southern hemisphere UTM zone
	public double to_meter;				// Multiplier to convert map units to 1.0m
	public double[] toWGS84;			// 3 or 7 term datum transform parameters
	public String units;				// meters, US survey feet, etc.
	public double vto_meter;			// Vertical conversion to meters
	public String vunits;				// Vertical units
	public double x_0;					// False easting
	public double y_0;					// False northing
	public int zone;					// UTM zone 

	
}
