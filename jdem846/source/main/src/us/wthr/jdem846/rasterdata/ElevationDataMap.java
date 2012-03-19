package us.wthr.jdem846.rasterdata;

import java.util.Hashtable;

import us.wthr.jdem846.gis.CoordinateSpaceAdjuster;
import us.wthr.jdem846.gis.exceptions.CoordinateSpaceException;

@SuppressWarnings("serial")
public class ElevationDataMap extends Hashtable<Integer, Double>
{
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	private int columns;
	
	private double latitudeResolution;
	private double longitudeResolution;
	
	private CoordinateSpaceAdjuster coordinateSpaceAdjuster;
	
	public ElevationDataMap(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution, int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
		
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		
		
		columns = (int) Math.ceil((east - west) / longitudeResolution);
		this.west = east + (longitudeResolution * columns);
		
		coordinateSpaceAdjuster = new CoordinateSpaceAdjuster(north, south, east, west);
	}
	
	public Double put(double latitude, double longitude, double elevation)
	{
		double adjLatitude = 0;
		double adjLongitude = 0;
		
		try {
			adjLatitude = coordinateSpaceAdjuster.adjustLatitude(latitude);
			adjLongitude = coordinateSpaceAdjuster.adjustLongitude(longitude);
		} catch (CoordinateSpaceException ex) {
			return elevation;
		}
		
		
		int key = getHashKey(adjLatitude, adjLongitude);
		return super.put(key, elevation);
	}
	
	public Double get(Double latitude, double longitude, double ifNull)
	{
		
		double adjLatitude = 0;
		double adjLongitude = 0;
		
		try {
			adjLatitude = coordinateSpaceAdjuster.adjustLatitude(latitude);
			adjLongitude = coordinateSpaceAdjuster.adjustLongitude(longitude);
		} catch (CoordinateSpaceException ex) {
			return ifNull;
		}
		
		int key = getHashKey(adjLatitude, adjLongitude);
		Double value = super.get(key);
		if (value != null) {
			return value;
		} else {
			return ifNull;
		}
		
	}
	
	
	protected int getHashKey(double latitude, double longitude)
	{
		//int columns = (int) Math.ceil((east - west) / longitudeResolution);
		
		
		
		int row = (int) Math.round((north - latitude) / latitudeResolution);
		int column = (int) Math.round((longitude - west) / longitudeResolution);
		
		int key = (row * columns) + column;
		return key;
	}
	
	public static ElevationDataMap create(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution)
	{
		int rows = (int) Math.ceil((north - south) / latitudeResolution) + 1;
		int columns = (int) Math.ceil((east - west) / longitudeResolution) + 1;
		int initialCapacity = rows * columns;
		float loadFactor = 1.25f;
		
		return new ElevationDataMap(north, south, east, west, latitudeResolution, longitudeResolution, initialCapacity, loadFactor);
		
		
	}
}
