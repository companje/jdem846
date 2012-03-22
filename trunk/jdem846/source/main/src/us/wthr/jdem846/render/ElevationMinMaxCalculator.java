package us.wthr.jdem846.render;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.DataSourceException;
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
	
	
	public ElevationMinMaxCalculator(ModelContext modelContext)
	{
		this.modelContext = modelContext;
		
		latitudeResolution = modelContext.getModelDimensions().getOutputLatitudeResolution();
		longitudeResolution = modelContext.getModelDimensions().getOutputLongitudeResolution();
		
		getStandardResolutionElevation = JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.standardResolutionRetrieval");
		interpolateData = JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.interpolateToHigherResolution");
		averageOverlappedData = JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.averageOverlappedData");
		
		
		
	}
	
	
	public ElevationMinMax calculateMinAndMax() throws DataSourceException
	{

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		

		if (modelContext.getRasterDataContext().getRasterDataListSize() > 0) {
		
			double north = modelContext.getNorth();
			double south = modelContext.getSouth();
			double east = modelContext.getEast();
			double west = modelContext.getWest();
			
			
			
			
			for (double lon = west; lon < east - modelContext.getRasterDataContext().getLongitudeResolution(); lon+=longitudeResolution) {
				for (double lat = north; lat > south + modelContext.getRasterDataContext().getLatitudeResolution(); lat-=latitudeResolution) {
					double elevation = getElevation(lat, lon);

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
		
		log.info("Elevation data maximum: " + max);
		log.info("Elevation data minimum: " + min);
		
		
		ElevationMinMax minMax = new ElevationMinMax(min, max);
		return minMax;
		//rasterDataContext.setDataMaximumValue(max);
		//rasterDataContext.setDataMinimumValue(min);
		
		
		
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
