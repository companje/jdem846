package us.wthr.jdem846.util;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DataTypeEnum;
import us.wthr.jdem846.exception.DataSourceException;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

public class BytesUtil
{
	public static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.LSBFIRST;
	
	
	public static <T> T fromBytes(byte[] bytes, DataTypeEnum dataType, ByteOrder order)
	{
		return fromBytes(bytes, 0, dataType, order);
	}
	
	public static <T> T fromBytes(byte[] bytes, int offset, DataTypeEnum dataType, ByteOrder order)
	{
		Number n = 0;
		
		if (dataType == DataTypeEnum.Byte) {
			n = bytes[offset];
		} else if (dataType == DataTypeEnum.UInt16) {
			n = toUInt16(bytes, offset, order);
		} else if (dataType == DataTypeEnum.Int16) {
			n = toInt16(bytes, offset, order);
		} else if (dataType == DataTypeEnum.Uint32) {
			n = toUInt32(bytes, offset, order);
		} else if (dataType == DataTypeEnum.Int32) {
			n = toInt32(bytes, offset, order);
		} else if (dataType == DataTypeEnum.CInt16) {
			n = toInt16(bytes, offset, order);
		} else if (dataType == DataTypeEnum.CInt32) {
			n = toInt32(bytes, offset, order);
		} else if (dataType == DataTypeEnum.Float32) {
			n = toFloat32(bytes, offset, order);
		} else if (dataType == DataTypeEnum.CFloat32) {
			n = toFloat32(bytes, offset, order);
		} else if (dataType == DataTypeEnum.Float64) {
			n = toFloat64(bytes, offset, order);
		} else if (dataType == DataTypeEnum.CFloat64) {
			n = toFloat64(bytes, offset, order);
		} else {
			throw new DataSourceException("Invalid or unsupported data type specified: " + dataType.name());
		}
		
		return (T) dataType.clazz().cast(n);
	}
	
	/////////////////////////////////////////
	// Unsigned Short (16 bit) Integer
	/////////////////////////////////////////
	
	public static int toUInt16(byte[] bytes)
	{
		return toUInt16(bytes, 0, DEFAULT_BYTE_ORDER);
	}
	
	public static int toUInt16(byte[] bytes, int offset)
	{
		return toUInt16(bytes, DEFAULT_BYTE_ORDER);
	}
	
	public static int toUInt16(byte[] bytes, ByteOrder order)
	{
		return toUInt16(bytes, 0, order);
	}
	
	public static int toUInt16(byte[] bytes, int offset, ByteOrder order)
	{
		return toUInt16(bytes[offset], bytes[offset + 1], order);
	}
	
	public static int toUInt16(byte b0, byte b1)
	{
		return toUInt16(b0, b1, DEFAULT_BYTE_ORDER);
	}
	
	public static int toUInt16(byte b0, byte b1, ByteOrder order)
	{
		if (order == ByteOrder.LSBFIRST) {
			return Ints.fromBytes((byte) 0x0, (byte) 0x0, b1, b0);
		} else if (order == ByteOrder.MSBFIRST) {
			return Ints.fromBytes((byte) 0x0, (byte) 0x0, b0, b1);
		} else {
			throw new UnsupportedOperationException("Unsupported byte ordering specified: " + order);
		}
	}
	
	
	
	
	/////////////////////////////////////////
	// Signed Short (16 bit) Integer
	/////////////////////////////////////////
	
	public static short toInt16(byte[] bytes)
	{
		return toInt16(bytes, 0);
	}
	
	public static short toInt16(byte[] bytes, int offset)
	{
		return toInt16(bytes, offset, DEFAULT_BYTE_ORDER);
	}
	
	public static short toInt16(byte[] bytes, ByteOrder order)
	{
		return toInt16(bytes, 0, order);
	}
	
	public static short toInt16(byte[] bytes, int offset, ByteOrder order)
	{
		return toInt16(bytes[offset], bytes[offset + 1], order);
	}
	
	public static short toInt16(byte b0, byte b1)
	{
		return toInt16(b0, b1, DEFAULT_BYTE_ORDER);
	}
	
	public static short toInt16(byte b0, byte b1, ByteOrder order)
	{
		short s = (short) toUInt16(b0, b1, order);
		return s;
	}
	
	
	/////////////////////////////////////////
	// Unsigned Integer (32 Bit)
	/////////////////////////////////////////
	
	public static long toUInt32(byte[] bytes)
	{
		return toUInt32(bytes, 0);
	}
	
	public static long toUInt32(byte[] bytes, int offset)
	{
		return toUInt32(bytes, offset, DEFAULT_BYTE_ORDER);
	}
	
	public static long toUInt32(byte[] bytes, ByteOrder order)
	{
		return toUInt32(bytes
						, 0
						, order);
	}
	
	public static long toUInt32(byte[] bytes, int offset, ByteOrder order)
	{
		return toUInt32(bytes[offset + 0]
						, bytes[offset + 1]
						, bytes[offset + 2]
						, bytes[offset + 3]
						, order);
	}
	
	public static long toUInt32(byte b0, byte b1, byte b2, byte b3)
	{
		return toUInt32(b0, b1, b2, b3, DEFAULT_BYTE_ORDER);
	}
	
	public static long toUInt32(byte b0, byte b1, byte b2, byte b3, ByteOrder order)
	{
		if (order == ByteOrder.MSBFIRST) {
			return toInt64((byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, b0, b1, b2, b3, order);
		} else if (order == ByteOrder.LSBFIRST) {
			return toInt64((byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, b3, b2, b1, b0, order);
		} else {
			throw new UnsupportedOperationException("Unsupported byte ordering specified: " + order);
		}
	}
	
	/////////////////////////////////////////
	// Signed Integer (32 Bit)
	/////////////////////////////////////////
	
	public static int toInt32(byte[] bytes)
	{
		return toInt32(bytes, 0);
	}
	
	public static int toInt32(byte[] bytes, int offset)
	{
		return toInt32(bytes, offset, DEFAULT_BYTE_ORDER);
	}
	
	public static int toInt32(byte[] bytes, ByteOrder order)
	{
		return toInt32(bytes
						, 0
						, order);
	}
	
	public static int toInt32(byte[] bytes, int offset, ByteOrder order)
	{
		return toInt32(bytes[offset + 0]
						, bytes[offset + 1]
						, bytes[offset + 2]
						, bytes[offset + 3]
						, order);
	}
	
	public static int toInt32(byte b0, byte b1, byte b2, byte b3)
	{
		return toInt32(b0, b1, b2, b3);
	}
	
	public static int toInt32(byte b0, byte b1, byte b2, byte b3, ByteOrder order)
	{
		if (order == ByteOrder.MSBFIRST) {
			return Ints.fromBytes(b0, b1, b2, b3);
		} else if (order == ByteOrder.LSBFIRST) {
			return Ints.fromBytes(b3, b2, b1, b0);
		} else {
			throw new UnsupportedOperationException("Unsupported byte ordering specified: " + order);
		}
	}
	
	/////////////////////////////////////////
	// Signed Long Integer (64 Bit)
	/////////////////////////////////////////
	
	public static long toInt64(byte[] bytes)
	{
		return toInt64(bytes, 0);
	}
	
	public static long toInt64(byte[] bytes, int offset)
	{
		return toInt64(bytes, offset, DEFAULT_BYTE_ORDER);
	}
	
	public static long toInt64(byte[] bytes, ByteOrder order)
	{
		return toInt64(bytes
						, 0
						, order);
	}
	
	public static long toInt64(byte[] bytes, int offset, ByteOrder order)
	{
		return toInt64(bytes[offset + 0]
						, bytes[offset + 1]
						, bytes[offset + 2]
						, bytes[offset + 3]
						, bytes[offset + 4]
						, bytes[offset + 5]
						, bytes[offset + 6]
						, bytes[offset + 7]
						, order);
	}
	
	public static long toInt64(byte b0, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7)
	{
		return toInt64(b0, b1, b2, b3, b4, b5, b6, b7, DEFAULT_BYTE_ORDER);
	}
	
	public static long toInt64(byte b0, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, ByteOrder order)
	{
		if (order == ByteOrder.MSBFIRST) {
			return Longs.fromBytes(b0, b1, b2, b3, b4, b5, b6, b7);
		} else if (order == ByteOrder.LSBFIRST) {
			return Longs.fromBytes(b7, b6, b5, b4, b3, b2, b1, b0);
		} else {
			throw new UnsupportedOperationException("Unsupported byte ordering specified: " + order);
		}
	}
	
	
	/////////////////////////////////////////
	// Signed Floating Point Decimal (32 Bit)
	/////////////////////////////////////////
	
	public static float toFloat32(byte[] bytes)
	{
		return toFloat32(bytes, 0);
	}
	
	public static float toFloat32(byte[] bytes, int offset)
	{
		return toFloat32(bytes, offset, DEFAULT_BYTE_ORDER);
	}
	
	public static float toFloat32(byte[] bytes, ByteOrder order)
	{
		return toFloat32(bytes
						, 0
						, order);
	}
	
	public static float toFloat32(byte[] bytes, int offset, ByteOrder order)
	{
		return toFloat32(bytes[offset + 0]
						, bytes[offset + 1]
						, bytes[offset + 2]
						, bytes[offset + 3]
						, order);
	}
	
	public static float toFloat32(byte b0, byte b1, byte b2, byte b3)
	{
		return toFloat32(b0, b1, b2, b3, DEFAULT_BYTE_ORDER);
	}
	
	public static float toFloat32(byte b0, byte b1, byte b2, byte b3, ByteOrder order)
	{
		int intBits = 0;
		if (order == ByteOrder.MSBFIRST) {
			intBits = ((b0 & 0xFF) << 24) |
						((b1 & 0xFF) << 16) |
						((b2 & 0xFF) << 8) |
						(b3 & 0xFF);
		} else if (order == ByteOrder.LSBFIRST) {
			intBits = ((b3 & 0xFF) << 24) |
						((b2 & 0xFF) << 16) |
						((b1 & 0xFF) << 8) |
						(b0 & 0xFF);
		} else {
			throw new UnsupportedOperationException("Unsupported byte ordering specified: " + order);
		}
		
		return Float.intBitsToFloat(intBits);
	}
	
	/////////////////////////////////////////
	// Signed Double Precision Floating Point Decimal (64 Bit)
	/////////////////////////////////////////
	
	public static double toFloat64(byte[] bytes)
	{
		return toFloat64(bytes, 0);
	}
	
	public static double toFloat64(byte[] bytes, int offset)
	{
		return toFloat64(bytes, 0, DEFAULT_BYTE_ORDER);
	}
	
	public static double toFloat64(byte[] bytes, ByteOrder byteOrder)
	{
		return toFloat64(bytes
						, 0
						, byteOrder);
	}
	
	public static double toFloat64(byte[] bytes, int offset, ByteOrder byteOrder)
	{
		return toFloat64(bytes[offset + 0]
						, bytes[offset + 1]
						, bytes[offset + 2]
						, bytes[offset + 3]
						, bytes[offset + 4]
						, bytes[offset + 5]
						, bytes[offset + 6]
						, bytes[offset + 7]
						, byteOrder);
	}
	
	public static double toFloat64(byte b0, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7)
	{
		return toFloat64(b0, b1, b2, b3, b4, b5, b6, b7, DEFAULT_BYTE_ORDER);
	}
	
	public static double toFloat64(byte b0, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, ByteOrder order)
	{
		return Double.longBitsToDouble(toInt64(b0, b1, b2, b3, b4, b5, b6, b7, order));
	}
	
	
	
	
}
