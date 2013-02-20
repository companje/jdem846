package us.wthr.jdem846.buffers.cache;

import us.wthr.jdem846.buffers.IFloatBuffer;
import us.wthr.jdem846.buffers.impl.HighCapacityMappedFloatBuffer;
import us.wthr.jdem846.buffers.impl.StandardCapacityFloatBuffer;

public class MemCachingFloatBuffer extends AbstractTypedMemCachingBuffer<Float, IFloatBuffer> implements IFloatBuffer
{
	
	private ICacheRangeVisitor cacheRangeVisitor;
	
	private HighCapacityMappedFloatBuffer diskBuffer;
	private StandardCapacityFloatBuffer memBuffer;
	
	public MemCachingFloatBuffer(long bufferCapacity, int cacheCapacity, ICacheRangeVisitor cacheRangeVisitor)
	{
		super(cacheRangeVisitor);
		this.diskBuffer = new HighCapacityMappedFloatBuffer(bufferCapacity);
		this.memBuffer = new StandardCapacityFloatBuffer(cacheCapacity);
	}
	
	protected MemCachingFloatBuffer(HighCapacityMappedFloatBuffer diskBuffer, StandardCapacityFloatBuffer memBuffer, ICacheRangeVisitor cacheRangeVisitor)
	{
		super(cacheRangeVisitor);
		this.diskBuffer = diskBuffer;
		this.memBuffer = memBuffer;
	}
	
	@Override
	public MemCachingFloatBuffer duplicate()
	{
		return new MemCachingFloatBuffer( (HighCapacityMappedFloatBuffer) getDiskBuffer().duplicate()
										, (StandardCapacityFloatBuffer) getMemBuffer().duplicate()
										, cacheRangeVisitor);
	}

	@Override
	public void put(float[] values, long startIndex, int offset, int count)
	{
		for (int i = 0; i < count; i++) {
			put(startIndex + i, values[offset + i]);
		}
	}

	@Override
	protected IFloatBuffer getDiskBuffer()
	{
		return diskBuffer;
	}

	@Override
	protected IFloatBuffer getMemBuffer()
	{
		return memBuffer;
	}

	
	

}