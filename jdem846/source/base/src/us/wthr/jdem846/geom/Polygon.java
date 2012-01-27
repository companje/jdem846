package us.wthr.jdem846.geom;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Polygon<T>
{
	
	private List<Edge<T>> edges = new ArrayList<Edge<T>>();
	
	
	public Polygon()
	{
		
	}
	
	public void addEdge(Edge<T> edge)
	{
		edges.add(edge);
	}
	
	public void addEdge(T x0, T y0, T x1, T y1)
	{
		edges.add(new Edge<T>(new Vertex<T>(x0, y0), new Vertex<T>(x1, y1)));
		
	}
	
	
	public Edge<T>[] getEdges(Shape path, boolean sort)
	{
		
		Edge<T>[] edgeArray = new Edge[edges.size()];
		edges.toArray(edgeArray);
		
		
		if (sort) {
			Arrays.sort(edgeArray, new Comparator<Edge<T>>() {
				public int compare(Edge<T> e0, Edge<T> e1)
				{
					return e0.compareTo(e1);
				}
			});
		}
		
		return edgeArray;
		
	}
	
}
