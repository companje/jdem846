package us.wthr.jdem846.image;

import java.util.LinkedList;
import java.util.List;

public class ImageDefinition implements IImageDefinition
{


	private double north = 90.0;
	private double south = -90.0;
	private double east = 180.0;
	private double west = -180.0;

	private double latitudeResolution = 0.0;
	private double longitudeResolution = 0.0;

	private int imageWidth = 0;
	private int imageHeight = 0;

	private double layerTransparency = 1.0;

	private List<IImageDefinitionChangeListener> changeListeners = new LinkedList<IImageDefinitionChangeListener>();

	private boolean locked = false;

	public ImageDefinition()
	{

	}

	public int getImageWidth()
	{
		return imageWidth;
	}

	public void setImageWidth(int imageWidth)
	{
		if (!locked) {
			this.imageWidth = imageWidth;
			this.fireDefinitionChangeListener();
		}
	}

	public int getImageHeight()
	{
		return imageHeight;
	}

	public void setImageHeight(int imageHeight)
	{
		if (!locked) {
			this.imageHeight = imageHeight;
			this.fireDefinitionChangeListener();
		}
	}


	public long getFileSize()
	{
		return this.imageHeight * this.imageWidth;
	}

	public double getNorth()
	{
		return north;
	}

	public void setNorth(double north)
	{
		if (!locked) {
			this.north = north;
			this.fireDefinitionChangeListener();
		}
	}

	public double getSouth()
	{
		return south;
	}

	public void setSouth(double south)
	{
		if (!locked) {
			this.south = south;
			this.fireDefinitionChangeListener();
		}
	}

	public double getEast()
	{
		return east;
	}

	public void setEast(double east)
	{
		if (!locked) {
			this.east = east;
			this.fireDefinitionChangeListener();
		}
	}

	public double getWest()
	{
		return west;
	}

	public void setWest(double west)
	{
		if (!locked) {
			this.west = west;
			this.fireDefinitionChangeListener();
		}
	}

	public double getLatitudeResolution()
	{
		return latitudeResolution;
	}

	public void setLatitudeResolution(double latitudeResolution)
	{
		if (!locked) {
			this.latitudeResolution = latitudeResolution;
			this.fireDefinitionChangeListener();
		}
	}

	public double getLongitudeResolution()
	{
		return longitudeResolution;
	}

	public void setLongitudeResolution(double longitudeResolution)
	{
		if (!locked) {
			this.longitudeResolution = longitudeResolution;
			this.fireDefinitionChangeListener();
		}
	}

	
	@Override
	public double getLayerTransparency()
	{
		return layerTransparency;
	}

	@Override
	public void setLayerTransparency(double layerTransparency)
	{
		if (!locked) {
			this.layerTransparency = layerTransparency;
			this.fireDefinitionChangeListener();
		}
	}

	public boolean isLocked()
	{
		return locked;
	}

	public void setLocked(boolean locked)
	{
		this.locked = locked;
	}

	public void determineNorth()
	{
		if (!locked) {
			setNorth(getSouth() + (getImageHeight() * getLatitudeResolution()));
		}
	}

	public void determineSouth()
	{
		if (!locked) {
			setSouth(getNorth() - (getImageHeight() * getLatitudeResolution()));
		}
	}

	public void determineLatitudeResolution()
	{
		if (!locked) {
			setLatitudeResolution((getNorth() - getSouth()) / getImageHeight());
		}
	}

	public void determineWest()
	{
		setWest(getEast() - (getImageWidth() * getLongitudeResolution()));
	}

	public void determineEast()
	{
		if (!locked) {
			setEast(getWest() + (getImageWidth() * getLongitudeResolution()));
		}
	}

	public void determineLongitudeResolution()
	{
		if (!locked) {
			setLongitudeResolution((getEast() - getWest()) / getImageWidth());
		}
	}

	public void addDefinitionChangeListener(IImageDefinitionChangeListener l)
	{
		this.changeListeners.add(l);
	}

	public boolean removeDefinitionChangeListener(IImageDefinitionChangeListener l)
	{
		return this.changeListeners.remove(l);
	}

	protected void fireDefinitionChangeListener()
	{
		for (IImageDefinitionChangeListener l : this.changeListeners) {
			l.onDefinitionChanged(this);
		}
	}

	public ImageDefinition copy()
	{
		ImageDefinition clone = new ImageDefinition();
		clone.north = this.north;
		clone.south = this.south;
		clone.east = this.east;
		clone.west = this.west;
		clone.imageWidth = this.imageWidth;
		clone.imageHeight = this.imageHeight;
		clone.latitudeResolution = this.latitudeResolution;
		clone.longitudeResolution = this.longitudeResolution;
		clone.layerTransparency = this.layerTransparency;
		clone.locked = this.locked;
		return clone;
	}

}
