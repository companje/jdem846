package us.wthr.jdem846.modelgrid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.util.ByteConversions;

public abstract class ModelGridFileIO
{
	public static final String FILE_HEADER_PREFIX = "jdemgrid-->";
	public static final int FILE_HEADER_PREFIX_LENGTH = FILE_HEADER_PREFIX.getBytes().length;
	public static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.LSBFIRST;
	
	
	public static long calculateEstimatedFileSize(IModelGrid modelGrid)
	{
		long headerSizeBytes = 72;
		long dataCellSizeBytes = 12;
		long dataSizeBytes = dataCellSizeBytes * (modelGrid.getWidth() * modelGrid.getHeight());
		
		return (headerSizeBytes + dataSizeBytes);
	}
	
	
	protected static long write(double value, OutputStream out) throws IOException
	{
		byte[] buffer8 = new byte[8];
		ByteConversions.doubleToBytes(value, buffer8, ModelGridFileIO.DEFAULT_BYTE_ORDER);
		return write(buffer8, 8, out);
		
	}
	
	protected static long write(float value, OutputStream out) throws IOException
	{
		byte[] buffer4 = new byte[4];
		ByteConversions.floatToBytes(value, buffer4, ModelGridFileIO.DEFAULT_BYTE_ORDER);
		return write(buffer4, 4, out);
	}
	
	protected static long write(int value, OutputStream out) throws IOException
	{
		byte[] buffer4 = new byte[4];
		ByteConversions.intToBytes(value, buffer4, ModelGridFileIO.DEFAULT_BYTE_ORDER);
		return write(buffer4, 4, out);
	}

	
	protected static long write(long value, OutputStream out) throws IOException
	{
		byte[] buffer8 = new byte[8];
		ByteConversions.longToBytes(value, buffer8, ModelGridFileIO.DEFAULT_BYTE_ORDER);
		return write(buffer8, 8, out);
	}
	
	protected static long write(String value, OutputStream out) throws IOException
	{
		byte[] bytes = value.getBytes();
		return write(bytes, bytes.length, out);
	}
	
	protected static long write(byte[] bytes, int length, OutputStream out) throws IOException
	{
		out.write(bytes, 0, length);
		return length;
	}
	
	protected static double readDouble(InputStream in) throws IOException
	{
		byte[] bytes = new byte[8];
		read(bytes, 8, in);
		return ByteConversions.bytesToDouble(bytes, DEFAULT_BYTE_ORDER);
	}
	
	protected static float readFloat(InputStream in) throws IOException
	{
		byte[] bytes = new byte[4];
		read(bytes, 4, in);
		return ByteConversions.bytesToFloat(bytes, DEFAULT_BYTE_ORDER);
	}
	
	protected static int readInt(InputStream in) throws IOException
	{
		byte[] bytes = new byte[4];
		read(bytes, 4, in);
		return ByteConversions.bytesToInt(bytes, DEFAULT_BYTE_ORDER);
	}
	
	protected static long readLong(InputStream in) throws IOException
	{
		byte[] bytes = new byte[8];
		read(bytes, 8, in);
		return ByteConversions.bytesToLong(bytes, DEFAULT_BYTE_ORDER);
	}
	
	protected static String readString(int length, InputStream in) throws IOException
	{
		byte[] bytes = new byte[length];
		read(bytes, length, in);
		return new String(bytes);
	}
	
	protected static long read(byte[] bytes, int length, InputStream in) throws IOException
	{
		int bytesRead = in.read(bytes, 0, length);
		
		if (bytesRead < length) {
			throw new IOException("Insufficient bytes read. Read: " + bytesRead + ", Required: " + length);
		}
		
		return bytesRead;
	}
}
