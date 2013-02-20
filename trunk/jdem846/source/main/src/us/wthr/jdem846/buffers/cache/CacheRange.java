package us.wthr.jdem846.buffers.cache;

public class CacheRange {
	
	
	private long startIndex;
	private int length;
	
	public CacheRange(long startIndex, int length)
	{
		this.startIndex = startIndex;
		this.length = length;
	}

	public long getStartIndex() 
	{
		return startIndex;
	}

	public int getLength() 
	{
		return length;
	}
	
	public long getEndIndex()
	{
		return startIndex + length;
	}
	
}
