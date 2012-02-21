package us.wthr.jdem846.render.render2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.DemPoint;
import us.wthr.jdem846.ModelContext;
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
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.MapPoint;
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
	private double longitudeResolution;
	private double latitudeGridSize;
	private double longitudeGridSize; 
	private boolean useSimpleCanvasFill;
	private RayTracing lightSourceRayTracer;
	private boolean rayTraceShadows;
	private double shadowIntensity;
	private double lightAzimuth;
	private double lightElevation;
	
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

	private double latitudeSlices = 50;
	private double longitudeSlices = 50;
	
	private boolean getStandardResolutionElevation = true;
	private boolean interpolateData = true;
	private boolean averageOverlappedData = true;
	
	
	
	private double northLimit;
	private double southLimit;
	private double eastLimit;
	private double westLimit;
	
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
		
		resetBuffers();
		//setUpLightSource();
		//rowRenderer = new RowRenderer(modelContext, modelColoring, modelCanvas);
	}
	
	public void prepare(double north, double south, double east, double west, boolean resetCache, boolean resetDataRange)
	{
		log.info("Resetting simple renderer cache");
		
		northLimit = north;
		southLimit = south;
		eastLimit = east;
		westLimit = west;
		
		tiledPrecaching = getModelOptions().getPrecacheStrategy().equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_TILED);
		
		
		latitudeResolution = modelContext.getRasterDataContext().getEffectiveLatitudeResolution();
		longitudeResolution = modelContext.getRasterDataContext().getEffectiveLongitudeResolution();
		
		//latitudeSlices = modelContext.getModelOptions().getDoubleOption("us.wthr.jdem846.modelOptions.latitudeSlices");
		//longitudeSlices = modelContext.getModelOptions().getDoubleOption("us.wthr.jdem846.modelOptions.longitudeSlices");
		
		
		//latitudeResolution = (northLimit - southLimit - effectiveLatitudeResolution) / latitudeSlices;
		//longitudeResolution = (eastLimit - westLimit - effectiveLongitudeResolution) / longitudeSlices;

		
		if (resetCache) {
			elevationMap = ElevationDataMap.create(northLimit, 
					southLimit - latitudeResolution, 
					eastLimit + longitudeResolution, 
					westLimit, 
					modelContext.getRasterDataContext().getEffectiveLatitudeResolution(), 
					modelContext.getRasterDataContext().getEffectiveLongitudeResolution());
		}
		
		if (resetDataRange) {
			try {
				determineDataRangeLowRes();
			} catch (Exception ex) {
				log.error("Error determining elevation min & max: " + ex.getMessage(), ex);
			}
		}
		
		setUpLightSource(modelContext.getLightingContext().getLightingElevation(), modelContext.getLightingContext().getLightingAzimuth());
		
		interpolateData = modelContext.getModelOptions().getBooleanOption("us.wthr.jdem846.modelOptions.simpleRenderer.data.interpolate");
		averageOverlappedData = modelContext.getModelOptions().getBooleanOption("us.wthr.jdem846.modelOptions.simpleRenderer.data.averageOverlappedData");
			
		getStandardResolutionElevation = modelContext.getModelOptions().getBooleanOption("us.wthr.jdem846.modelOptions.simpleRenderer.data.standardResolutionRetrieval");
		
		
		lightingEnabled = modelContext.getLightingContext().isLightingEnabled();
		spotExponent = modelContext.getLightingContext().getSpotExponent();
		relativeLightIntensity = modelContext.getLightingContext().getRelativeLightIntensity();
		relativeDarkIntensity = modelContext.getLightingContext().getRelativeDarkIntensity();
		lightingMultiple = modelContext.getLightingContext().getLightingMultiple();
		
		lightAzimuth = modelContext.getLightingContext().getLightingAzimuth();
		lightElevation = modelContext.getLightingContext().getLightingElevation();
		
		
		modelColoring = ColoringRegistry.getInstance(modelContext.getModelOptions().getColoringType()).getImpl();
		projection = modelContext.getModelCanvas().getCanvasProjection();
		
		
		
		modelContext.getModelDimensions().outputLatitudeResolution = latitudeResolution;
		modelContext.getModelDimensions().outputLongitudeResolution = longitudeResolution;
		
		rayTraceShadows = modelContext.getLightingContext().getRayTraceShadows();
		shadowIntensity = modelContext.getLightingContext().getShadowIntensity();
		if (rayTraceShadows) {
			lightSourceRayTracer = new RayTracing(lightAzimuth,
					lightElevation,
					modelContext,
					new RasterDataFetchHandler() {
						public double getRasterData(double latitude, double longitude) throws Exception {
							return getRasterData(latitude, longitude);
						}
			});
		} else {
			lightSourceRayTracer = null;
		}
		
	}
	
	public void dispose()
	{
		elevationMap.clear();
		elevationMap = null;
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
		try {
			loadDataBuffers(northLimit, southLimit-latitudeResolution, eastLimit+longitudeResolution, westLimit);
		} catch (RenderEngineException ex) {
			throw new RenderEngineException("Error loading data buffer: " + ex.getMessage(), ex);
		}

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
		unloadDataBuffers();


	}
	

	protected void paintRasterPlot(ModelCanvas canvas) throws Exception
	{
		int[] rgba = new int[4];
		rgba[3] = 255;
		

		RasterDataContext rasterDataContext = modelContext.getRasterDataContext();
		
		if (rasterDataContext.getRasterDataListSize() == 0) {
			return;
		}
		
		
		double north = northLimit;
		double south = southLimit;
		double east = eastLimit;
		double west = westLimit;

		double maxLon = east + longitudeResolution;
		double minLat = south - latitudeResolution;
		
		TriangleStrip strip = null;

		for (double lat = north; lat >= minLat; lat-=latitudeResolution) {
			//strip.reset();
			
			strip = new TriangleStrip();
			
			for (double lon = west; lon <= maxLon; lon+=longitudeResolution) {
				double nwLat = lat;
				double nwLon = lon;
				double swLat = lat - latitudeResolution;
				double swLon = lon;

				double elev = 0;
				
				// NW
				if ((elev = calculateShadedColor(nwLat, nwLon, rgba)) == DemConstants.ELEV_NO_DATA) 
					continue;
				Vertex nwVtx = createVertex(nwLat, nwLon, elev, rgba);
				
				// SW
				if ((elev = calculateShadedColor(swLat, swLon, rgba)) == DemConstants.ELEV_NO_DATA)
					continue;
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
		
		
		double min = modelContext.getRasterDataContext().getDataMinimumValue();
		double max = modelContext.getRasterDataContext().getDataMaximumValue();
		modelColoring.getGradientColor(midElev, min, max, colorBufferA);
		
		
		/*
		 * Ok, just trust me on these ones.... 
		 */
		
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
			
			double dot = calculateDotProduct();
			
			if (this.rayTraceShadows) {
				if (lightSourceRayTracer.isRayBlocked(latitude, longitude, midElev)) {
					// I'm not 100% happy with this method...
					dot = dot - (2 * shadowIntensity);
					if (dot < -1.0) {
						dot = -1.0;
					}
				}
			}
		
			copyRgba(colorBufferA, colorBufferB);
			ColorAdjustments.adjustBrightness(colorBufferB, dot);
			ColorAdjustments.interpolateColor(colorBufferA, colorBufferB, rgba, lightingMultiple);
		
		} else {
			copyRgba(colorBufferA, rgba);
		}
		rgba[3] = 255;
		
		return midElev;
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
	
	protected double calculateDotProduct()
	{
		double dot = perspectives.dotProduct(normal, sunsource);
		dot = Math.pow(dot, spotExponent);
		
		if (dot > 0) {
			dot *= relativeLightIntensity;
		} else if (dot < 0) {
			dot *= relativeDarkIntensity;
		}

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
	
	
	protected void setUpLightSource(double solarElevation, double solarAzimuth)
	{
		
		Vector sun = new Vector(0.0, 0.0, -1.0);
		Vector angles = new Vector(solarElevation, -solarAzimuth, 0.0);
		sun.rotate(angles);

		sunsource[0] = sun.getX();
		sunsource[1] = sun.getY();
		sunsource[2] = sun.getZ();
		
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

		if (getStandardResolutionElevation) {
			data = dataContext.getDataStandardResolution(latitude, longitude, averageOverlappedData, interpolateData);
		} else {
			data = dataContext.getDataAtEffectiveResolution(latitude, longitude, averageOverlappedData, interpolateData);
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
		if (cache) {
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
		
		if (cache) {
			elevationMap.put(latitude, longitude, elevation);
		}
		
		return elevation;
	}
	
	protected void resetBuffers()
	{

		
		backLeftPoints[0] = -1.0;
		backLeftPoints[1] = 0.0;
		backLeftPoints[2] = -1.0;
		
		backRightPoints[0] = 1.0;
		backRightPoints[1] = 0.0;
		backRightPoints[2] = -1.0;
		
		frontLeftPoints[0] = -1.0;
		frontLeftPoints[1] = 0.0;
		frontLeftPoints[2] = 1.0;
		
		frontRightPoints[0] = 1.0;
		frontRightPoints[1] = 0.0;
		frontRightPoints[2] = 1.0;
		
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
	

	protected ModelOptions getModelOptions()
	{
		return modelContext.getModelOptions();
	}
	
	protected LightingContext getLightingContext()
	{
		return modelContext.getLightingContext();
	}
	

}
