package us.wthr.jdem846.rasterdata.generic;

import us.wthr.jdem846.DataTypeEnum;
import us.wthr.jdem846.exception.DataSourceException;

public class DataTypeUtil {
	

	
	public static Number[] createDataArray(DataTypeEnum dataType, int length) throws DataSourceException
	{
		Number[] array = null;
		
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
	
	public static Number[][] createDataArray(DataTypeEnum dataType, int rows, int columns) throws DataSourceException
	{
		Number[][] array = null;
		
		if (dataType.clazz().equals(Byte.class)) {
			array = new Byte[rows][columns];
		} else if (dataType.clazz().equals(Short.class)) {
			array = new Short[rows][columns];
		} else if (dataType.clazz().equals(Integer.class)) {
			array = new Integer[rows][columns];
		} else if (dataType.clazz().equals(Long.class)) {
			array = new Long[rows][columns];
		} else if (dataType.clazz().equals(Float.class)) {
			array = new Float[rows][columns];
		} else if (dataType.clazz().equals(Double.class)) {
			array = new Double[rows][columns];
		} else {
			throw new DataSourceException("Invalid or unsupported data type specified: " + dataType.name());
		}
		

		return array;
	}
	
	
}
