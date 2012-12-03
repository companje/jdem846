package us.wthr.jdem846;

public enum DataTypeEnum {
	Byte(1),
	UInt16(2),
	Int16(2),
	Uint32(4),
	Int32(4),
	Float32(4),
	Float64(8),
	CInt16(2),
	CInt32(4),
	CFloat32(4),
	CFloat64(8);
	
	private int numBytes = 1;
	
	DataTypeEnum(int numBytes)
	{
		this.numBytes = numBytes;
	}
	
	public int numberOfBytes() { return numBytes; }
	public int numberOfBits() { return numBytes * 8; }
	
}
