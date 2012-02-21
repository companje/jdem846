package us.wthr.jdem846.render;

import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.geom.TriangleStrip;

public class TriangleStripFill
{
	
	private TriangleStrip strip;
	
	public TriangleStripFill(TriangleStrip strip)
	{
		this.strip = strip;
	}
	
	public void fill(ModelCanvas modelCanvas) throws CanvasException
	{
		modelCanvas.fillShape(strip);
	}
}
