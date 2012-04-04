package us.wthr.jdem846.rasterdata;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.gis.elevation.ElevationMinMaxEstimation;
import us.wthr.jdem846.gis.elevation.ElevationSample;
import us.wthr.jdem846.gis.elevation.ElevationSampleMap;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ElevationMinMaxEstimationTestMain extends AbstractTestMain
{
	private static Log log = null;
	
	
	public static void main(String[] args)
	{
		try {
			initialize(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(ElevationMinMaxEstimationTestMain.class);
		
		try {
			ElevationMinMaxEstimationTestMain testMain = new ElevationMinMaxEstimationTestMain();
			//testMain.doTestingJson();
			//testMain.doTestingCsv();
			//testMain.doTestingViaClass();
			testMain.doTestingViaPlanet();
		} catch (Exception ex) {
			log.error("Uncaught exception while running test main: " + ex.getMessage(), ex);
		}
		
		
		
		
	}
	
	/*
	public void doTestingJson() throws Exception
	{
		String samplesPath = "resources://gis/elevation-minmax-samples-earth.json";
		log.info("Opening " + samplesPath);
		
		
		InputStream in = JDemResourceLoader.getAsInputStream(samplesPath);
		String jsonTxt = IOUtils.toString( in );
		JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );
		
		double north = json.getDouble("north");
		double south = json.getDouble("south");
		double east = json.getDouble("east");
		double west = json.getDouble("west");
		
		log.info("North: " + north);
		log.info("South: " + south);
		log.info("East: " + east);
		log.info("West: " + west);
		
		
		
		
		
		
		
	}
	*/
	
	
	public void doTestingViaPlanet() throws Exception
	{
		Planet earth = PlanetsRegistry.getPlanet("Earth");
		
		ElevationMinMaxEstimation earthMinMax = ElevationMinMaxEstimation.load(earth);
		
		/*
		 * North/South: 44.32361111087598/44.211666666424
		 * East/West: -71.24472221867558/-71.384722218685
		 */
		
		
		double latitude = (44.32361111087598 + 44.211666666424) / 2.0;
		double longitude = (-71.24472221867558 + -71.384722218685) / 2.0;
		
		log.info("Test Latitude: " + latitude);
		log.info("Test Longitude: " + longitude);
		
		//ElevationSample minMax = (ElevationSample) earthMinMax.getMinMax(latitude, longitude);
		
		ElevationSample minMax = (ElevationSample) earthMinMax.getMinMax(44.32361111087598, 44.211666666424, -71.24472221867558, -71.384722218685);
		
		
		log.info("Latitude: " + minMax.getLatitude());
		log.info("Longitude: " + minMax.getLongitude());
		log.info("Minimum: " + minMax.getMinimumElevation());
		log.info("Maximum: " + minMax.getMaximumElevation());
		log.info("Mean: " + minMax.getMeanElevation());
	}
	
	public void doTestingViaClass() throws Exception
	{
		String samplesPath = "resources://gis/elevation-minmax-samples-earth.csv";
		ElevationMinMaxEstimation.load(samplesPath);
		
	}
	
	public void doTestingCsv() throws Exception
	{
		String samplesPath = "resources://gis/elevation-minmax-samples-earth.csv";
		
		File samplesFile = JDemResourceLoader.getAsFile(samplesPath);
		
		log.info("Opening " + samplesPath);
		LineNumberReader reader = new LineNumberReader(new FileReader(samplesFile));
		
		String line = null;
		
		double north = 90;
		double south = -90;
		double east = 180;
		double west = -180;
		
		double latitudeResolution = 1.0;
		double longitudeResolution = 1.0;
		
		ElevationSampleMap sampleMap = null;//new ElevationSampleMap(north, south, east, west, latitudeResolution, longitudeResolution);
		

		
		while ((line = reader.readLine()) != null) {
			
			if (reader.getLineNumber() == 1) {
				north = Double.parseDouble(line);
				log.info("North: " + north);
			} else if (reader.getLineNumber() == 2) {
				south = Double.parseDouble(line);
				log.info("South: " + south);
			} else if (reader.getLineNumber() == 3) {
				east = Double.parseDouble(line);
				log.info("East: " + east);
			} else if (reader.getLineNumber() == 4) {
				west = Double.parseDouble(line);
				log.info("West: " + west);
			} else if (reader.getLineNumber() == 5) {
				latitudeResolution = Double.parseDouble(line);
				log.info("Latitude Resolution: " + latitudeResolution);
			} else if (reader.getLineNumber() == 6) {
				longitudeResolution = Double.parseDouble(line);
				log.info("Longitude Resolution: " + longitudeResolution);
			} else if (reader.getLineNumber() >= 7) {
				
				if (sampleMap == null) {
					sampleMap = new ElevationSampleMap(north, south, east, west, latitudeResolution, longitudeResolution);
				}
				
				String[] parts = line.split(", ");
				if (parts.length != 6) {
					continue;
				}
				
				double latitude = Double.parseDouble(parts[0]);
				double longitude = Double.parseDouble(parts[1]);
				double minimumElevation = Double.parseDouble(parts[2]);
				double maximumElevation = Double.parseDouble(parts[3]);
				double meanElevation = Double.parseDouble(parts[4]);
				double medianElevation = Double.parseDouble(parts[5]);
				
				ElevationSample sample = new ElevationSample(latitude, 
															longitude, 
															minimumElevation,
															maximumElevation,
															meanElevation,
															medianElevation);
				
				
				sampleMap.add(sample);
			}
		}
		
		log.info("Finished");
		reader.close();
	}
}
