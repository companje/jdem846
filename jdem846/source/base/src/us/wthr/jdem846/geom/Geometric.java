package us.wthr.jdem846.geom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Geometric<T> 
{
	private List<Edge<T>> edges = new ArrayList<Edge<T>>();
	
	public Geometric()
	{
		
	}
	
	
	public Edge<T>[] getEdges()
	{
		return getEdges(false);
	}
	
	public Edge<T>[] getEdges(boolean sort)
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
