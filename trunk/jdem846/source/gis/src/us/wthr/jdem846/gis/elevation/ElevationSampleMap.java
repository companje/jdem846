package us.wthr.jdem846.gis.elevation;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ElevationSampleMap
{
	private static Log log = Logging.getLog(ElevationSampleMap.class);
	
	private double north;
	private double south;
	private double east;
	private double west;
	private double latitudeResolution;
	private double longitudeResolution;
	
	private int rows = 0;
	private int columns = 0;
	
	private ElevationSample[] samples;
	
	public ElevationSampleMap(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution)
	{
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		
		rows = (int) Math.round((north - south) / latitudeResolution);
		columns = (int) Math.round((east - west) / longitudeResolution);
		
		samples = new ElevationSample[rows * columns];
		
		log.info("Allocated elevation sample array with: " + rows + " rows and " + columns + " columns (" + (rows * columns) + " total elements)");
		
	}
	
	public boolean add(ElevationSample sample)
	{
		int index = getIndex(sample.getLatitude(), sample.getLongitude());
		
		if (index < 0 || index >= samples.length) {
			return false;
		}
		
		samples[index] = sample;
		
		return true;
	}
	
	public ElevationSample get(double latitude, double longitude)
	{
		int index = getIndex(latitude, longitude);
		
		if (index < 0 || index >= samples.length) {
			return null;
		}
		
		return samples[index];
	}
	
	
	
	protected int getIndex(double latitude, double longitude)
	{
		int column = (int) Math.floor((longitude - west) / longitudeResolution);
		int row = (int) Math.floor((north - latitude) / latitudeResolution);
		
		int index = row * columns + column;
		return index;
	}
	
	
	public void dispose()
	{
		if (samples != null) {
			samples = null;
		}
	}
	
	
	
}
