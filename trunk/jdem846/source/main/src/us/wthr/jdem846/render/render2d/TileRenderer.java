package us.wthr.jdem846.render.render2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
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
import us.wthr.jdem846.input.DataBounds;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.SubsetDataPackage;
import us.wthr.jdem846.lighting.LightingContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.BasicRenderEngine;
import us.wthr.jdem846.render.CanvasRectangleFill;
import us.wthr.jdem846.render.InterruptibleProcess;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.MatrixBuffer;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.RayTracing;
import us.wthr.jdem846.render.RayTracing.RasterDataFetchHandler;
import us.wthr.jdem846.render.RenderPipeline;
import us.wthr.jdem846.render.ShadowBuffer;
import us.wthr.jdem846.render.gfx.Vector;
import us.wthr.jdem846.scripting.ScriptProxy;

public class TileRenderer extends InterruptibleProcess
{
	private static Log log = Logging.getLog(TileRenderer.class);

	private ModelContext modelContext;
	private ModelColoring modelColoring;
	private ModelCanvas modelCanvas;
	private Perspectives perspectives = new Perspectives();
	private RenderPipeline renderPipeline;
	
	//protected RasterDataContext dataRasterContextSubset;

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
	private int[] triangleColorNW;
	private int[] triangleColorSE;
	private int[] color;
	private int[] reliefColor;
	private int[] hillshadeColor;
	private double sunsource[];	
	private double normal[];
	private double backLeftPoints[];
	private double backRightPoints[];
	private double frontLeftPoints[];
	private double frontRightPoints[];
	private DemPoint point;

	
	private double northLimit;
	private double southLimit;
	private double eastLimit;
	private double westLimit;
	//private ModelPoint[][] pointBuffer;
	private MatrixBuffer<ModelPoint> pointBuffer;
	
	//private ShadowBuffer shadowBuffer;
	//private RowRenderer rowRenderer;
	
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
		
		tiledPrecaching = getModelOptions().getPrecacheStrategy().equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_TILED);
		latitudeResolution = modelContext.getRasterDataContext().getLatitudeResolution();
		
		
		gridSize = getModelOptions().getGridSize();
		metersResolution = getRasterDataContext().getMetersResolution();
		lightingEnabled = getLightingContext().isLightingEnabled();
		tiledPrecaching = getModelOptions().getPrecacheStrategy().equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_TILED);
		doublePrecisionHillshading = getModelOptions().getDoublePrecisionHillshading();
		relativeLightIntensity = getLightingContext().getRelativeLightIntensity();
		relativeDarkIntensity = getLightingContext().getRelativeDarkIntensity();
		//hillShadeType = getModelOptions().getHillShadeType();
		spotExponent = getLightingContext().getSpotExponent();
		lightingMultiple = getLightingContext().getLightingMultiple();
		elevationMax = getRasterDataContext().getDataMaximumValue();
		elevationMin = getRasterDataContext().getDataMinimumValue();
		useSimpleCanvasFill = getModelOptions().getUseSimpleCanvasFill();
		
		solarElevation = getLightingContext().getLightingElevation();
		solarAzimuth = getLightingContext().getLightingAzimuth();
		rayTraceShadows = getLightingContext().getRayTraceShadows();
		shadowIntensity = getLightingContext().getShadowIntensity();
		
		latitudeResolution = modelContext.getRasterDataContext().getLatitudeResolution();
		longitudeResolution = modelContext.getRasterDataContext().getLongitudeResolution();
		latitudeGridSize = gridSize * latitudeResolution;
		longitudeGridSize = gridSize * longitudeResolution; 
		
		
		if (rayTraceShadows) {
			lightSourceRayTracer = new RayTracing(getLightingContext().getLightingAzimuth(),
					getLightingContext().getLightingElevation(),
					modelContext,
					new RasterDataFetchHandler() {
						public double getRasterData(double latitude, double longitude) throws Exception {
							return _getRasterData(latitude, longitude);
						}
			});
		} else {
			lightSourceRayTracer = null;
		}
		
		
		//shadowBuffer = new ShadowBuffer(modelContext);
		
		resetBuffers();
		setUpLightSource();
		//rowRenderer = new RowRenderer(modelContext, modelColoring, modelCanvas);
	}
	
	
	public void renderTile(double northLimit, double southLimit, double eastLimit, double westLimit) throws RenderEngineException
	{
		this.northLimit = northLimit;
		this.southLimit = southLimit;
		this.eastLimit = eastLimit;
		this.westLimit = westLimit;
		
		RasterDataContext dataProxy = modelContext.getRasterDataContext();//.getSubSet(northLimit, southLimit, eastLimit, westLimit);

		// TODO: If Buffered
		if (tiledPrecaching) {
			try {
				dataProxy.fillBuffers(northLimit, southLimit, eastLimit, westLimit);
			} catch (Exception ex) {
				throw new RenderEngineException("Failed to buffer data: " + ex.getMessage(), ex);
			}
		}

		onTileBefore(modelCanvas);
		
		//List<ModelPoint> points = new LinkedList<ModelPoint>();
		
		
		log.info("Initializing model point buffer...");
		//int rows = dataProxy.getDataRows();
		//int columns = dataProxy.getDataColumns();
		int rows = (int) Math.ceil((northLimit - southLimit) / latitudeResolution);
		int columns = (int) Math.ceil((eastLimit - westLimit) / longitudeResolution);
		
		if (rows <= 0 || columns <= 0) {
			return;
		}
		
		ModelPoint point = null;
		//pointBuffer = new ModelPoint[rows][columns];
		pointBuffer = new MatrixBuffer<ModelPoint>(columns, rows);
		
		log.info("Processing data points...");
		doElevation(northLimit, westLimit);
		
		
		if (rayTraceShadows) {
			log.info("Calculating shadows...");
			for (int y = 0; y < rows; y++) {
				for (int x = 0; x < columns; x++) {
					point = getModelPoint(x, y);
					if (point != null) {
						try {
							point.setDot(calculateRayTracedDotProduct(point.getLatitude(), point.getLongitude(), point.getElevation(), point.getDot()));
						} catch (RayTracingException ex) {
							throw new RenderEngineException("Error on ray tracing: " + ex.getMessage(), ex);
						}
					}
				}
			}
		}
		
		
		
		log.info("Rendering data points...");
		
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < columns; x++) {
				point = getModelPoint(x, y);
				//point = pointBuffer[y][x];
				if (point != null) {
					try {
						renderModelPoint(point);
					} catch (CanvasException ex) {
						throw new RenderEngineException("Error rendering point: " + ex.getMessage(), ex);
					}
				}
				this.checkPause();
				if (isCancelled()) {
					break;
				}
			}
			this.checkPause();
			if (isCancelled()) {
				break;
			}
		}
		
		
		pointBuffer.dispose();
		pointBuffer = null;
		
		
		//LinkedList<RowRenderRunnable> renderQueue = new LinkedList<RowRenderRunnable>();
		
		
		//for (double latitude = northLimit; latitude >= southLimit; latitude -= latitudeResolution) {
		//for (double latitude = southLimit; latitude <= northLimit; latitude += latitudeResolution) {
		//	rowRenderer.renderRow(latitude, eastLimit, westLimit);
			//this.checkPause();
			//if (isCancelled()) {
			//	break;
			//}
			
		//}
		
		
		onTileAfter(modelCanvas);
		
		if (tiledPrecaching) {
			try {
				dataProxy.clearBuffers();
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Failed to clear buffer data: " + ex.getMessage(), ex);
			}
		}
		

	}
	

	protected ModelPoint getModelPoint(double latitude, double longitude)
	{
		int y = (int) Math.floor((northLimit - latitude) / latitudeResolution);
		int x = (int) Math.floor((longitude - westLimit) / longitudeResolution);
		
		return getModelPoint(x, y);
	}
	
	protected ModelPoint getModelPoint(int x, int y)
	{
		//if (y < pointBuffer.length && x < pointBuffer[0].length) {
		//	return pointBuffer[y][x];
		if (x >= 0 && x < pointBuffer.getWidth() && y >= 0 && y < pointBuffer.getHeight()) {
			return pointBuffer.get(x, y);
		} else {
			return null;
		}
	}
	
	protected void setModelPoint(double latitude, double longitude, ModelPoint modelPoint)
	{
		int y = (int) Math.floor((northLimit - latitude) / latitudeResolution);
		int x = (int) Math.floor((longitude - westLimit) / longitudeResolution);
		setModelPoint(x, y, modelPoint);
	}
	
	protected void setModelPoint(int x, int y, ModelPoint modelPoint)
	{
		//if (y < pointBuffer.length && x < pointBuffer[0].length) {
		//	pointBuffer[y][x] = modelPoint;
		if (x >= 0 && x < pointBuffer.getWidth() && y >= 0 && y < pointBuffer.getHeight()) {
			pointBuffer.set(x, y, modelPoint);
		} 
	}
	
	protected ModelPoint doElevation(double latitude, double longitude) throws RenderEngineException
	{
		//int row = (int) Math.floor((northLimit - latitude) / latitudeResolution);
		//int column = (int) Math.floor((longitude - westLimit) / longitudeResolution);
		
		ModelPoint exists = getModelPoint(latitude, longitude);
		if (exists != null) {
			return exists;
		}
		
		//if (pointBuffer[row][column] != null) {
		//	return pointBuffer[row][column];
		//}
		
		
		
		
		double elevationNW = 0.0;
		try {
			elevationNW = getRasterData(latitude, longitude);
		} catch (DataSourceException ex) {
			throw new RenderEngineException("Data error: " + ex.getMessage(), ex);
		}
		
		if (elevationNW == DemConstants.ELEV_NO_DATA)
			return null;
		
		
		
		double elevationSW = elevationNW;
		double elevationSE = elevationNW;
		double elevationNE = elevationNW;
		
		ModelPoint point = null;
		
		
		if (latitude - latitudeResolution >= southLimit) {
			point = doElevation(latitude-latitudeResolution, longitude);
			if (point != null)
				elevationSW = point.getElevation();
		}
		
		
		if ((latitude - latitudeResolution >= southLimit) && (longitude+longitudeResolution <= eastLimit)) {
			point = doElevation(latitude-latitudeResolution, longitude+longitudeResolution);
			elevationSE = point.getElevation();
		}
		
		if ((longitude+longitudeResolution <= eastLimit)) {
			point = doElevation(latitude, longitude+longitudeResolution);
			if (point != null)
				elevationNE = point.getElevation();
		}
		
		
		if (elevationSW == DemConstants.ELEV_NO_DATA) 
			elevationSW = elevationNW;
		
		if (elevationSE == DemConstants.ELEV_NO_DATA)
			elevationSE = elevationNW;
		
		if (elevationNE == DemConstants.ELEV_NO_DATA)
			elevationNE = elevationNW;
		
		//if (elevationNW == DemConstants.ELEV_NO_DATA) {
		//	return null;
		//}
		
		double pointElevation = (elevationNW + elevationSW +  elevationNE) / 3.0;
		calculateNormal(elevationNW, elevationSW, elevationSE, elevationNE, normal);
		modelColoring.getGradientColor(pointElevation, elevationMin, elevationMax, reliefColor);
		onGetPointColor(latitude, longitude, pointElevation, elevationMin, elevationMax, reliefColor);
		/*
		try {
			shadowBuffer.setRectangle(latitude, longitude, elevationNW,
					latitude-latitudeResolution, longitude, elevationSW,
					latitude-latitudeResolution, longitude+longitudeResolution, elevationSE,
					latitude, longitude+longitudeResolution, elevationNE);
		} catch (RayTracingException ex) {
			throw new RenderEngineException("Shadow buffer error: " + ex.getMessage(), ex);
		}
		*/
		double dot = calculateDotProduct();
		
		
		
		
		point = new ModelPoint(latitude, longitude, elevationNW, reliefColor, dot);
		//pointBuffer[row][column] = point;
		setModelPoint(latitude, longitude, point);
		
		
		//try {
		//	renderModelPoint(point);
		//} catch (CanvasException ex) {
		//	throw new RenderEngineException("Canvas exception in render process: " + ex.getMessage(), ex);
		//}
		
		this.checkPause();
		if (isCancelled()) {
			throw new RenderEngineException("Process Cancelled");
			//break;
		}
		
		return point;
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
	
	
	protected double calculateRayTracedDotProduct(double latitude, double longitude, double elevation, double dot) throws RayTracingException
	{
		if (lightSourceRayTracer.isRayBlocked(latitude, longitude, elevation)) {
			// I'm not 100% happy with this method...
			dot = dot - (2 * shadowIntensity);
			if (dot < -1.0) {
				dot = -1.0;
			}
		}
		return dot;
	}
	
	protected void calculateNormal(double nw, double sw, double se, double ne, double[] normal)
	{
		backLeftPoints[1] = nw;
		backRightPoints[1] = ne;
		frontLeftPoints[1] = sw;
		frontRightPoints[1] = se;
		
		perspectives.calcNormal(backLeftPoints, frontLeftPoints, backRightPoints, normal);
	}
	
	
	protected void renderModelPoint(ModelPoint point) throws CanvasException, RenderEngineException
	{
		if (point.getElevation() == DemConstants.ELEV_NO_DATA) {
			return;
		}
		
		double latitude = point.getLatitude();
		double longitude = point.getLongitude();
		//double elevation = point.getElevation();
		
		double dot = point.getDot();
		
		int row = (int) Math.floor((northLimit - latitude) / latitudeResolution);
		int column = (int) Math.floor((longitude - westLimit) / longitudeResolution);
		
		
		double nw = point.getElevation();
		double sw = nw;
		double se = nw;
		double ne = nw;
		
		ModelPoint other = null;
		
		other = getModelPoint(column, row+1);
		if (other != null)
			sw = other.getElevation();
		
		other = getModelPoint(column+1, row+1);
		if (other != null)
			se = other.getElevation();
		
		other = getModelPoint(column+1, row);
		if (other != null)
			ne = other.getElevation();
		
		/*
		try {
			
			double shaded = shadowBuffer.isShaded(latitude, longitude, nw,
					latitude-latitudeResolution, longitude, sw,
					latitude-latitudeResolution, longitude+longitudeResolution, se,
					latitude, longitude+longitudeResolution, ne);
			
			if (shaded == 1.0) {
			
				dot = dot - (2 * (shaded * shadowIntensity));
				if (dot < -1.0) {
					dot = -1.0;
				}
			}
		} catch (RayTracingException ex) {
			throw new RenderEngineException("Error determining shadow buffer state: " + ex.getMessage(), ex);
		}
		*/
		
		point.getColor(reliefColor);
		point.getColor(hillshadeColor);
		
		ColorAdjustments.adjustBrightness(hillshadeColor, dot);
		ColorAdjustments.interpolateColor(reliefColor, hillshadeColor, color, lightingMultiple);
		
		
	if (renderPipeline == null) {
			
			if (useSimpleCanvasFill) {
				modelCanvas.fillRectangle(color, 
							latitude, longitude, 
							latitudeResolution, longitudeResolution,
							point.getElevation());
			} else {
				modelCanvas.fillRectangle(color,
						latitude, longitude, nw,
						latitude-latitudeResolution, longitude, sw,
						latitude-latitudeResolution, longitude+longitudeResolution, se,
						latitude, longitude+longitudeResolution, ne);
			}
			
		} else {
			
			if (useSimpleCanvasFill) {
				renderPipeline.submit(new CanvasRectangleFill(color, latitude, longitude, 
							latitudeResolution, longitudeResolution,
							point.getElevation()));
				
			} else {
				renderPipeline.submit(new CanvasRectangleFill(color, 
						latitude, longitude, nw,
						latitude-latitudeResolution, longitude, sw,
						latitude-latitudeResolution, longitude+longitudeResolution, se,
						latitude, longitude+longitudeResolution, ne));
			}
			
		}
		
		
	}
	
	
	protected void setUpLightSource()
	{
		
		Vector sun = new Vector(0.0, 0.0, -1.0);
		Vector angles = new Vector(solarElevation, -solarAzimuth, 0.0);
		sun.rotate(angles);

		sunsource[0] = sun.getX();
		sunsource[1] = sun.getY();
		sunsource[2] = sun.getZ();
		
		// TODO: TEST THIS!
		/*
		sunsource[0] = 0.0;
		sunsource[1] = 0.0;
		sunsource[2] = 0.0;
		getSpherePoint(solarAzimuth, solarElevation, 1.0, sunsource);
		*/
		
	}
	
	
	
	
	
	
	

	protected double getRasterDataRaw(double latitude, double longitude) throws DataSourceException
	{
		double data = DemConstants.ELEV_NO_DATA;
		
		//if (dataRasterContextSubset != null) {
		//	data = dataRasterContextSubset.getData(latitude, longitude, false, true);
		//} else {
			data = getRasterDataContext().getData(latitude, longitude, false, true);
		//}
		
		return data;
	}
	
	protected double _getRasterData(double latitude, double longitude) throws DataSourceException, RenderEngineException
	{
		
		return getRasterData(latitude, longitude);
	}
	
	protected double getRasterData(double latitude, double longitude) throws DataSourceException, RenderEngineException
	{
		
		ModelPoint modelPoint = getModelPoint(latitude, longitude);
		if (modelPoint != null) {
			return modelPoint.getElevation();
		}
		
		double elevation = 0;
		
		
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
		
		
		
		return elevation;
	}
	
	protected void resetBuffers()
	{
		triangleColorNW = new int[4];
		triangleColorSE = new int[4];
		color = new int[4];
		reliefColor = new int[4];
		hillshadeColor = new int[4];
		
		sunsource = new double[3];	
		normal = new double[3];
		
		backLeftPoints = new double[3];
		backRightPoints = new double[3];
		
		frontLeftPoints = new double[3];
		frontRightPoints = new double[3];
		point = new DemPoint();

		
		color[0] = color[1] = color[2] = 0; color[3] = 0xFF;
		triangleColorNW[0] = triangleColorNW[1] = triangleColorNW[2]; triangleColorNW[3] = 0xFF;
		triangleColorSE[0] = triangleColorSE[1] = triangleColorSE[2]; triangleColorSE[3] = 0xFF;
		reliefColor[0] = reliefColor[1] = reliefColor[2]; reliefColor[3] = 0xFF;
		hillshadeColor[0] = hillshadeColor[1] = hillshadeColor[2]; hillshadeColor[3] = 0xFF;

		sunsource[0] = sunsource[1] = sunsource[2] = 0.0;
		normal[0] = normal[1] = normal[2] = 0.0;
		
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
	


	
	public static ModelCanvas render(int fromRow, int toRow, int fromColumn, int toColumn, ModelContext modelContext) throws RenderEngineException
	{
		ModelColoring modelColoring = ColoringRegistry.getInstance(modelContext.getModelOptions().getColoringType()).getImpl();
		return TileRenderer.render(fromRow, toRow, fromColumn, toColumn, modelContext, modelColoring, null);
	}
	
	public static ModelCanvas render(int fromRow, int toRow, int fromColumn, int toColumn, ModelContext modelContext, ModelCanvas canvas) throws RenderEngineException
	{
		ModelColoring modelColoring = ColoringRegistry.getInstance(modelContext.getModelOptions().getColoringType()).getImpl();
		return TileRenderer.render(fromRow, toRow, fromColumn, toColumn, modelContext, modelColoring, canvas);
	}
	
	public static ModelCanvas render(int fromRow, int toRow, int fromColumn, int toColumn, ModelContext modelContext, ModelColoring modelColoring) throws RenderEngineException
	{
		return TileRenderer.render(fromRow, toRow, fromColumn, toColumn, modelContext, modelColoring, null);
	}
	
	public static ModelCanvas render(int fromRow, int toRow, int fromColumn, int toColumn, ModelContext modelContext, ModelColoring modelColoring, ModelCanvas canvas) throws RenderEngineException
	{
		if (canvas == null) {
            int width = (int)(toColumn - fromColumn) + 1;
            int height = (int)(toRow - fromRow) + 1;
            log.info("Creating default canvas of width/height: " + width + "/" + height);
            canvas = new ModelCanvas(modelContext);
            //canvas = new DemCanvas(DEFAULT_BACKGROUND, width, height);
		}
		
		TileRenderer renderer = new TileRenderer(modelContext, modelColoring, canvas);
		renderer.renderTile(fromRow, toRow, fromColumn, toColumn);
		
		return canvas;
	}

	protected double asin(double a)
	{
		return Math.asin(a);
	}
	
	protected double atan2(double a, double b)
	{
		return Math.atan2(a, b);
	}
	
	protected double sqr(double a)
	{
		return (a*a);
	}
	
	protected double abs(double a)
	{
		return Math.abs(a);
	}
	
	protected double pow(double a, double b)
	{
		return Math.pow(a, b);
	}
	
	protected double sqrt(double d)
	{
		return Math.sqrt(d);
	}
	
	protected double cos(double d)
	{
		return Math.cos(Math.toRadians(d));
	}
	
	protected double sin(double d)
	{
		return Math.sin(Math.toRadians(d));
	}
}
