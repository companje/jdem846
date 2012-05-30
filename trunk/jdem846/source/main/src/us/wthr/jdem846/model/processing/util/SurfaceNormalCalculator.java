package us.wthr.jdem846.model.processing.util;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.model.ModelPointGrid;
import us.wthr.jdem846.model.processing.dataload.CornerEnum;
import us.wthr.jdem846.rasterdata.RasterDataContext;

public class SurfaceNormalCalculator
{
	private static Log log = Logging.getLog(SurfaceNormalCalculator.class);
	
	
	private ModelPointGrid modelGrid;
	
	private double latitudeResolution;
	private double longitudeResolution;
	
	protected Perspectives perspectives = new Perspectives();


	private double meanRadius = DemConstants.EARTH_MEAN_RADIUS;
	
	private double[] normalBufferA = new double[3];
	private double[] normalBufferB = new double[3];
	
	protected double backLeftPoints[] = new double[3];
	protected double backRightPoints[] = new double[3];
	protected double frontLeftPoints[] = new double[3];
	protected double frontRightPoints[] = new double[3];
	
	public SurfaceNormalCalculator(ModelPointGrid modelGrid, Planet planet, double latitudeResolution, double longitudeResolution)
	{
		this.modelGrid = modelGrid;
		this.meanRadius = planet.getMeanRadius();
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
	}
	
	public SurfaceNormalCalculator(Planet planet, double latitudeResolution, double longitudeResolution)
	{
		this.modelGrid = null;
		this.meanRadius = planet.getMeanRadius();
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
	}
	
	public SurfaceNormalCalculator(double latitudeResolution, double longitudeResolution)
	{
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
	}
	
	
	public void calculateNormal(double latitude, double longitude, double[] normal)
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
		
		calculateNormal(latitude,
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
			
		calculateNormal(latitude, 
				longitude,
				elevation,
				elevation,
				elevation,
				elevation,
				elevation,
				normal);
	}
	
	
	public void calculateNormal(double latitude, 
								double longitude, 
								double midElev,
								double nElev,
								double sElev,
								double eElev,
								double wElev,
								double[] normal)
	{
		resetBuffers(latitude, longitude);

		
		/*
		fillPointXYZ(xyzN, nLat, nLon, nElev);
		fillPointXYZ(xyzS, sLat, sLon, sElev);
		fillPointXYZ(xyzE, eLat, eLon, eElev);
		fillPointXYZ(xyzW, wLat, wLon, wElev);
		fillPointXYZ(xyzC, latitude, longitude, midElev);
		
		perspectives.calcNormal(xyzN, xyzW, xyzC, normalNW); // NW
		perspectives.calcNormal(xyzW, xyzS, xyzC, normalSW); // SW
		perspectives.calcNormal(xyzC, xyzS, xyzE, normalSE); // SE
		perspectives.calcNormal(xyzN, xyzC, xyzE, normalNE); // NE
		
		normalBufferB[0] = (normalNW[0] + normalSW[0] + normalSE[0] + normalNE[0]) / 4.0;
		normalBufferB[1] = (normalNW[1] + normalSW[1] + normalSE[1] + normalNE[1]) / 4.0;
		normalBufferB[2] = (normalNW[2] + normalSW[2] + normalSE[2] + normalNE[2]) / 4.0;
		
		
		midPoint.setNormal(normalBufferB);
		*/
		
		
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
			perspectives.calcNormal(backLeftPoints, frontLeftPoints, backRightPoints, normal);
		} else if (corner == CornerEnum.SOUTHWEST) {
			perspectives.calcNormal(backLeftPoints, frontLeftPoints, frontRightPoints, normal);
		} else if (corner == CornerEnum.SOUTHEAST) {
			perspectives.calcNormal(frontLeftPoints, frontRightPoints, backRightPoints, normal);
		} else if (corner == CornerEnum.NORTHEAST) {
			perspectives.calcNormal(backLeftPoints, frontRightPoints, backRightPoints, normal);
		}
		
	}
	
	protected void fillPointXYZ(double[] P, double latitude, double longitude, double elevation)
	{
		double radius = meanRadius * 1000 + elevation;

		Spheres.getPoint3D(longitude, latitude, radius, P);
		
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
