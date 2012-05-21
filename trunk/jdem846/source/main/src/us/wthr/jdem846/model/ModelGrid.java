package us.wthr.jdem846.model;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;

public class ModelGrid extends ModelPointGrid
{
	private static Log log = Logging.getLog(ModelGrid.class);
	
	
	private ModelPoint[] grid;
	
	private boolean isDisposed = false;
	
	public ModelGrid(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution)
	{
		super(north, south, east, west, latitudeResolution, longitudeResolution);

		grid = new ModelPoint[gridLength];
		
		log.info("Model Grid Width/Height: " + width + "/" + height + ", Length: " + gridLength);
		
		for (int i = 0; i < gridLength; i++) {
			grid[i] = new ModelPoint();
		}
		
		reset();
	}
	
	public void dispose()
	{
		if (isDisposed())
			return;
		
		grid = null;
	}
	
	public boolean isDisposed()
	{
		return isDisposed;
	}
	
	public void reset()
	{
		
	}
	
	public ModelPoint get(double latitude, double longitude)
	{
		int index = getIndex(latitude, longitude);
		
		if (grid != null && index >= 0 && index < this.gridLength) {
			return grid[index];
		} else {
			// TODO: Throw
			return null;
		}
	}
	

}
