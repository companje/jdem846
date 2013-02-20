package us.wthr.jdem846.buffers.cache;

import us.wthr.jdem846.buffers.IDoubleBuffer;
import us.wthr.jdem846.buffers.impl.HighCapacityMappedDoubleBuffer;
import us.wthr.jdem846.buffers.impl.StandardCapacityDoubleBuffer;


public class MemCachingDoubleBuffer extends AbstractTypedMemCachingBuffer<Double, IDoubleBuffer> implements IDoubleBuffer
{
	
	private ICacheRangeVisitor cacheRangeVisitor;
	
	private HighCapacityMappedDoubleBuffer diskBuffer;
	private StandardCapacityDoubleBuffer memBuffer;
	
	public MemCachingDoubleBuffer(long bufferCapacity, int cacheCapacity, ICacheRangeVisitor cacheRangeVisitor)
	{
		super(cacheRangeVisitor);
		this.diskBuffer = new HighCapacityMappedDoubleBuffer(bufferCapacity);
		this.memBuffer = new StandardCapacityDoubleBuffer(cacheCapacity);
	}
	
	protected MemCachingDoubleBuffer(HighCapacityMappedDoubleBuffer diskBuffer, StandardCapacityDoubleBuffer memBuffer, ICacheRangeVisitor cacheRangeVisitor)
	{
		super(cacheRangeVisitor);
		this.diskBuffer = diskBuffer;
		this.memBuffer = memBuffer;
	}
	
	@Override
	public MemCachingDoubleBuffer duplicate()
	{
		return new MemCachingDoubleBuffer( (HighCapacityMappedDoubleBuffer) getDiskBuffer().duplicate()
										, (StandardCapacityDoubleBuffer) getMemBuffer().duplicate()
										, cacheRangeVisitor);
	}

	@Override
	public void put(double[] values, long startIndex, int offset, int count)
	{
		for (int i = 0; i < count; i++) {
			put(startIndex + i, values[offset + i]);
		}
	}

	@Override
	protected IDoubleBuffer getDiskBuffer()
	{
		return diskBuffer;
	}

	@Override
	protected IDoubleBuffer getMemBuffer()
	{
		return memBuffer;
	}

	
	

}