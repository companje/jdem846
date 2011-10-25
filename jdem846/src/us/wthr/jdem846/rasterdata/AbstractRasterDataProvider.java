package us.wthr.jdem846.rasterdata;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;

public abstract class AbstractRasterDataProvider implements RasterData
{

	protected abstract void setDataMinimum(double value);
	protected abstract void setDataMaximum(double value);
	
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
	
	
	public void calculatenMinAndMax() throws DataSourceException
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
	
}
