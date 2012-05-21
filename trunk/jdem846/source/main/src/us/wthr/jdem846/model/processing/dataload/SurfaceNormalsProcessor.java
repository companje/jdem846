package us.wthr.jdem846.model.processing.dataload;

import java.util.Calendar;
import java.util.TimeZone;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.exception.RayTracingException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.gis.Coordinate;
import us.wthr.jdem846.gis.CoordinateTypeEnum;
import us.wthr.jdem846.gis.datetime.EarthDateTime;
import us.wthr.jdem846.gis.datetime.SolarCalculator;
import us.wthr.jdem846.gis.datetime.SolarPosition;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.lighting.LightSourceSpecifyTypeEnum;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vectors;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.model.ModelPoint;
import us.wthr.jdem846.model.ModelPointHandler;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.RayTracing;
import us.wthr.jdem846.render.RayTracing.RasterDataFetchHandler;

@GridProcessing(id="us.wthr.jdem846.model.processing.dataload.SurfaceNormalsProcessor",
				name="Surface Normals Process",
				type=GridProcessingTypesEnum.DATA_LOAD,
				optionModel=SurfaceNormalsOptionModel.class,
				enabled=true
				)
public class SurfaceNormalsProcessor extends AbstractGridProcessor implements GridProcessor, ModelPointHandler
{
	private static Log log = Logging.getLog(SurfaceNormalsProcessor.class);

	private double latitudeResolution;
	private double longitudeResolution;
	
	private double lightingMultiple = 1.0;
	
	private double[] normalBufferA = new double[3];
	private double[] normalBufferB = new double[3];
	
	protected double backLeftPoints[] = new double[3];
	protected double backRightPoints[] = new double[3];
	protected double frontLeftPoints[] = new double[3];
	protected double frontRightPoints[] = new double[3];

	private double[] xyzN = new double[3];
	private double[] xyzS = new double[3];
	private double[] xyzE = new double[3];
	private double[] xyzW = new double[3];
	private double[] xyzC = new double[3];
	
	private double[] normalNW = new double[3];
	private double[] normalNE = new double[3];
	private double[] normalSW = new double[3];
	private double[] normalSE = new double[3];
	
	protected Perspectives perspectives = new Perspectives();

	private Planet planet;
	
	double north;
	double south;
	double east;
	double west;
	
	public SurfaceNormalsProcessor()
	{
		
	}
	
	public SurfaceNormalsProcessor(ModelContext modelContext, ModelGrid modelGrid)
	{
		super(modelContext, modelGrid);
	}
	
	@Override
	public void prepare() throws RenderEngineException
	{
		SurfaceNormalsOptionModel optionModel = (SurfaceNormalsOptionModel) this.getProcessOptionModel();
		
	
		latitudeResolution = getModelDimensions().getOutputLatitudeResolution();
		longitudeResolution = getModelDimensions().getOutputLongitudeResolution();
		
		north = getGlobalOptionModel().getNorthLimit();
		south = getGlobalOptionModel().getSouthLimit();
		east = getGlobalOptionModel().getEastLimit();
		west = getGlobalOptionModel().getWestLimit();
		
		
		planet = PlanetsRegistry.getPlanet(getGlobalOptionModel().getPlanet());
		
		
	}

	@Override
	public void process() throws RenderEngineException
	{
		super.process();
	}
	
	@Override
	public void onCycleStart() throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModelLatitudeStart(double latitude)
			throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModelPoint(double latitude, double longitude)
			throws RenderEngineException
	{
		resetBuffers(latitude, longitude);

		ModelPoint modelPoint = modelGrid.get(latitude, longitude);
		
		calculateNormal(modelPoint, latitude, longitude);
		
		
	}

	@Override
	public void onModelLatitudeEnd(double latitude)
			throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCycleEnd() throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}


	protected void calculateNormal(ModelPoint midPoint, double latitude, double longitude)
	{
		double eLat = latitude;
		double eLon = longitude + longitudeResolution;
		
		double sLat = latitude - latitudeResolution;
		double sLon = longitude;
		
		double wLat = latitude;
		double wLon = longitude - longitudeResolution;
		
		double nLat = latitude + latitudeResolution;
		double nLon = longitude;
		
		ModelPoint ePoint = modelGrid.get(eLat, eLon);
		ModelPoint sPoint = modelGrid.get(sLat, sLon);
		ModelPoint wPoint = modelGrid.get(wLat, wLon);
		ModelPoint nPoint = modelGrid.get(nLat, nLon);
		
		double midElev = midPoint.getElevation();
		double eElev = (ePoint != null) ? ePoint.getElevation() : midPoint.getElevation();
		double sElev = (sPoint != null) ? sPoint.getElevation() : midPoint.getElevation();
		double wElev = (wPoint != null) ? wPoint.getElevation() : midPoint.getElevation();
		double nElev = (nPoint != null) ? nPoint.getElevation() : midPoint.getElevation();
		
		
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
		
		/*
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
		
		normalBufferB[0] = normalBufferB[0] / 4.0;
		normalBufferB[1] = normalBufferB[1] / 4.0;
		normalBufferB[2] = normalBufferB[2] / 4.0;
		
		midPoint.setNormal(normalBufferB);
		*/
		
	}
	
	
	
	
	protected void calculateNormal(double nw, double sw, double se, double ne, CornerEnum corner, double[] normal)
	{
		
		
		
		backLeftPoints[1] = nw * lightingMultiple;
		backRightPoints[1] = ne * lightingMultiple;
		frontLeftPoints[1] = sw * lightingMultiple;
		frontRightPoints[1] = se * lightingMultiple;
		
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
		double meanRadius = DemConstants.EARTH_MEAN_RADIUS;
		
		if (planet != null) {
			meanRadius = planet.getMeanRadius();
		}
		meanRadius = meanRadius * 1000 + elevation;

		Spheres.getPoint3D(longitude, latitude, meanRadius, P);
		
	}
	

	

	

	
	
	protected void resetBuffers(double latitude, double longitude)
	{
		double meanRadius = DemConstants.EARTH_MEAN_RADIUS;
		
		if (planet != null) {
			meanRadius = planet.getMeanRadius();
		}
		
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
