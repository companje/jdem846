package us.wthr.jdem846.buffers;

public interface IBuffer<T> 
{
	
	public T get(long index);
	
	public void put(long index, T value);
	public void put(T[] values, long offset, long count);
	
	public long capacity();
	
	public void dispose();
}
