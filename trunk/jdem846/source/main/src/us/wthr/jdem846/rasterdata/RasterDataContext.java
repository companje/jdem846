package us.wthr.jdem846.rasterdata;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.DataContext;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;

public class RasterDataContext implements DataContext
{
	private static Log log = Logging.getLog(RasterDataContext.class);
	private static final double NOT_SET = DemConstants.ELEV_NO_DATA;
	
	
	private List<RasterData> rasterDataList = new LinkedList<RasterData>();
	private List<RasterDataRowColumnBox> rasterDataRowColumnBoxes = new LinkedList<RasterDataRowColumnBox>();
	
	private double east = 180.0;
	private double west = -180.0;
	private double north = 90.0;
	private double south = -90.0;
	
	private double latitudeResolution;
	private double longitudeResolution;
	
	private double effectiveLatitudeResolution = NOT_SET;
	private double effectiveLongitudeResolution = NOT_SET;
	
	private double dataMinimumValue = 0;
	private double dataMaximumValue = 0;
	
	private double metersResolution;
	
	private boolean isDisposed = false;
	
	public RasterDataContext()
	{
		
	}
	
	public void prepare() throws DataSourceException
	{
		
		east = -180.0;
		west = 180.0;
		north = -90.0;
		south = 90.0;
		
		latitudeResolution = Double.MAX_VALUE;
		longitudeResolution = Double.MAX_VALUE;
		
		rasterDataRowColumnBoxes.clear();
		
		for (RasterData rasterData : rasterDataList) {
			
			if (rasterData.getNorth() > north)
				north = rasterData.getNorth();
			
			if (rasterData.getSouth() < south)
				south = rasterData.getSouth();
			
			if (rasterData.getEast() > east)
				east = rasterData.getEast();
			
			if (rasterData.getWest() < west)
				west = rasterData.getWest();
			
			if (rasterData.getLatitudeResolution() < latitudeResolution)
				latitudeResolution = rasterData.getLatitudeResolution();
			
			if (rasterData.getLongitudeResolution() < longitudeResolution)
				longitudeResolution = rasterData.getLongitudeResolution();
			
			RasterDataRowColumnBox rowColBox = new RasterDataRowColumnBox(this.longitudeToColumn(rasterData.getWest()), this.latitudeToRow(rasterData.getNorth()), rasterData.getColumns(), rasterData.getRows());
			rasterDataRowColumnBoxes.add(rowColBox);
		}
		
		metersResolution = getMetersResolution(DemConstants.EARTH_MEAN_RADIUS);
		
		
		north = validateLatitude(north);
		south = validateLatitude(south);
		west = validateLongitude(west);
		east = validateLongitude(east);
		
		log.info("Prepared RasterDataProxy to region N/S/E/W: " + north + "/" + south + "/" + east + "/" + west);
		log.info("Prepared RasterDataProxy to lat/long resolutions: " + latitudeResolution + "/" + longitudeResolution);
		log.info("Prepared Meters Resolution: " + metersResolution);
	}
	
	protected double validateLongitude(double longitude)
	{
		if (longitude <= -180.0)
			longitude = -180.0;
		if (longitude >= 180.0)
			longitude = 180.0;
		return longitude;
	}
	
	protected double validateLatitude(double latitude)
	{
		if (latitude <= -90.0)
			latitude = -90.0;
		if (latitude >= 90.0)
			latitude = 90.0;
		return latitude;
	}
	
	public void dispose() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Raster data proxy already disposed.");
		}
		
		for (RasterData rasterData : rasterDataList) {
			if (!rasterData.isDisposed()) {
				rasterData.dispose();
			}
		}
		
		
		// TODO: Finish
		isDisposed = true;
	}

	
	public boolean isDisposed()
	{
		return isDisposed;
	}
	
	public void calculateElevationMinMax(boolean full) throws DataSourceException
	{
		log.info("Calculating elevation minimums & Maximums");
		
		dataMinimumValue = Double.MAX_VALUE;
		dataMaximumValue = Double.MIN_VALUE;
		
		for (RasterData rasterData : rasterDataList) {
			double minValue = rasterData.getDataMinimum();
			double maxValue = rasterData.getDataMaximum();
			
			if (minValue == DemConstants.ELEV_NO_DATA || maxValue == DemConstants.ELEV_NO_DATA) {
				if (full) {
					rasterData.calculateMinAndMax();
				} else {
					continue;
				}
			}
			
			
			
			
			
			if (rasterData.getDataMinimum() < dataMinimumValue)
				dataMinimumValue = rasterData.getDataMinimum();
			
			if (rasterData.getDataMaximum() > dataMaximumValue)
				dataMaximumValue = rasterData.getDataMaximum();
			
		}
		
		if (dataMinimumValue == Double.MAX_VALUE) {
			dataMinimumValue = DemConstants.ELEV_NO_DATA;
		}
		
		if (dataMaximumValue == Double.MIN_VALUE) {
			dataMaximumValue = DemConstants.ELEV_NO_DATA;
		}
	}
	

	public double  getMetersResolution()
	{
		return metersResolution;
	}
	
	public double getMetersResolution(double meanRadius)
	{
		double lat1 = getSouth();
		double lon1 = getWest();
		double lat2 = lat1 + getLatitudeResolution();
		double lon2 = lon1 + getLongitudeResolution();
		double R = meanRadius;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		

		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon/2) * Math.sin(dLon/2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = R * c * 1000;
		return d;
	}
	
	
	public void addRasterData(RasterData rasterData) throws DataSourceException
	{
		rasterDataList.add(rasterData);
		prepare();
	}

	public void removeRasterData(RasterData rasterData) throws DataSourceException
	{
		if (rasterDataList.remove(rasterData)) {
			prepare();
		}
	}
	
	public RasterData removeRasterData(int index) throws DataSourceException
	{
		RasterData removed = rasterDataList.remove(index);
		prepare();
		return removed;
	}
	
	public List<RasterData> getRasterDataList()
	{
		return rasterDataList;
	}
	
	public int getRasterDataListSize()
	{
		return rasterDataList.size();
	}
	

	public List<RasterDataRowColumnBox> getRasterDataRowColumnBoxes()
	{
		return rasterDataRowColumnBoxes;
	}

	
	public boolean dataOverlaps(RasterDataRowColumnBox bounds)
	{
		for (RasterDataRowColumnBox inputBounds : rasterDataRowColumnBoxes) {
			if (inputBounds.overlaps(bounds))
				return true;
		}
		return false;
	}
	
	public void fillBuffers() throws DataSourceException
	{
		fillBuffers(north, south, east, west);
	}
	
	public void fillBuffers(double north, double south, double east, double west) throws DataSourceException
	{
		int fillCount = 0;
		for (RasterData rasterData : rasterDataList) {
			if (rasterData.fillBuffer(north, south, east, west)) {
				fillCount++;
			}
		}

		log.info("" + fillCount + " data rasters matched the caching range");
		
	}
	
	public void clearBuffers() throws DataSourceException
	{
		for (RasterData rasterData : rasterDataList) {
			rasterData.clearBuffer();
		}
	}
	
	
	public double getData(double latitude, double longitude) throws DataSourceException
	{
		return getData(latitude, longitude, false, false);
	}
	
	public double getData(double latitude, double longitude, boolean avgOfAllRasterValues, boolean interpolate) throws DataSourceException
	{
		if (effectiveLatitudeResolution == NOT_SET && effectiveLongitudeResolution == NOT_SET) {
			return getDataStandardResolution(latitude, longitude, avgOfAllRasterValues, interpolate);
		} else {
			return getDataAtEffectiveResolution(latitude, longitude, avgOfAllRasterValues, interpolate);
		}
	}
	
	protected double getDataStandardResolution(double latitude, double longitude, boolean avgOfAllRasterValues, boolean interpolate) throws DataSourceException
	{
		double value = 0;
		double dataMatches = 0;
		
		for (RasterData rasterData : rasterDataList) {
			if (rasterData.contains(latitude, longitude)) {
				double rasterValue = rasterData.getData(latitude, longitude, interpolate);
				
				if (!avgOfAllRasterValues) {
					return rasterValue;
				}
				
				if (rasterValue != DemConstants.ELEV_NO_DATA && !Double.isNaN(rasterValue)) {
					value += rasterValue;
					dataMatches++;
				}
			}
		}
		
		return (value / dataMatches);
	}
	
	protected double getDataAtEffectiveResolution(double latitude, double longitude, boolean avgOfAllRasterValues, boolean interpolate) throws DataSourceException
	{
		
		
		double width = effectiveLongitudeResolution / longitudeResolution;
		double height = effectiveLatitudeResolution / latitudeResolution;
		
		double northLimit = getNorth();
		double southLimit = getSouth();
		double eastLimit = getEast();
		double westLimit = getWest();
		
		
		double effectiveRows = (north - south) / effectiveLatitudeResolution;
		double effectiveColumns = (east - west) / effectiveLongitudeResolution;
		
		double dataNorth = latitude;
		double dataSouth = latitude - effectiveLatitudeResolution;
		
		double dataWest = longitude;
		double dataEast = longitude + effectiveLongitudeResolution;
		
		//double topRow = MathExt.floor(((north - latitude) / (north - south)) * effectiveRows);
		//double bottomRow = MathExt.ceil(((north - (latitude - effectiveLatitudeResolution)) / (north - south)) * effectiveRows);
		
		//double leftColumn = MathExt.floor(((longitude - west) / (east - west)) * effectiveColumns);
		//double rightColumn = MathExt.ceil((((longitude + effectiveLongitudeResolution) - west) / (east - west)) * effectiveColumns);
		
		double topRow = MathExt.floor(((northLimit - dataNorth ) / (northLimit - southLimit)) * effectiveRows) - 1;
		double bottomRow = MathExt.ceil(((northLimit - dataSouth ) / (northLimit - southLimit)) * effectiveRows);
		
		double leftColumn = MathExt.floor(((dataWest - westLimit) / (eastLimit - westLimit)) * effectiveColumns);
		double rightColumn = MathExt.ceil(((dataEast - westLimit) / (eastLimit - westLimit)) * effectiveColumns);
		
		
		
		int dataWidth = (int) (rightColumn - leftColumn )+ 1;
		int dataHeight = (int) (bottomRow - topRow) + 1;
		
		//double topLatitude = north - topRow * effectiveLatitudeResolution;
		//double bottomLatitude = topLatitude - (dataHeight * effectiveLatitudeResolution);
		
		//double topLatitude = northLimit - topRow * effectiveLatitudeResolution;
		//double bottomLatitude = northLimit - bottomRow * effectiveLatitudeResolution;
		//double leftLongitude = westLimit + leftColumn * effectiveLongitudeResolution;
		//double rightLongitude = westLimit + rightColumn * effectiveLongitudeResolution;
		
		
		
		//double leftLongitude = west + leftColumn * effectiveLongitudeResolution;
		//double rightLongtiude = leftLongitude + (dataWidth * effectiveLongitudeResolution);
		
		
		
		
		//int dataWidth = (int) Math.ceil(width);
		//int dataHeight = (int) Math.ceil(height);
		
		double[][] data = new double[dataHeight][dataWidth];
		
		
		for (int h = 0; h < dataHeight; h++) {
			for (int w = 0; w < dataWidth; w++) {
				double getLat = dataNorth - (h * latitudeResolution);
				double getLon = dataWest + (w * longitudeResolution);
				double value = getDataStandardResolution(getLat, getLon, avgOfAllRasterValues, interpolate);
				data[h][w] = value;
			}
		}
		
		// Not 100% correct, but works for now...
		double dataSum = 0;
		double dataPoints = 0;
		for (int h = 0; h < dataHeight; h++) {
			for (int w = 0; w < dataWidth; w++) {
				double value = data[h][w];
				if (value != DemConstants.ELEV_NO_DATA && !Double.isNaN(value)) {
					dataSum += value;
					dataPoints++;
				}
			}
		}
		
		double finalValue = dataSum / dataPoints;
		return finalValue;
	}

	
	public RasterDataContext getSubSet(double north, double south, double east, double west) throws DataSourceException
	{
		RasterDataLatLongBox subsetLatLongBox = new RasterDataLatLongBox(north, south, east, west);
		// TODO: This isn't working properly.

		RasterDataContext newDataProxy = new RasterDataContext();
		
		for (RasterData rasterData : rasterDataList) {
			if (rasterData.intersects(subsetLatLongBox)) {
				newDataProxy.getRasterDataList().add(rasterData);
			}
			
		}
		
		newDataProxy.prepare();

		
		return newDataProxy;
	}
	
	
	public int latitudeToRow(double latitude)
	{
		// Nearest neighbor
		return (int) Math.floor((north - latitude) / this.getLatitudeResolution());
	}
	
	public double rowToLatitude(int row)
	{
		return (north - ((double)row * this.getLatitudeResolution()));
	}
	
	public int longitudeToColumn(double longitude)
	{
		// Nearest neighbor
		return (int) Math.floor((longitude - west) / this.getLongitudeResolution());
	}
	
	public double columnToLongitude(int column)
	{
		return west + ((double)column * this.getLongitudeResolution());
	}
	
	
	public double getNorth()
	{
		return validateLatitude(north); 
	}
	
	public double getSouth()
	{
		return validateLatitude(south);
	}
	
	public double getEast()
	{
		return validateLongitude(east);
	}
	
	public double getWest()
	{
		return validateLongitude(west);
	}
	
	public int getDataRows()
	{
		return getDataRows(north, south);
	}
	
	public int getDataRows(double north, double south)
	{
		return (int) Math.floor((north - south) / this.getLatitudeResolution());
	}
	
	public int getDataColumns()
	{
		return getDataColumns(east, west);
	}
	
	public int getDataColumns(double east, double west)
	{
		return (int) Math.floor((east - west) / this.getLongitudeResolution());
	}

	
	public double getLatitudeResolution() 
	{
		return latitudeResolution;
	}
	
	public void setLatitudeResolution(double latitudeResolution)
	{
		this.latitudeResolution = latitudeResolution;
	}

	public double getLongitudeResolution() 
	{
		return longitudeResolution;
	}
	
	public void setLongitudeResolution(double longitudeResolution)
	{
		this.longitudeResolution = longitudeResolution;
	}

	public double getDataMinimumValue()
	{
		return dataMinimumValue;
	}

	public void setDataMinimumValue(double dataMinimumValue)
	{
		this.dataMinimumValue = dataMinimumValue;
	}
	
	public double getDataMaximumValue()
	{
		return dataMaximumValue;
	}
	
	public void setDataMaximumValue(double dataMaximumValue)
	{
		this.dataMaximumValue = dataMaximumValue;
	}
	
	
	
	public double getEffectiveLatitudeResolution()
	{
		if (effectiveLatitudeResolution != NOT_SET)
			return effectiveLatitudeResolution;
		else
			return getLatitudeResolution();
	}

	public void setEffectiveLatitudeResolution(double effectiveLatitudeResolution)
	{
		this.effectiveLatitudeResolution = effectiveLatitudeResolution;
	}

	public double getEffectiveLongitudeResolution()
	{
		if (effectiveLongitudeResolution != NOT_SET)
			return effectiveLongitudeResolution;
		else
			return getLongitudeResolution();
	}

	public void setEffectiveLongitudeResolution(double effectiveLongitudeResolution)
	{
		this.effectiveLongitudeResolution = effectiveLongitudeResolution;
	}

	public RasterDataContext copy() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Cannot copy object: already disposed");
		}
		
		RasterDataContext clone = new RasterDataContext();
		clone.north = getNorth();
		clone.south = getSouth();
		clone.east = getEast();
		clone.west = getWest();
		clone.latitudeResolution = getLatitudeResolution();
		clone.longitudeResolution = getLongitudeResolution();
		clone.effectiveLatitudeResolution = getEffectiveLatitudeResolution();
		clone.effectiveLongitudeResolution = getEffectiveLongitudeResolution();
		clone.dataMaximumValue = getDataMaximumValue();
		clone.dataMinimumValue = getDataMinimumValue();
		clone.isDisposed = isDisposed(); // Should be false at this point...		
		clone.metersResolution = getMetersResolution();
		
		for (RasterData rasterData : rasterDataList) {
			clone.rasterDataList.add(rasterData.copy());
		}
		
		//List<RasterDataRowColumnBox> rasterDataRowColumnBoxes
		
		for (RasterDataRowColumnBox box : rasterDataRowColumnBoxes) {
			clone.rasterDataRowColumnBoxes.add(box.copy());
		}
		
		return clone;
	}
}
