package us.wthr.jdem846.modelgrid;

import us.wthr.jdem846.input.InputSourceData;


public class ModelGridHeader implements InputSourceData
{
	public String gridPrefix;
	
	public double north;
	public double south;
	public double east;
	public double west;
	public double latitudeResolution;
	public double longitudeResolution;

	public int width;
	public int height;

	public double minimum;
	public double maximum;
	
	public long dateCreated;
}
