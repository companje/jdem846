package us.wthr.jdem846.buffers.impl;

import us.wthr.jdem846.buffers.IFloatBuffer;
import us.wthr.jdem846.util.ByteConversions;

public class HighCapacityMappedFloatBuffer implements IFloatBuffer
{
	private final static int FLOAT_SIZE_BYTES = (Float.SIZE / 8);
	private HighCapacityMappedByteBuffer byteBuffer;
	private long capacity = 0;
	
	public HighCapacityMappedFloatBuffer(long capacity)
	{
		this.capacity = capacity;
		
		byteBuffer = new HighCapacityMappedByteBuffer(capacity * FLOAT_SIZE_BYTES);
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
	public Float get(long index)
	{
		byte buffer[] = {0x0, 0x0, 0x0, 0x0};
		long byteIndex = index * FLOAT_SIZE_BYTES;
		buffer[0] = byteBuffer.get(byteIndex);
		buffer[1] = byteBuffer.get(byteIndex + 1);
		buffer[2] = byteBuffer.get(byteIndex + 2);
		buffer[3] = byteBuffer.get(byteIndex + 3);
		
		float v = ByteConversions.bytesToFloat(buffer);
		return v;
	}

	@Override
	public void put(long index, Float value)
	{
		byte buffer[] = {0x0, 0x0, 0x0, 0x0};
		ByteConversions.floatToBytes(value, buffer);
		
		long byteIndex = index * FLOAT_SIZE_BYTES;
		byteBuffer.put(buffer, byteIndex, 0, 4);
	}

	@Override
	public void put(Float[] values, long startIndex, int offset, int count)
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
	public void dispose()
	{
		this.close();
	}

	
}
