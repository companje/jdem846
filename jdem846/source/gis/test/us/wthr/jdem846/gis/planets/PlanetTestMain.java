package us.wthr.jdem846.gis.planets;

import java.io.InputStream;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class PlanetTestMain extends AbstractTestMain
{
	
	private static Log log = null;
	
	public static void main(String[] args)
	{
		try {
			initialize(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		log = Logging.getLog(PlanetTestMain.class);
		
		PlanetTestMain tester = new PlanetTestMain();
		try {
			tester.toTesting();
		} catch (Exception ex) {
			log.error("Error in test: " + ex.getMessage(), ex);
		}
	}
	
	
	public void toTesting() throws Exception
	{
		log.info("Starting test...");
		
		List<Planet> planetList = PlanetsRegistry.getPlanetList();
		for (Planet planet : planetList) {
			log.info("Found planet: " + planet.getName());
		}
	}
	
	
	public void __toTesting() throws Exception
	{
		log.info("Starting test...");
		
		String planetsJsonPath = JDem846Properties.getProperty("us.wthr.jdem846.astro") + "/planets.json";
		
		InputStream is = JDemResourceLoader.getAsInputStream(planetsJsonPath);
		String jsonTxt = IOUtils.toString( is );
		
		JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );
		
		JSONArray planets = json.getJSONArray("planets");
		
		//int count = 0;
		
		for (int i = 0; i < planets.size(); i++) {
			JSONObject planetObj = planets.getJSONObject(i);
			 
			String name = planetObj.getString("name");
			
			
			double aphelion = planetObj.getDouble("aphelion");
			double perihelion = planetObj.getDouble("perihelion");
			double semiMajorAxis = planetObj.getDouble("semiMajorAxis");
			double eccentricity = planetObj.getDouble("eccentricity");
			double orbitalPeriod = planetObj.getDouble("orbitalPeriod");
			double averageOrbitalSpeed = planetObj.getDouble("averageOrbitalSpeed");
			double meanAnomaly = planetObj.getDouble("meanAnomaly");
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
			double albedoGeometric = planetObj.getDouble("albedoGeometric");
			double albedoBond = planetObj.getDouble("albedoBond");
			
			Planet planet = new Planet();
			planet.setName(name);
			planet.setAphelion(aphelion);
			planet.setPerihelion(perihelion);
			planet.setSemiMajorAxis(semiMajorAxis);
			planet.setEccentricity(eccentricity);
			planet.setOrbitalPeriod(orbitalPeriod);
			planet.setAverageOrbitalSpeed(averageOrbitalSpeed);
			planet.setMeanAnomaly(meanAnomaly);
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
			planet.setAlbedoGeometric(albedoGeometric);
			planet.setAlbedoBond(albedoBond);
			
			
			log.info("Loading planet " + name);
		}
		
	}
}
