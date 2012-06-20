package us.wthr.jdem846.model.processing.util;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vectors;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.model.ModelPointGrid;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.model.processing.dataload.CornerEnum;
import us.wthr.jdem846.rasterdata.RasterDataContext;

public class SurfaceNormalCalculator
{
	private static Log log = Logging.getLog(SurfaceNormalCalculator.class);
	
	
	private ModelPointGrid modelGrid;
	
	private double latitudeResolution;
	private double longitudeResolution;

	private double meanRadius = DemConstants.EARTH_MEAN_RADIUS;
	
	private double[] normalBufferA = new double[3];
	private double[] normalBufferB = new double[3];
	
	protected double backLeftPoints[] = new double[3];
	protected double backRightPoints[] = new double[3];
	protected double frontLeftPoints[] = new double[3];
	protected double frontRightPoints[] = new double[3];
	
	protected double xyzN[] = new double[3];
	protected double xyzS[] = new double[3];
	protected double xyzE[] = new double[3];
	protected double xyzW[] = new double[3];
	protected double xyzC[] = new double[3];
	
	
	protected double normalNW[] = new double[3];
	protected double normalSW[] = new double[3];
	protected double normalSE[] = new double[3];
	protected double normalNE[] = new double[3];
	
	protected ViewPerspective viewPerspective;
	
	public SurfaceNormalCalculator(ModelPointGrid modelGrid, Planet planet, double latitudeResolution, double longitudeResolution)
	{
		this(modelGrid, planet, latitudeResolution, longitudeResolution, null);
	}
	
	public SurfaceNormalCalculator(ModelPointGrid modelGrid, Planet planet, double latitudeResolution, double longitudeResolution, ViewPerspective viewPerspective)
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
	
	public void calculateNormalSpherical(double latitude, double longitude, double[] normal)
	{
		
		double eLat = latitude;
		double eLon = longitude + longitudeResolution;
		
		double sLat = latitude - latitudeResolution;
		double sLon = longitude;
		
		double wLat = latitude;
		double wLon = longitude - longitudeResolution;
		
		double nLat = latitude + latitudeResolution;
		double nLon = longitude;
		
		double midElev = modelGrid.getElevation(latitude, longitude);
		double eElev = modelGrid.getElevation(eLat, eLon);
		double sElev = modelGrid.getElevation(sLat, sLon);
		double wElev = modelGrid.getElevation(wLat, wLon);
		double nElev = modelGrid.getElevation(nLat, nLon);
		
		eElev = (eElev == DemConstants.ELEV_NO_DATA) ? midElev : eElev;
		sElev = (sElev == DemConstants.ELEV_NO_DATA) ? midElev : sElev;
		wElev = (wElev == DemConstants.ELEV_NO_DATA) ? midElev : wElev;
		nElev = (nElev == DemConstants.ELEV_NO_DATA) ? midElev : nElev;
		
		calculateNormalSpherical(latitude,
						longitude,
						midElev,
						nElev,
						sElev,
						eElev,
						wElev,
						normal);
	}
	
	public void calculateNormalSpherical(double latitude, 
			double longitude, 
			double elevation,
			double[] normal)
	{
			
		calculateNormalSpherical(latitude, 
				longitude,
				elevation,
				elevation,
				elevation,
				elevation,
				elevation,
				normal);
	}
	
	
	public void calculateNormalSpherical(double latitude, 
								double longitude, 
								double midElev,
								double nElev,
								double sElev,
								double eElev,
								double wElev,
								double[] normal)
	{
		resetBuffers(latitude, longitude);

		
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
		
		normal[0] = (normalNW[0] + normalSW[0] + normalSE[0] + normalNE[0]) / 4.0;
		normal[1] = (normalNW[1] + normalSW[1] + normalSE[1] + normalNE[1]) / 4.0;
		normal[2] = (normalNW[2] + normalSW[2] + normalSE[2] + normalNE[2]) / 4.0;

		
	}
	
	
	protected void fillPointXYZ(double[] P, double latitude, double longitude, double elevation)
	{
		double radius = meanRadius * 1000 + elevation;

		Spheres.getPoint3D(longitude, latitude, radius, P);
		
		if (viewPerspective != null) {
			Vectors.rotate(0.0, viewPerspective.getRotateY(), 0.0, P);
			Vectors.rotate(viewPerspective.getRotateX(), 0.0, 0.0, P);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void calculateNormalFlat(double latitude, double longitude, double[] normal)
	{
		
		double eLat = latitude;
		double eLon = longitude + longitudeResolution;
		
		double sLat = latitude - latitudeResolution;
		double sLon = longitude;
		
		double wLat = latitude;
		double wLon = longitude - longitudeResolution;
		
		double nLat = latitude + latitudeResolution;
		double nLon = longitude;
		
		double midElev = modelGrid.getElevation(latitude, longitude);
		double eElev = modelGrid.getElevation(eLat, eLon);
		double sElev = modelGrid.getElevation(sLat, sLon);
		double wElev = modelGrid.getElevation(wLat, wLon);
		double nElev = modelGrid.getElevation(nLat, nLon);
		
		eElev = (eElev == DemConstants.ELEV_NO_DATA) ? midElev : eElev;
		sElev = (sElev == DemConstants.ELEV_NO_DATA) ? midElev : sElev;
		wElev = (wElev == DemConstants.ELEV_NO_DATA) ? midElev : wElev;
		nElev = (nElev == DemConstants.ELEV_NO_DATA) ? midElev : nElev;
		
		calculateNormalFlat(latitude,
						longitude,
						midElev,
						nElev,
						sElev,
						eElev,
						wElev,
						normal);
	}
	
	public void calculateNormal(double latitude, 
			double longitude, 
			double elevation,
			double[] normal)
	{
			
		calculateNormalFlat(latitude, 
				longitude,
				elevation,
				elevation,
				elevation,
				elevation,
				elevation,
				normal);
	}
	
	
	public void calculateNormalFlat(double latitude, 
								double longitude, 
								double midElev,
								double nElev,
								double sElev,
								double eElev,
								double wElev,
								double[] normal)
	{
		resetBuffers(latitude, longitude);


		
		calculateNormal(0.0, wElev, midElev, nElev, CornerEnum.SOUTHEAST, normalBufferA);
		normalBufferB[0] = normalBufferA[0];
		normalBufferB[1] = normalBufferA[1];
		normalBufferB[2] = normalBufferA[2];
		
		// SW Normal
		calculateNormal(wElev, 0.0, sElev, midElev, CornerEnum.NORTHEAST, normalBufferA);
		normalBufferB[0] += normalBufferA[0];
		normalBufferB[1] += normalBufferA[1];
		normalBufferB[2] += normalBufferA[2];
		
		// SE Normal
		calculateNormal(midElev, sElev, 0.0, eElev, CornerEnum.NORTHWEST, normalBufferA);
		normalBufferB[0] += normalBufferA[0];
		normalBufferB[1] += normalBufferA[1];
		normalBufferB[2] += normalBufferA[2];
		
		// NE Normal
		calculateNormal(nElev, midElev, eElev, 0.0, CornerEnum.SOUTHWEST, normalBufferA);
		normalBufferB[0] += normalBufferA[0];
		normalBufferB[1] += normalBufferA[1];
		normalBufferB[2] += normalBufferA[2];
		
		normal[0] = normalBufferB[0] / 4.0;
		normal[1] = normalBufferB[1] / 4.0;
		normal[2] = normalBufferB[2] / 4.0;
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected void calculateNormal(double nw, double sw, double se, double ne, CornerEnum corner, double[] normal)
	{
		
		
		
		backLeftPoints[1] = nw;
		backRightPoints[1] = ne;
		frontLeftPoints[1] = sw;
		frontRightPoints[1] = se;
		
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
		
		backLeftPoints[0] = -xzRes;
		backLeftPoints[1] = 0.0;
		backLeftPoints[2] = -xzRes;
		
		backRightPoints[0] = xzRes;
		backRightPoints[1] = 0.0;
		backRightPoints[2] = -xzRes;
		
		frontLeftPoints[0] = -xzRes;
		frontLeftPoints[1] = 0.0;
		frontLeftPoints[2] = xzRes;
		
		frontRightPoints[0] = xzRes;
		frontRightPoints[1] = 0.0;
		frontRightPoints[2] = xzRes;
		
	}
}
