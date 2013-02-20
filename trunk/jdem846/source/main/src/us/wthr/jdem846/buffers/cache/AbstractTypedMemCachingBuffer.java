package us.wthr.jdem846.buffers.cache;

import us.wthr.jdem846.buffers.IBuffer;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


public abstract class AbstractTypedMemCachingBuffer<T, B extends IBuffer<T>>
{
	private static Log log = Logging.getLog(AbstractTypedMemCachingBuffer.class);
	
	private ICacheRangeVisitor cacheRangeVisitor;
	private CacheRange cacheRange;
	
	
	private boolean isDirty = false;
	
	public AbstractTypedMemCachingBuffer(ICacheRangeVisitor cacheRangeVisitor)
	{
		this.cacheRangeVisitor = cacheRangeVisitor;
	}
	
	protected abstract B getDiskBuffer();
	protected abstract B getMemBuffer();
	

	
	protected void writeMemBufferToDisk()
	{
		if (cacheRange == null || isDirty == false) {
			return;
		}
		
		log.info("Writing cache to index " + cacheRange.getStartIndex() + " to index " + cacheRange.getEndIndex());
		for (int i = 0; i < cacheRange.getLength(); i++) {
			T value = getMemBuffer().get(i);
			getDiskBuffer().put(cacheRange.getStartIndex() + i, value);
		}
		
	}
	
	protected int diskCacheBufferIndexToMemBufferIndex(long diskBufferIndex)
	{
		int memBufferIndex = (int) (diskBufferIndex - cacheRange.getStartIndex());
		return memBufferIndex;
	}
	
	protected int getMemBufferIndex(long diskBufferIndex)
	{
		
		if (cacheRange == null || diskBufferIndex < cacheRange.getStartIndex() || diskBufferIndex > cacheRange.getEndIndex()) {
			
			writeMemBufferToDisk();
			
			cacheRange = this.cacheRangeVisitor.getCacheRange(diskBufferIndex);
			if (cacheRange != null) {
				log.info("Loading cache from index " + cacheRange.getStartIndex() + " to index " + cacheRange.getEndIndex());
				
				for (long i = cacheRange.getStartIndex(); i < cacheRange.getEndIndex(); i++) {

					int memBufferIndex = diskCacheBufferIndexToMemBufferIndex(i);
					T b = getDiskBuffer().get(i);
					getMemBuffer().put(memBufferIndex, b);
					
				}
				
				isDirty = false;
			}
		}
		
		
		if (cacheRange != null && diskBufferIndex >= cacheRange.getStartIndex() && diskBufferIndex <= cacheRange.getEndIndex()) {
			return diskCacheBufferIndexToMemBufferIndex(diskBufferIndex);
		} else {
			return -1;
		}
	}
	
	

	public void close()
	{
		getDiskBuffer().close();
		getMemBuffer().close();
	}


	public boolean isOpen()
	{
		return getDiskBuffer().isOpen();
	}


	public T get(long index)
	{
		int memBufferIndex = getMemBufferIndex(index);
		return getMemBuffer().get(memBufferIndex);
	}

	
	public void put(long index, T value)
	{
		int memBufferIndex = getMemBufferIndex(index);
		getMemBuffer().put(memBufferIndex, value);
		isDirty = true;
		//getDiskBuffer().put(index, value);
	}

	
	public void put(T[] values, long startIndex, int offset, int count)
	{
		for (int i = 0; i < count; i++) {
			put(startIndex + i, values[offset + i]);
		}
	}
	

	
	public long capacity()
	{
		return getDiskBuffer().capacity();
	}

	
	public long capacityBytes()
	{
		return getDiskBuffer().capacityBytes();
	}

	
	public void dispose()
	{
		getDiskBuffer().dispose();
		getMemBuffer().dispose();
	}


}
