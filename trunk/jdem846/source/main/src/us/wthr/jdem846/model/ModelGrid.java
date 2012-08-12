package us.wthr.jdem846.model;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;

public class ModelGrid extends ModelPointGrid
{
	private static Log log = Logging.getLog(ModelGrid.class);
	
	
	private ModelPoint[] grid;
	
	private boolean isDisposed = false;
	
	public ModelGrid(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution, double minimum, double maximum)
	{
		super(north, south, east, west, latitudeResolution, longitudeResolution, minimum, maximum);

		grid = new ModelPoint[(int)gridLength];
		
		log.info("Model Grid Width/Height: " + width + "/" + height + ", Length: " + gridLength);
		
		for (int i = 0; i < gridLength; i++) {
			grid[i] = null;//new ModelPoint();
		}
		
		reset();
	}
	
	public void dispose()
	{
		if (isDisposed())
			return;
		
		for (int i = 0; i < gridLength; i++) {
			if (grid[i] != null) {
				grid[i].dispose();
			}
			grid[i] = null;
		}

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
		
		ModelPoint point = null;
		
		if (grid != null && index >= 0 && index < this.gridLength) {
			point = grid[index];
			if (point == null) {
				point = new BasicModelPoint();
				grid[index] = point;
			}
		} else {
			// TODO: Throw
		}
		
		return point;
	}
	

}
