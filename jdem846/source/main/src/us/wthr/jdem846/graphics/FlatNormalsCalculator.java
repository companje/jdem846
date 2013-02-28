package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.math.Vector;

public class FlatNormalsCalculator extends SphericalNormalsCalculator implements INormalsCalculator
{

	public FlatNormalsCalculator(double latitudeResolution, double longitudeResolution)
	{
		this(DemConstants.EARTH_MEAN_RADIUS, latitudeResolution, longitudeResolution, null);
	}
	
	public FlatNormalsCalculator(Planet planet, double latitudeResolution, double longitudeResolution)
	{
		this(planet, latitudeResolution, longitudeResolution, null);
	}
	
	public FlatNormalsCalculator(Planet planet, double latitudeResolution, double longitudeResolution, ElevationFetchCallback elevationFetchCallback)
	{
		super(planet, latitudeResolution, longitudeResolution, elevationFetchCallback);

	}
	
	public FlatNormalsCalculator(double latitudeResolution, double longitudeResolution, ElevationFetchCallback elevationFetchCallback)
	{
		this(DemConstants.EARTH_MEAN_RADIUS, latitudeResolution, longitudeResolution, elevationFetchCallback);
	}
	
	public FlatNormalsCalculator(double _meanRadius, double latitudeResolution, double longitudeResolution)
	{
		this(_meanRadius, latitudeResolution, longitudeResolution, null);
	}
	
	public FlatNormalsCalculator(double _meanRadius, double latitudeResolution, double longitudeResolution, ElevationFetchCallback elevationFetchCallback)
	{
		super(_meanRadius, latitudeResolution, longitudeResolution, elevationFetchCallback);
	}
	


	
	@Override
	public void calculateNormal(double latitude, double longitude, Vector normal)
	{
		calculateNormal(latitude, longitude, normal, elevationFetchCallback);
	}
	
	@Override
	public void calculateNormal(double latitude, double longitude, Vector normal, ElevationFetchCallback elevationFetchCallback)
	{
		double eLat = latitude;
		double eLon = longitude + longitudeResolution;

		double sLat = latitude - latitudeResolution;
		double sLon = longitude;

		double wLat = latitude;
		double wLon = longitude - longitudeResolution;

		double nLat = latitude + latitudeResolution;
		double nLon = longitude;

		
		double midElev = elevationFetchCallback.getElevation(latitude, longitude);
		double eElev = elevationFetchCallback.getElevation(eLat, eLon);
		double sElev = elevationFetchCallback.getElevation(sLat, sLon);
		double wElev = elevationFetchCallback.getElevation(wLat, wLon);
		double nElev = elevationFetchCallback.getElevation(nLat, nLon);

		eElev = (eElev == DemConstants.ELEV_NO_DATA) ? midElev : eElev;
		sElev = (sElev == DemConstants.ELEV_NO_DATA) ? midElev : sElev;
		wElev = (wElev == DemConstants.ELEV_NO_DATA) ? midElev : wElev;
		nElev = (nElev == DemConstants.ELEV_NO_DATA) ? midElev : nElev;

		calculateNormal(latitude, longitude, midElev, nElev, sElev, eElev, wElev, normal);
	}
	
	@Override
	public void calculateNormal(double latitude, double longitude, double elevation, Vector normal)
	{

		calculateNormal(latitude, longitude, elevation, elevation, elevation, elevation, elevation, normal);
	}

	@Override
	public void calculateNormal(double latitude, double longitude, double midElev, double nElev, double sElev, double eElev, double wElev, Vector normal)
	{
		super.calculateNormal(0, 0, midElev, nElev, sElev, eElev, wElev, normal);

	}

	
}
