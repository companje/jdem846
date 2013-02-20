package us.wthr.jdem846.buffers.cache;

import us.wthr.jdem846.buffers.IByteBuffer;
import us.wthr.jdem846.buffers.impl.HighCapacityMappedByteBuffer;
import us.wthr.jdem846.buffers.impl.StandardCapacityByteBuffer;


public class MemCachingByteBuffer extends AbstractTypedMemCachingBuffer<Byte, IByteBuffer> implements IByteBuffer
{
	
	private ICacheRangeVisitor cacheRangeVisitor;
	
	private HighCapacityMappedByteBuffer diskBuffer;
	private StandardCapacityByteBuffer memBuffer;
	
	public MemCachingByteBuffer(long bufferCapacity, int cacheCapacity, ICacheRangeVisitor cacheRangeVisitor)
	{
		super(cacheRangeVisitor);
		this.diskBuffer = new HighCapacityMappedByteBuffer(bufferCapacity);
		this.memBuffer = new StandardCapacityByteBuffer(cacheCapacity);
	}
	
	protected MemCachingByteBuffer(HighCapacityMappedByteBuffer diskBuffer, StandardCapacityByteBuffer memBuffer, ICacheRangeVisitor cacheRangeVisitor)
	{
		super(cacheRangeVisitor);
		this.diskBuffer = diskBuffer;
		this.memBuffer = memBuffer;
	}
	
	@Override
	public MemCachingByteBuffer duplicate()
	{
		return new MemCachingByteBuffer( (HighCapacityMappedByteBuffer) getDiskBuffer().duplicate()
										, (StandardCapacityByteBuffer) getMemBuffer().duplicate()
										, cacheRangeVisitor);
	}

	@Override
	public void put(byte[] values, long startIndex, int offset, int count)
	{
		for (int i = 0; i < count; i++) {
			put(startIndex + i, values[offset + i]);
		}
	}

	@Override
	protected IByteBuffer getDiskBuffer()
	{
		return diskBuffer;
	}

	@Override
	protected IByteBuffer getMemBuffer()
	{
		return memBuffer;
	}

	

}
