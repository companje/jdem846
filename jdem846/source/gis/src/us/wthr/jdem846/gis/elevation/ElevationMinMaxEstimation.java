package us.wthr.jdem846.gis.elevation;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;

import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;

public class ElevationMinMaxEstimation
{
	private static Log log = Logging.getLog(ElevationMinMaxEstimation.class);
	
	private static Map<String, ElevationMinMaxEstimation> minMaxEstimationMap = new HashMap<String, ElevationMinMaxEstimation>();
	
	private double north;
	private double south;
	private double east;
	private double west;
	private double latitudeResolution;
	private double longitudeResolution;
	
	private ElevationSampleMap sampleMap;
	
	protected ElevationMinMaxEstimation(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution)
	{
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		
		sampleMap = new ElevationSampleMap(north, south, east, west, latitudeResolution, longitudeResolution);
		
	}
	
	
	
	public ElevationMinMax getMinMax(double latitude, double longitude)
	{
		return sampleMap.get(latitude, longitude);
	}
	
	public ElevationMinMax getMinMax(double north, double south, double east, double west)
	{
		
		
		double min = Double.NaN;
		double max = Double.NaN;
		double mean = 0;
		double samples = 0;
		
		for (double latitude = north; latitude >= south; latitude -= latitudeResolution) {
			
			for (double longitude = west; longitude <= east; longitude += longitudeResolution) {
				
				ElevationSample sample = sampleMap.get(latitude, longitude);
				if (sample != null) {
					
					if (Double.isNaN(min)) {
						min = sample.getMinimumElevation();
					} else {
						min = MathExt.min(min, sample.getMinimumElevation());
					}
					
					if (Double.isNaN(max)) {
						max = sample.getMaximumElevation();
					} else {
						max = MathExt.max(max, sample.getMaximumElevation());
					}
					
					mean += sample.getMeanElevation();
					samples++;
				}
			}
			
		}
		
		if (samples > 0) {
			mean = mean / samples;
		}
		
		ElevationSample minMax = new ElevationSample();
		minMax.setMinimumElevation(min);
		minMax.setMaximumElevation(max);
		minMax.setMeanElevation(mean);
		
		return minMax;
	}
	
	
	public static ElevationMinMaxEstimation load(Planet planet) throws Exception
	{
		if (planet.getElevationSamplesPath() != null) {
			return load(planet.getElevationSamplesPath());
		} else {
			return null;
		}
	}
	
	
	public static ElevationMinMaxEstimation load(String url) throws Exception
	{
		
		if (minMaxEstimationMap.containsKey(url)) {
			return minMaxEstimationMap.get(url);
		}
		
		if (url == null || url.equalsIgnoreCase("null")) {
			return null;
		}
		
		File samplesFile = JDemResourceLoader.getAsFile(url);
		
		log.info("Opening " + url);
		LineNumberReader reader = new LineNumberReader(new FileReader(samplesFile));
		
		String line = null;
		
		double north = 90;
		double south = -90;
		double east = 180;
		double west = -180;
		
		double latitudeResolution = 1.0;
		double longitudeResolution = 1.0;

		ElevationMinMaxEstimation instance = null;

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
				
				if (instance == null) {
					instance = new ElevationMinMaxEstimation(north, south, east, west, latitudeResolution, longitudeResolution);
				}
				
				//if (sampleMap == null) {
				////	sampleMap = new ElevationSampleMap(north, south, east, west, latitudeResolution, longitudeResolution);
				//}
				
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
				
				
				instance.sampleMap.add(sample);
			}
		}
		
		log.info("Finished");
		reader.close();
		
		minMaxEstimationMap.put(url, instance);
		
		return instance;
	}
	
	
}
