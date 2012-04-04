package us.wthr.jdem846.render;

import java.util.ArrayList;
import java.util.List;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class MatrixBuffer<E>
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(MatrixBuffer.class);
	
	private List<E> buffer;
	
	private int width;
	private int height;
	
	public MatrixBuffer(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		int capacity = (width * height);
		
		buffer = new ArrayList<E>(capacity);
		for (int i = 0; i < capacity; i++) {
			buffer.add(null);
		}
		
	}
	
	public void fill(E value)
	{
		int capacity = (width * height);
		for (int i = 0; i < capacity; i++) {
			buffer.set(i, value);
		}
	}
	
	public void set(int x, int y, E value)
	{
		if (x < 0 || x >= width) {
			throw new IndexOutOfBoundsException("Invalid index (x): " + x);
		}
		
		if (y < 0 || y >= height) {
			throw new IndexOutOfBoundsException("Invalid index (y): " + y);
		}
		
		buffer.set(this.getPosition(x, y), value);
	}
	
	public E get(int x, int y)
	{
		if (x < 0 || x >= width) {
			throw new IndexOutOfBoundsException("Invalid index (x): " + x);
		}
		
		if (y < 0 || y >= height) {
			throw new IndexOutOfBoundsException("Invalid index (y): " + y);
		}
		
		return buffer.get(this.getPosition(x, y));
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	protected int getPosition(int x, int y)
	{
		return (y * width) + x;
	}
	
	public void dispose()
	{
		buffer.clear();
	}
	
}
