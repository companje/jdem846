package us.wthr.jdem846.buffers.impl;

import us.wthr.jdem846.buffers.IDoubleBuffer;
import us.wthr.jdem846.util.ByteConversions;

public class HighCapacityMappedDoubleBuffer implements IDoubleBuffer
{
	private final static int DOUBLE_SIZE_BYTES = (Double.SIZE / 8);
	private HighCapacityMappedByteBuffer byteBuffer;
	private long capacity = 0;
	
	public HighCapacityMappedDoubleBuffer(long capacity)
	{
		this.capacity = capacity;
		
		byteBuffer = new HighCapacityMappedByteBuffer(capacity * DOUBLE_SIZE_BYTES);
	}
	
	
	@Override
	public void close()
	{
		byteBuffer.close();
	}

	@Override
	public boolean isOpen()
	{
		return byteBuffer.isOpen();
	}

	@Override
	public Double get(long index)
	{
		byte buffer[] = {0x0, 0x0, 0x0, 0x0,  0x0, 0x0, 0x0, 0x0};
		long byteIndex = index * DOUBLE_SIZE_BYTES;
		
		for (int i = 0; i < 8; i++)
			buffer[i] = byteBuffer.get(byteIndex + i);
		
		double v = ByteConversions.bytesToDouble(buffer);
		return v;
	}

	@Override
	public void put(long index, Double value)
	{
		byte buffer[] = {0x0, 0x0, 0x0, 0x0,  0x0, 0x0, 0x0, 0x0};
		ByteConversions.doubleToBytes(value, buffer);
		
		long byteIndex = index * DOUBLE_SIZE_BYTES;
		byteBuffer.put(buffer, byteIndex, 0, DOUBLE_SIZE_BYTES);
	}

	@Override
	public void put(Double[] values, long startIndex, int offset, int count)
	{
		// Really Slow & inefficient
		for (int i = 0; i < count; i++) {
			put(startIndex + i, values[offset + i]);
		}
		
	}

	@Override
	public long capacity()
	{
		return capacity;
	}
	
	@Override
	public long capacityBytes()
	{
		return byteBuffer.capacityBytes();
	}

	@Override
	public void dispose()
	{
		this.close();
	}

}
