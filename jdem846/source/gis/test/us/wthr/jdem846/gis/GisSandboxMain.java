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
			if (part.startsWith("+proj=")) {
				spec.projCode = part.substring(6);
			} else if (part.startsWith("+zone=")) {
				spec.zone = Integer.parseInt(part.substring(6));
			} else if (part.equals("+south")) {
				spec.isSouth = true;
			} else if (part.startsWith("+datum=")) {
				String datumCode = part.substring(7);
				spec.datumCode = datumCode;
				spec.datum = DatumsListRegistry.getDatum(datumCode);
			} else if (part.startsWith("+towgs84=")) {
				String[] s_toWGS84 = part.substring(9).split(",");
				spec.toWGS84 = new double[s_toWGS84.length];
				for (int j = 0; j < s_toWGS84.length; j++) {
					spec.toWGS84[j] = Double.parseDouble(s_toWGS84[j]);
				}
			} else if (part.startsWith("+ellps=")) {
				spec.ellipseCode = part.substring(7);
				spec.ellipse = DatumsListRegistry.getEllipsoid(spec.ellipseCode);
			} else if (part.startsWith("+lat_")) {
				
			} else if (part.startsWith("+lon_")) {
				
			} else if (part.startsWith("+x_")) {
				
			} else if (part.startsWith("+y_")) {
				
			} else if (part.startsWith("+k=")) {
				
			} else if (part.startsWith("+units=")) {
				
			} else if (part.startsWith("+no_defs")) {
				
			} else if (part.startsWith("+lat_ts=")) {
				
			}
			
			/*
			 * public int id;
		public String projCode;
		public int zone;
		public boolean isSouth = false;
		public String datumCode;
		public String ellipseCode;
		public Ellipsoid ellipse;
		public String unitsCode;
		public String toWGS84;
			 */
		}
		
		System.out.printf(" %d proj: %s, zone: %d, datum: %s, towgs84: %s, ellps: %s\n", spec.id, spec.projCode, spec.zone, spec.datumCode, spec.toWGS84, spec.ellipseCode);
		
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
	
	class GridSpecification
	{
		public int id;
		public String projCode;
		public int zone;
		public boolean isSouth = false;
		public String datumCode;
		public Datum datum;
		public String ellipseCode;
		public Ellipsoid ellipse;
		public String unitsCode;
		public double[] toWGS84;
		
		
		
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
