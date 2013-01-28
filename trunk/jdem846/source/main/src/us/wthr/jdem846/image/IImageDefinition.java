package us.wthr.jdem846.image;

//import us.wthr.jdem846.rasterdata.generic.DefinitionChangeListener;

public interface IImageDefinition
{
	public int getImageWidth();

	public void setImageWidth(int imageWidth);

	public int getImageHeight();

	public void setImageHeight(int imageHeight);


	public long getFileSize();

	public double getNorth();

	public void setNorth(double north);

	public double getSouth();

	public void setSouth(double south);

	public double getEast();

	public void setEast(double east);

	public double getWest();

	public void setWest(double west);

	public double getLatitudeResolution();

	public void setLatitudeResolution(double latitudeResolution);

	public double getLongitudeResolution();

	public void setLongitudeResolution(double longitudeResolution);


	public boolean isLocked();

	public void setLocked(boolean locked);

	public void determineNorth();

	public void determineSouth();

	public void determineLatitudeResolution();

	public void determineWest();

	public void determineEast();

	public void determineLongitudeResolution();

	//public void addDefinitionChangeListener(DefinitionChangeListener l);

	//public boolean removeDefinitionChangeListener(DefinitionChangeListener l);

	public IImageDefinition copy();
}
