package us.wthr.jdem846.model.processing.dataload;

import java.math.BigDecimal;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.model.ModelPoint;
import us.wthr.jdem846.model.ModelPointHandler;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.scripting.ScriptProxy;

@GridProcessing(id="us.wthr.jdem846.model.processing.dataload.GridLoadProcessor",
				name="Data Grid Load Process",
				type=GridProcessingTypesEnum.DATA_LOAD,
				optionModel=GridLoadOptionModel.class,
				enabled=true
				)
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
	
	protected double minimumElevation = Double.NaN;
	protected double maximumElevation = Double.NaN;
	
	public GridLoadProcessor()
	{
		
	}
	
	
	public GridLoadProcessor(ModelContext modelContext, ModelGrid modelGrid)
	{
		super(modelContext, modelGrid);
	}
	
	@Override
	public void prepare() throws RenderEngineException
	{
		GridLoadOptionModel optionModel = (GridLoadOptionModel) this.getProcessOptionModel();
	
		useScripting = getGlobalOptionModel().getUseScripting();
		//tiledPrecaching = JDem846Properties.getProperty("us.wthr.jdem846.performance.precacheStrategy").equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_TILED);
		
		tiledPrecaching = getGlobalOptionModel().getPrecacheStrategy().equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_TILED);
		
		north = getGlobalOptionModel().getNorthLimit();
		south = getGlobalOptionModel().getSouthLimit();
		east = getGlobalOptionModel().getEastLimit();
		west = getGlobalOptionModel().getWestLimit();
		
		getStandardResolutionElevation = getGlobalOptionModel().getStandardResolutionElevation();
		averageOverlappedData = getGlobalOptionModel().getAverageOverlappedData();
		interpolateData = getGlobalOptionModel().getInterpolateData();
		
		//getStandardResolutionElevation = JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.standardResolutionRetrieval");
		//interpolateData = JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.interpolateToHigherResolution");
		//averageOverlappedData = JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.averageOverlappedData");
		
		latitudeResolution = getModelDimensions().getLatitudeResolution();
		//tileHeight = JDem846Properties.getIntProperty("us.wthr.jdem846.performance.tileSize");
		tileHeight = getGlobalOptionModel().getTileSize();
		cacheHeight = latitudeResolution * tileHeight;
		nextCachePoint = north;
		
		minimumElevation = Double.NaN;
		maximumElevation = Double.NaN;
	}

	@Override
	public void process() throws RenderEngineException
	{
		
		boolean fullCaching = getGlobalOptionModel().getPrecacheStrategy().equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_FULL);
		RasterDataContext dataContext = (dataRasterContextSubset != null) ? dataRasterContextSubset : modelContext.getRasterDataContext();
		
		if (fullCaching) {
			try {
				dataContext.fillBuffers();
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Error loading full data buffer: " + ex.getMessage(), ex);
			}
		}
		
		super.process();
		
		if (fullCaching) {
			try {
				dataContext.clearBuffers();
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Error clearing full data buffer: " + ex.getMessage(), ex);
			}	
		}

		//modelContext.getRasterDataContext().setDataMaximumValue(maximumElevation);
		//modelContext.getRasterDataContext().setDataMinimumValue(minimumElevation);
		
		log.info("Found Minimum Elevation: " + minimumElevation);
		log.info("Found Maximum Elevation: " + maximumElevation);
		
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
				//ModelPoint modelPoint = modelGrid.get(latitude, longitude);
				//if (modelPoint != null) {
					//modelPoint.setElevation(elev);
					modelGrid.setElevation(latitude, longitude, elev);
					if (Double.isNaN(minimumElevation)) {
						minimumElevation = elev;
					} else {
						minimumElevation = MathExt.min(minimumElevation, elev);
					}
					
					if (Double.isNaN(maximumElevation)) {
						maximumElevation = elev;
					} else {
						maximumElevation = MathExt.max(maximumElevation, elev);
					}
				
				//}
				
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
			ScriptProxy scriptProxy = modelContext.getScriptingContext().getScriptProxy();
			if (scriptProxy != null) {
				result = scriptProxy.onGetElevationBefore(latitude, longitude);
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
			ScriptProxy scriptProxy = modelContext.getScriptingContext().getScriptProxy();
			if (scriptProxy != null) {
				result = scriptProxy.onGetElevationAfter(latitude, longitude, elevation);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
		return result;
	}
	
	
	
	public boolean tiledPrecaching()
	{
		return tiledPrecaching;
	}

	public void setTiledPrecaching(boolean tiledPrecaching)
	{
		this.tiledPrecaching = tiledPrecaching;
	}

	public boolean useScripting()
	{
		return useScripting;
	}

	public void setUseScripting(boolean useScripting)
	{
		this.useScripting = useScripting;
	}

	public boolean isGetStandardResolutionElevation()
	{
		return getStandardResolutionElevation;
	}

	public void setGetStandardResolutionElevation(
			boolean getStandardResolutionElevation)
	{
		this.getStandardResolutionElevation = getStandardResolutionElevation;
	}

	public boolean interpolateData()
	{
		return interpolateData;
	}

	public void setInterpolateData(boolean interpolateData)
	{
		this.interpolateData = interpolateData;
	}

	public boolean averageOverlappedData()
	{
		return averageOverlappedData;
	}

	public void setAverageOverlappedData(boolean averageOverlappedData)
	{
		this.averageOverlappedData = averageOverlappedData;
	}
	
	
}
