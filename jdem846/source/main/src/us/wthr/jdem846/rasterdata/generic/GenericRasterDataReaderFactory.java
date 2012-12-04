package us.wthr.jdem846.rasterdata.generic;

import java.io.File;

import us.wthr.jdem846.exception.DataSourceException;

public class GenericRasterDataReaderFactory {
	
	
	public static GenericRasterDataReader createInstance(File dataFile, RasterDefinition rasterDefinition) throws DataSourceException
	{
		return new GenericRasterDataReader(dataFile, rasterDefinition);
	}
	
}
