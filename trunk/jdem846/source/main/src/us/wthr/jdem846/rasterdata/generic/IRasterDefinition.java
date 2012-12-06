package us.wthr.jdem846.rasterdata.generic;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DataTypeEnum;
import us.wthr.jdem846.InterleavingTypeEnum;

public interface IRasterDefinition
{

	public int getImageWidth();

	public void setImageWidth(int imageWidth);

	public int getImageHeight();

	public void setImageHeight(int imageHeight);

	public int getNumBands();

	public void setNumBands(int numBands);

	public int getHeaderSize();

	public void setHeaderSize(int headerSize);

	public long getFileSize();

	public DataTypeEnum getDataType();

	public void setDataType(DataTypeEnum dataType);

	public ByteOrder getByteOrder();

	public void setByteOrder(ByteOrder byteOrder);

	public InterleavingTypeEnum getInterleavingType();

	public void setInterleavingType(InterleavingTypeEnum interleavingType);

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

	public double getNoData();

	public void setNoData(double noData);

	public boolean isLocked();

	public void setLocked(boolean locked);

	public void determineNorth();

	public void determineSouth();

	public void determineLatitudeResolution();

	public void determineWest();

	public void determineEast();

	public void determineLongitudeResolution();

	public void addDefinitionChangeListener(DefinitionChangeListener l);

	public boolean removeDefinitionChangeListener(DefinitionChangeListener l);

	public RasterDefinition copy();
}
