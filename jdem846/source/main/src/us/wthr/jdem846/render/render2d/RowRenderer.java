package us.wthr.jdem846.render.render2d;

import java.math.BigDecimal;

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
import us.wthr.jdem846.lighting.LightingContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.InterruptibleProcess;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.RayTracing;
import us.wthr.jdem846.render.RayTracing.RasterDataFetchHandler;
import us.wthr.jdem846.render.gfx.Vector;
import us.wthr.jdem846.scripting.ScriptProxy;

public class RowRenderer extends InterruptibleProcess
{
	private static Log log = Logging.getLog(RowRenderer.class);
	
	
	private ModelContext modelContext;
	private ModelColoring modelColoring;
	private ModelCanvas modelCanvas;
	private Perspectives perspectives;
	
	protected RasterDataContext dataRasterContextSubset;
	
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
	private boolean tiledPrecaching;
	private double latitudeResolution;
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


	
	public RowRenderer(ModelContext modelContext, ModelColoring modelColoring, ModelCanvas modelCanvas, RasterDataContext dataRasterContextSubset)
	{
		this.modelContext = modelContext;
		this.modelColoring = modelColoring;
		this.modelCanvas = modelCanvas;
		this.dataRasterContextSubset = dataRasterContextSubset;
		this.perspectives = new Perspectives();
		
		if (this.modelColoring == null) {
			this.modelColoring = ColoringRegistry.getInstance(modelContext.getModelOptions().getColoringType()).getImpl();
		}
		
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
		useSimpleCanvasFill = false;//getModelOptions().getUseSimpleCanvasFill();
		
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
		
		resetBuffers();
		setUpLightSource();
	}
	
	
	public void renderRow(double latitude, double eastLimit, double westLimit) throws RenderEngineException
	{
		
		if (modelCanvas == null) {
			modelCanvas = modelContext.getModelCanvas();
		}
		
		
		for (double longitude = westLimit; longitude <= eastLimit; longitude += longitudeResolution) {

			if (doublePrecisionHillshading) {
				renderCellDoublePrecision(latitude, longitude);
			} else {
				renderCellStandardPrecision(latitude, longitude);
			}
			
			
			
			this.checkPause();
			if (isCancelled()) {
				break;
			}
			
		}
	}
	
	
	
	

	
	protected void renderCellStandardPrecision(double latitude, double longitude) throws RenderEngineException
	{

		try {
			getPoint(latitude, longitude, gridSize, point);
		} catch (DataSourceException ex) {
			throw new RenderEngineException("Error loading elevation data: " + ex.getMessage(), ex);
		}
		
		
		if (point.condition == DemConstants.STAT_SUCCESSFUL) {
			
			backLeftPoints[1] = point.backLeftElevation;
			backRightPoints[1] = point.backRightElevation;
			frontLeftPoints[1] = point.frontLeftElevation;
			frontRightPoints[1] = point.frontRightElevation;
			
			
			double avgElevationNW = (backLeftPoints[1] + frontLeftPoints[1] + backRightPoints[1] + frontRightPoints[1]) / 4.0;
			renderTriangle(latitude, longitude, avgElevationNW, backLeftPoints, frontLeftPoints, backRightPoints, triangleColorNW);
			
			try {

				if (useSimpleCanvasFill) {
					modelCanvas.fillRectangle(triangleColorNW, 
								latitude, longitude, 
								latitudeResolution, longitudeResolution,
								avgElevationNW);
				} else {
					modelCanvas.fillRectangle(triangleColorNW,
								latitude, longitude, backLeftPoints[1],
								latitude-latitudeResolution, longitude, frontLeftPoints[1],
								latitude-latitudeResolution, longitude+longitudeResolution, frontRightPoints[1],
								latitude, longitude+longitudeResolution, backRightPoints[1]);
				}

			} catch (CanvasException ex) {
				throw new RenderEngineException("Failed to fill points on canvas: " + ex.getMessage(), ex);
			}
			
		}
	}
	
	protected void renderCellDoublePrecision(double latitude, double longitude) throws RenderEngineException
	{
		
		
		
		try {
			getPoint(latitude, longitude, gridSize, point);
		} catch (DataSourceException ex) {
			throw new RenderEngineException("Error loading elevation data: " + ex.getMessage(), ex);
		}
		
		
		if (point.condition == DemConstants.STAT_SUCCESSFUL) {
			
			backLeftPoints[1] = point.backLeftElevation;
			backRightPoints[1] = point.backRightElevation;
			frontLeftPoints[1] = point.frontLeftElevation;
			frontRightPoints[1] = point.frontRightElevation;
			
			// North West Triangle
			double avgElevationNW = (backLeftPoints[1] + frontLeftPoints[1] + backRightPoints[1]) / 3.0;
			renderTriangle(latitude, longitude, avgElevationNW, backLeftPoints, frontLeftPoints, backRightPoints, triangleColorNW);
			

			try {
				modelCanvas.fillTriangle(triangleColorNW, 
										latitude, longitude, backLeftPoints[1],
										latitude-latitudeResolution, longitude, frontLeftPoints[1],
										latitude, longitude+longitudeResolution, backRightPoints[1]);
			} catch (CanvasException ex) {
				throw new RenderEngineException("Failed to fill point on canvas: " + ex.getMessage(), ex);
			}
			
			
			// South East Triangle
			double avgElevationSE = (frontRightPoints[1] + frontLeftPoints[1] + backRightPoints[1]) / 3.0;
			renderTriangle(latitude, longitude, avgElevationSE, frontLeftPoints, frontRightPoints, backRightPoints, triangleColorSE);
			

			try {
				modelCanvas.fillTriangle(triangleColorSE, 
										latitude-latitudeResolution, longitude, frontLeftPoints[1],
										latitude-latitudeResolution, longitude+longitudeResolution, frontRightPoints[1],
										latitude, longitude+longitudeResolution, backRightPoints[1]);
			} catch (CanvasException ex) {
				throw new RenderEngineException("Failed to fill point on canvas: " + ex.getMessage(), ex);
			}
			
		}
		
		
		
	}
	

	
	
	
	
	protected void renderTriangle(double latitude, double longitude, double pointElevation, double[] p0, double[] p1, double[] p2, int[] triangeColor) throws RenderEngineException
	{
		
		
		perspectives.calcNormal(p0, p1, p2, normal);
		modelColoring.getGradientColor(pointElevation, elevationMin, elevationMax, reliefColor);
		onGetPointColor(latitude, longitude, pointElevation, elevationMin, elevationMax, reliefColor);
		
		//if (hillShadeType != DemConstants.HILLSHADING_NONE) {
		if (lightingEnabled) {
			hillshadeColor[0] = reliefColor[0];
			hillshadeColor[1] = reliefColor[1];
			hillshadeColor[2] = reliefColor[2];
			
			double dot = perspectives.dotProduct(normal, sunsource);
			dot = Math.pow(dot, spotExponent);
			
			if (rayTraceShadows) {
				//if (isRayBlocked(latitude, longitude, pointElevation)) {
				
				try {
					if (lightSourceRayTracer.isRayBlocked(latitude, longitude, pointElevation)) {
						// I'm not 100% happy with this method...
						dot = dot - (2 * shadowIntensity);
						if (dot < -1.0) {
							dot = -1.0;
						}
					}
				} catch (RayTracingException ex) {
					throw new RenderEngineException("Failure testing ray traced shadow: " + ex.getMessage(), ex);
				}
			}
			
			
			if (dot > 0) {
				dot *= relativeLightIntensity;
			} else if (dot < 0) {
				dot *= relativeDarkIntensity;
			}
			
			ColorAdjustments.adjustBrightness(hillshadeColor, dot);
			ColorAdjustments.interpolateColor(reliefColor, hillshadeColor, triangeColor, lightingMultiple);
			
			
		} else {
			triangeColor[0] = reliefColor[0];
			triangeColor[1] = reliefColor[1];
			triangeColor[2] = reliefColor[2];
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
		
		if (dataRasterContextSubset != null) {
			data = dataRasterContextSubset.getData(latitude, longitude, false, true);
		} else {
			data = getRasterDataContext().getData(latitude, longitude, false, true);
		}
		
		return data;
	}
	
	protected double _getRasterData(double latitude, double longitude) throws DataSourceException, RenderEngineException
	{
		return getRasterData(latitude, longitude);
	}
	
	protected double getRasterData(double latitude, double longitude) throws DataSourceException, RenderEngineException
	{
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
	

	protected void getPoint(double latitude, double longitude, DemPoint point) throws DataSourceException, RenderEngineException
	{
		getPoint(latitude, longitude, 1, point);
	}
	
	
	
	protected void getPoint(double latitude, double longitude, double gridSize, DemPoint point) throws DataSourceException, RenderEngineException
	{
		if (getRasterDataContext() == null) {
			point.condition = DemConstants.STAT_NO_DATA_PACKAGE;
			return;
		}

		
		
		double elevation_bl = getRasterData(latitude, longitude);
		if (elevation_bl == DemConstants.ELEV_NO_DATA) {
			point.condition = DemConstants.STAT_INVALID_ELEVATION;
			return;
		}
		
		
		double elevation_br = getRasterData(latitude, longitude + longitudeGridSize);
		double elevation_fl = getRasterData(latitude - latitudeGridSize, longitude);
		double elevation_fr = getRasterData(latitude - latitudeGridSize, longitude + longitudeGridSize);
		
		point.backLeftElevation = elevation_bl;

		if (elevation_br != DemConstants.ELEV_NO_DATA) {
			point.backRightElevation = elevation_br;
		} else {
			point.backRightElevation = elevation_bl;
		}

		if (elevation_fl != DemConstants.ELEV_NO_DATA) {
			point.frontLeftElevation = elevation_fl;
		} else {
			point.frontLeftElevation = elevation_bl;
		}
		
		if (elevation_fr != DemConstants.ELEV_NO_DATA) {
			point.frontRightElevation = elevation_fr;
		} else {
			point.frontRightElevation = elevation_bl;
		}
		
		
		if (point.getBackLeftElevation() == 0 
			&& point.getBackRightElevation() == 0 
			&& point.getFrontLeftElevation() == 0
			&& point.getFrontRightElevation() == 0) {
			//point.setMiddleElevation(0);
			point.condition = DemConstants.STAT_FLAT_SEA_LEVEL;
		} else {
			//point.setMiddleElevation((point.getBackLeftElevation() + point.getBackRightElevation() + point.getFrontLeftElevation() + point.getFrontRightElevation()) / 4.0f);
			point.condition = DemConstants.STAT_SUCCESSFUL;
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
}
