package us.wthr.jdem846.render.render3;

import java.math.BigDecimal;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.scripting.ScriptProxy;

public class GridLoadProcessor extends AbstractGridProcessor implements GridProcessor, ModelPointHandler
{
	private static Log log = Logging.getLog(GridLoadProcessor.class);
	
	
	protected RasterDataContext dataRasterContextSubset;
	
	protected boolean getStandardResolutionElevation = true;
	protected boolean interpolateData = true;
	protected boolean averageOverlappedData = true;
	
	protected boolean tiledPrecaching = false;
	protected boolean useScripting = true;
	
	protected double north;
	protected double south;
	protected double east;
	protected double west;
	
	protected double latitudeResolution;
	protected double tileHeight;
	protected double cacheHeight;
	protected double nextCachePoint;
	
	public GridLoadProcessor(ModelContext modelContext, ModelGrid modelGrid)
	{
		super(modelContext, modelGrid);
	}
	
	@Override
	public void prepare() throws RenderEngineException
	{
		tiledPrecaching = JDem846Properties.getProperty("us.wthr.jdem846.performance.precacheStrategy").equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_TILED);
		
		north = modelContext.getNorth();
		south = modelContext.getSouth();
		east = modelContext.getEast();
		west = modelContext.getWest();
		
		getStandardResolutionElevation = JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.standardResolutionRetrieval");
		interpolateData = JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.interpolateToHigherResolution");
		averageOverlappedData = JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.averageOverlappedData");
		
		latitudeResolution = modelContext.getRasterDataContext().getLatitudeResolution();
		tileHeight = JDem846Properties.getIntProperty("us.wthr.jdem846.performance.tileSize");
		cacheHeight = latitudeResolution * tileHeight;
		nextCachePoint = north;
	}

	@Override
	public void process() throws RenderEngineException
	{
		super.process();
	}
	
	public void onCycleStart() throws RenderEngineException
	{
		
	}
	
	@Override
	public void onModelLatitudeStart(double latitude) throws RenderEngineException
	{
		if (latitude <= nextCachePoint && tiledPrecaching) {
			
			double southCache = latitude - cacheHeight - latitudeResolution;
			try {
				unloadDataBuffers();
				loadDataBuffers(latitude, southCache, east, west);
			} catch (RenderEngineException ex) {
				throw new RenderEngineException("Error loading data buffer: " + ex.getMessage(), ex);
			}
			
			nextCachePoint = latitude - cacheHeight;
		}
	}
	
	@Override
	public void onModelPoint(double latitude, double longitude) throws RenderEngineException
	{
		try {
			double elev = getElevation(latitude, longitude);
			if (elev != DemConstants.ELEV_NO_DATA) {
				modelGrid.get(latitude, longitude).setElevation(elev);
			}
			
		} catch (Exception ex) {
			throw new RenderEngineException("Error processing point elevation: " + ex.getMessage(), ex);
		}
		
		
	}
	
	@Override
	public void onModelLatitudeEnd(double latitude) throws RenderEngineException
	{
		
	}
	
	public void onCycleEnd() throws RenderEngineException
	{
		if (tiledPrecaching) {
			unloadDataBuffers();
		}
	}

	protected double getRasterDataRaw(double latitude, double longitude) throws DataSourceException
	{
		double data = DemConstants.ELEV_NO_DATA;
		
		//RasterDataContext rasterDataContext = modelContext.getRasterDataContext();
		RasterDataContext dataContext = (dataRasterContextSubset != null) ? dataRasterContextSubset :  modelContext.getRasterDataContext();

		
		if (dataContext.getRasterDataListSize() > 0) {
			if (getStandardResolutionElevation) {
				data = dataContext.getDataStandardResolution(latitude, longitude, averageOverlappedData, interpolateData);
			} else {
				data = dataContext.getDataAtEffectiveResolution(latitude, longitude, averageOverlappedData, interpolateData);
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
	
	
	

	protected void loadRasterDataSubset(double north, double south, double east, double west) throws DataSourceException
	{
		dataRasterContextSubset = modelContext.getRasterDataContext().getSubSet(north, south, east, west);
	}
	
	protected void loadDataBuffers(double north, double south, double east, double west) throws RenderEngineException
	{
		
		RasterDataContext dataContext = (dataRasterContextSubset != null) ? dataRasterContextSubset : modelContext.getRasterDataContext();
		
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
		RasterDataContext dataContext = (dataRasterContextSubset != null) ? dataRasterContextSubset : modelContext.getRasterDataContext();
		if (tiledPrecaching) {
			try {
				dataContext.clearBuffers();
			} catch (Exception ex) {
				throw new RenderEngineException("Failed to clear buffer data: " + ex.getMessage(), ex);
			}
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
	
}