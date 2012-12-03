package us.wthr.jdem846.rasterdata.generic;

import us.wthr.jdem846.DataTypeEnum;
import us.wthr.jdem846.exception.DataSourceException;

public class DataTypeUtil {
	

	
	public static Object[] createDataArray(DataTypeEnum dataType, int length) throws DataSourceException
	{
		Object[] array = null;
		
		if (dataType == DataTypeEnum.Byte) {
			array = new Byte[length];
		} else if (dataType == DataTypeEnum.UInt16
					|| dataType == DataTypeEnum.Int16
					|| dataType == DataTypeEnum.Uint32
					|| dataType == DataTypeEnum.Int32
					|| dataType == DataTypeEnum.CInt16
					|| dataType == DataTypeEnum.CInt32) {
			array = new Integer[length];
		} else if (dataType == DataTypeEnum.Float32
					|| dataType == DataTypeEnum.CFloat32) {
			array = new Float[length];
		} else if (dataType == DataTypeEnum.Float64
					|| dataType == DataTypeEnum.CFloat64) {
			array = new Double[length];
		} else {
			throw new DataSourceException("Invalid or unsupported data type specified: " + dataType.name());
		}
		
		return array;
	}
	
	
	
}
