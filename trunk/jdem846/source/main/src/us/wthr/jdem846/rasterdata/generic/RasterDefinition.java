package us.wthr.jdem846.rasterdata.generic;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DataTypeEnum;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.InterleavingTypeEnum;

public class RasterDefinition {
	
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
		this.imageWidth = imageWidth;
		this.fireDefinitionChangeListener();
	}

	public int getImageHeight() 
	{
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) 
	{
		this.imageHeight = imageHeight;
		this.fireDefinitionChangeListener();
	}

	public int getNumBands()
	{
		return numBands;
	}

	public void setNumBands(int numBands) 
	{
		this.numBands = numBands;
		this.fireDefinitionChangeListener();
	}

	public int getHeaderSize() 
	{
		return headerSize;
	}

	public void setHeaderSize(int headerSize) 
	{
		this.headerSize = headerSize;
		this.fireDefinitionChangeListener();
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
		this.dataType = dataType;
		this.fireDefinitionChangeListener();
	}

	public ByteOrder getByteOrder() 
	{
		return byteOrder;
	}

	public void setByteOrder(ByteOrder byteOrder) 
	{
		this.byteOrder = byteOrder;
		this.fireDefinitionChangeListener();
	}

	public InterleavingTypeEnum getInterleavingType() 
	{
		return interleavingType;
	}

	public void setInterleavingType(InterleavingTypeEnum interleavingType) 
	{
		this.interleavingType = interleavingType;
		this.fireDefinitionChangeListener();
	}

	public double getNorth() 
	{
		return north;
	}

	public void setNorth(double north) 
	{
		this.north = north;
		this.fireDefinitionChangeListener();
	}

	public double getSouth()
	{
		return south;
	}

	public void setSouth(double south) 
	{
		this.south = south;
		this.fireDefinitionChangeListener();
	}

	public double getEast() 
	{
		return east;
	}

	public void setEast(double east) 
	{
		this.east = east;
		this.fireDefinitionChangeListener();
	}

	public double getWest() 
	{
		return west;
	}

	public void setWest(double west) 
	{
		this.west = west;
		this.fireDefinitionChangeListener();
	}
	

	public double getLatitudeResolution() 
	{
		return latitudeResolution;
	}

	public void setLatitudeResolution(double latitudeResolution) 
	{
		this.latitudeResolution = latitudeResolution;
		this.fireDefinitionChangeListener();
	}

	public double getLongitudeResolution()
	{
		return longitudeResolution;
	}

	public void setLongitudeResolution(double longitudeResolution) 
	{
		this.longitudeResolution = longitudeResolution;
		this.fireDefinitionChangeListener();
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
		setNorth(getSouth() + (getImageHeight() * getLatitudeResolution()));
	}
	
	public void determineSouth()
	{
		setSouth(getNorth() - (getImageHeight() * getLatitudeResolution()));
	}
	
	public void determineLatitudeResolution()
	{
		setLatitudeResolution((getNorth() - getSouth()) / getImageHeight());
	}
	
	public void determineWest()
	{
		setWest(getEast() - (getImageWidth() * getLongitudeResolution()));
	}
	
	public void determineEast()
	{
		setEast(getWest() + (getImageWidth() * getLongitudeResolution()));
	}
	
	public void determineLongitudeResolution()
	{
		setLongitudeResolution((getWest() - getEast()) / getImageWidth());
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
			l.onDefinitionChanged();
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
		return clone;
	}
	
}
