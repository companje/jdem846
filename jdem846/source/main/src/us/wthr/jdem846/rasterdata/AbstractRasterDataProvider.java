package us.wthr.jdem846.rasterdata;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public abstract class AbstractRasterDataProvider implements RasterData
{
	private static Log log = Logging.getLog(AbstractRasterDataProvider.class);
	
	
	private double latitudeResolution;
	private double longitudeResolution;
	

	private double dataMinimum = DemConstants.ELEV_NO_DATA;
	private double dataMaximum = DemConstants.ELEV_NO_DATA;
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	private int rows;
	private int columns;

	private RasterDataLatLongBox dataLatLongBox;
	
	
	protected void prepare()
	{
		dataLatLongBox = new RasterDataLatLongBox(getNorth(), getSouth() + latitudeResolution, getEast(), getWest() - longitudeResolution);
	}
	
	public boolean contains(double latitude, double longitude)
	{
		// TODO: This is overly simplistic. Make this a bit more robust.
		
		if (latitude > south && latitude <= north && longitude >= west && longitude < east) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean intersects(RasterDataLatLongBox otherBox)
	{
		return dataLatLongBox.intersects(otherBox);
	}
	
	public double  getMetersResolution()
	{
		return getMetersResolution(DemConstants.EARTH_MEAN_RADIUS);
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
	
	
	public void calculateMinAndMax() throws DataSourceException
	{
		setDataMaximum(-50000);
		setDataMinimum(50000);

		
		double northLimit = getNorth();
		double southLimit = getSouth();
		double eastLimit = getEast();
		double westLimit = getWest();
		
		double latitudeResolution = this.getLatitudeResolution();
		double longitudeResolution = this.getLongitudeResolution();
		
		int tileSize = 1000;
		double tileLatitudeHeight = latitudeResolution * tileSize - latitudeResolution;
		double tileLongitudeWidth = longitudeResolution * tileSize - longitudeResolution;
		
		boolean bufferAlreadyFilled = this.isBufferFilled();
		
		for (double tileNorth = northLimit; tileNorth > southLimit; tileNorth -= tileLatitudeHeight) {
			double tileSouth = tileNorth - tileLatitudeHeight;
			if (tileSouth <= southLimit) {
				tileSouth = southLimit + latitudeResolution;
			}
			
			for (double tileWest = westLimit; tileWest < eastLimit; tileWest += tileLongitudeWidth) {
				double tileEast = tileWest + tileLongitudeWidth;
				
				if (tileEast >= eastLimit) {
					tileEast = eastLimit - longitudeResolution;
				}
				
				if (!bufferAlreadyFilled) {
					this.fillBuffer(tileNorth, tileSouth, tileEast, tileWest);
				}
				
				calculateSubsetMinAndMax(tileNorth, tileSouth, tileEast, tileWest);
				
				if (!bufferAlreadyFilled) {
					this.clearBuffer();
				}
			}
			
		}

	}
	
	protected void calculateSubsetMinAndMax(double northLimit, double southLimit, double eastLimit, double westLimit) throws DataSourceException
	{
		double max = getDataMaximum();
		double min = getDataMinimum();
		
		for (double latitude = northLimit; latitude >= southLimit; latitude -= latitudeResolution) {

			for (double longitude = westLimit; longitude <= eastLimit; longitude += longitudeResolution) {
				double elevation = getData(latitude, longitude);
				if (elevation == DemConstants.ELEV_NO_DATA)
					continue;

				if (elevation > max) {
					max = elevation;
				} 
				if (elevation < min) {
					min = elevation;
				}
			}
		}
		
		setDataMinimum(min);
		setDataMaximum(max);
	}
	
	
	protected double latitudeToRow(double latitude)
	{
		return ((north - latitude) / this.getLatitudeResolution());
	}
	
	protected double rowToLatitude(int row)
	{
		return (north - ((double)row * this.getLatitudeResolution()));
	}
	
	protected double longitudeToColumn(double longitude)
	{
		return ((longitude - west) / this.getLongitudeResolution());
	}
	
	protected double columnToLongitude(int column)
	{
		return west + ((double)column * this.getLongitudeResolution());
	}
	
	
	public double getData(double latitude, double longitude) throws DataSourceException
	{
		return getData(latitude, longitude, false);
	}
	
	@Override
	public double getData(double latitude, double longitude, boolean interpolate) throws DataSourceException
	{
		double fetchRow = this.latitudeToRow(latitude);
		double fetchColumn = this.longitudeToColumn(longitude);
		
		int row = (int) fetchRow;
		int column = (int) fetchColumn;
		
		
		double s00 = 0;
		double s01 = 0;
		double s10 = 0;
		double s11 = 0;
		
		double xFrac = fetchColumn - column;
		double yFrac = fetchRow - row;
		
		s00 = getData(row, column);
		
		if (!interpolate) {
			return s00;
		}
		
		if ((column + 1 < columns && row + 1 < rows)) {
			
			s01 = getData(row, column + 1);
			s10 = getData(row + 1, column);
			s11 = getData(row + 1, column + 1);
			
			
			
			s00 = getNotElevNoData(s00, s01, s10, s11);
			s01 = getNotElevNoData(s01, s00, s11, s10);
			s10 = getNotElevNoData(s10, s00, s11, s01);
			s11 = getNotElevNoData(s11, s01, s10, s00);

		} else {
			s11 = s10 = s01 = s00;
		}
		

		double data = interpolate(s00, s01, s10, s11, xFrac, yFrac);

		return data;

	}
	
	protected double getNotElevNoData(double...values)
	{
		for (int i = 0; i < values.length; i++) {
			if (values[i] != DemConstants.ELEV_NO_DATA) {
				return values[i];
			}
		}
		return DemConstants.ELEV_NO_DATA;
	}
	
	protected double interpolate(double s00, double s01, double s10, double s11, double xFrac, double yFrac)
	{
		double s0 = (s01 - s00)*xFrac + s00;
        double s1 = (s11 - s10)*xFrac + s10;
        return (s1 - s0)*yFrac + s0;
	}

	public double getLatitudeResolution()
	{
		return latitudeResolution;
	}
	
	protected void setLatitudeResolution(double latitudeResolution)
	{
		this.latitudeResolution = latitudeResolution;
	}
	
	public double getLongitudeResolution()
	{
		return longitudeResolution;
	}
	
	protected void setLongitudeResolution(double longitudeResolution)
	{
		this.longitudeResolution = longitudeResolution;
	}
	
	public double getNorth()
	{
		return validateLatitude(north);
	}
	
	protected void setNorth(double north) 
	{
		this.north = north;
		prepare();
	}
	
	public double getSouth()
	{
		return validateLatitude(south);
	}
	
	protected void setSouth(double south) 
	{
		this.south = south;
		prepare();
	}
	
	public double getEast()
	{
		return validateLongitude(east);
	}
	
	protected void setEast(double east)
	{
		this.east = east;
		prepare();
	}
	
	public double getWest()
	{
		return validateLongitude(west);
	}
	
	protected void setWest(double west)
	{
		this.west = west;
		prepare();
	}
	
	public int getRows(double north, double south)
	{
		return (int) Math.round((north - south) / this.getLatitudeResolution());
	}
	
	public int getRows()
	{
		return rows;
	}
	
	protected void setRows(int rows)
	{
		this.rows = rows;
	}
	
	public int getColumns(double east, double west)
	{
		return (int) Math.round((east - west) / this.getLongitudeResolution());
	}
	
	public int getColumns()
	{
		return columns;
	}
	
	protected void setColumns(int columns)
	{
		this.columns = columns;
	}
	
	public double getDataMinimum()
	{
		return dataMinimum;
	}
	
	protected void setDataMinimum(double dataMinimum) 
	{
		this.dataMinimum = dataMinimum;
	}	
	
	public double getDataMaximum() 
	{
		return dataMaximum;
	}


	protected void setDataMaximum(double dataMaximum) 
	{
		this.dataMaximum = dataMaximum;
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
	
	protected void copyFields(AbstractRasterDataProvider clone) throws DataSourceException
	{
		clone.latitudeResolution = this.getLatitudeResolution();
		clone.longitudeResolution = this.getLongitudeResolution();
		clone.dataMinimum = this.getDataMinimum();
		clone.dataMaximum = this.getDataMaximum();
		clone.north = this.getNorth();
		clone.south = this.getSouth();
		clone.east = this.getEast();
		clone.west = this.getWest();
		clone.rows = this.getRows();
		clone.columns = this.getColumns();
		clone.dataLatLongBox = this.dataLatLongBox.copy();

	}

}
