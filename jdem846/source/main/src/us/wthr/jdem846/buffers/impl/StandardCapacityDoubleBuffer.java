package us.wthr.jdem846.buffers.impl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import us.wthr.jdem846.buffers.IDoubleBuffer;
import us.wthr.jdem846.exception.BufferException;

public class StandardCapacityDoubleBuffer implements IDoubleBuffer
{
	private DoubleBuffer buffer;
	
	public StandardCapacityDoubleBuffer(int capacity)
	{
		this(capacity, false);
	}
	
	public StandardCapacityDoubleBuffer(int capacity, boolean direct)
	{
		if (direct) {
			ByteBuffer bb = ByteBuffer.allocateDirect(capacity * (Double.SIZE / 8));
			this.buffer = bb.asDoubleBuffer();
		} else {
			this.buffer = DoubleBuffer.allocate(capacity);
		}
	}
	
	protected StandardCapacityDoubleBuffer(DoubleBuffer buffer)
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
	public Double get(long index)
	{
		return buffer.get((int)index);
	}

	@Override
	public void put(long index, Double value)
	{
		buffer.put((int)index, value);
	}
	
	@Override
	public void put(double[] values, long startIndex, int offset, int count)
	{
		// Really Slow & inefficient
		for (int i = 0; i < count; i++) {
			put(startIndex + i, values[offset + i]);
		}
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
		return buffer.capacity();
	}

	@Override
	public long capacityBytes()
	{
		return capacity() * (Double.SIZE / 8);
	}

	@Override
	public void dispose()
	{
		close();
	}

	@Override
	public IDoubleBuffer duplicate()
	{
		return new StandardCapacityDoubleBuffer(buffer.duplicate());
	}
}
