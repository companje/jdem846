package us.wthr.jdem846.gis;

import us.wthr.jdem846.gis.exceptions.CoordinateSpaceException;

public class CoordinateSpaceAdjuster {
	
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	public CoordinateSpaceAdjuster(double north, double south, double east, double west)
	{
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
	}
	
	
	public double adjustLatitude(double latitude) throws CoordinateSpaceException
	{
		if (latitude > north) {
			return adjustLatitudeDown(latitude);
		} else if (latitude < south) {
			return adjustLatitudeUp(latitude);
		} else {
			return latitude;
		}
	}
	
	
	protected double adjustLatitudeDown(double latitude) throws CoordinateSpaceException
	{
		while (latitude > north) {
			latitude -= 180.0;
		}
		
		if (latitude < south) {
			throw new CoordinateSpaceException("Coordinate does not adjust into bounds.");
		} else {
			return latitude;
		}
	}
	
	protected double adjustLatitudeUp(double latitude) throws CoordinateSpaceException
	{
		while (latitude < south) {
			latitude += 180.0;
		}
		
		if (latitude > north) {
			throw new CoordinateSpaceException("Coordinate does not adjust into bounds.");
		} else {
			return latitude;
		}
	}
	
	
	public double adjustLongitude(double longitude) throws CoordinateSpaceException
	{
		
		if (longitude < west) {
			return adjustLongitudeRight(longitude);
		} else if (longitude > east) {
			return adjustLongitudeLeft(longitude);
		} else {
			return longitude;
		}
		
	}
	
	
	protected double adjustLongitudeLeft(double longitude) throws CoordinateSpaceException
	{
		while (longitude > east) {
			longitude -= 360.0;
		}
		
		if (longitude < west) {
			throw new CoordinateSpaceException("Coordinate does not adjust into bounds.");
		} else {
			return longitude;
		}
		
		
	}
	
	protected double adjustLongitudeRight(double longitude) throws CoordinateSpaceException
	{
		
		while (longitude < west) {
			longitude += 360.0;
		}
		
		if (longitude > east) {
			throw new CoordinateSpaceException("Coordinate does not adjust into bounds.");
		} else {
			return longitude;
		}
		
		
	}
	
	
	public boolean contains(double latitude, double longitude)
	{
		try {
			adjustLatitude(latitude);
			adjustLongitude(longitude);
			return true;
		} catch (CoordinateSpaceException ex) {
			return false;
		}
	}
	

	public double getNorth() {
		return north;
	}

	public void setNorth(double north) {
		this.north = north;
	}

	public double getSouth() {
		return south;
	}

	public void setSouth(double south) {
		this.south = south;
	}

	public double getEast() {
		return east;
	}

	public void setEast(double east) {
		this.east = east;
	}

	public double getWest() {
		return west;
	}

	public void setWest(double west) {
		this.west = west;
	}
	
	
	
	
}
