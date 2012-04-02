package us.wthr.jdem846.render.render3;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.TimeZone;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.gis.Coordinate;
import us.wthr.jdem846.gis.CoordinateTypeEnum;
import us.wthr.jdem846.gis.datetime.EarthDateTime;
import us.wthr.jdem846.gis.datetime.SolarCalculator;
import us.wthr.jdem846.gis.datetime.SolarPosition;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.lighting.LightSourceSpecifyTypeEnum;
import us.wthr.jdem846.lighting.LightingContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.RayTracing;
import us.wthr.jdem846.render.RayTracing.RasterDataFetchHandler;
import us.wthr.jdem846.render.gfx.Vector;

import us.wthr.jdem846.render.scaling.ElevationScaler;
import us.wthr.jdem846.render.scaling.ElevationScalerFactory;
import us.wthr.jdem846.scripting.ScriptProxy;

public class ModelBuilder
{
	private static Log log = Logging.getLog(ModelBuilder.class);
	
	private ModelContext modelContext;
	private ModelGrid modelGrid;
	private ModelColoring modelColoring;
	private ElevationScaler elevationScaler;
	
	protected double relativeLightIntensity;
	protected double relativeDarkIntensity;
	protected int spotExponent;
	
	private double latitudeResolution;
	private double longitudeResolution;
	
	private double[] normalBufferA = new double[3];
	private double[] normalBufferB = new double[3];
	
	protected double backLeftPoints[] = new double[3];
	protected double backRightPoints[] = new double[3];
	protected double frontLeftPoints[] = new double[3];
	protected double frontRightPoints[] = new double[3];
	
	protected double sunsource[] = new double[3];
	protected double solarElevation;
	protected double solarAzimuth;
	protected double solarZenith;
	
	protected int[] rgbaBuffer = new int[4];
	
	protected Perspectives perspectives = new Perspectives();
	
	private double lightingMultiple = 1.0;
	
	private Planet planet;
	
	double north;
	double south;
	double east;
	double west;
	
	protected double maximumElevationTrue;
	protected double minimumElevation;
	protected double maximumElevation;
	
	protected boolean getStandardResolutionElevation = true;
	protected boolean interpolateData = true;
	protected boolean averageOverlappedData = true;
	
	protected double lightZenith;
	protected double darkZenith;
	protected LightSourceSpecifyTypeEnum lightSourceType;
	protected long lightOnDate;
	protected boolean recalcLightOnEachPoint;
	protected SolarCalculator solarCalculator;
	protected SolarPosition position;
	protected EarthDateTime datetime;
	protected Coordinate latitudeCoordinate;
	protected Coordinate longitudeCoordinate;
	protected boolean sunIsUp = false;
	
	protected boolean tiledPrecaching;
	
	protected RayTracing lightSourceRayTracer;
	protected boolean rayTraceShadows;
	protected double shadowIntensity;
	
	protected boolean useScripting = true;
	
	public ModelBuilder(ModelContext modelContext, ModelGrid modelGrid)
	{
		this.modelContext = modelContext;
		this.modelGrid = modelGrid;
		
		
		
	}
	
	public void prepare() throws RenderEngineException
	{

		tiledPrecaching = JDem846Properties.getProperty("us.wthr.jdem846.performance.precacheStrategy").equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_TILED);
		

		
		relativeLightIntensity = modelContext.getLightingContext().getRelativeLightIntensity();
		relativeDarkIntensity = modelContext.getLightingContext().getRelativeDarkIntensity();
		spotExponent = modelContext.getLightingContext().getSpotExponent();
		
		minimumElevation = modelContext.getRasterDataContext().getDataMinimumValue();
		maximumElevation = modelContext.getRasterDataContext().getDataMaximumValue();
		maximumElevationTrue = modelContext.getRasterDataContext().getDataMaximumValueTrue();
		
		
		try {
			elevationScaler = ElevationScalerFactory.createElevationScaler(modelContext.getModelOptions().getElevationScaler(), modelContext.getModelOptions().getElevationMultiple(), minimumElevation, maximumElevationTrue);
		} catch (Exception ex) {
			throw new RenderEngineException("Error creating elevation scaler: " + ex.getMessage(), ex);
		}
		modelContext.getRasterDataContext().setElevationScaler(elevationScaler);
		maximumElevation = modelContext.getRasterDataContext().getDataMaximumValue();
		

		modelColoring = ColoringRegistry.getInstance(modelContext.getModelOptions().getColoringType()).getImpl();
		modelColoring.setElevationScaler(elevationScaler);
		
		
		
		latitudeResolution = modelContext.getModelDimensions().getOutputLatitudeResolution();
		longitudeResolution = modelContext.getModelDimensions().getOutputLongitudeResolution();
		
		north = modelContext.getNorth();
		south = modelContext.getSouth();
		east = modelContext.getEast();
		west = modelContext.getWest();
		
		
		planet = PlanetsRegistry.getPlanet(modelContext.getModelOptions().getOption(ModelOptionNamesEnum.PLANET));
		
		solarAzimuth = modelContext.getLightingContext().getLightingAzimuth();
		solarElevation = modelContext.getLightingContext().getLightingElevation();

		lightSourceType = modelContext.getLightingContext().getLightSourceSpecifyType();
		lightOnDate = modelContext.getLightingContext().getLightingOnDate();
		recalcLightOnEachPoint = modelContext.getLightingContext().getRecalcLightOnEachPoint();
		lightZenith = modelContext.getLightingContext().getLightZenith();
		darkZenith = modelContext.getLightingContext().getDarkZenith();
		
		
		if (lightSourceType == LightSourceSpecifyTypeEnum.BY_AZIMUTH_AND_ELEVATION) {
			sunIsUp = true;
			setUpLightSource(0, 0, modelContext.getLightingContext().getLightingElevation(), modelContext.getLightingContext().getLightingAzimuth(), true);
		}
		
		if (lightSourceType == LightSourceSpecifyTypeEnum.BY_DATE_AND_TIME) {
			
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("GMT"));
			
			cal.setTimeInMillis(lightOnDate);
			
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			int second = cal.get(Calendar.SECOND);
			
			log.info("Setting initial date/time to " + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);
			
			position = new SolarPosition();
			datetime = new EarthDateTime(year, month, day, hour, minute, second, 0, false);
			latitudeCoordinate = new Coordinate(0.0, CoordinateTypeEnum.LATITUDE);
			longitudeCoordinate = new Coordinate(0.0, CoordinateTypeEnum.LONGITUDE);
			solarCalculator = new SolarCalculator();
			solarCalculator.setDatetime(datetime);
			
		
		
			
			if (!recalcLightOnEachPoint) {
				
				double latitude = (north + south) / 2.0;
				double longitude = (east + west) / 2.0;
				
				setUpLightSource(latitude, longitude, 0, 0, true);
			}
		}
		
		getStandardResolutionElevation = JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.standardResolutionRetrieval");
		interpolateData = JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.interpolateToHigherResolution");
		averageOverlappedData = JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.averageOverlappedData");
		
		lightingMultiple = modelContext.getLightingContext().getLightingMultiple();
		rayTraceShadows = modelContext.getLightingContext().getRayTraceShadows();
		shadowIntensity = modelContext.getLightingContext().getShadowIntensity();
		if (rayTraceShadows) {
			lightSourceRayTracer = new RayTracing(
					modelContext,
					new RasterDataFetchHandler() {
						public double getRasterData(double latitude, double longitude) throws Exception {
							return getElevationAtPoint(latitude, longitude);
						}
			});
		} else {
			lightSourceRayTracer = null;
		}
		
		
	}
	
	
	public void process() throws RenderEngineException
	{
		
		ModelPointCycler pointCycler = new ModelPointCycler(modelContext);
		
		log.info("Filling model grid...");
		pointCycler.forEachModelPoint(true, new ModelPointAdapter() {
			public void onModelPoint(double latitude, double longitude)
			{
				try {
					double elev = getElevation(latitude, longitude);
					if (elev != DemConstants.ELEV_NO_DATA) {
						modelGrid.get(latitude, longitude).setElevation(elev);
					}
					
				} catch (Exception ex) {
					log.error("Error processing point elevation: " + ex.getMessage(), ex);
				}
			}
		});
		
		log.info("Calculating surface normals...");
		pointCycler.forEachModelPoint(false, new ModelPointAdapter() {
			public void onModelPoint(double latitude, double longitude)
			{
				try {
					calculateNormal(latitude, longitude);
				} catch (Exception ex) {
					log.error("Error processing point surface normal: " + ex.getMessage(), ex);
				}
			}
		});
		
		log.info("Calculating dot products...");
		pointCycler.forEachModelPoint(false, new ModelPointAdapter() {
			public void onModelPoint(double latitude, double longitude)
			{
				try {
					calculateDotProduct(latitude, longitude);
				} catch (Exception ex) {
					log.error("Error processing point dot product: " + ex.getMessage(), ex);
				}
			}
		});
		
		
		log.info("Calculating grid colors...");
		pointCycler.forEachModelPoint(false, new ModelPointAdapter() {
			public void onModelPoint(double latitude, double longitude)
			{
				try {
					processPointColor(latitude, longitude);
				} catch (Exception ex) {
					log.error("Error processing point color: " + ex.getMessage(), ex);
				}
			}
		});
	}
	
	
	protected void calculateNormal(double latitude, double longitude)
	{
		resetBuffers(latitude, longitude);
		
		ModelPoint midPoint = modelGrid.get(latitude, longitude);
		
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
	

	
	protected void calculateDotProduct(double latitude, double longitude)
	{
		ModelPoint modelPoint = modelGrid.get(latitude, longitude);
		
		double dot = perspectives.dotProduct(modelPoint.getNormal(), sunsource);
		
		
		double lower = lightZenith;
		double upper = darkZenith;
		
		if (solarZenith > lower && solarZenith <= upper) {
			double range = (solarZenith - lower) / (upper - lower);
			dot = dot - (2 * range);
		} else if (solarZenith > upper) {
			dot = dot - (2 * 1.0);
		}
		if (dot < -1.0) {
			dot = -1.0;
		}
		
		modelPoint.setDotProduct(dot);
		
	}
	
	



	protected void processPointColor(double latitude, double longitude) throws Exception
	{
		ModelPoint modelPoint = modelGrid.get(latitude, longitude);
		getPointColor(latitude, longitude, modelPoint.getElevation(), rgbaBuffer);
		
		double dot = modelPoint.getDotProduct();
		
		if (dot > 0) {
			dot *= relativeLightIntensity;
		} else if (dot < 0) {
			dot *= relativeDarkIntensity;
		}
	
		if (spotExponent != 1) {
			dot = MathExt.pow(dot, spotExponent);
		}

		ColorAdjustments.adjustBrightness(rgbaBuffer, dot);
		modelPoint.setRgba(rgbaBuffer);
	}
	
	protected void getPointColor(double latitude, double longitude, double elevation, int[] rgba) throws DataSourceException
	{
		
		if (modelContext.getImageDataContext() != null
				&& modelContext.getImageDataContext().getColor(latitude, longitude, rgba)) {
			// All right, then
		} else {
			modelColoring.getGradientColor(elevation, minimumElevation, maximumElevation, rgba);
		}

	}
	
	
	
	protected double getElevationAtPoint(double latitude, double longitude)
	{
		ModelPoint modelPoint = modelGrid.get(latitude, longitude);
		if (modelPoint != null) {
			return modelPoint.getElevation();
		} else {
			return DemConstants.ELEV_NO_DATA;
		}
	}
	
	
	protected double getRasterDataRaw(double latitude, double longitude) throws DataSourceException
	{
		double data = DemConstants.ELEV_NO_DATA;
		
		RasterDataContext rasterDataContext = modelContext.getRasterDataContext();
		//RasterDataContext dataContext = (dataRasterContextSubset != null) ? dataRasterContextSubset :  getRasterDataContext();
		
		boolean getStandardResolutionElevation = true;
		
		if (rasterDataContext.getRasterDataListSize() > 0) {
			if (getStandardResolutionElevation) {
				data = rasterDataContext.getDataStandardResolution(latitude, longitude, true, true);
			} else {
				data = rasterDataContext.getDataAtEffectiveResolution(latitude, longitude, true, true);
			}
		} else if (modelContext.getImageDataContext().getImageListSize() > 0) {
			data = 0;
		} else {
			data = DemConstants.ELEV_NO_DATA;
		}
		
		return data;
	}
	

	protected double getElevation(double latitude, double longitude) throws DataSourceException, RenderEngineException
	{

		
		double elevation = DemConstants.ELEV_NO_DATA;

		
		if (useScripting) {
			try {
				Object before = onGetElevationBefore(latitude, longitude);
				
				if (before instanceof Double) {
					return (Double) before;
				} else if (before instanceof BigDecimal) {
					return ((BigDecimal)before).doubleValue();
				} else if (before instanceof Integer) {
					return ((Integer)before).doubleValue();
				}
				
			} catch (Exception ex) {
				throw new RenderEngineException("Error executing onGetElevationBefore(" + latitude + ", " + longitude + ")", ex);
			}
		}
		
		elevation = getRasterDataRaw(latitude, longitude);
		
		if (useScripting) {
			try {
				Object after = onGetElevationAfter(latitude, longitude, elevation);
				
				if (after instanceof Double) {
					elevation = (Double) after;
				} else if (after instanceof BigDecimal) {
					elevation = ((BigDecimal)after).doubleValue();
				} else if (after instanceof Integer) {
					elevation = ((Integer)after).doubleValue();
				}
				
			} catch (Exception ex) {
				throw new RenderEngineException("Error executing onGetElevationAfter(" + latitude + ", " + longitude + ", " + elevation + ")", ex);
			}
		}

		
		return elevation;
	}

	protected void setUpLightSource(double latitude, double longitude, double solarElevation, double solarAzimuth, boolean force)
	{

		if (force && lightSourceType == LightSourceSpecifyTypeEnum.BY_AZIMUTH_AND_ELEVATION) {
			setUpLightSourceBasic(solarElevation, solarAzimuth);
		} else if (lightSourceType == LightSourceSpecifyTypeEnum.BY_DATE_AND_TIME) {
			setUpLightSourceOnDate(latitude, longitude);
		}
		
		
		
	}
	
	protected void setUpLightSourceOnDate(double latitude, double longitude)
	{
		// This stuff in here probably can be vastly optimized...
		
		//int timezone = (int) Math.floor((longitude / 180.0) * 12.0);

		//datetime.setTimezone(timezone);
		
		latitudeCoordinate.fromDecimal(latitude);
		longitudeCoordinate.fromDecimal(longitude);

		
		solarCalculator.update();
		solarCalculator.setLatitude(latitudeCoordinate);
		solarCalculator.setLongitude(longitudeCoordinate);
		
		solarAzimuth = solarCalculator.solarAzimuthAngle();
		solarElevation = solarCalculator.solarElevationAngle();
		solarZenith = solarCalculator.solarZenithAngle();
		
		if (solarZenith > darkZenith) {
			sunIsUp = false;
		} else {
			sunIsUp = true;
		}
		//sunIsUp = true;
		setUpLightSourceBasic(solarElevation, solarAzimuth);
		
	}
	
	protected void setUpLightSourceBasic(double solarElevation, double solarAzimuth)
	{
		
		sunsource[0] = 0.0;
		sunsource[1] = 0.0;
		sunsource[2] = -149598000000.0;
		Vector.rotate(solarElevation, -solarAzimuth, 0, sunsource);
		
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
	
	

	protected void onTileBefore(ModelCanvas modelCanvas) throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.onTileBefore(modelContext, modelCanvas);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
	}
	
	protected void onTileAfter(ModelCanvas modelCanvas) throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.onTileBefore(modelContext, modelCanvas);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
	}
	
	
	protected Object onGetElevationBefore(double latitude, double longitude) throws RenderEngineException
	{
		Object result = null;
		try {
			ScriptProxy scriptProxy = modelContext.getScriptProxy();
			if (scriptProxy != null) {
				result = scriptProxy.onGetElevationBefore(modelContext, latitude, longitude);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
		return result;
	}

	protected Object onGetElevationAfter(double latitude, double longitude, double elevation) throws RenderEngineException
	{
		Object result = null;
		try {
			ScriptProxy scriptProxy = modelContext.getScriptProxy();
			if (scriptProxy != null) {
				result = scriptProxy.onGetElevationAfter(modelContext, latitude, longitude, elevation);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
		return result;
	}
	
	
	//scriptColorBuffer
	protected void onGetPointColor(double latitude, double longitude, double elevation, double elevationMinimum, double elevationMaximum, int[] color) throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.onGetPointColor(modelContext, latitude, longitude, elevation, elevationMinimum, elevationMaximum, color);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}

	}
	
}
