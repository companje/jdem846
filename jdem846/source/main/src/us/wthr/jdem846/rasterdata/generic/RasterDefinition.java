package us.wthr.jdem846.rasterdata.generic;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DataTypeEnum;
import us.wthr.jdem846.InterleavingTypeEnum;

public class RasterDefinition {
	
	private double north = 90.0;
	private double south = -90.0;
	private double east = 180.0;
	private double west = -180.0;
	
	private int imageWidth = 0;
	private int imageHeight = 0;
	private int numBands = 1;
	private int headerSize = 0;
	private DataTypeEnum dataType = DataTypeEnum.Byte;
	private ByteOrder byteOrder = ByteOrder.MSBFIRST;
	private InterleavingTypeEnum interleavingType = InterleavingTypeEnum.Pixel;
	
	private List<DefinitionChangeListener> changeListeners = new LinkedList<DefinitionChangeListener>();
	
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
	}

	public int getImageHeight() 
	{
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) 
	{
		this.imageHeight = imageHeight;
	}

	public int getNumBands()
	{
		return numBands;
	}

	public void setNumBands(int numBands) 
	{
		this.numBands = numBands;
	}

	public int getHeaderSize() 
	{
		return headerSize;
	}

	public void setHeaderSize(int headerSize) 
	{
		this.headerSize = headerSize;
	}

	public long getFileSize() 
	{
		return 0;
	}


	public DataTypeEnum getDataType() 
	{
		return dataType;
	}

	public void setDataType(DataTypeEnum dataType) 
	{
		this.dataType = dataType;
	}

	public ByteOrder getByteOrder() 
	{
		return byteOrder;
	}

	public void setByteOrder(ByteOrder byteOrder) 
	{
		this.byteOrder = byteOrder;
	}

	public InterleavingTypeEnum getInterleavingType() 
	{
		return interleavingType;
	}

	public void setInterleavingType(InterleavingTypeEnum interleavingType) 
	{
		this.interleavingType = interleavingType;
	}

	public double getNorth() 
	{
		return north;
	}

	public void setNorth(double north) 
	{
		this.north = north;
	}

	public double getSouth()
	{
		return south;
	}

	public void setSouth(double south) 
	{
		this.south = south;
	}

	public double getEast() 
	{
		return east;
	}

	public void setEast(double east) 
	{
		this.east = east;
	}

	public double getWest() 
	{
		return west;
	}

	public void setWest(double west) 
	{
		this.west = west;
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
		return clone;
	}
	
}
