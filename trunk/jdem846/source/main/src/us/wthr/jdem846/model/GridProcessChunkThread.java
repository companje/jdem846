package us.wthr.jdem846.model;

import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.model.processing.GridProcessMethodStack;

public class GridProcessChunkThread extends Thread
{
	
	protected Boolean completed = false;
	
	protected LatitudeProcessedList latitudeProcessedList;
	protected ModelProgram modelProgram;
	protected int threadNumber;
	protected double north;
	protected double south;
	protected double east;
	protected double west;
	protected double latitudeResolution;
	protected double longitudeResolution;
	protected RenderEngineException exception = null;
	
	public GridProcessChunkThread(LatitudeProcessedList latitudeProcessedList, ModelProgram modelProgram, int threadNumber, double north, double south, double east, double west, double latitudeResolution, double longitudeResolution)
	{
		this.setName("GridProcessChunk #" + threadNumber);
		
		this.latitudeProcessedList = latitudeProcessedList;
		this.modelProgram = modelProgram;
		this.threadNumber = threadNumber;
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
	}
	
	
	public void run()
	{
		setCompleted(false);
		
		GridProcessMethodStack processStack = modelProgram.getProcessStack();
		
		for (double latitude = north; latitude >= south; latitude -= latitudeResolution) {
			
			if (this.latitudeProcessedList != null) {
				if (this.latitudeProcessedList.isLatitudeProcessed(latitude)) {
					continue;
				} else {
					this.latitudeProcessedList.setLatitudeProcessed(latitude);
				}
			}
			
			try {
				processStack.onLatitudeStart(latitude);
			} catch (Exception ex) {
				this.exception = new RenderEngineException("Error invoking onLatitudeStart: " + ex.getMessage(), ex);
				this.setCompleted(true);
				return;
			}
			
			for (double longitude = west; longitude <= east; longitude += longitudeResolution) {
				try {
					processStack.onModelPoint(latitude, longitude);
				} catch (Exception ex) {
					this.exception = new RenderEngineException("Error invoking onModelPoint: " + ex.getMessage(), ex);
					this.setCompleted(true);
					return;
				}
			}
			
			try {
				processStack.onLatitudeEnd(latitude);
			} catch (Exception ex) {
				this.exception = new RenderEngineException("Error invoking onLatitudeEnd: " + ex.getMessage(), ex);
				this.setCompleted(true);
				return;
			}
			
			//checkPause();
			//if (isCancelled()) {
			//	log.warn("Render process cancelled, model not complete.");
			//	break;
			//}
		}
		
		
		try {
			modelProgram.getRasterDataContext().clearBuffers();
		} catch (DataSourceException ex) {
			this.exception = new RenderEngineException("Error clearing raster buffers: " + ex.getMessage(), ex);
		}
		
		setCompleted(true);
	}
	
	
	
	protected void setCompleted(boolean completed)
	{
		synchronized(this.completed) {
			this.completed = completed;
		}
	}
	
	public boolean isCompleted()
	{
		synchronized(completed) {
			return completed;
		}
	}


	public RenderEngineException getException()
	{
		return exception;
	}
	
	
	
}
