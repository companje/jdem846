package us.wthr.jdem846.buffers;

/** Interface to buffers which may have a total capacity over 2GB. 
 * 
 * @author Kevin Gill
 *
 * @param <T>
 */
public interface IBuffer<T> 
{
	public void close();
	public boolean isOpen();
	
	public T get(long index);
	
	public void put(long index, T value);
	public void put(T[] values, long startIndex, int offset, int count);
	
	public long capacity();
	public long capacityBytes();
	
	public void dispose();
	
	public IBuffer<T> duplicate();
}
