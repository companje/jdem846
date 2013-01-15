package us.wthr.jdem846.buffers.impl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import us.wthr.jdem846.buffers.IFloatBuffer;
import us.wthr.jdem846.exception.BufferException;

public class StandardCapacityFloatBuffer implements IFloatBuffer
{
	private FloatBuffer buffer;
	
	public StandardCapacityFloatBuffer(int capacity)
	{
		this(capacity, false);
	}
	
	public StandardCapacityFloatBuffer(int capacity, boolean direct)
	{
		if (direct) {
			ByteBuffer bb = ByteBuffer.allocateDirect(capacity * (Float.SIZE / 8));
			this.buffer = bb.asFloatBuffer();
		} else {
			this.buffer = FloatBuffer.allocate(capacity);
		}
	}
	
	protected StandardCapacityFloatBuffer(FloatBuffer buffer)
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
	public Float get(long index)
	{
		return buffer.get((int)index);
	}

	@Override
	public void put(long index, Float value)
	{
		buffer.put((int)index, value);
	}
	
	@Override
	public void put(float[] values, long startIndex, int offset, int count)
	{
		// Really Slow & inefficient
		for (int i = 0; i < count; i++) {
			put(startIndex + i, values[offset + i]);
		}
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
		return buffer.capacity();
	}

	@Override
	public long capacityBytes()
	{
		return capacity() * (Float.SIZE / 8);
	}

	@Override
	public void dispose()
	{
		close();
	}

	@Override
	public IFloatBuffer duplicate()
	{
		return new StandardCapacityFloatBuffer(buffer.duplicate());
	}
}
