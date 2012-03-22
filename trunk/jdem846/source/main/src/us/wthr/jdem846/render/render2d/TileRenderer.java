package us.wthr.jdem846.render.render2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.DemPoint;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RayTracingException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.geom.TriangleStrip;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.gis.Coordinate;
import us.wthr.jdem846.gis.CoordinateTypeEnum;
import us.wthr.jdem846.gis.datetime.EarthDateTime;
import us.wthr.jdem846.gis.datetime.SolarCalculator;
import us.wthr.jdem846.gis.datetime.SolarPosition;
import us.wthr.jdem846.gis.datetime.SolarUtil;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.lighting.LightSourceSpecifyTypeEnum;
import us.wthr.jdem846.lighting.LightingContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.rasterdata.ElevationDataMap;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.BasicRenderEngine;
import us.wthr.jdem846.render.CanvasProjection;
import us.wthr.jdem846.render.CanvasRectangleFill;
import us.wthr.jdem846.render.InterruptibleProcess;
import us.wthr.jdem846.render.MatrixBuffer;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.RayTracing;
import us.wthr.jdem846.render.RayTracing.RasterDataFetchHandler;
import us.wthr.jdem846.render.RenderPipeline;
import us.wthr.jdem846.render.ShadowBuffer;
import us.wthr.jdem846.render.TriangleStripFill;
import us.wthr.jdem846.render.gfx.Vector;
import us.wthr.jdem846.scripting.ScriptProxy;
import us.wthr.jdem846.util.ColorSerializationUtil;

public class TileRenderer extends InterruptibleProcess
{
	private enum CornerEnum {
		NORTHEAST,
		NORTHWEST,
		SOUTHEAST,
		SOUTHWEST
	};
	
	private static Log log = Logging.getLog(TileRenderer.class);

	private ModelContext modelContext;
	private ModelColoring modelColoring;
	private ModelCanvas modelCanvas;
	private RenderPipeline renderPipeline;
	private CanvasProjection projection;
	
	
	protected RasterDataContext dataRasterContextSubset;

	private boolean tiledPrecaching;
	private double latitudeResolution;
	
	
	private double gridSize;
	private boolean doublePrecisionHillshading;
	private boolean lightingEnabled;
	private double relativeLightIntensity;
	private double relativeDarkIntensity;
	private double metersResolution;
	private int spotExponent;
	private double lightingMultiple;
	private double elevationMax;
	private double elevationMin;
	private double solarElevation;
	private double solarAzimuth;
	private double solarZenith;
	private double longitudeResolution;
	private double latitudeGridSize;
	private double longitudeGridSize; 
	private boolean useSimpleCanvasFill;
	private RayTracing lightSourceRayTracer;
	private boolean rayTraceShadows;
	private double shadowIntensity;
	private boolean doubleBuffered;
	private double elevationMultiple;
	private double minimumElevation;
	private double maximumElevation;
	
	
	private MapPoint point = new MapPoint();
	private Perspectives perspectives = new Perspectives();
	private double normal[] = new double[3];
	private double backLeftPoints[] = new double[3];
	private double backRightPoints[] = new double[3];
	private double frontLeftPoints[] = new double[3];
	private double frontRightPoints[] = new double[3];
	private double sunsource[] = new double[3];	
	private int colorBufferA[] = new int[4];
	private int colorBufferB[] = new int[4];
	private double[] pointNormal = new double[3];
	private int[] backgroundColor = new int[4];
	
	private double latitudeSlices = 50;
	private double longitudeSlices = 50;
	
	private boolean getStandardResolutionElevation = true;
	private boolean interpolateData = true;
	private boolean averageOverlappedData = true;
	
	private double lightZenith;
	private double darkZenith;
	private LightSourceSpecifyTypeEnum lightSourceType;
	private long lightOnDate;
	private boolean recalcLightOnEachPoint;
	private SolarCalculator solarCalculator;
	private SolarPosition position;
	private EarthDateTime datetime;
	private Coordinate latitudeCoordinate;
	private Coordinate longitudeCoordinate;
	private boolean sunIsUp = false;
	
	private double northLimit;
	private double southLimit;
	private double eastLimit;
	private double westLimit;
	
	private int tileHeight;
	
	private ElevationDataMap elevationMap;

	public TileRenderer(ModelContext modelContext)
	{
		this(modelContext, null, null, null);
	}
	
	public TileRenderer(ModelContext modelContext, RenderPipeline pipeline)
	{
		this(modelContext, null, null, pipeline);
	}
	
	public TileRenderer(ModelContext modelContext, ModelColoring modelColoring)
	{
		this(modelContext, modelColoring, null, null);
	}
	
	public TileRenderer(ModelContext modelContext, ModelColoring modelColoring, RenderPipeline renderPipeline)
	{
		this(modelContext, modelColoring, null, renderPipeline);
	}
	
	public TileRenderer(ModelContext modelContext, ModelColoring modelColoring, ModelCanvas modelCanvas)
	{
		this(modelContext, modelColoring, modelCanvas, null);
	}
	
	public TileRenderer(ModelContext modelContext, ModelColoring modelColoring, ModelCanvas modelCanvas, RenderPipeline renderPipeline)
	{
		this.modelContext = modelContext;
		this.modelColoring = modelColoring;
		this.modelCanvas = modelCanvas;
		this.renderPipeline = renderPipeline;
		

		
		
		//shadowBuffer = new ShadowBuffer(modelContext);
		
		//resetBuffers();
		//setUpLightSource();
		//rowRenderer = new RowRenderer(modelContext, modelColoring, modelCanvas);
	}
	
	public void prepare(double north, double south, double east, double west, boolean resetCache, boolean resetDataRange)
	{
		log.info("Preparing tile renderer properties");
		
		modelContext.updateContext();
		
		northLimit = north;
		southLimit = south;
		eastLimit = east;
		westLimit = west;
		
		
		tileHeight = JDem846Properties.getIntProperty("us.wthr.jdem846.performance.tileSize");
		
		ColorSerializationUtil.stringToColor(modelContext.getModelOptions().getBackgroundColor(), backgroundColor);
		
		tiledPrecaching = JDem846Properties.getProperty("us.wthr.jdem846.performance.precacheStrategy").equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_TILED);
		
		
		//latitudeResolution = modelContext.getLatitudeResolution();
		//longitudeResolution = modelContext.getLongitudeResolution();
		latitudeResolution = modelContext.getModelDimensions().getOutputLatitudeResolution();
		longitudeResolution = modelContext.getModelDimensions().getOutputLongitudeResolution();
		
		//latitudeResolution = modelContext.getRasterDataContext().getEffectiveLatitudeResolution();
		//longitudeResolution = modelContext.getRasterDataContext().getEffectiveLongitudeResolution();
		
		//latitudeSlices = modelContext.getModelOptions().getDoubleOption("us.wthr.jdem846.modelOptions.latitudeSlices");
		//longitudeSlices = modelContext.getModelOptions().getDoubleOption("us.wthr.jdem846.modelOptions.longitudeSlices");
		
		
		//latitudeResolution = (northLimit - southLimit - effectiveLatitudeResolution) / latitudeSlices;
		//longitudeResolution = (eastLimit - westLimit - effectiveLongitudeResolution) / longitudeSlices;
		
		doubleBuffered = JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.doubleBuffered");
		
		if (doubleBuffered && resetCache) {
			elevationMap = ElevationDataMap.create(northLimit, 
					southLimit - latitudeResolution, 
					eastLimit + longitudeResolution, 
					westLimit, 
					modelContext.getModelDimensions().getOutputLatitudeResolution(),
					modelContext.getModelDimensions().getOutputLongitudeResolution());
		}
		
		if (resetDataRange) {
			try {
				determineDataRangeLowRes();
			} catch (Exception ex) {
				log.error("Error determining elevation min & max: " + ex.getMessage(), ex);
			}
		}
		
		minimumElevation = modelContext.getRasterDataContext().getDataMinimumValue();
		maximumElevation = modelContext.getRasterDataContext().getDataMaximumValue();
		
		
		LightingContext lightingContext = modelContext.getLightingContext();
		lightSourceType = lightingContext.getLightSourceSpecifyType();
		lightOnDate = lightingContext.getLightingOnDate();
		recalcLightOnEachPoint = lightingContext.getRecalcLightOnEachPoint();
		lightZenith = lightingContext.getLightZenith();
		darkZenith = lightingContext.getDarkZenith();
		
		
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
				
				double latitude = (northLimit + southLimit) / 2.0;
				double longitude = (eastLimit + westLimit) / 2.0;
				
				setUpLightSource(latitude, longitude, 0, 0, true);
			}
		}
		
		
		//getStandardResolutionElevation = modelContext.getModelOptions().getBooleanOption(ModelOptionNamesEnum.STANDARD_RESOLUTION_RETRIEVAL);
		//interpolateData = modelContext.getModelOptions().getBooleanOption(ModelOptionNamesEnum.INTERPOLATE_HIGHER_RESOLUTION);
		//averageOverlappedData = modelContext.getModelOptions().getBooleanOption(ModelOptionNamesEnum.AVERAGE_OVERLAPPING_RASTER_DATA);
		
		getStandardResolutionElevation = JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.standardResolutionRetrieval");
		interpolateData = JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.interpolateToHigherResolution");
		averageOverlappedData = JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.averageOverlappedData");
		
		
		lightingEnabled = modelContext.getLightingContext().isLightingEnabled();
		spotExponent = modelContext.getLightingContext().getSpotExponent();
		relativeLightIntensity = modelContext.getLightingContext().getRelativeLightIntensity();
		relativeDarkIntensity = modelContext.getLightingContext().getRelativeDarkIntensity();
		lightingMultiple = modelContext.getLightingContext().getLightingMultiple();
		
		solarAzimuth = modelContext.getLightingContext().getLightingAzimuth();
		solarElevation = modelContext.getLightingContext().getLightingElevation();
		
		elevationMultiple = modelContext.getModelOptions().getElevationMultiple();
		
		modelColoring = ColoringRegistry.getInstance(modelContext.getModelOptions().getColoringType()).getImpl();
		projection = modelContext.getModelCanvas().getCanvasProjection();
		
		
		
		//modelContext.getModelDimensions().outputLatitudeResolution = latitudeResolution;
		//modelContext.getModelDimensions().outputLongitudeResolution = longitudeResolution;
		
		rayTraceShadows = modelContext.getLightingContext().getRayTraceShadows();
		shadowIntensity = modelContext.getLightingContext().getShadowIntensity();
		if (rayTraceShadows) {
			lightSourceRayTracer = new RayTracing(
					modelContext,
					new RasterDataFetchHandler() {
						public double getRasterData(double latitude, double longitude) throws Exception {
							return _getRasterData(latitude, longitude);
						}
			});
		} else {
			lightSourceRayTracer = null;
		}
		
		//this.resetBuffers();
	}
	
	public void dispose()
	{
		log.info("Disposing tile renderer");
		
		if (elevationMap != null) {
			elevationMap.clear();
			elevationMap = null;
		}
		
		
		perspectives = null;
		normal = null;
		backLeftPoints = null;
		backRightPoints = null;
		frontLeftPoints = null;
		frontRightPoints = null;
		sunsource = null;	
		colorBufferA = null;
		colorBufferB = null;
		pointNormal = null;
		backgroundColor = null;
	}
	
	public void renderTile() throws RenderEngineException
	{
		
		
		
		try {
			loadRasterDataSubset(northLimit, southLimit, eastLimit, westLimit);
		} catch (DataSourceException ex) {
			throw new RenderEngineException("Error loading data subset: " + ex.getMessage(), ex);
		}
		
		
		//RasterDataContext dataProxy = modelContext.getRasterDataContext();//.getSubSet(northLimit, southLimit, eastLimit, westLimit);

		// TODO: If Buffered
		/*
		try {
			loadDataBuffers(northLimit, southLimit-latitudeResolution, eastLimit+longitudeResolution, westLimit);
		} catch (RenderEngineException ex) {
			throw new RenderEngineException("Error loading data buffer: " + ex.getMessage(), ex);
		}
		*/
		
		onTileBefore(modelCanvas);
		
		//List<ModelPoint> points = new LinkedList<ModelPoint>();
		
		
		//log.info("Initializing model point buffer...");
		//elevationMap = ElevationDataMap.create(northLimit, southLimit-latitudeResolution, eastLimit+longitudeResolution, westLimit, latitudeResolution, longitudeResolution);
		

		log.info("Processing data points...");
		
		ModelCanvas modelCanvas = modelContext.getModelCanvas();
		

		
		//if (modelContext.getModelOptions().getBooleanOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintRasterPreview")) {
			try {
				paintRasterPlot(modelCanvas);
			} catch (Exception ex) {
				log.error("Error painting raster grid: " + ex.getMessage(), ex);
			}
		//}
		/*
		for (double latitude = northLimit; latitude > southLimit; latitude-=latitudeResolution) {
			for (double longitude = westLimit; longitude < eastLimit; longitude+=longitudeResolution) {
		
		//for (double latitude = southLimit; latitude <= northLimit + latitudeResolution; latitude+=latitudeResolution) {
		//	for (double longitude = eastLimit; longitude >= westLimit - longitudeResolution; longitude-=longitudeResolution) {
				doElevation(latitude, longitude);
				
				this.checkPause();
				if (isCancelled()) {
					break;
				}
			}
			
		}
		*/
		
		
		//elevationMap.clear();
		//elevationMap = null;

		onTileAfter(modelCanvas);
		//unloadDataBuffers();


	}
	

	protected void paintRasterPlot(ModelCanvas canvas) throws Exception
	{
		int[] rgba = new int[4];
		rgba[3] = 255;
		

		RasterDataContext rasterDataContext = modelContext.getRasterDataContext();
		ImageDataContext imageDataContext = modelContext.getImageDataContext();
		
		if (rasterDataContext.getRasterDataListSize() == 0 && imageDataContext.getImageListSize() == 0) {
			return;
		}
		
		
		double north = northLimit;
		double south = southLimit;
		double east = eastLimit;
		double west = westLimit;

		//double maxLon = east;
		//double minLat = south;
		
		double maxLon = east + longitudeResolution;
		double minLat = south - latitudeResolution;
		
		TriangleStrip strip = null;

		//double cacheHeight = ((north - south) / 12.0);
		double cacheHeight = rasterDataContext.getLatitudeResolution() * tileHeight;
		double nextCachePoint = north;
		
		for (double lat = north; lat >= minLat; lat-=latitudeResolution) {
			//strip.reset();
			double lastElevN = rasterDataContext.getDataMinimumValue();
			double lastElevS = rasterDataContext.getDataMinimumValue();
			
			if (lastElevN == DemConstants.ELEV_NO_DATA && lastElevS == DemConstants.ELEV_NO_DATA) {
				lastElevN = 0;
				lastElevS = 0;
			}
			
			double nwLat = lat;
			double swLat = lat - latitudeResolution;
			
			strip = new TriangleStrip();
			
			if (lat <= nextCachePoint && tiledPrecaching) {
				
				double southCache = lat - cacheHeight - latitudeResolution;
				try {
					unloadDataBuffers();
					loadDataBuffers(nwLat, southCache, east, west);
				} catch (RenderEngineException ex) {
					throw new RenderEngineException("Error loading data buffer: " + ex.getMessage(), ex);
				}
				
				nextCachePoint = lat - cacheHeight;
			}
			
			for (double lon = west; lon <= maxLon; lon+=longitudeResolution) {
				
				double nwLon = lon;
				
				double swLon = lon;

				double elev = 0;
				
				
				// NW
				elev = calculateShadedColor(nwLat, nwLon, rgba);
				if (elev == DemConstants.ELEV_NO_DATA) {
					rgba[0] = backgroundColor[0];
					rgba[1] = backgroundColor[1];
					rgba[2] = backgroundColor[2];
					rgba[3] = backgroundColor[3];
					elev = lastElevN;
					//continue;
				} else {
					lastElevN = elev;
				}
				Vertex nwVtx = createVertex(nwLat, nwLon, elev, rgba);
				
				
				// SW
				elev = calculateShadedColor(swLat, swLon, rgba);
				if (elev == DemConstants.ELEV_NO_DATA) {
					rgba[0] = backgroundColor[0];
					rgba[1] = backgroundColor[1];
					rgba[2] = backgroundColor[2];
					rgba[3] = backgroundColor[3];
					elev = lastElevS;
					//continue;
				} else {
					lastElevS = elev;
				}
				Vertex swVtx = createVertex(swLat, swLon, elev, rgba);
				
				
				strip.addVertex(nwVtx);
				strip.addVertex(swVtx);

				
				
			}
			
			if (renderPipeline != null) {
				renderPipeline.submit(new TriangleStripFill(strip));
			} else {
				canvas.fillShape(strip);
			}
			
			
			//
			
		}
		
		unloadDataBuffers();
		
	}
	

	protected double calculateShadedColor(double latitude, double longitude, int[] rgba) throws DataSourceException, RayTracingException, RenderEngineException
	{

		
		double eLat = latitude;
		double eLon = longitude + longitudeResolution;
		
		double sLat = latitude - latitudeResolution;
		double sLon = longitude;
		
		double wLat = latitude;
		double wLon = longitude - longitudeResolution;
		
		double nLat = latitude + latitudeResolution;
		double nLon = longitude;
		
		
		double midElev = getRasterData(latitude, longitude);
		
		
		
		if (midElev == DemConstants.ELEV_NO_DATA)
			return DemConstants.ELEV_NO_DATA;
		
		
		//double min = modelContext.getRasterDataContext().getDataMinimumValue();
		//double max = modelContext.getRasterDataContext().getDataMaximumValue();
		//modelColoring.getGradientColor(midElev, min, max, colorBufferA);
		getPointColor(latitude, longitude, midElev, colorBufferA);
		onGetPointColor(latitude, longitude, midElev, minimumElevation, maximumElevation, colorBufferA);
		
		
		
		if (lightingEnabled) {
			
			
			double eElev = getRasterData(eLat, eLon);
			double sElev = getRasterData(sLat, sLon);
			double wElev = getRasterData(wLat, wLon);
			double nElev = getRasterData(nLat, nLon);
			
			
			if (eElev == DemConstants.ELEV_NO_DATA)
				eElev = midElev;
			if (sElev == DemConstants.ELEV_NO_DATA)
				sElev = midElev;
			if (wElev == DemConstants.ELEV_NO_DATA)
				wElev = midElev;
			if (nElev == DemConstants.ELEV_NO_DATA)
				nElev = midElev;
			
			resetBuffers(latitude, longitude);
			setUpLightSource(latitude, longitude, 0, 0, recalcLightOnEachPoint);
			
			/*
			 * Ok, just trust me on these ones.... 
			 */
			
			// NW Normal
			calculateNormal(0.0, wElev, midElev, nElev, CornerEnum.SOUTHEAST, normal);
			pointNormal[0] = normal[0];
			pointNormal[1] = normal[1];
			pointNormal[2] = normal[2];
			
			// SW Normal
			calculateNormal(wElev, 0.0, sElev, midElev, CornerEnum.NORTHEAST, normal);
			pointNormal[0] += normal[0];
			pointNormal[1] += normal[1];
			pointNormal[2] += normal[2];
			
			// SE Normal
			calculateNormal(midElev, sElev, 0.0, eElev, CornerEnum.NORTHWEST, normal);
			pointNormal[0] += normal[0];
			pointNormal[1] += normal[1];
			pointNormal[2] += normal[2];
			
			// NE Normal
			calculateNormal(nElev, midElev, eElev, 0.0, CornerEnum.SOUTHWEST, normal);
			pointNormal[0] += normal[0];
			pointNormal[1] += normal[1];
			pointNormal[2] += normal[2];
			
			normal[0] = pointNormal[0] / 4.0;
			normal[1] = pointNormal[1] / 4.0;
			normal[2] = pointNormal[2] / 4.0;
			
			double dot = calculateDotProduct(latitude, longitude);
			
			if (this.rayTraceShadows) {
				if (lightSourceRayTracer.isRayBlocked(this.solarElevation, this.solarAzimuth, latitude, longitude, midElev)) {
					// I'm not 100% happy with this method...
					dot = dot - (2 * shadowIntensity);
					if (dot < -1.0) {
						dot = -1.0;
					}
				}
			}
			
			if (dot > 0) {
				dot *= relativeLightIntensity;
			} else if (dot < 0) {
				dot *= relativeDarkIntensity;
			}
			
			dot = Math.pow(dot, spotExponent);
			
			
			copyRgba(colorBufferA, rgba);
			ColorAdjustments.adjustBrightness(rgba, dot);
			//ColorAdjustments.adjustBrightness(colorBufferB, dot);
			//ColorAdjustments.interpolateColor(colorBufferA, colorBufferB, rgba, lightingMultiple);
		
			
			
		} else {
			copyRgba(colorBufferA, rgba);
		}
		rgba[3] = 255;
		
		return midElev;
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
	
	protected double calculateDotProduct(double latitude, double longitude)
	{
		
		double dot = calculateTerrainDotProduct();

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
		
		
		
		
		
		return dot;
		
		
	}
	
	protected double calculateSphericalDotProduct(double latitude, double longitude)
	{
		return 0;
		/*
		calculateNormal(0, 0, 0, 0, CornerEnum.NORTHEAST, pointNormal);
		double dot = perspectives.dotProduct(pointNormal, sunsource);
		return dot;
		*/
	}
	
	protected double calculateTerrainDotProduct()
	{
		
		
		double dot = perspectives.dotProduct(normal, sunsource);
		
		return dot;
	}
	
	
	protected Vertex createVertex(double lat, double lon, double elev, int[] rgba) throws MapProjectionException
	{
    	projection.getPoint(lat, lon, elev, point);
    	
    	double x = point.column;
    	double y = point.row;
    	double z = point.z;
		
    	Vertex v = new Vertex(x, y, z, rgba);
    	return v;
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
	
	
	protected void loadRasterDataSubset(double north, double south, double east, double west) throws DataSourceException
	{
		dataRasterContextSubset = getRasterDataContext().getSubSet(north, south, east, west);
	}
	
	protected void loadDataBuffers(double north, double south, double east, double west) throws RenderEngineException
	{
		
		RasterDataContext dataContext = (dataRasterContextSubset != null) ? dataRasterContextSubset : getRasterDataContext();
		
		if (tiledPrecaching) {
			try {
				dataContext.fillBuffers(north, south, east, west);
			} catch (Exception ex) {
				throw new RenderEngineException("Failed to buffer data: " + ex.getMessage(), ex);
			}
		}
	}
	
	
	protected void unloadDataBuffers() throws RenderEngineException
	{
		RasterDataContext dataContext = (dataRasterContextSubset != null) ? dataRasterContextSubset : getRasterDataContext();
		if (tiledPrecaching) {
			try {
				dataContext.clearBuffers();
			} catch (Exception ex) {
				throw new RenderEngineException("Failed to clear buffer data: " + ex.getMessage(), ex);
			}
		}
	}
	
	

	protected double getRasterDataRaw(double latitude, double longitude) throws DataSourceException
	{
		double data = DemConstants.ELEV_NO_DATA;
		
		RasterDataContext dataContext = (dataRasterContextSubset != null) ? dataRasterContextSubset :  getRasterDataContext();
		
		if (dataContext.getRasterDataListSize() > 0) {
			if (getStandardResolutionElevation) {
				data = dataContext.getDataStandardResolution(latitude, longitude, averageOverlappedData, interpolateData);
			} else {
				data = dataContext.getDataAtEffectiveResolution(latitude, longitude, averageOverlappedData, interpolateData);
			}
		} else if (getImageDataContext().getImageListSize() > 0) {
			data = 0;
		} else {
			data = DemConstants.ELEV_NO_DATA;
		}
		
		return data;
	}
	
	protected double _getRasterData(double latitude, double longitude) throws DataSourceException, RenderEngineException
	{
		
		return getRasterData(latitude, longitude);
	}
	
	protected double getRasterData(double latitude, double longitude) throws DataSourceException, RenderEngineException
	{
		return getRasterData(latitude, longitude, true);
	}
	
	protected double getRasterData(double latitude, double longitude, boolean cache) throws DataSourceException, RenderEngineException
	{
		
		//ModelPoint modelPoint = getModelPoint(latitude, longitude);
		//if (modelPoint != null) {
		//	return modelPoint.getElevation();
		//}
		
		double elevation = DemConstants.ELEV_NO_DATA;
		if (doubleBuffered && cache) {
			elevation = elevationMap.get(latitude, longitude, DemConstants.ELEV_NO_DATA);
			if (elevation != DemConstants.ELEV_NO_DATA)
				return elevation;
		}
		
		
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
		
		elevation = getRasterDataRaw(latitude, longitude);

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
		
		if (doubleBuffered && cache) {
			elevationMap.put(latitude, longitude, elevation);
		}
		
		return elevation;
	}
	
	protected void resetBuffers(double latitude, double longitude)
	{
		double effLatRes = modelContext.getRasterDataContext().getEffectiveLatitudeResolution();
		double effLonRes = modelContext.getRasterDataContext().getEffectiveLongitudeResolution();
		
		Planet planet = PlanetsRegistry.getPlanet(modelContext.getModelOptions().getOption(ModelOptionNamesEnum.PLANET));
		double meanRadius = DemConstants.EARTH_MEAN_RADIUS;
		
		if (planet != null) {
			meanRadius = planet.getMeanRadius();
		}
		
		double resolutionMeters = RasterDataContext.getMetersResolution(meanRadius, latitude, longitude, effLatRes, effLonRes);
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
	
	public void determineDataRangeLowRes() throws DataSourceException, RenderEngineException
	{
		log.info("Redetermining elevation minimum & maximum...");
		
		RasterDataContext rasterDataContext = modelContext.getRasterDataContext();
		
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		if (rasterDataContext.getRasterDataListSize() > 0) {
		
			double north = rasterDataContext.getNorth();
			double south = rasterDataContext.getSouth();
			double east = rasterDataContext.getEast();
			double west = rasterDataContext.getWest();
			
			//double latStep = (north - south - modelContext.getRasterDataContext().getEffectiveLatitudeResolution()) / latitudeSlices;
			//double lonStep = (east - west - modelContext.getRasterDataContext().getEffectiveLongitudeResolution()) / longitudeSlices;
			
			
			
			for (double lon = west; lon < east - rasterDataContext.getLongitudeResolution(); lon+=longitudeResolution) {
				for (double lat = north; lat > south + rasterDataContext.getLatitudeResolution(); lat-=latitudeResolution) {
					double elevation = getRasterData(lat, lon);
					
					if (!Double.isNaN(elevation) && elevation != DemConstants.ELEV_NO_DATA) {
						min = MathExt.min(elevation, min);
						max = MathExt.max(elevation, max);
					}
					
				}
				
			}
		} else {
			max = 0;
			min = 0;
		}
		
		rasterDataContext.setDataMaximumValue(max);
		rasterDataContext.setDataMinimumValue(min);
		
		
	}
	
	protected void copyRgba(int[] rgba0, int[] rgba1)
	{
		rgba1[0] = rgba0[0];
		rgba1[1] = rgba0[1];
		rgba1[2] = rgba0[2];
		rgba1[3] = rgba0[3];
		
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected RasterDataContext getRasterDataContext()
	{
		return modelContext.getRasterDataContext();
	}
	
	protected ImageDataContext getImageDataContext()
	{
		return modelContext.getImageDataContext();
	}

	protected ModelOptions getModelOptions()
	{
		return modelContext.getModelOptions();
	}
	
	protected LightingContext getLightingContext()
	{
		return modelContext.getLightingContext();
	}
	

}
