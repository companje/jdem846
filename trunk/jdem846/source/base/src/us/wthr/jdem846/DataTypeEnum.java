package us.wthr.jdem846;

public enum DataTypeEnum {
	Byte(1, Byte.class),
	UInt16(2, Integer.class),
	Int16(2, Short.class),
	Uint32(4, Long.class),
	Int32(4, Integer.class),
	Float32(4, Float.class),
	Float64(8, Double.class),
	CInt16(2, Short.class),
	CInt32(4, Integer.class),
	CFloat32(4, Float.class),
	CFloat64(8, Double.class);
	
	private int numBytes = 1;
	private Class<?> clazz;
	
	DataTypeEnum(int numBytes, Class<?> clazz)
	{
		this.numBytes = numBytes;
		this.clazz = clazz;
	}
	
	public Class<?> clazz() { return clazz; }
	public int numberOfBytes() { return numBytes; }
	public int numberOfBits() { return numBytes * 8; }
	
}
