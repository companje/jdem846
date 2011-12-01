package us.wthr.jdem846.gis;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;

import us.wthr.jdem846.AppRegistry;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.Registry;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@Registry
public class DatumsListRegistry implements AppRegistry
{
	private static Log log = Logging.getLog(DatumsListRegistry.class);
	
	private static final Map<String, PrimeMeridian> primeMeridianMap = new HashMap<String, PrimeMeridian>();
	private static final Map<String, Ellipsoid> ellipsoidMap = new HashMap<String, Ellipsoid>();
	private static final Map<String, Datum> datumMap = new HashMap<String, Datum>();
	private static final Map<String, Unit> unitMap = new HashMap<String, Unit>();
	
	protected DatumsListRegistry()
	{
		
	}
	
	@Initialize
	public static void init()
	{
		log.info("Loading GIS information...");
		
		String datumsJsonPath = JDem846Properties.getProperty("us.wthr.jdem846.datumsFile");
		
		JSONObject json = null;
		
		try {
			json = getDatumsJSONObject(datumsJsonPath);
		} catch (Exception ex) {
			log.error("Error loading GIS JSON info: " + ex.getMessage(), ex);
			return;
		}
		
		
		
		try {
			loadUnits(json);
		} catch (Exception ex) {
			log.error("Error loading unit definitions: " + ex.getMessage(), ex);
		}
		
		try {
			loadPrimeMeridians(json);
		} catch (Exception ex) {
			log.error("Error loading prime meridian definitions: " + ex.getMessage(), ex);
		}
		
		try {
			loadEllipsoids(json);
		} catch (Exception ex) {
			log.error("Error loading ellipsoid definitions: " + ex.getMessage(), ex);
		}
		
		try {
			loadDatums(json);
		} catch (Exception ex) {
			log.error("Error loading datum definitions: " + ex.getMessage(), ex);
		}
		
		
	}
	
	
	protected static void loadPrimeMeridians(JSONObject json)  throws Exception
	{

		JSONArray primeMeridians = json.getJSONArray("primeMeridians");
		
		
		for (int i = 0; i < primeMeridians.size(); i++) {
			JSONObject primeMeridianObj = primeMeridians.getJSONObject(i);

			String name = primeMeridianObj.getString("name");
			String definition = primeMeridianObj.getString("definition");
			double offset = primeMeridianObj.getDouble("offset");
			 
			PrimeMeridian primeMeridian = new PrimeMeridian(name, definition, offset);
			primeMeridianMap.put(name, primeMeridian);
		
		}
		
		log.info("Loaded " + primeMeridianMap.size() + " prime meridian definitions");
	}
	
	protected static void loadEllipsoids(JSONObject json) throws Exception
	{
		JSONArray ellipsoids = json.getJSONArray("ellipsoids");
		
		for (int i = 0; i < ellipsoids.size(); i++) {
			JSONObject ellipsoidObj = ellipsoids.getJSONObject(i);
				 
			String name = ellipsoidObj.getString("name");
			String shortName = ellipsoidObj.getString("shortName");
			double equatorRadius = ellipsoidObj.getDouble("equatorRadius");
			double poleRadius = ellipsoidObj.getDouble("poleRadius");
			double reciprocalFlattening = ellipsoidObj.getDouble("reciprocalFlattening");
			double eccentricity = ellipsoidObj.getDouble("eccentricity");
			double eccentricity2 = ellipsoidObj.getDouble("eccentricity2");
			 
			Ellipsoid ellipsoid = new Ellipsoid(name, shortName, equatorRadius, poleRadius, reciprocalFlattening, eccentricity, eccentricity2);
			ellipsoidMap.put(ellipsoid.getShortName(), ellipsoid);
		}
		 
		log.info("Loaded " + ellipsoidMap.size() + " ellipsoid definitions");
	}
	
	protected static void loadUnits(JSONObject json) throws Exception
	{
		JSONArray units = json.getJSONArray("units");
		
		 for (int i = 0; i < units.size(); i++) {
			 JSONObject unitObj = units.getJSONObject(i);
			 
			 String name = unitObj.getString("name");
			 String type = unitObj.getString("type");
			 double conversionFactor = unitObj.getDouble("conversionFactor");
			 
			 JSONArray latRangeObj = null;
			 JSONArray lonRangeObj = null;
			 
			 double[] latRange = null;
			 double[] lonRange = null;
			 
			 if (unitObj.get("latitudeRange") instanceof JSONArray) {
				 latRangeObj = unitObj.getJSONArray("latitudeRange");
				 latRange = new double[2];
				 latRange[0] = latRangeObj.getDouble(0);
				 latRange[1] = latRangeObj.getDouble(1);
			 }
			 
			 if (unitObj.get("longitudeRange") instanceof JSONArray) {
				 lonRangeObj = unitObj.getJSONArray("longitudeRange");
				 lonRange = new double[2];
				 lonRange[0] = lonRangeObj.getDouble(0);
				 lonRange[1] = lonRangeObj.getDouble(1);
			 }
			 
			 
			 
			 Unit unit = new Unit(name, conversionFactor, type, latRange, lonRange);
			 unitMap.put(name, unit);

			 
		 }
		 
		 log.info("Loaded " + unitMap.size() + " unit definitions");
	}
	
	
	protected static void loadDatums(JSONObject json) throws Exception
	{
		JSONArray datums = json.getJSONArray("datums");
		
		int count = 0;
		
		for (int i = 0; i < datums.size(); i++) {
			JSONObject datumObj = datums.getJSONObject(i);
			 
			String id = datumObj.getString("id");
			String ellipseName = datumObj.getString("ellipse");
			String datumName = datumObj.getString("datumName");
			String definition = datumObj.getString("definition");
			
			double[] toWGS84 = null;
			
			JSONArray toWGS84Obj = datumObj.getJSONArray("toWGS84");
			if (toWGS84Obj != null && toWGS84Obj.isArray() && !toWGS84Obj.isEmpty()) {
				toWGS84 = new double[toWGS84Obj.size()];
				for (int j = 0; j < toWGS84Obj.size(); j++) {
					toWGS84[j] = toWGS84Obj.getDouble(j);
				}
				
			}
			 
			Ellipsoid ellipse = ellipsoidMap.get(ellipseName);

			Datum datum = new Datum(id, datumName, definition, toWGS84, ellipse);
			datumMap.put(id, datum);
			datumMap.put(datumName, datum);
			count++;
		}
		 
		 
		log.info("Loaded " + count + " datum definitions");
	}
	
	
	protected static JSONObject getDatumsJSONObject(String datumsJsonPath) throws Exception
	{

		InputStream is = JDemResourceLoader.getAsInputStream(datumsJsonPath);
		String jsonTxt = IOUtils.toString( is );;

		JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );
		return json;

	}
	
	public static Datum getDatum(String name)
	{
		return (Datum) datumMap.get(name).clone();
	}
	
	public static Ellipsoid getEllipsoid(String name)
	{
		return (Ellipsoid) ellipsoidMap.get(name).clone();
	}
	
	public static PrimeMeridian getPrimeMeridian(String name)
	{
		return (PrimeMeridian) primeMeridianMap.get(name).clone();
	}
	
	public static Unit getUnit(String name)
	{
		return (Unit) unitMap.get(name).clone();
	}
	
}
