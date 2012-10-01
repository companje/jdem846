package us.wthr.jdem846.graphics;

import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.math.Vector;

public class TexVertex extends Vertex 
{
	public double left = 0;
	public double front = 0;
	public boolean useTexture = false;
	
	public TexVertex()
	{
		
	}
	
	public TexVertex(double left, double front)
	{
		this.left = left;
		this.front = front;
	}
	
	
	public TexVertex(Vector v, int rgba, double left, double front)
	{
		super(v, rgba);
		this.left = left;
		this.front = front;
	}
	
	public TexVertex(Vector v, double left, double front)
	{
		super(v);
		this.left = left;
		this.front = front;
	}
	
}
