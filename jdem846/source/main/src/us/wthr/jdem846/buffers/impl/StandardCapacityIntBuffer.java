package us.wthr.jdem846.buffers.impl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import us.wthr.jdem846.buffers.IIntBuffer;
import us.wthr.jdem846.exception.BufferException;

public class StandardCapacityIntBuffer implements IIntBuffer
{
	private IntBuffer buffer;
	
	public StandardCapacityIntBuffer(int capacity)
	{
		this(capacity, false);
	}
	
	public StandardCapacityIntBuffer(int capacity, boolean direct)
	{
		if (direct) {
			ByteBuffer bb = ByteBuffer.allocateDirect(capacity * (Integer.SIZE / 8));
			this.buffer = bb.asIntBuffer();
		} else {
			this.buffer = IntBuffer.allocate(capacity);
		}
	}
	
	protected StandardCapacityIntBuffer(IntBuffer buffer)
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
	public Integer get(long index)
	{
		return buffer.get((int)index);
	}

	@Override
	public void put(long index, Integer value)
	{
		buffer.put((int)index, value);
	}
	
	@Override
	public void put(int[] values, long startIndex, int offset, int count)
	{
		// Really Slow & inefficient
		for (int i = 0; i < count; i++) {
			put(startIndex + i, values[offset + i]);
		}
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
		return buffer.capacity();
	}

	@Override
	public long capacityBytes()
	{
		return capacity() * (Integer.SIZE / 8);
	}

	@Override
	public void dispose()
	{
		close();
	}

	@Override
	public IIntBuffer duplicate()
	{
		return new StandardCapacityIntBuffer(buffer.duplicate());
	}
}
