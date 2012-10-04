package us.wthr.jdem846.model;

import us.wthr.jdem846.math.MathExt;

public class LatitudeProcessedList
{
	double north;
	double latitudeResolution;
	
	boolean[] processedList;
	
	
	public LatitudeProcessedList(double north, double latitudeResolution, int numRows)
	{
		this.north = north;
		this.latitudeResolution = latitudeResolution;
		this.processedList = new boolean[numRows];
		
		for (int i = 0; i < processedList.length; i++) {
			processedList[i] = false;
		}
	}
	
	protected int index(double latitude)
	{
		return (int) MathExt.round((north - latitude) / latitudeResolution);
	}
	
	public boolean isLatitudeProcessed(double latitude)
	{
		int index = index(latitude);
		if (index < 0 || index >= processedList.length) {
			return false;
		} else {
			synchronized(processedList) {
				return processedList[index];
			}
		}
	}
	
	public void setLatitudeProcessed(double latitude)
	{
		int index = index(latitude);
		if (index >= 0 && index < processedList.length) {
			synchronized(processedList) {
				processedList[index] = true;
			}
		}
	}
	
	public void reset()
	{
		if (processedList != null) {
			for (int i = 0; i < processedList.length; i++) {
				processedList[i] = false;
			}
		}
	}
	
}
