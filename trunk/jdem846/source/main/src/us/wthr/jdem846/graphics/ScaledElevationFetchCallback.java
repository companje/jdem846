package us.wthr.jdem846.graphics;

import us.wthr.jdem846.modelgrid.IModelGrid;
import us.wthr.jdem846.scaling.ElevationScaler;

public class ScaledElevationFetchCallback implements ElevationFetchCallback
{

	protected IModelGrid modelGrid;
	protected ElevationScaler scaler;
	
	public ScaledElevationFetchCallback(IModelGrid modelGrid, ElevationScaler scaler)
	{
		this.modelGrid = modelGrid;
		this.scaler = scaler;
	}
	
	
	@Override
	public double getElevation(double latitude, double longitude)
	{
		double elevation = modelGrid.getElevation(latitude, longitude, true);
		if (scaler != null) {
			elevation = scaler.scale(elevation);
		}
		return elevation;
	}

}
