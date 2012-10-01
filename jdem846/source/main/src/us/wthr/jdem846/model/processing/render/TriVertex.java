package us.wthr.jdem846.model.processing.render;

import us.wthr.jdem846.geom.Triangle;
import us.wthr.jdem846.geom.Vertex;

public class TriVertex {
	
	public Vertex v0;
	public Vertex v1;
	public Vertex v2;
	
	private int advanceCount = 0;
	
	private Triangle triangle;
	
	public TriVertex()
	{
		this.v0 = new Vertex();
		this.v1 = new Vertex();
		this.v2 = new Vertex();
		
		this.triangle = new Triangle();
	}
	
	void advance()
	{
		Vertex v = v0;
		v0 = v1;
		v1 = v2;
		v2 = v;
		advanceCount++;
	}
	
	void reset()
	{
		this.advanceCount = 0;
	}
	
	boolean canRender()
	{
		return this.advanceCount > 2;
	}
	
	Triangle getTriangle()
	{
		this.triangle.setVerteces(v0, v1, v2);
		return this.triangle;
	}
	
	
}
