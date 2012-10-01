package us.wthr.jdem846.model;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.WatchableModelPoint.ModelPointChangedListener;

public class BufferedModelGrid extends ModelPointGrid
{
	private static Log log = Logging.getLog(BufferedModelGrid.class);
	
	
	private float[] elevationGrid;
	private int[] rgbaGrid;
	private ModelPointChangedHandler changeHandler;
	
	private boolean isDisposed = false;
	
	public BufferedModelGrid(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution, double minimum, double maximum)
	{
		super(north, south, east, west, latitudeResolution, longitudeResolution, minimum, maximum);
		
		log.info("Allocating elevation and RGBA grid buffers of length " + gridLength);
		
		elevationGrid = new float[(int)gridLength];
		rgbaGrid = new int[(int)gridLength];
		
		changeHandler = new ModelPointChangedHandler();
		
		//reset();
	}

	@Override
	public void dispose()
	{
		elevationGrid = null;
		rgbaGrid = null;
	}

	@Override
	public boolean isDisposed()
	{
		return isDisposed;
	}

	@Override
	public void reset()
	{
		
		for (int i = 0; i < gridLength; i++) {
			elevationGrid[i] = (float) DemConstants.ELEV_UNDETERMINED;
			rgbaGrid[i] = 0x0;
		}
		
	}
	
	@Override
	public int[] getModelTexture()
	{
		return rgbaGrid;
	}
	
	@Override
	public ModelPoint get(double latitude, double longitude)
	{
		int index = getIndex(latitude, longitude);
		
		if (/*grid != null && */index >= 0 && index < this.gridLength) {
			try {
				return get(index);
			} catch (Exception ex) {
				// TODO: Add some real error handling here!
				log.error("Error fetching model point at index " + index + ": " + ex.getMessage(), ex);
				return null;
			}
		} else {
			// TODO: Throw
			return null;
		}
	}
	
	
	public ModelPoint get(int index) throws Exception
	{
		if (index >= 0 && index < this.gridLength) {
			BufferedModelPointProxy modelPoint = new BufferedModelPointProxy(index);
			
			modelPoint.setElevation(elevationGrid[index]);
			modelPoint.setRgba(rgbaGrid[index]);
			
			modelPoint.addModelPointChangedListener(changeHandler);
			return modelPoint;
		} else {
			return null;
		}
	}
	
	@Override
	public double getElevation(double latitude, double longitude, boolean basic)
	{
		int index = getIndex(latitude, longitude);
		if (index >= 0 && index < this.gridLength) {
			return elevationGrid[index];
		} else {
			return DemConstants.ELEV_NO_DATA;
		}
	}
	
	@Override
	public void setElevation(double latitude, double longitude, double elevation)
	{
		int index = getIndex(latitude, longitude);
		if (index >= 0 && index < this.gridLength) {
			elevationGrid[index] = (float) elevation;
			getElevationHistogramModel().add(elevation);
		}
	}
	
	@Override
	public void getRgba(double latitude, double longitude, int[] fill) 
	{
		ColorUtil.intToRGBA(getRgba(latitude, longitude), fill);
	}
	
	@Override
	public int getRgba(double latitude, double longitude)
	{
		int index = getIndex(latitude, longitude);
		if (index >= 0 && index < this.gridLength) {
			return rgbaGrid[index];
		} else {
			return 0x0;
		}
	}
	
	@Override
	public void setRgba(double latitude, double longitude, int rgba)
	{
		int index = getIndex(latitude, longitude);
		if (index >= 0 && index < this.gridLength) {
			rgbaGrid[index] = rgba;
		}
	}
	
	@Override
	public void setRgba(double latitude, double longitude, int[] rgba)
	{
		this.setRgba(latitude, longitude, ColorUtil.rgbaToInt(rgba));
	}
	

	
	
	
	

	public void set(BufferedModelPointProxy modelPoint) throws Exception
	{
		
		elevationGrid[modelPoint.index] = (float) modelPoint.getElevation();
		rgbaGrid[modelPoint.index] = modelPoint.getRgba();
	
	}
	
	
	class ModelPointChangedHandler implements ModelPointChangedListener
	{
		
		public ModelPointChangedHandler()
		{

		}
		
		public void onModelPointChanged(ModelPoint modelPoint)
		{
			BufferedModelPointProxy modelPointProxy = (BufferedModelPointProxy) modelPoint;
			try {
				set(modelPointProxy);
			} catch (Exception ex) {
				log.error("Error writing model point to cache: " + ex.getMessage(), ex);
			}
		}
	}
	
	
	
	
	class BufferedModelPointProxy extends WatchableModelPoint
	{
		
		public int index;
		
		public BufferedModelPointProxy(int index)
		{
			this.index = index;
		}
		

		
	}
}
