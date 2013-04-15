package us.wthr.jdem846.modelgrid;

import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.ElevationHistogramModel;

public abstract class BaseModelGrid implements IModelGrid
{
	private static Log log = Logging.getLog(BaseModelGrid.class);

	protected double north;
	protected double south;
	protected double east;
	protected double west;
	protected double latitudeResolution;
	protected double longitudeResolution;

	protected int width;
	protected int height;

	protected long gridLength;

	protected double minimum;
	protected double maximum;

	private ElevationHistogramModel elevationHistogramModel;

	private boolean isCompleted = false;
	
	
	
	
	public BaseModelGrid(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution, double minimum, double maximum)
	{
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		this.minimum = minimum;
		this.maximum = maximum;

		double _height = (this.north - this.south + latitudeResolution) / latitudeResolution;
		double _width = (this.east - this.west + longitudeResolution) / longitudeResolution;

		this.height = (int) MathExt.round(_height);
		this.width = (int) MathExt.ceil(_width);

		gridLength = (long) height * (long) width;

		elevationHistogramModel = new ElevationHistogramModel(500, minimum, maximum);

	}
	
	public BaseModelGrid(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution, double minimum, double maximum, int width, int height)
	{
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		this.minimum = minimum;
		this.maximum = maximum;

		this.height = height;
		this.width = width;

		gridLength = (long) height * (long) width;

		elevationHistogramModel = new ElevationHistogramModel(500, minimum, maximum);
	}
	
	@Override
	public boolean isCompleted()
	{
		return isCompleted;
	}
	
	@Override
	public void setCompleted(boolean completed)
	{
		isCompleted = completed;
	}

	@Override
	public double getElevation(double latitude, double longitude) throws DataSourceException
	{
		return getElevation(latitude, longitude, false);
	}

	protected int getIndex(double latitude, double longitude)
	{
		int column = (int) Math.round((longitude - west) / longitudeResolution);
		int row = (int) Math.round((north - latitude) / latitudeResolution);

		if (column < 0 || column >= width) {
			return -1;
		}

		if (row < 0 || row >= height) {
			return -1;
		}

		int index = row * width + column;
		return index;
	}
	
	protected int getIndex(int x, int y)
	{
		int index = y * width + x;
		return index;
	}

	@Override
	public ElevationHistogramModel getElevationHistogramModel()
	{
		return elevationHistogramModel;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public double getNorth()
	{
		return north;
	}

	@Override
	public double getSouth()
	{
		return south;
	}

	@Override
	public double getEast()
	{
		return east;
	}

	@Override
	public double getWest()
	{
		return west;
	}

	@Override
	public double getLatitudeResolution()
	{
		return latitudeResolution;
	}

	@Override
	public double getLongitudeResolution()
	{
		return longitudeResolution;
	}

	@Override
	public long getGridLength()
	{
		return gridLength;
	}

	@Override
	public double getMinimum()
	{
		return minimum;
	}

	@Override
	public void setMinimum(double minimum)
	{
		this.minimum = minimum;
	}

	@Override
	public double getMaximum()
	{
		return maximum;
	}

	@Override
	public void setMaximum(double maximum)
	{
		this.maximum = maximum;
	}

}
