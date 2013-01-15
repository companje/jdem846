package us.wthr.jdem846.buffers.impl;

import java.nio.ByteBuffer;

import us.wthr.jdem846.buffers.IByteBuffer;
import us.wthr.jdem846.exception.BufferException;

public class StandardCapacityByteBuffer implements IByteBuffer
{
	
	private ByteBuffer buffer;
	
	public StandardCapacityByteBuffer(int capacity)
	{
		this(capacity, false);
	}
	
	public StandardCapacityByteBuffer(int capacity, boolean direct)
	{
		if (direct) {
			this.buffer = ByteBuffer.allocateDirect(capacity);
		} else {
			this.buffer = ByteBuffer.allocate(capacity);
		}
	}
	
	protected StandardCapacityByteBuffer(ByteBuffer buffer)
	{
		this.buffer = buffer;
	}
	
	@Override
	public void close()
	{
		if (!isOpen()) {
			throw new BufferException("Cannot close: buffer not open to begin with");
		}
		this.buffer = null;
	}

	@Override
	public boolean isOpen()
	{
		return (buffer != null);
	}

	@Override
	public Byte get(long index)
	{
		return buffer.get((int)index);
	}

	@Override
	public void put(long index, Byte value)
	{
		buffer.put((int)index, value);
	}
	
	@Override
	public void put(byte[] values, long startIndex, int offset, int count)
	{
		// Really Slow & inefficient
		for (int i = 0; i < count; i++) {
			put(startIndex + i, values[offset + i]);
		}
	}
	
	@Override
	public void put(Byte[] values, long startIndex, int offset, int count)
	{
		// Really Slow & inefficient
		for (int i = 0; i < count; i++) {
			put(startIndex + i, values[offset + i]);
		}
	}

	@Override
	public long capacity()
	{
		return buffer.capacity();
	}

	@Override
	public long capacityBytes()
	{
		return capacity();
	}

	@Override
	public void dispose()
	{
		close();
	}

	@Override
	public IByteBuffer duplicate()
	{
		return new StandardCapacityByteBuffer(buffer.duplicate());
	}

}
