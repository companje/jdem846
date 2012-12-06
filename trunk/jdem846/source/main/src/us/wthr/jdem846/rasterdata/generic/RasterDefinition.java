package us.wthr.jdem846.rasterdata.generic;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DataTypeEnum;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.InterleavingTypeEnum;

public class RasterDefinition implements IRasterDefinition
{

	private double north = 90.0;
	private double south = -90.0;
	private double east = 180.0;
	private double west = -180.0;

	private double latitudeResolution = 1.0;
	private double longitudeResolution = 1.0;

	private int imageWidth = 0;
	private int imageHeight = 0;
	private int numBands = 1;
	private int headerSize = 0;
	private DataTypeEnum dataType = DataTypeEnum.Byte;
	private ByteOrder byteOrder = ByteOrder.MSBFIRST;
	private InterleavingTypeEnum interleavingType = InterleavingTypeEnum.Pixel;

	private double noData = DemConstants.ELEV_NO_DATA;

	private List<DefinitionChangeListener> changeListeners = new LinkedList<DefinitionChangeListener>();

	private boolean locked = false;

	public RasterDefinition()
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

	public int getNumBands()
	{
		return numBands;
	}

	public void setNumBands(int numBands)
	{
		if (!locked) {
			this.numBands = numBands;
			this.fireDefinitionChangeListener();
		}
	}

	public int getHeaderSize()
	{
		return headerSize;
	}

	public void setHeaderSize(int headerSize)
	{
		if (!locked) {
			this.headerSize = headerSize;
			this.fireDefinitionChangeListener();
		}
	}

	public long getFileSize()
	{
		return this.imageHeight * this.imageWidth * (this.dataType.numberOfBytes() * this.numBands) + this.headerSize;
	}

	public DataTypeEnum getDataType()
	{
		return dataType;
	}

	public void setDataType(DataTypeEnum dataType)
	{
		if (!locked) {
			this.dataType = dataType;
			this.fireDefinitionChangeListener();
		}
	}

	public ByteOrder getByteOrder()
	{
		return byteOrder;
	}

	public void setByteOrder(ByteOrder byteOrder)
	{
		if (!locked) {
			this.byteOrder = byteOrder;
			this.fireDefinitionChangeListener();
		}
	}

	public InterleavingTypeEnum getInterleavingType()
	{
		return interleavingType;
	}

	public void setInterleavingType(InterleavingTypeEnum interleavingType)
	{
		if (!locked) {
			this.interleavingType = interleavingType;
			this.fireDefinitionChangeListener();
		}
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

	public double getNoData()
	{
		return noData;
	}

	public void setNoData(double noData)
	{
		this.noData = noData;
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
			setLongitudeResolution((getWest() - getEast()) / getImageWidth());
		}
	}

	public void addDefinitionChangeListener(DefinitionChangeListener l)
	{
		this.changeListeners.add(l);
	}

	public boolean removeDefinitionChangeListener(DefinitionChangeListener l)
	{
		return this.changeListeners.remove(l);
	}

	protected void fireDefinitionChangeListener()
	{
		for (DefinitionChangeListener l : this.changeListeners) {
			l.onDefinitionChanged(this);
		}
	}

	public RasterDefinition copy()
	{
		RasterDefinition clone = new RasterDefinition();
		clone.north = this.north;
		clone.south = this.south;
		clone.east = this.east;
		clone.west = this.west;
		clone.imageWidth = this.imageWidth;
		clone.imageHeight = this.imageHeight;
		clone.numBands = this.numBands;
		clone.headerSize = this.headerSize;
		clone.dataType = this.dataType;
		clone.byteOrder = this.byteOrder;
		clone.interleavingType = this.interleavingType;
		clone.latitudeResolution = this.latitudeResolution;
		clone.longitudeResolution = this.longitudeResolution;
		clone.noData = this.noData;
		clone.locked = this.locked;
		return clone;
	}

}
