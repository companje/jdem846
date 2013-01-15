package us.wthr.jdem846.buffers.impl;

import us.wthr.jdem846.buffers.ILongBuffer;
import us.wthr.jdem846.util.ByteConversions;

public class HighCapacityMappedLongBuffer implements ILongBuffer
{
	private final static int LONG_SIZE_BYTES = (Long.SIZE / 8);
	private HighCapacityMappedByteBuffer byteBuffer;
	private long capacity = 0;
	
	public HighCapacityMappedLongBuffer(long capacity)
	{
		this.capacity = capacity;
		
		byteBuffer = new HighCapacityMappedByteBuffer(capacity * LONG_SIZE_BYTES);
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
	public Long get(long index)
	{
		byte buffer[] = new byte[LONG_SIZE_BYTES];
		long byteIndex = index * LONG_SIZE_BYTES;
		for (int i = 0; i < LONG_SIZE_BYTES; i++) {
			buffer[i] = byteBuffer.get(byteIndex + i);
		}
		
		long v = ByteConversions.bytesToLong(buffer);
		return v;
	}

	@Override
	public void put(long index, Long value)
	{
		byte buffer[] = new byte[LONG_SIZE_BYTES];
		ByteConversions.longToBytes(value, buffer);
		
		long byteIndex = index * LONG_SIZE_BYTES;
		byteBuffer.put(buffer, byteIndex, 0, LONG_SIZE_BYTES);
	}
	
	@Override
	public void put(long[] values, long startIndex, int offset, int count)
	{
		// Really Slow & inefficient
		for (int i = 0; i < count; i++) {
			put(startIndex + i, values[offset + i]);
		}
	}
	
	@Override
	public void put(Long[] values, long startIndex, int offset, int count)
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

	@Override
	public ILongBuffer duplicate()
	{
		return null;
	}
}
