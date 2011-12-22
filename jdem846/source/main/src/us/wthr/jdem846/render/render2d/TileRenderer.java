package us.wthr.jdem846.render.render2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
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
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.input.DataBounds;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.SubsetDataPackage;
import us.wthr.jdem846.lighting.LightingContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.BasicRenderEngine;
import us.wthr.jdem846.render.InterruptibleProcess;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.gfx.Vector;
import us.wthr.jdem846.scripting.ScriptProxy;

public class TileRenderer extends InterruptibleProcess
{
	private static Log log = Logging.getLog(TileRenderer.class);

	private ModelContext modelContext;
	private ModelColoring modelColoring;
	private ModelCanvas modelCanvas;
	private Perspectives perspectives;
	
	protected RasterDataContext dataRasterContextSubset;
	
	private double gridSize;
	private double elevationMultiple;
	private boolean doublePrecisionHillshading;
	private boolean lightingEnabled;
	private double relativeLightIntensity;
	private double relativeDarkIntensity;
	private double metersResolution;
	//private int hillShadeType;
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
	
	private boolean rayTraceShadows;
	private double shadowIntensity;
	
	private int[] triangleColorNW = {0, 0, 0, 0};
	private int[] triangleColorSE = {0, 0, 0, 0};
	private int[] color = {0, 0, 0, 0};
	private int[] reliefColor = {0, 0, 0, 0};
	private int[] hillshadeColor = {0, 0, 0, 0};
	
	private double sunsource[] = {0.0, 0.0, 0.0};	
	private double normal[] = {0.0, 0.0, 0.0};
	
	private double backLeftPoints[] = {-1.0, 0.0f, -1.0};
	private double backRightPoints[] = {1.0, 0.0f, -1.0};
	
	private double frontLeftPoints[] = {-1.0, 0.0f, 1.0};
	private double frontRightPoints[] = {1.0, 0.0f, 1.0};
	private DemPoint point = new DemPoint();

	
	public TileRenderer(ModelContext modelContext, ModelColoring modelColoring, ModelCanvas modelCanvas)
	{
		this.modelContext = modelContext;
		this.modelColoring = modelColoring;
		this.modelCanvas = modelCanvas;
		this.perspectives = new Perspectives();
		
		gridSize = getModelOptions().getGridSize();
		
		metersResolution = getRasterDataContext().getMetersResolution();
		lightingEnabled = getLightingContext().isLightingEnabled();
		tiledPrecaching = getModelOptions().getPrecacheStrategy().equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_TILED);
		elevationMultiple = getModelOptions().getElevationMultiple();
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
	}
	
	
	public void renderTile(double northLimit, double southLimit, double eastLimit, double westLimit) throws RenderEngineException
	{
		
		RasterDataContext dataProxy = modelContext.getRasterDataContext();//.getSubSet(northLimit, southLimit, eastLimit, westLimit);

		// TODO: If Buffered
		if (tiledPrecaching) {
			try {
				dataProxy.fillBuffers(northLimit, southLimit, eastLimit, westLimit);
			} catch (Exception ex) {
				throw new RenderEngineException("Failed to buffer data: " + ex.getMessage(), ex);
			}
		}

		
		resetBuffers();
		setUpLightSource();

		onTileBefore(modelCanvas);
		

		for (double latitude = northLimit; latitude >= southLimit; latitude -= latitudeResolution) {
			
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
			
			this.checkPause();
			if (isCancelled()) {
				break;
			}
			
		}
		

		onTileAfter(modelCanvas);
		
		if (tiledPrecaching) {
			try {
				dataProxy.clearBuffers();
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Failed to clear buffer data: " + ex.getMessage(), ex);
			}
		}
		

	}
	
	/** Ray trace from coordinate to the source of light. If there is another
	 * higher elevation that blocks the way, then return true. Return false if
	 * the trace either not blocked or it exceeds the maximum data elevation and 
	 * it can then be assumed to not be blocked. Points with no data are skipped
	 * and the loop continues on. Note: This initial implementation
	 * assumes a flat Earth and is therefore not technically accurate.
	 * 
	 * @param centerLatitude
	 * @param centerLongitude
	 * @param centerElevation
	 * @return
	 * @throws RenderEngineException
	 */
	protected boolean isRayBlocked(double centerLatitude, double centerLongitude, double centerElevation) throws RenderEngineException
	{
		double[] points = {0.0, 0.0, 0.0};
		
		
		//for (double radius = longitudeResolution; radius < (longitudeResolution * 100.0); radius += longitudeResolution) {
		double radius = longitudeResolution;
		while (true) {
			getSpherePoint(solarAzimuth, solarElevation, radius, points);
			
			double latitude = centerLatitude + points[0];
			double longitude = centerLongitude - points[2];
			
			
			if (latitude > getRasterDataContext().getNorth() ||
					latitude < getRasterDataContext().getSouth() ||
					longitude > getRasterDataContext().getEast() ||
					longitude < getRasterDataContext().getWest()) {
				return false;
			}
			
			double resolution = (points[1] / longitudeResolution);
			double rayElevation = centerElevation + (resolution * metersResolution);
			double pointElevation = 0;
			//log.info("Radius: " + radius + ", X/Y/Z: " + points[0] + "/" + points[1] + "/" + points[2] + ", Center Elevation: " + centerElevation + ", Ray Elevation: " + rayElevation);;
			try {
				pointElevation = getRasterData(latitude, longitude);
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Failed to get elevation for point: " + ex.getMessage(), ex);
			}
			
			if (pointElevation == DemConstants.ELEV_NO_DATA) {
				continue;
			}
			
			if (pointElevation > rayElevation) {
				return true;
			}
			
			if (rayElevation > this.elevationMax) {
				return false;
			}
			
			/*
			try {
				modelCanvas.fillCircle(Color.RED, latitude, longitude, rayElevation, 3);
			} catch (CanvasException ex) {
				throw new RenderEngineException("Failed to render circle on canvas: " + ex.getMessage(), ex);
			}
			*/
			
			radius += longitudeResolution;
		}
		
	}
	
	
	protected void drawRayTrace(double centerLatitude, double centerLongitude, double centerElevation) throws RenderEngineException
	{
		double[] points = {0.0, 0.0, 0.0};
		int[] color = {255, 0, 0, 0};
		double maxRadius = 100;
		int alphaStep = (int) Math.round(255.0 / maxRadius);
		
		for (double radius = longitudeResolution; radius < (longitudeResolution * maxRadius); radius += longitudeResolution) {
			color[3] = color[3] + alphaStep;
			if (color[3] > 255) {
				color[3] = 255;
			}
			getSpherePoint(solarAzimuth, solarElevation, radius, points);
			
			double latitude = centerLatitude + points[0];
			double longitude = centerLongitude - points[2];
			double resolution = (points[1] / longitudeResolution);
			double rayElevation = centerElevation + (resolution * metersResolution);
			//resolution = modelContext.getRasterDataContext().getMetersResolution();
			//elev = (((elevation - max) / resolution) + Math.abs(min)) * elevationMultiple;
			double pointElevation = 0;
			//log.info("Radius: " + radius + ", X/Y/Z: " + points[0] + "/" + points[1] + "/" + points[2] + ", Center Elevation: " + centerElevation + ", Ray Elevation: " + rayElevation);;
			try {
				pointElevation = getRasterData(latitude, longitude);
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Failed to get elevation for point: " + ex.getMessage(), ex);
			}

			try {
				modelCanvas.fillCircle(color, latitude, longitude, rayElevation, 1);
			} catch (CanvasException ex) {
				throw new RenderEngineException("Failed to render circle on canvas: " + ex.getMessage(), ex);
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
				if (isRayBlocked(latitude, longitude, pointElevation)) {
					
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
	
	protected void getSpherePoint(double theta, double phi, double radius, double[] points)
	{
		double _y = sqrt(pow(radius, 2) - pow(radius * cos(phi), 2));
		double r0 = sqrt(pow(radius, 2) - pow(_y, 2));

		double _b = r0 * cos(theta );
        double _z = sqrt(pow(r0, 2) - pow(_b, 2));
        double _x = sqrt(pow(r0, 2) - pow(_z, 2));
        if (theta <= 90.0) {
                _z *= -1.0;
        } else if (theta  <= 180.0) {
                _x *= -1.0;
                _z *= -1.0;
        } else if (theta  <= 270.0) {
                _x *= -1.0;
        }

        if (phi >= 0) { 
                _y = abs(_y);
        } else {
                _y = abs(_y) * -1;
        }


        points[0] = _x;
        points[1] = _y;
        points[2] = _z;

       // double mag = sqrt(sqr(_x)+sqr(_y)+sqr(_z));   
       // _x /= mag;   
        //_y /= mag;   
       // _z /= mag; 
        
        //points[3] = (atan2(_x, _z)/(Math.PI*2)) + 0.5f;   
       // points[4] =  (asin(_y) / Math.PI) + 0.5f;
	}
	

	public void precacheData() throws DataSourceException
	{
		if (dataRasterContextSubset != null) {
			dataRasterContextSubset.fillBuffers();
		}
	}
	
	public void unloadData() throws DataSourceException
	{
		if (dataRasterContextSubset != null) {
			dataRasterContextSubset.clearBuffers();
		}
	}
	
	
	public void loadDataSubset(double north, double south, double east, double west) throws DataSourceException
	{
		dataRasterContextSubset = getRasterDataContext().getSubSet(north, south, east, west);
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
