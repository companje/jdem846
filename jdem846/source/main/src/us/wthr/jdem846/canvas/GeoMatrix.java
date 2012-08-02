package us.wthr.jdem846.canvas;

import scala.actors.threadpool.Arrays;
import us.wthr.jdem846.DemConstants;

public class GeoMatrix extends AbstractBuffer
{
	
	private final static float NO_VALUE = (float) DemConstants.ELEV_NO_DATA;
	
	
	private float[] longitudeBuffer;
	private float[] latitudeBuffer;
	private float[] elevationBuffer;
	
	public GeoMatrix(int width, int height)
	{
		super(width, height, 1);
		
		longitudeBuffer = new float[getBufferLength()];
		latitudeBuffer = new float[getBufferLength()];
		elevationBuffer = new float[getBufferLength()];
		
		//reset();
	}
	
	
	public void dispose()
	{
		longitudeBuffer = null;
		latitudeBuffer = null;
		elevationBuffer = null;
	}
	
	public void reset()
	{
		if (longitudeBuffer != null)
			Arrays.fill(longitudeBuffer, NO_VALUE);
		
		if (latitudeBuffer != null)
			Arrays.fill(latitudeBuffer, NO_VALUE);
		
		if (elevationBuffer != null)
			Arrays.fill(elevationBuffer, NO_VALUE);
		

	}
	
	
	public void set(double x, double y, double latitude, double longitude, double elevation)
	{

		int index = this.getIndex(x, y);
		
		if (index >= 0 && index < getBufferLength()) {
			latitudeBuffer[index] = (float) latitude;
			longitudeBuffer[index] = (float) longitude;
			elevationBuffer[index] = (float) elevation;
		} else {
			// TODO: Throw
		}
	}
	
	public double getLatitude(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (index >= 0 && index < getBufferLength()) {
			return latitudeBuffer[index];
		} else {
			return NO_VALUE;
			// TODO: Throw
		}
	}
	
	public double getLongitude(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (index >= 0 && index < getBufferLength()) {
			return longitudeBuffer[index];
		} else {
			return NO_VALUE;
			// TODO: Throw
		}
	}
	
	public double getElevation(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (index >= 0 && index < getBufferLength()) {
			return elevationBuffer[index];
		} else {
			return NO_VALUE;
			// TODO: Throw
		}
	}
	
	
	
}
