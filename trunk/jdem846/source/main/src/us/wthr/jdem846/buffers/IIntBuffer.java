package us.wthr.jdem846.buffers;

public interface IIntBuffer extends IBuffer<Integer>
{
	public void put(int[] values, long startIndex, int offset, int count);
}
