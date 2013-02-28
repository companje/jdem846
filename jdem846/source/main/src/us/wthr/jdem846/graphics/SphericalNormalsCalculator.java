package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.math.Vectors;

public class SphericalNormalsCalculator implements INormalsCalculator
{
	
	protected double latitudeResolution;
	protected double longitudeResolution;

	protected double meanRadius = DemConstants.EARTH_MEAN_RADIUS;
	protected Planet planet = null;
	

	protected Vector backLeftPoints = new Vector();
	protected Vector backRightPoints = new Vector();
	protected Vector frontLeftPoints = new Vector();
	protected Vector frontRightPoints = new Vector();

	protected Vector xyzN = new Vector();
	protected Vector xyzS = new Vector();
	protected Vector xyzE = new Vector();
	protected Vector xyzW = new Vector();
	protected Vector xyzC = new Vector();

	protected Vector normalNW = new Vector();
	protected Vector normalSW = new Vector();
	protected Vector normalSE = new Vector();
	protected Vector normalNE = new Vector();
	
	protected ElevationFetchCallback elevationFetchCallback;
	
	
	public SphericalNormalsCalculator(double latitudeResolution, double longitudeResolution)
	{
		this(DemConstants.EARTH_MEAN_RADIUS, latitudeResolution, longitudeResolution, null);
	}
	
	public SphericalNormalsCalculator(Planet planet, double latitudeResolution, double longitudeResolution)
	{
		this(planet, latitudeResolution, longitudeResolution, null);
	}
	
	public SphericalNormalsCalculator(Planet planet, double latitudeResolution, double longitudeResolution, ElevationFetchCallback elevationFetchCallback)
	{
		//this((planet != null) ? planet.getMeanRadius() : DemConstants.EARTH_MEAN_RADIUS, latitudeResolution, longitudeResolution, elevationFetchCallback);
		this.planet = planet;
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		
		if (elevationFetchCallback != null) {
			this.elevationFetchCallback = elevationFetchCallback;	
		} else {
			this.elevationFetchCallback = new ElevationFetchCallback() {

				@Override
				public double getElevation(double latitude, double longitude)
				{
					return meanRadius;
				}
				
			};
		}
	}
	
	public SphericalNormalsCalculator(double latitudeResolution, double longitudeResolution, ElevationFetchCallback elevationFetchCallback)
	{
		this(DemConstants.EARTH_MEAN_RADIUS, latitudeResolution, longitudeResolution, elevationFetchCallback);
	}
	
	public SphericalNormalsCalculator(double _meanRadius, double latitudeResolution, double longitudeResolution)
	{
		this(_meanRadius, latitudeResolution, longitudeResolution, null);
	}
	
	public SphericalNormalsCalculator(double _meanRadius, double latitudeResolution, double longitudeResolution, ElevationFetchCallback elevationFetchCallback)
	{
		this.meanRadius = _meanRadius;
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		
		if (elevationFetchCallback != null) {
			this.elevationFetchCallback = elevationFetchCallback;	
		} else {
			this.elevationFetchCallback = new ElevationFetchCallback() {

				@Override
				public double getElevation(double latitude, double longitude)
				{
					return meanRadius;
				}
				
			};
		}
	}
	

	
	public void calculateNormal(double latitude, double longitude, Vector normal)
	{
		calculateNormal(latitude, longitude, normal, elevationFetchCallback);

	}
	
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
	
	
	public void calculateNormal(double latitude, double longitude, double elevation, Vector normal)
	{

		calculateNormal(latitude, longitude, elevation, elevation, elevation, elevation, elevation, normal);
	}

	public void calculateNormal(double latitude, double longitude, double midElev, double nElev, double sElev, double eElev, double wElev, Vector normal)
	{
		// resetBuffers(latitude, longitude);

		double eLat = latitude;
		double eLon = longitude + longitudeResolution;

		double sLat = latitude - latitudeResolution;
		double sLon = longitude;

		double wLat = latitude;
		double wLon = longitude - longitudeResolution;

		double nLat = latitude + latitudeResolution;
		double nLon = longitude;

		fillPointXYZ(xyzN, nLat, nLon, nElev);
		fillPointXYZ(xyzS, sLat, sLon, sElev);
		fillPointXYZ(xyzE, eLat, eLon, eElev);
		fillPointXYZ(xyzW, wLat, wLon, wElev);
		fillPointXYZ(xyzC, latitude, longitude, midElev);

		Vectors.calcNormal(xyzN, xyzW, xyzC, normalNW); // NW
		Vectors.calcNormal(xyzW, xyzS, xyzC, normalSW); // SW
		Vectors.calcNormal(xyzC, xyzS, xyzE, normalSE); // SE
		Vectors.calcNormal(xyzN, xyzC, xyzE, normalNE); // NE

		normal.x = (normalNW.x + normalSW.x + normalSE.x + normalNE.x) / 4.0;
		normal.y = (normalNW.y + normalSW.y + normalSE.y + normalNE.y) / 4.0;
		normal.z = (normalNW.z + normalSW.z + normalSE.z + normalNE.z) / 4.0;

	}

	protected void fillPointXYZ(Vector P, double latitude, double longitude, double elevation)
	{
		

		double _latitude = latitude;
		double _longitude = longitude;

		if (_latitude >= 90) {
			_latitude = 90 - (_latitude - 90.0);
			if (_longitude < 0) {
				_longitude += 180.0;
			} else {
				_longitude -= 180.0;
			}

		}
		
		if (this.planet != null) {
			planet.getEllipsoid().getXyzCoordinates(_latitude, _longitude, elevation, P);
		} else {
			double radius = meanRadius + elevation;
			Spheres.getPoint3D(_longitude, _latitude, radius, P);
		}
		

	}
	
	
	
	
}
