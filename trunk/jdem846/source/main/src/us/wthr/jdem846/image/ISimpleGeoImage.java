package us.wthr.jdem846.image;

public interface ISimpleGeoImage
{

	public double getNorth();

	public void setNorth(double north);

	public double getSouth();

	public void setSouth(double south);

	public double getEast();

	public void setEast(double east);

	public double getWest();

	public void setWest(double west);

	public int getHeight();

	public int getWidth();

	public double getLatitudeResolution();

	public double getLongitudeResolution();
	
	
	public IImageDefinition getImageDefinition();
}
