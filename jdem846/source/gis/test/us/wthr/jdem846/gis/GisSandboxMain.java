package us.wthr.jdem846.gis;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;

import org.apache.commons.io.IOUtils;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.gis.projections.ProjectionsTestMain;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class GisSandboxMain extends AbstractTestMain
{
	
	private static Log log = null;
	
	public static void main(String[] args)
	{
		try {
			initialize(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(GisSandboxMain.class);
		
		GisSandboxMain tester = new GisSandboxMain();
		try {
			tester.doTestingProjGridSpecifications();
		} catch (Exception ex) {
			log.error("Aw Snap! -> " + ex.getMessage(), ex);
		}
		
	}
	
	
	public void doTestingProjGridSpecifications() throws Exception
	{
		String gisResourcesRootPath = JDem846Properties.getProperty("us.wthr.jdem846.gisResources");
		
		String testSpecFilePath = gisResourcesRootPath + "/epsg.txt";
		InputStream in = JDemResourceLoader.getAsInputStream(testSpecFilePath);
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
		
		String line = null;
		
		while ((line = reader.readLine()) != null) {
			if (isSpecLine(line)) {
				System.out.println(line);
				GridSpecification spec = parseSpecification(line);
			}
		}
		
	}
	
	protected GridSpecification parseSpecification(String line)
	{
		GridSpecification spec = new GridSpecification();
		
		
		String[] parts = line.split(" ");
		spec.id = Integer.parseInt(parts[0].replace("<", "").replace(">", ""));
		
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			
			
			if (part.startsWith("+a=")) {
				spec.a = Double.parseDouble(part.substring(3));
			} else if (part.startsWith("+alpha=")) {
				spec.alpha = Double.parseDouble(part.substring(7));
			} else if (part.startsWith("+axis=")) { //+axis=wsu
				spec.axis = part.substring(6);
				//spec.axis = AxisOrientationEnum.getByValue(part.substring(6));
			} else if (part.startsWith("+b=")) {
				spec.b = Double.parseDouble(part.substring(3));
			} else if (part.startsWith("+datum=")) {
				String datumCode = part.substring(7);
				spec.datum = datumCode;
				//spec.datum = DatumsListRegistry.getDatum(datumCode);
			} else if (part.startsWith("+ellps=")) {
				spec.ellps = part.substring(7);
				//spec.ellipse = DatumsListRegistry.getEllipsoid(spec.ellipseCode);
			} else if (part.startsWith("+k=")) {
				spec.k = Double.parseDouble(part.substring(3));
			} else if (part.startsWith("+k_0=")) {
				spec.k_0 = Double.parseDouble(part.substring(5));
			} else if (part.startsWith("+lat_0=")) {
				spec.lat_0 = Double.parseDouble(part.substring(7));
			} else if (part.startsWith("+lat_1=")) {
				spec.lat_1 = Double.parseDouble(part.substring(7));
			} else if (part.startsWith("+lat_2=")) {
				spec.lat_2 = Double.parseDouble(part.substring(7));
			} else if (part.startsWith("+lat_ts=")) {
				spec.lat_ts = Double.parseDouble(part.substring(8));
			} else if (part.startsWith("+lon_0=")) {
				spec.lon_0 = Double.parseDouble(part.substring(7));
			} else if (part.startsWith("+lonc=")) {
				spec.lonc = Double.parseDouble(part.substring(6));
			} else if (part.startsWith("+lon_wrap=")) {
				spec.lon_wrap = Double.parseDouble(part.substring(10));
			} else if (part.startsWith("+nadgrids=")) {
				spec.nadgrids = part.substring(10);
			} else if (part.startsWith("+no_defs")) {
				spec.no_defs = true;
			} else if (part.startsWith("+over")) {
				spec.over = true;
			} else if (part.startsWith("+pm=")) {
				spec.pm = part.substring(4);
			} else if (part.startsWith("+proj=")) {
				spec.proj = part.substring(6);
			} else if (part.equals("+south")) {
				spec.south = true;
			} else if (part.startsWith("+to_meter=")) {
				spec.to_meter = Double.parseDouble(part.substring(10));
			} else if (part.startsWith("+towgs84=")) {
				String[] s_toWGS84 = part.substring(9).split(",");
				spec.toWGS84 = new double[s_toWGS84.length];
				for (int j = 0; j < s_toWGS84.length; j++) {
					spec.toWGS84[j] = Double.parseDouble(s_toWGS84[j]);
				}
			} else if (part.startsWith("+units=")) {
				spec.units = part.substring(7);
			} else if (part.startsWith("+vto_meter=")) {
				spec.vto_meter = Double.parseDouble(part.substring(11));
			} else if (part.startsWith("+vunits=")) {
				spec.vunits = part.substring(8);
			} else if (part.startsWith("+x_0=")) {
				spec.x_0 = Double.parseDouble(part.substring(5));
			} else if (part.startsWith("+y_0=")) {
				spec.y_0 = Double.parseDouble(part.substring(5));
			} else if (part.startsWith("+zone=")) {
				spec.zone = Integer.parseInt(part.substring(6));
			} 

		}
		
		System.out.printf(" %d proj: %s, zone: %d, datum: %s, towgs84: %s, ellps: %s, k: %f\n", spec.id, spec.proj, spec.zone, spec.datum, spec.toWGS84, spec.ellps, spec.k);
		
		return spec;
	}
	
	protected boolean isSpecLine(String line)
	{
		if (line == null)
			return false;
		String[] parts = line.split(" ");
		if (parts[0].charAt(0) == '<')
			return true;
		else
			return false;
	}
	
	
	/** http://trac.osgeo.org/proj/wiki/GenParms
	 * 
	 * @author Kevin M. Gill
	 *
	 */
	class GridSpecification
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
	
	
	
	
	
	
	
	
	
	
	
	public void doTestingDatumsJSON() throws Exception
	{
		String datumsJsonPath = JDem846Properties.getProperty("us.wthr.jdem846.datumsFile");
		
		JSONObject json = getDatumsJSONObject(datumsJsonPath);
		
		JSONArray ellipsoids = json.getJSONArray("ellipsoids");
		
		 for (int i = 0; i < ellipsoids.size(); i++) {
			 JSONObject ellipsoid = ellipsoids.getJSONObject(i);
			 
			 String name = ellipsoid.getString("name");
			 String shortName = ellipsoid.getString("shortName");
			 double equatorRadius = ellipsoid.getDouble("equatorRadius");
			 double poleRadius = ellipsoid.getDouble("poleRadius");
			 double reciprocalFlattening = ellipsoid.getDouble("reciprocalFlattening");
			 
			 double eccentricity = 0.0;
			 double eccentricity2 = 1.0;
			 
	
			 if (reciprocalFlattening != 0.0) {
				 double flattening = 1.0 / reciprocalFlattening;
				 double f = flattening;
				 
				 eccentricity2 = 2 * f - f * f;
				 poleRadius = equatorRadius * Math.sqrt(1.0 - eccentricity2);
				 ellipsoid.put("poleRadius", ""+poleRadius);
				 
			 } else {
				 reciprocalFlattening = equatorRadius / (equatorRadius - poleRadius);
				 if (Double.isInfinite(reciprocalFlattening)) {
					 reciprocalFlattening = 0.0;
				 }
				 ellipsoid.put("reciprocalFlattening", ""+reciprocalFlattening);
				 eccentricity2 = 1.0 - (poleRadius * poleRadius) / (equatorRadius * equatorRadius);
			 }
			 eccentricity = Math.sqrt(eccentricity2);
	
			 
			 ellipsoid.put("eccentricity", ""+eccentricity);
			 ellipsoid.put("eccentricity2", ""+eccentricity2);
			 System.out.printf("%10s %7.11f %7.11f %1.17f %1.17f %3.11f\n", shortName, equatorRadius, poleRadius, eccentricity, eccentricity2, reciprocalFlattening);
			 
			 //System.out.println(shortName + " --- " + poleRadius + " --- " + eccentricity + " --- " + eccentricity2);
		 }
		
		System.out.print(json.toString(2));
	}
	
	public JSONObject getDatumsJSONObject(String datumsJsonPath) throws Exception
	{

		InputStream is = JDemResourceLoader.getAsInputStream(datumsJsonPath);
		String jsonTxt = IOUtils.toString( is );;
		//JsonConfig config = new JsonConfig();
		//config.setAllowNonStringKeys(true);
		
		JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );
		return json;

	}
	
}
