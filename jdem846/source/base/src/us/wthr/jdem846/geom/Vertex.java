package us.wthr.jdem846.geom;

import java.util.Comparator;

public class Vertex<T> implements Comparable<Vertex<T>>
{
	public T x;
	public T y;
	
	public Vertex(Vertex<T> copy)
	{
		this.x = copy.x;
		this.y = copy.y;
	}
	
	public Vertex(T x, T y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(Vertex<T> other)
	{
		if (other.y instanceof Double) {
			Double y0 = (Double) this.y;
			Double y1 = (Double) other.y;
			return y0.compareTo(y1);
		} else if (other.y instanceof Integer) {
			Integer y0 = (Integer) this.y;
			Integer y1 = (Integer) other.y;
			return y0.compareTo(y1);
		} else {
			return 0;
		}
		
	}

	
}
