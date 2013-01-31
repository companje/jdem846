package us.wthr.jdem846.graphics;

import us.wthr.jdem846.buffers.IIntBuffer;
import us.wthr.jdem846.math.MathExt;

public class SubTextureIntBuffer implements IIntBuffer
{
	
	private IIntBuffer parentBuffer;
	private long subTextureX;
	private long subTextureY;
	private long subTextureWidth;
	private long subTextureHeight;
	
	private long subTextureCapacity;
	
	private long parentTextureWidth;
	private long parentTextureHeight;
	
	public SubTextureIntBuffer(IIntBuffer parentBuffer, long parentTextureWidth, long parentTextureHeight, long subTextureX, long subTextureY, long subTextureWidth, long subTextureHeight)
	{
		this.parentBuffer = parentBuffer;
		this.parentTextureWidth = parentTextureWidth;
		this.parentTextureHeight = parentTextureHeight;
		this.subTextureX = subTextureX;
		this.subTextureY = subTextureY;
		this.subTextureWidth = subTextureWidth;
		this.subTextureHeight = subTextureHeight;
		
		subTextureCapacity = subTextureWidth * subTextureHeight;
	}
	
	
	@Override
	public void close()
	{
		parentBuffer = null;
	}

	@Override
	public boolean isOpen()
	{
		return (parentBuffer != null && parentBuffer.isOpen());
	}

	
	protected long indexToParentBufferIndex(long index)
	{
		long subTextureRow = (long) MathExt.floor((double)index / (double)subTextureWidth);
		long subTextureColumn = (long) MathExt.floor((double)index - ((double) subTextureRow * (double)subTextureWidth));
		
		long parentTextureIndex = ((subTextureRow + subTextureY) * parentTextureWidth) + (subTextureColumn + subTextureX);
		
		return parentTextureIndex;
	}
	
	@Override
	public Integer get(long index)
	{
		return parentBuffer.get(indexToParentBufferIndex(index));
	}

	@Override
	public void put(long index, Integer value)
	{
		parentBuffer.put(indexToParentBufferIndex(index), value);
	}

	
	@Override
	public void put(int[] values, long startIndex, int offset, int count)
	{
		// Really Slow & inefficient
		for (int i = 0; i < count; i++) {
			put(indexToParentBufferIndex(startIndex + i), values[offset + i]);
		}
	}
	
	@Override
	public void put(Integer[] values, long startIndex, int offset, int count)
	{
		// Really Slow & inefficient
		for (int i = 0; i < count; i++) {
			put(indexToParentBufferIndex(startIndex + i), values[offset + i]);
		}
	}

	@Override
	public long capacity()
	{
		return subTextureCapacity;
	}

	@Override
	public long capacityBytes()
	{
		return subTextureCapacity * (Integer.SIZE / 8);
	}

	@Override
	public void dispose()
	{
		close();
	}

	@Override
	public IIntBuffer duplicate()
	{
		return new SubTextureIntBuffer(parentBuffer, parentTextureWidth, parentTextureHeight, subTextureX, subTextureY, subTextureWidth, subTextureHeight);
	}

	

}
