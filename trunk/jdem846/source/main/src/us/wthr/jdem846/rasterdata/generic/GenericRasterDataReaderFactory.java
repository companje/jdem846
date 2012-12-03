package us.wthr.jdem846.rasterdata.generic;

import java.io.File;

import us.wthr.jdem846.DataTypeEnum;
import us.wthr.jdem846.exception.DataSourceException;

public class GenericRasterDataReaderFactory {
	
	
	public static GenericRasterDataReader<?> createInstance(File dataFile, RasterDefinition rasterDefinition) throws DataSourceException
	{
		if (rasterDefinition.getDataType() == DataTypeEnum.Byte) {
			return new GenericRasterDataReader<Byte>(dataFile, rasterDefinition);
		} else if (rasterDefinition.getDataType() == DataTypeEnum.UInt16
					|| rasterDefinition.getDataType() == DataTypeEnum.Int16
					|| rasterDefinition.getDataType() == DataTypeEnum.Uint32
					|| rasterDefinition.getDataType() == DataTypeEnum.Int32
					|| rasterDefinition.getDataType() == DataTypeEnum.CInt16
					|| rasterDefinition.getDataType() == DataTypeEnum.CInt32) {
			return new GenericRasterDataReader<Integer>(dataFile, rasterDefinition);
		} else if (rasterDefinition.getDataType() == DataTypeEnum.Float32
					|| rasterDefinition.getDataType() == DataTypeEnum.CFloat32) {
			return new GenericRasterDataReader<Float>(dataFile, rasterDefinition);
		} else if (rasterDefinition.getDataType() == DataTypeEnum.Float64
					|| rasterDefinition.getDataType() == DataTypeEnum.CFloat64) {
			return new GenericRasterDataReader<Double>(dataFile, rasterDefinition);
		} else {
			throw new DataSourceException("Invalid or unsupported data type specified: " + rasterDefinition.getDataType().name());
		}
	}
	
}
