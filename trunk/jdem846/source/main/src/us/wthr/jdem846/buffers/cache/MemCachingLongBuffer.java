package us.wthr.jdem846.buffers.cache;

import us.wthr.jdem846.buffers.ILongBuffer;
import us.wthr.jdem846.buffers.impl.HighCapacityMappedLongBuffer;
import us.wthr.jdem846.buffers.impl.StandardCapacityLongBuffer;


public class MemCachingLongBuffer extends AbstractTypedMemCachingBuffer<Long, ILongBuffer> implements ILongBuffer
{
	
	private ICacheRangeVisitor cacheRangeVisitor;
	
	private HighCapacityMappedLongBuffer diskBuffer;
	private StandardCapacityLongBuffer memBuffer;
	
	public MemCachingLongBuffer(long bufferCapacity, int cacheCapacity, ICacheRangeVisitor cacheRangeVisitor)
	{
		super(cacheRangeVisitor);
		this.diskBuffer = new HighCapacityMappedLongBuffer(bufferCapacity);
		this.memBuffer = new StandardCapacityLongBuffer(cacheCapacity);
	}
	
	protected MemCachingLongBuffer(HighCapacityMappedLongBuffer diskBuffer, StandardCapacityLongBuffer memBuffer, ICacheRangeVisitor cacheRangeVisitor)
	{
		super(cacheRangeVisitor);
		this.diskBuffer = diskBuffer;
		this.memBuffer = memBuffer;
	}
	
	@Override
	public MemCachingLongBuffer duplicate()
	{
		return new MemCachingLongBuffer( (HighCapacityMappedLongBuffer) getDiskBuffer().duplicate()
										, (StandardCapacityLongBuffer) getMemBuffer().duplicate()
										, cacheRangeVisitor);
	}

	@Override
	public void put(long[] values, long startIndex, int offset, int count)
	{
		for (int i = 0; i < count; i++) {
			put(startIndex + i, values[offset + i]);
		}
	}

	@Override
	protected ILongBuffer getDiskBuffer()
	{
		return diskBuffer;
	}

	@Override
	protected ILongBuffer getMemBuffer()
	{
		return memBuffer;
	}

	
	

}