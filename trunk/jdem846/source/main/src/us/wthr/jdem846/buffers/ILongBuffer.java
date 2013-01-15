package us.wthr.jdem846.buffers;

public interface ILongBuffer extends IBuffer<Long>
{
	public void put(long[] values, long startIndex, int offset, int count);
}
