package us.wthr.jdem846.buffers.impl;

import us.wthr.jdem846.buffers.IIntBuffer;
import us.wthr.jdem846.util.ByteConversions;

public class HighCapacityMappedIntBuffer implements IIntBuffer
{
	private final static int INT_SIZE_BYTES = (Integer.SIZE / 8);
	private HighCapacityMappedByteBuffer byteBuffer;
	private long capacity = 0;
	
	public HighCapacityMappedIntBuffer(long capacity)
	{
		this.capacity = capacity;
		
		byteBuffer = new HighCapacityMappedByteBuffer(capacity * INT_SIZE_BYTES);
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
	public Integer get(long index)
	{
		byte buffer[] = {0x0, 0x0, 0x0, 0x0};
		long byteIndex = index * INT_SIZE_BYTES;
		buffer[0] = byteBuffer.get(byteIndex);
		buffer[1] = byteBuffer.get(byteIndex + 1);
		buffer[2] = byteBuffer.get(byteIndex + 2);
		buffer[3] = byteBuffer.get(byteIndex + 3);
		
		int v = ByteConversions.bytesToInt(buffer);
		return v;
	}

	@Override
	public void put(long index, Integer value)
	{
		byte buffer[] = {0x0, 0x0, 0x0, 0x0};
		ByteConversions.intToBytes(value, buffer);
		
		long byteIndex = index * INT_SIZE_BYTES;
		byteBuffer.put(buffer, byteIndex, 0, 4);
	}

	@Override
	public void put(Integer[] values, long startIndex, int offset, int count)
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
