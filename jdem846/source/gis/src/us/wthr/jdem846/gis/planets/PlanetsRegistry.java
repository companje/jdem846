package us.wthr.jdem846.gis.planets;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import us.wthr.jdem846.AppRegistry;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.Registry;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@Registry
public class PlanetsRegistry implements AppRegistry
{
	private static Log log = Logging.getLog(PlanetsRegistry.class);
	
	private static final Map<String, Planet> planetsMap = new HashMap<String, Planet>();
	
	
	public PlanetsRegistry()
	{
		
	}
	
	@Initialize
	public static void init()
	{
		log.info("Loading planet information...");
	
		String planetsJsonPath = JDem846Properties.getProperty("us.wthr.jdem846.astro") + "/planets.json";
		JSONObject json = null;
		
		try {
			json = getPlanetsJsonObject(planetsJsonPath);
		} catch (Exception ex) {
			log.error("Error loading Planet JSON info: " + ex.getMessage(), ex);
			return;
		}
		
		JSONArray planets = json.getJSONArray("planets");
		
		int count = 0;
		
		try {
			for (int i = 0; i < planets.size(); i++) {
				JSONObject planetObj = planets.getJSONObject(i);
				loadPlanet(planetObj);
				count++;
			}
		} catch (Exception ex) {
			log.error("Error loading planet data: " + ex.getMessage(), ex);
		}
		log.info("Loaded " + count + " planet records");
		
		
	}
	
	protected static void loadPlanet(JSONObject planetObj)  throws Exception
	{
		String name = planetObj.getString("name");
		
		
		double aphelion = planetObj.getDouble("aphelion");
		double perihelion = planetObj.getDouble("perihelion");
		double semiMajorAxis = planetObj.getDouble("semiMajorAxis");
		double eccentricity = planetObj.getDouble("eccentricity");
		double orbitalPeriod = planetObj.getDouble("orbitalPeriod");
		double synodicPeriod = planetObj.getDouble("synodicPeriod");
		double averageOrbitalSpeed = planetObj.getDouble("averageOrbitalSpeed");
		double meanAnomaly = planetObj.getDouble("meanAnomaly");
		double inclinationToEcliptic = planetObj.getDouble("inclinationToEcliptic");
		double inclinationToSunsEquator = planetObj.getDouble("inclinationToSunsEquator");
		double inclinationToInvariablePlane = planetObj.getDouble("inclinationToInvariablePlane");
		double longitudeOfAscendingNode = planetObj.getDouble("longitudeOfAscendingNode");
		double argumentOfPerihelion = planetObj.getDouble("argumentOfPerihelion");
		double meanRadius = planetObj.getDouble("meanRadius");
		double equatorialRadius = planetObj.getDouble("equatorialRadius");
		double polarRadius = planetObj.getDouble("polarRadius");
		double flattening = planetObj.getDouble("flattening");
		double circumferenceEquatorial = planetObj.getDouble("circumferenceEquatorial");
		double circumferenceMeridional = planetObj.getDouble("circumferenceMeridional");
		double surfaceArea = planetObj.getDouble("surfaceArea");
		double volume = planetObj.getDouble("volume");
		double mass = planetObj.getDouble("mass");
		double meanDensity = planetObj.getDouble("meanDensity");
		double equatorialSurfaceGravity = planetObj.getDouble("equatorialSurfaceGravity");
		double escapeVelocity = planetObj.getDouble("escapeVelocity");
		double siderealRotationPeriod = planetObj.getDouble("siderealRotationPeriod");
		double equatorialRotationVelocity = planetObj.getDouble("equatorialRotationVelocity");
		double axialTilt = planetObj.getDouble("axialTilt");
		double northPoleRightAscension = planetObj.getDouble("northPoleRightAscension");
		double northPoleDeclination = planetObj.getDouble("northPoleDeclination");
		double albedoGeometric = planetObj.getDouble("albedoGeometric");
		double albedoBond = planetObj.getDouble("albedoBond");
		
		Planet planet = new Planet();
		planet.setName(name);
		planet.setAphelion(aphelion);
		planet.setPerihelion(perihelion);
		planet.setSemiMajorAxis(semiMajorAxis);
		planet.setEccentricity(eccentricity);
		planet.setOrbitalPeriod(orbitalPeriod);
		planet.setSynodicPeriod(synodicPeriod);
		planet.setAverageOrbitalSpeed(averageOrbitalSpeed);
		planet.setMeanAnomaly(meanAnomaly);
		planet.setInclinationToEcliptic(inclinationToEcliptic);
		planet.setInclinationToSunsEquator(inclinationToSunsEquator);
		planet.setInclinationToInvariablePlane(inclinationToInvariablePlane);
		planet.setLongitudeOfAscendingNode(longitudeOfAscendingNode);
		planet.setArgumentOfPerihelion(argumentOfPerihelion);
		planet.setMeanRadius(meanRadius);
		planet.setEquatorialRadius(equatorialRadius);
		planet.setPolarRadius(polarRadius);
		planet.setFlattening(flattening);
		planet.setCircumferenceEquatorial(circumferenceEquatorial);
		planet.setCircumferenceMeridional(circumferenceMeridional);
		planet.setSurfaceArea(surfaceArea);
		planet.setVolume(volume);
		planet.setMass(mass);
		planet.setMeanDensity(meanDensity);
		planet.setEquatorialSurfaceGravity(equatorialSurfaceGravity);
		planet.setEscapeVelocity(escapeVelocity);
		planet.setSiderealRotationPeriod(siderealRotationPeriod);
		planet.setEquatorialRotationVelocity(equatorialRotationVelocity);
		planet.setAxialTilt(axialTilt);
		planet.setNorthPoleRightAscension(northPoleRightAscension);
		planet.setNorthPoleDeclination(northPoleDeclination);
		planet.setAlbedoGeometric(albedoGeometric);
		planet.setAlbedoBond(albedoBond);
		
		
		log.info("Loaded planet " + name);
		planetsMap.put(name.toUpperCase(), planet);
	}
	
	
	
	
	
	private static JSONObject getPlanetsJsonObject(String planetsJsonPath) throws Exception
	{
		InputStream is = JDemResourceLoader.getAsInputStream(planetsJsonPath);
		String jsonTxt = IOUtils.toString( is );
		
		JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );
		return json;
	}
	
	public static Planet getPlanet(String name)
	{
		return planetsMap.get(name.toUpperCase());
	}
	
	public static List<Planet> getPlanetList()
	{
		List<Planet> planetList = new LinkedList<Planet>();
		
		for (String name : planetsMap.keySet()) {
			planetList.add(planetsMap.get(name));
		}
		
		return planetList;
	}
	
}
