package us.wthr.jdem846.gis;

import java.io.InputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.gis.input.proj4.Proj4GridParser;
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
		
		Proj4GridParser parser = new Proj4GridParser();
		parser.parseFile(testSpecFilePath);
		
		
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	public void doTestingDatumsJSON() throws Exception
	{
		String datumsJsonPath = JDem846Properties.getProperty("us.wthr.jdem846.datumsFile");
		
		JSONObject json = getDatumsJSONObject(datumsJsonPath);
		
		JSONArray ellipsoids = json.getJSONArray("ellipsoids");
		
		 for (int i = 0; i < ellipsoids.size(); i++) {
			 JSONObject ellipsoid = ellipsoids.getJSONObject(i);
			 
			 //String name = ellipsoid.getString("name");
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
