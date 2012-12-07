package us.wthr.jdem846.model.processing.util;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.math.Vectors;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.model.processing.dataload.CornerEnum;
import us.wthr.jdem846.modelgrid.IModelGrid;
import us.wthr.jdem846.rasterdata.RasterDataContext;

public class SurfaceNormalCalculator
{
	private static Log log = Logging.getLog(SurfaceNormalCalculator.class);

	private IModelGrid modelGrid;

	private double latitudeResolution;
	private double longitudeResolution;

	private double meanRadius = DemConstants.EARTH_MEAN_RADIUS;

	private Vector normalBufferA = new Vector();
	private Vector normalBufferB = new Vector();

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

	protected ViewPerspective viewPerspective;

	public SurfaceNormalCalculator(IModelGrid modelGrid, Planet planet, double latitudeResolution, double longitudeResolution)
	{
		this(modelGrid, planet, latitudeResolution, longitudeResolution, null);
	}

	public SurfaceNormalCalculator(IModelGrid modelGrid, Planet planet, double latitudeResolution, double longitudeResolution, ViewPerspective viewPerspective)
	{
		this.modelGrid = modelGrid;
		this.meanRadius = planet.getMeanRadius();
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		this.viewPerspective = viewPerspective;
	}

	public SurfaceNormalCalculator(Planet planet, double latitudeResolution, double longitudeResolution, ViewPerspective viewPerspective)
	{
		this.modelGrid = null;
		this.meanRadius = planet.getMeanRadius();
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		this.viewPerspective = viewPerspective;
	}

	public SurfaceNormalCalculator(double latitudeResolution, double longitudeResolution, ViewPerspective viewPerspective)
	{
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		this.viewPerspective = viewPerspective;
	}

	public void calculateNormalSpherical(double latitude, double longitude, Vector normal)
	{

		double eLat = latitude;
		double eLon = longitude + longitudeResolution;

		double sLat = latitude - latitudeResolution;
		double sLon = longitude;

		double wLat = latitude;
		double wLon = longitude - longitudeResolution;

		double nLat = latitude + latitudeResolution;
		double nLon = longitude;

		double midElev = modelGrid.getElevation(latitude, longitude, true);
		double eElev = modelGrid.getElevation(eLat, eLon, true);
		double sElev = modelGrid.getElevation(sLat, sLon, true);
		double wElev = modelGrid.getElevation(wLat, wLon, true);
		double nElev = modelGrid.getElevation(nLat, nLon, true);

		eElev = (eElev == DemConstants.ELEV_NO_DATA) ? midElev : eElev;
		sElev = (sElev == DemConstants.ELEV_NO_DATA) ? midElev : sElev;
		wElev = (wElev == DemConstants.ELEV_NO_DATA) ? midElev : wElev;
		nElev = (nElev == DemConstants.ELEV_NO_DATA) ? midElev : nElev;

		calculateNormalSpherical(latitude, longitude, midElev, nElev, sElev, eElev, wElev, normal);
	}

	public void calculateNormalSpherical(double latitude, double longitude, double elevation, Vector normal)
	{

		calculateNormalSpherical(latitude, longitude, elevation, elevation, elevation, elevation, elevation, normal);
	}

	public void calculateNormalSpherical(double latitude, double longitude, double midElev, double nElev, double sElev, double eElev, double wElev, Vector normal)
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
		double radius = meanRadius * 1000 + elevation;

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

		Spheres.getPoint3D(_longitude, _latitude, radius, P);

	}

	public void calculateNormalFlat(double latitude, double longitude, Vector normal)
	{

		double eLat = latitude;
		double eLon = longitude + longitudeResolution;

		double sLat = latitude - latitudeResolution;
		double sLon = longitude;

		double wLat = latitude;
		double wLon = longitude - longitudeResolution;

		double nLat = latitude + latitudeResolution;
		double nLon = longitude;

		double midElev = modelGrid.getElevation(latitude, longitude, true);
		double eElev = modelGrid.getElevation(eLat, eLon, true);
		double sElev = modelGrid.getElevation(sLat, sLon, true);
		double wElev = modelGrid.getElevation(wLat, wLon, true);
		double nElev = modelGrid.getElevation(nLat, nLon, true);

		eElev = (eElev == DemConstants.ELEV_NO_DATA) ? midElev : eElev;
		sElev = (sElev == DemConstants.ELEV_NO_DATA) ? midElev : sElev;
		wElev = (wElev == DemConstants.ELEV_NO_DATA) ? midElev : wElev;
		nElev = (nElev == DemConstants.ELEV_NO_DATA) ? midElev : nElev;

		calculateNormalFlat(latitude, longitude, midElev, nElev, sElev, eElev, wElev, normal);
	}

	public void calculateNormal(double latitude, double longitude, double elevation, Vector normal)
	{

		calculateNormalFlat(latitude, longitude, elevation, elevation, elevation, elevation, elevation, normal);
	}

	public void calculateNormalFlat(double latitude, double longitude, double midElev, double nElev, double sElev, double eElev, double wElev, Vector normal)
	{
		resetBuffers(latitude, longitude);

		calculateNormal(0.0, wElev, midElev, nElev, CornerEnum.SOUTHEAST, normalBufferA);
		normalBufferB.x = normalBufferA.x;
		normalBufferB.y = normalBufferA.y;
		normalBufferB.z = normalBufferA.z;

		// SW Normal
		calculateNormal(wElev, 0.0, sElev, midElev, CornerEnum.NORTHEAST, normalBufferA);
		normalBufferB.x += normalBufferA.x;
		normalBufferB.y += normalBufferA.y;
		normalBufferB.z += normalBufferA.z;

		// SE Normal
		calculateNormal(midElev, sElev, 0.0, eElev, CornerEnum.NORTHWEST, normalBufferA);
		normalBufferB.x += normalBufferA.x;
		normalBufferB.y += normalBufferA.y;
		normalBufferB.z += normalBufferA.z;

		// NE Normal
		calculateNormal(nElev, midElev, eElev, 0.0, CornerEnum.SOUTHWEST, normalBufferA);
		normalBufferB.x += normalBufferA.x;
		normalBufferB.y += normalBufferA.y;
		normalBufferB.z += normalBufferA.z;

		normal.x = normalBufferB.x / 4.0;
		normal.y = normalBufferB.y / 4.0;
		normal.z = normalBufferB.z / 4.0;

	}

	protected void calculateNormal(double nw, double sw, double se, double ne, CornerEnum corner, Vector normal)
	{

		backLeftPoints.y = nw;
		backRightPoints.y = ne;
		frontLeftPoints.y = sw;
		frontRightPoints.y = se;

		if (corner == CornerEnum.NORTHWEST) {
			Vectors.calcNormal(backLeftPoints, frontLeftPoints, backRightPoints, normal);
		} else if (corner == CornerEnum.SOUTHWEST) {
			Vectors.calcNormal(backLeftPoints, frontLeftPoints, frontRightPoints, normal);
		} else if (corner == CornerEnum.SOUTHEAST) {
			Vectors.calcNormal(frontLeftPoints, frontRightPoints, backRightPoints, normal);
		} else if (corner == CornerEnum.NORTHEAST) {
			Vectors.calcNormal(backLeftPoints, frontRightPoints, backRightPoints, normal);
		}

	}

	public void resetBuffers(double latitude, double longitude)
	{
		double resolutionMeters = RasterDataContext.getMetersResolution(meanRadius, latitude, longitude, latitudeResolution, longitudeResolution);
		double xzRes = (resolutionMeters / 2.0);

		backLeftPoints.x = -xzRes;
		backLeftPoints.y = 0.0;
		backLeftPoints.z = -xzRes;

		backRightPoints.x = xzRes;
		backRightPoints.y = 0.0;
		backRightPoints.z = -xzRes;

		frontLeftPoints.x = -xzRes;
		frontLeftPoints.y = 0.0;
		frontLeftPoints.z = xzRes;

		frontRightPoints.x = xzRes;
		frontRightPoints.y = 0.0;
		frontRightPoints.z = xzRes;

	}
}
