package us.wthr.jdem846.geom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import us.wthr.jdem846.math.MathExt;

public abstract class Geometric extends RenderableShape
{
	protected List<Edge> edges = new ArrayList<Edge>();
	
	public Geometric()
	{
		
	}
	
	public void addEdge(Edge edge)
	{
		edges.add(edge);
	}
	
	public void addEdge(double x0, double y0, double x1, double y1)
	{
		this.addEdge(x0, y0, 0.0, x1, y1, 0.0);
	}
	
	public void addEdge(double x0, double y0, double z0, double x1, double y1, double z1)
	{
		edges.add(new Edge(new Vertex(x0, y0, z0), new Vertex(x1, y1, z1)));
	}
	
	public Edge[] getEdges()
	{
		return getEdges(false);
	}
	
	public Edge[] getEdges(boolean sort)
	{
		
		Edge[] edgeArray = new Edge[edges.size()];
		edges.toArray(edgeArray);
		
		
		if (sort) {
			Arrays.sort(edgeArray, new Comparator<Edge>() {
				public int compare(Edge e0, Edge e1)
				{
					return e0.compareTo(e1);
				}
			});
		}
		
		return edgeArray;
		
	}
	
	@Override
	public Bounds getBounds()
	{
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		
		
		for (Edge edge : edges) {
			minY = MathExt.min(minY, edge.p0.y(), edge.p1.y());
			maxY = MathExt.max(maxY, edge.p0.y(), edge.p1.y());
			
			minX = MathExt.min(minX, edge.p0.x(), edge.p1.x());
			maxX = MathExt.max(maxX, edge.p0.x(), edge.p1.x());
		}
		
		Bounds bounds = new Bounds(minX, minY, (maxX - minX), (maxY - minY));
		
		return bounds;
	}
	
}
