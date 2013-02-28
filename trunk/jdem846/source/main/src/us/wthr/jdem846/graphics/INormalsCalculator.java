package us.wthr.jdem846.graphics;

import us.wthr.jdem846.math.Vector;

public interface INormalsCalculator
{
	public void calculateNormal(double latitude, double longitude, Vector normal);
	public void calculateNormal(double latitude, double longitude, Vector normal, ElevationFetchCallback elevationFetchCallback);
	public void calculateNormal(double latitude, double longitude, double elevation, Vector normal);
	public void calculateNormal(double latitude, double longitude, double midElev, double nElev, double sElev, double eElev, double wElev, Vector normal);

}
