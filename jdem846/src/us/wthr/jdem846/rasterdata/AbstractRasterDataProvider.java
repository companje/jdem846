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
		dataLatLongBox = new RasterDataLatLongBox(getNorth(), getSouth(), getEast(), getWest());
	}
	
	public boolean contains(double latitude, double longitude)
	{
		// TODO: This is overly simplistic. Make this a bit more robust.
		
		if (latitude > south && latitude < north && longitude > west && longitude < east) {
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
		
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				elevation = getData(row, col);
					
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
	
	protected int latitudeToRow(double latitude)
	{
		// Nearest neighbor
		return (int) Math.floor((north - latitude) / this.getLatitudeResolution());
	}
	
	protected double rowToLatitude(int row)
	{
		return (north - ((double)row * this.getLatitudeResolution()));
	}
	
	protected int longitudeToColumn(double longitude)
	{
		// Nearest neighbor
		return (int) Math.floor((longitude - west) / this.getLongitudeResolution());
	}
	
	public double columnToLongitude(int column)
	{
		return west + ((double)column * this.getLongitudeResolution());
	}
	
	@Override
	public double getData(double latitude, double longitude) throws DataSourceException
	{
		int row = this.latitudeToRow(latitude);
		int column = this.longitudeToColumn(longitude);
		return this.getData(row, column);
		
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
