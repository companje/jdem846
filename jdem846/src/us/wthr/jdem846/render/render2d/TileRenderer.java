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
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.input.DataBounds;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.SubsetDataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.BasicRenderEngine;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.gfx.Vector;
import us.wthr.jdem846.scripting.ScriptProxy;

public class TileRenderer
{
	private static Log log = Logging.getLog(TileRenderer.class);
	private static Color DEFAULT_BACKGROUND = new Color(0, 0, 0, 0);
	
	private ModelContext modelContext;
	private ModelColoring modelColoring;
	private DemCanvas canvas;
	private Perspectives perspectives;
	
	protected RasterDataContext dataRasterContextSubset;
	
	private double gridSize;
	private double elevationMultiple;
	private boolean doublePrecisionHillshading;
	private double relativeLightIntensity;
	private double relativeDarkIntensity;
	private int hillShadeType;
	private int spotExponent;
	private double lightingMultiple;
	private double elevationMax;
	private double elevationMin;
	private double solarElevation;
	private double solarAzimuth;
	private boolean tiledPrecaching;
	private int tileSize;
	
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
	
	
	private boolean cancel = false;
	
	public TileRenderer(ModelContext modelContext, ModelColoring modelColoring, DemCanvas canvas)
	{
		this.modelContext = modelContext;
		this.modelColoring = modelColoring;
		this.canvas = canvas;
		this.perspectives = new Perspectives();
		
		gridSize = getModelOptions().getGridSize();
		
		tiledPrecaching = getModelOptions().getPrecacheStrategy().equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_TILED);
		elevationMultiple = getModelOptions().getElevationMultiple();
		doublePrecisionHillshading = getModelOptions().getDoublePrecisionHillshading();
		relativeLightIntensity = getModelOptions().getRelativeLightIntensity();
		relativeDarkIntensity = getModelOptions().getRelativeDarkIntensity();
		hillShadeType = getModelOptions().getHillShadeType();
		spotExponent = getModelOptions().getSpotExponent();
		lightingMultiple = getModelOptions().getLightingMultiple();
		elevationMax = getRasterDataContext().getDataMaximumValue();
		elevationMin = getRasterDataContext().getDataMinimumValue();
		//elevationMax = getDataPackage().getMaxElevation();
		//elevationMin = getDataPackage().getMinElevation();
		
		solarElevation = getModelOptions().getLightingElevation();
		solarAzimuth = getModelOptions().getLightingAzimuth();
		tileSize = getModelOptions().getTileSize();
	}
	
	
	public void renderTile(double northLimit, double southLimit, double eastLimit, double westLimit) throws RenderEngineException
	{
		
		double latitudeResolution = modelContext.getRasterDataContext().getLatitudeResolution();
		double longitudeResolution = modelContext.getRasterDataContext().getLongitudeResolution();
		

		
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

		int imageRow = 0;
		int imageColumn = 0;
		
		for (double latitude = northLimit; latitude >= southLimit; latitude -= latitudeResolution) {
			imageColumn = 0;
			
			for (double longitude = westLimit; longitude <= eastLimit; longitude += longitudeResolution) {

				renderCell(latitude, longitude, imageRow, imageColumn);

				imageColumn++;
			}
			imageRow++;
		}
		
		if (tiledPrecaching) {
			try {
				dataProxy.clearBuffers();
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Failed to clear buffer data: " + ex.getMessage(), ex);
			}
		}
		

	}
	
	

	protected void renderCell(double latitude, double longitude, int imageRow, int imageColumn) throws RenderEngineException
	{
		

		
		try {
			getPoint(latitude, longitude, gridSize, point);
		} catch (DataSourceException ex) {
			throw new RenderEngineException("Error loading elevation data: " + ex.getMessage(), ex);
		}
		
		
		if (point.getCondition() == DemConstants.STAT_SUCCESSFUL) {
			
			backLeftPoints[1] = point.getBackLeftElevation() * elevationMultiple;
			backRightPoints[1] = point.getBackRightElevation() * elevationMultiple;
			frontLeftPoints[1] = point.getFrontLeftElevation() * elevationMultiple;
			frontRightPoints[1] = point.getFrontRightElevation() * elevationMultiple;
			
			// North West
			double avgElevationNW = (backLeftPoints[1] + frontLeftPoints[1] + backRightPoints[1]) / 3.0;
			renderTriangle(avgElevationNW, backLeftPoints, frontLeftPoints, backRightPoints, triangleColorNW);
			
			if (doublePrecisionHillshading) {
				
				// South East
				double avgElevationSE = (frontRightPoints[1] + frontLeftPoints[1] + backRightPoints[1]) / 3.0;
				renderTriangle(avgElevationSE, frontLeftPoints, frontRightPoints, backRightPoints, triangleColorSE);
	
				ColorAdjustments.interpolateColor(triangleColorNW, triangleColorSE, color, 0.5);
				
			} else {
				color[0] = triangleColorNW[0];
				color[1] = triangleColorNW[1];
				color[2] = triangleColorNW[2];
				color[3] = triangleColorNW[3];
			}

			canvas.setColor(imageColumn, imageRow, color);
			
		}
		
		
		
	}
	

	
	
	
	
	protected void renderTriangle(double pointElevation, double[] p0, double[] p1, double[] p2, int[] triangeColor) throws RenderEngineException
	{
		
		
		perspectives.calcNormal(p0, p1, p2, normal);
		modelColoring.getGradientColor(pointElevation, elevationMin, elevationMax, reliefColor);
		
		
		if (hillShadeType != DemConstants.HILLSHADING_NONE) {
			hillshadeColor[0] = reliefColor[0];
			hillshadeColor[1] = reliefColor[1];
			hillshadeColor[2] = reliefColor[2];
			
			double dot = perspectives.dotProduct(normal, sunsource);
			dot = Math.pow(dot, spotExponent);
			
			switch (hillShadeType) {
			case DemConstants.HILLSHADING_LIGHTEN:
				dot = (dot + 1.0) / 2.0;
				ColorAdjustments.adjustBrightness(hillshadeColor, 1.0 - dot);
				break;
			case DemConstants.HILLSHADING_DARKEN:
				dot = Math.abs((dot + 1.0) / 2.0) * -1.0;
				ColorAdjustments.adjustBrightness(hillshadeColor, dot);
				break;
			
			case DemConstants.HILLSHADING_COMBINED:
				
				if (dot > 0) {
					dot *= relativeLightIntensity;
				} else if (dot < 0) {
					dot *= relativeDarkIntensity;
				}
				
				ColorAdjustments.adjustBrightness(hillshadeColor, dot);
				break;
			}
			
			
			ColorAdjustments.interpolateColor(reliefColor, hillshadeColor, triangeColor, lightingMultiple);
			
			
		} else {
			triangeColor[0] = reliefColor[0];
			triangeColor[1] = reliefColor[1];
			triangeColor[2] = reliefColor[2];
		}
		

		//canvas.setColor(imageColumn, imageRow, color);
	}
	
	
	
	protected void setUpLightSource()
	{
		Vector sun = new Vector(0.0, 0.0, -1.0);
		Vector angles = new Vector(solarElevation, -solarAzimuth, 0.0);
		sun.rotate(angles);

		sunsource[0] = sun.getX();
		sunsource[1] = sun.getY();
		sunsource[2] = sun.getZ();

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
			data = dataRasterContextSubset.getData(latitude, longitude, false);
		} else {
			data = getRasterDataContext().getData(latitude, longitude, false);
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
			point.setCondition(DemConstants.STAT_NO_DATA_PACKAGE);
			return;
		}

		double latitudeGridSize = gridSize * getRasterDataContext().getLatitudeResolution();
		double longitudeGridSize = gridSize * getRasterDataContext().getLongitudeResolution(); 
		
		double elevation_bl = getRasterData(latitude, longitude);
		if (elevation_bl == DemConstants.ELEV_NO_DATA) {
			point.setCondition(DemConstants.STAT_INVALID_ELEVATION);
			return;
		}
		
		
		double elevation_br = getRasterData(latitude, longitude + longitudeGridSize);
		double elevation_fl = getRasterData(latitude - latitudeGridSize, longitude);
		double elevation_fr = getRasterData(latitude - latitudeGridSize, longitude + longitudeGridSize);
		
		point.setBackLeftElevation(elevation_bl);

		if (elevation_br != DemConstants.ELEV_NO_DATA) {
			point.setBackRightElevation(elevation_br);
		} else {
			point.setBackRightElevation(point.getBackLeftElevation());
		}

		if (elevation_fl != DemConstants.ELEV_NO_DATA) {
			point.setFrontLeftElevation(elevation_fl);
		} else {
			point.setFrontLeftElevation(point.getBackLeftElevation());
		}
		
		if (elevation_fr != DemConstants.ELEV_NO_DATA) {
			point.setFrontRightElevation(elevation_fr);
		} else {
			point.setFrontRightElevation(point.getBackLeftElevation());
		}
		
		
		if (point.getBackLeftElevation() == 0 
			&& point.getBackRightElevation() == 0 
			&& point.getFrontLeftElevation() == 0
			&& point.getFrontRightElevation() == 0) {
			//point.setMiddleElevation(0);
			point.setCondition(DemConstants.STAT_FLAT_SEA_LEVEL);
		} else {
			//point.setMiddleElevation((point.getBackLeftElevation() + point.getBackRightElevation() + point.getFrontLeftElevation() + point.getFrontRightElevation()) / 4.0f);
			point.setCondition(DemConstants.STAT_SUCCESSFUL);
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
	
	protected void resetBuffers()
	{
		color[0] = color[1] = color[2] = color[3] = 0;
		triangleColorNW[0] = triangleColorNW[1] = triangleColorNW[2] = triangleColorNW[3] = 0;
		triangleColorSE[0] = triangleColorSE[1] = triangleColorSE[2] = triangleColorSE[3] = 0;
		reliefColor[0] = reliefColor[1] = reliefColor[2] = reliefColor[3] = 0;
		hillshadeColor[0] = hillshadeColor[1] = hillshadeColor[2] = hillshadeColor[3] = 0;

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
	
	/** Requests that a rendering process is stopped.
	 * 
	 */
	public void cancel()
	{
		this.cancel = true;
	}
	
	/** Determines whether the rendering process has been requested to stop. This does not necessarily mean
	 * that the process <i>has</i> stopped as engine implementations need not check this value that often or
	 * at all.
	 * 
	 * @return Whether the rendering process has been requested to stop.
	 */
	public boolean isCancelled()
	{
		return cancel;
	}
	
	
	protected void onTileBefore(DemCanvas tileCanvas) throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.onTileBefore(modelContext, tileCanvas);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
	}
	
	protected void onTileAfter(DemCanvas tileCanvas) throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.onTileBefore(modelContext, tileCanvas);
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
	
	
	public static DemCanvas render(int fromRow, int toRow, int fromColumn, int toColumn, ModelContext modelContext) throws RenderEngineException
	{
		ModelColoring modelColoring = ColoringRegistry.getInstance(modelContext.getModelOptions().getColoringType()).getImpl();
		return TileRenderer.render(fromRow, toRow, fromColumn, toColumn, modelContext, modelColoring, null);
	}
	
	public static DemCanvas render(int fromRow, int toRow, int fromColumn, int toColumn, ModelContext modelContext, DemCanvas canvas) throws RenderEngineException
	{
		ModelColoring modelColoring = ColoringRegistry.getInstance(modelContext.getModelOptions().getColoringType()).getImpl();
		return TileRenderer.render(fromRow, toRow, fromColumn, toColumn, modelContext, modelColoring, canvas);
	}
	
	public static DemCanvas render(int fromRow, int toRow, int fromColumn, int toColumn, ModelContext modelContext, ModelColoring modelColoring) throws RenderEngineException
	{
		return TileRenderer.render(fromRow, toRow, fromColumn, toColumn, modelContext, modelColoring, null);
	}
	
	public static DemCanvas render(int fromRow, int toRow, int fromColumn, int toColumn, ModelContext modelContext, ModelColoring modelColoring, DemCanvas canvas) throws RenderEngineException
	{
		if (canvas == null) {
            int width = (int)(toColumn - fromColumn) + 1;
            int height = (int)(toRow - fromRow) + 1;
            log.info("Creating default canvas of width/height: " + width + "/" + height);
            canvas = new DemCanvas(DEFAULT_BACKGROUND, width, height);
		}
		
		TileRenderer renderer = new TileRenderer(modelContext, modelColoring, canvas);
		renderer.renderTile(fromRow, toRow, fromColumn, toColumn);
		
		return canvas;
	}

	
}
