package us.wthr.jdem846.rasterdata;


import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.gis.CoordinateSpaceAdjuster;


public class ElevationDataMap
{
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	private int columns;
	private int rows;
	
	private double latitudeResolution;
	private double longitudeResolution;
	
	private CoordinateSpaceAdjuster coordinateSpaceAdjuster;
	
	private double[] values;
	
	public ElevationDataMap(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution)
	{
		//super(initialCapacity, loadFactor);
		
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		
		
		columns = (int) Math.ceil((east - west) / longitudeResolution);
		this.west = east + (longitudeResolution * columns);
		
		rows = (int) Math.round((north - south) / latitudeResolution);
		columns = (int) Math.round((east - west) / longitudeResolution);
		
		values = new double[rows * columns];
		for (int i = 0; i < values.length; i++) {
			values[i] = DemConstants.ELEV_NO_DATA;
		}
		
		coordinateSpaceAdjuster = new CoordinateSpaceAdjuster(north, south, east, west);
	}
	
	public void dispose()
	{
		values = null;
	}
	
	
	protected int getIndex(double latitude, double longitude)
	{
		int column = (int) Math.floor((longitude - west) / longitudeResolution);
		int row = (int) Math.floor((north - latitude) / latitudeResolution);
		
		int index = row * columns + column;
		return index;
	}
	
	public double put(double latitude, double longitude, double elevation)
	{
		if (values == null) {
			return elevation;
		}
		
		double adjLatitude = 0;
		double adjLongitude = 0;
		
		if ((adjLatitude = coordinateSpaceAdjuster.adjustLatitude(latitude)) == DemConstants.ELEV_NO_DATA) {
			return elevation;
		}
		
		if ((adjLongitude = coordinateSpaceAdjuster.adjustLongitude(longitude)) == DemConstants.ELEV_NO_DATA) {
			return elevation;
		}
		

		int index = getIndex(adjLatitude, adjLongitude);
		if (index < 0 || index >= values.length) {
			return elevation;
		}
		
		values[index] = elevation;
		return elevation;
		
	}
	
	public double get(double latitude, double longitude, double ifNull)
	{
		
		if (values == null) {
			return ifNull;
		}
		
		double adjLatitude = 0;
		double adjLongitude = 0;
		
		if ((adjLatitude = coordinateSpaceAdjuster.adjustLatitude(latitude)) == DemConstants.ELEV_NO_DATA) {
			return ifNull;
		}
		
		if ((adjLongitude = coordinateSpaceAdjuster.adjustLongitude(longitude)) == DemConstants.ELEV_NO_DATA) {
			return ifNull;
		}
		
		int index = getIndex(adjLatitude, adjLongitude);
		if (index < 0 || index >= values.length) {
			return ifNull;
		}
		
		double value = values[index];
		if (value != DemConstants.ELEV_NO_DATA)
			return value;
		else
			return ifNull;
	
	}
	

	
	public static ElevationDataMap create(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution)
	{
		return new ElevationDataMap(north, south, east, west, latitudeResolution, longitudeResolution);
	}
}
