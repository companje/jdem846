package us.wthr.jdem846.render;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.rasterdata.RasterDataContext;

public class ElevationMinMaxCalculator
{
	private static Log log = Logging.getLog(ElevationMinMaxCalculator.class);
	
	private ModelContext modelContext;
	
	
	private double longitudeResolution;
	private double latitudeResolution;
	private boolean getStandardResolutionElevation = true;
	private boolean interpolateData = true;
	private boolean averageOverlappedData = true;
	
	private CancelIndicator cancelIndicator;
	
	public ElevationMinMaxCalculator(ModelContext modelContext)
	{
		this(modelContext, null);
	}
	
	public ElevationMinMaxCalculator(ModelContext modelContext, CancelIndicator cancelIndicator)
	{
		this.modelContext = modelContext;
		
		if (cancelIndicator != null) {
			this.cancelIndicator = cancelIndicator;
		} else {
			this.cancelIndicator = new CancelIndicator();
		}
		
		latitudeResolution = modelContext.getModelDimensions().getOutputLatitudeResolution();
		longitudeResolution = modelContext.getModelDimensions().getOutputLongitudeResolution();
		
		getStandardResolutionElevation = JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.standardResolutionRetrieval");
		interpolateData = JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.interpolateToHigherResolution");
		averageOverlappedData = JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.averageOverlappedData");
		
		
		
	}
	
	
	public ElevationMinMax calculateMinAndMax() throws DataSourceException
	{
		log.info("Calculating elevation min/max");
		
		
		
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		double north = modelContext.getNorth();
		double south = modelContext.getSouth();
		double east = modelContext.getEast();
		double west = modelContext.getWest();
		
		
		boolean tiledPrecaching = JDem846Properties.getProperty("us.wthr.jdem846.performance.precacheStrategy").equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_TILED);;
		double tileHeight = JDem846Properties.getIntProperty("us.wthr.jdem846.performance.tileSize");
		
		double cacheHeight = modelContext.getRasterDataContext().getLatitudeResolution() * tileHeight;
		double nextCachePoint = north;
		
		double maxLon = east + longitudeResolution;
		double minLat = south - latitudeResolution;
		
		if (modelContext.getRasterDataContext().getRasterDataListSize() > 0) {

			
			for (double lat = north; lat >= minLat; lat-=latitudeResolution) {
				
				double nwLat = lat;
				double swLat = lat - latitudeResolution;
				
				if (lat <= nextCachePoint && tiledPrecaching) {
					
					double southCache = lat - cacheHeight - latitudeResolution;
					try {
						unloadDataBuffers();
						loadDataBuffers(nwLat, southCache, east, west);
					} catch (RenderEngineException ex) {
						throw new DataSourceException("Error loading data buffer: " + ex.getMessage(), ex);
					}
					
					nextCachePoint = lat - cacheHeight;
				}
				
				
				for (double lon = west; lon <= maxLon; lon+=longitudeResolution) {
					double elevation = getElevation(lat, lon);

					if (!Double.isNaN(elevation) && elevation != DemConstants.ELEV_NO_DATA) {
						min = MathExt.min(elevation, min);
						max = MathExt.max(elevation, max);
					}
					
					if (cancelIndicator != null && cancelIndicator.isCancelled()) {
						break;
					}
				}
				
				if (cancelIndicator != null && cancelIndicator.isCancelled()) {
					break;
				}
			}
			
			
			if (tiledPrecaching) {
				try {
					unloadDataBuffers();
				} catch (RenderEngineException ex) {
					throw new DataSourceException("Error unloading data buffer: " + ex.getMessage(), ex);
				}
			}
			
		} else {
			max = 0;
			min = 0;
		}
		
		log.info("Elevation data maximum: " + max);
		log.info("Elevation data minimum: " + min);
		
		
		ElevationMinMax minMax = new ElevationMinMax(min, max);
		return minMax;

		
	}
	
	public void cancel()
	{
		if (this.cancelIndicator != null) {
			this.cancelIndicator.cancel(); 
		}
	}
	
	
	protected void loadDataBuffers(double north, double south, double east, double west) throws RenderEngineException
	{
		
		RasterDataContext dataContext = modelContext.getRasterDataContext();
		
		//if (tiledPrecaching) {
			try {
				dataContext.fillBuffers(north, south, east, west);
			} catch (Exception ex) {
				throw new RenderEngineException("Failed to buffer data: " + ex.getMessage(), ex);
			}
		//}
	}
	
	
	protected void unloadDataBuffers() throws RenderEngineException
	{
		RasterDataContext dataContext =  modelContext.getRasterDataContext();
		//if (tiledPrecaching) {
			try {
				dataContext.clearBuffers();
			} catch (Exception ex) {
				throw new RenderEngineException("Failed to clear buffer data: " + ex.getMessage(), ex);
			}
		//}
	}
	
	protected double getElevation(double latitude, double longitude) throws DataSourceException
	{
		
		double elevation = DemConstants.ELEV_NO_DATA;

		if (modelContext.getRasterDataContext().getRasterDataListSize() > 0) {
			if (getStandardResolutionElevation) {
				elevation = modelContext.getRasterDataContext().getDataStandardResolution(latitude, longitude, averageOverlappedData, interpolateData);
			} else {
				elevation = modelContext.getRasterDataContext().getDataAtEffectiveResolution(latitude, longitude, averageOverlappedData, interpolateData);
			} 
		} 
		
		return elevation;
	}
	

}
