package us.wthr.jdem846.buffers.impl;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import us.wthr.jdem846.buffers.ILongBuffer;
import us.wthr.jdem846.exception.BufferException;

public class StandardCapacityLongBuffer implements ILongBuffer
{
	private LongBuffer buffer;
	
	public StandardCapacityLongBuffer(int capacity)
	{
		this(capacity, false);
	}
	
	public StandardCapacityLongBuffer(int capacity, boolean direct)
	{
		if (direct) {
			ByteBuffer bb = ByteBuffer.allocateDirect(capacity * (Long.SIZE / 8));
			this.buffer = bb.asLongBuffer();
		} else {
			this.buffer = LongBuffer.allocate(capacity);
		}
	}
	
	protected StandardCapacityLongBuffer(LongBuffer buffer)
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
	public Long get(long index)
	{
		return buffer.get((int)index);
	}

	@Override
	public void put(long index, Long value)
	{
		buffer.put((int)index, value);
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
		return buffer.capacity();
	}

	@Override
	public long capacityBytes()
	{
		return capacity() * (Long.SIZE / 8);
	}

	@Override
	public void dispose()
	{
		close();
	}

	@Override
	public ILongBuffer duplicate()
	{
		return new StandardCapacityLongBuffer(buffer.duplicate());
	}
}
