package us.wthr.jdem846.buffers.cache;

import us.wthr.jdem846.buffers.IIntBuffer;
import us.wthr.jdem846.buffers.impl.HighCapacityMappedIntBuffer;
import us.wthr.jdem846.buffers.impl.StandardCapacityIntBuffer;

public class MemCachingIntBuffer extends AbstractTypedMemCachingBuffer<Integer, IIntBuffer> implements IIntBuffer
{
	
	private ICacheRangeVisitor cacheRangeVisitor;
	
	private HighCapacityMappedIntBuffer diskBuffer;
	private StandardCapacityIntBuffer memBuffer;
	
	public MemCachingIntBuffer(long bufferCapacity, int cacheCapacity, ICacheRangeVisitor cacheRangeVisitor)
	{
		super(cacheRangeVisitor);
		this.diskBuffer = new HighCapacityMappedIntBuffer(bufferCapacity);
		this.memBuffer = new StandardCapacityIntBuffer(cacheCapacity);
	}
	
	protected MemCachingIntBuffer(HighCapacityMappedIntBuffer diskBuffer, StandardCapacityIntBuffer memBuffer, ICacheRangeVisitor cacheRangeVisitor)
	{
		super(cacheRangeVisitor);
		this.diskBuffer = diskBuffer;
		this.memBuffer = memBuffer;
	}
	
	@Override
	public MemCachingIntBuffer duplicate()
	{
		return new MemCachingIntBuffer( (HighCapacityMappedIntBuffer) getDiskBuffer().duplicate()
										, (StandardCapacityIntBuffer) getMemBuffer().duplicate()
										, cacheRangeVisitor);
	}

	@Override
	public void put(int[] values, long startIndex, int offset, int count)
	{
		for (int i = 0; i < count; i++) {
			put(startIndex + i, values[offset + i]);
		}
	}

	@Override
	protected IIntBuffer getDiskBuffer()
	{
		return diskBuffer;
	}

	@Override
	protected IIntBuffer getMemBuffer()
	{
		return memBuffer;
	}

	
	

}
