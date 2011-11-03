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
	

	private double dataMinimum;
	private double dataMaximum;
	
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

		double elevation = 0;
		
		int rows = getRows();
		int columns = getColumns();

		double max = getDataMaximum();
		double min = getDataMinimum();
		
		double northLimit = getNorth();
		double southLimit = getSouth();
		double eastLimit = getEast();
		double westLimit = getWest();
		
		double latitudeResolution = this.getLatitudeResolution();
		double longitudeResolution = this.getLongitudeResolution();
		
		for (double latitude = northLimit; latitude > southLimit; latitude-=latitudeResolution) {
			
			for (double longitude = westLimit; longitude < eastLimit; longitude+=longitudeResolution) {
				elevation = getData(latitude, longitude);
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
	
	@Override
	public double getData(double latitude, double longitude) throws DataSourceException
	{
		double fetchRow = this.latitudeToRow(latitude);
		double fetchColumn = this.longitudeToColumn(longitude);
		
		int row = (int) fetchRow;
		int column = (int) fetchColumn;
		
		
		
		
		if ((fetchRow != row || fetchColumn != column) &&
				(column + 1 < columns && row + 1 < rows)) {
			
			double data = interpolate(getData(row, column), // s00
					getData(row, column + 1),				// s01
					getData(row + 1, column),				// s10
					getData(row + 1, column + 1),			// s11
					fetchColumn - column,					// xFrac
					fetchRow - row);						// yFrac

			return data;
		} else {
			return this.getData(row, column);
		}
		
		
		
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
		return north;
	}
	
	protected void setNorth(double north) 
	{
		this.north = north;
		prepare();
	}
	
	public double getSouth()
	{
		return south;
	}
	
	protected void setSouth(double south) 
	{
		this.south = south;
		prepare();
	}
	
	public double getEast()
	{
		return east;
	}
	
	protected void setEast(double east)
	{
		this.east = east;
		prepare();
	}
	
	public double getWest()
	{
		return west;
	}
	
	protected void setWest(double west)
	{
		this.west = west;
		prepare();
	}
	
	public int getRows()
	{
		return rows;
	}
	
	protected void setRows(int rows)
	{
		this.rows = rows;
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
		
	
	

}
