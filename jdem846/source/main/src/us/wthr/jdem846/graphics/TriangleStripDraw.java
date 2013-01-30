package us.wthr.jdem846.graphics;

import us.wthr.jdem846.geom.util.SignTrianglePointTest;
import us.wthr.jdem846.graphics.framebuffer.FrameBuffer;
import us.wthr.jdem846.math.MathExt;

public class TriangleStripDraw extends PrimitiveDraw
{
	private TexTriangle triangle = new TexTriangle();
	
	
	
	private TexVertex vtx0 = new TexVertex();
	private TexVertex vtx1 = new TexVertex();
	private TexVertex vtx2 = new TexVertex();
	private int advances = 0;
	
	
	public TriangleStripDraw(FrameBuffer frameBuffer)
	{
		super(frameBuffer);
		
		
	}

	public void vertex(double x, double y, double z)
	{
		TexVertex v = advance();
		v.vector.x = x;
		v.vector.y = y;
		v.vector.z = z;
		
		v.rgba = this.color;
		
		if (this.texture != null) {
			v.useTexture = true;
			v.left = texture.left;
			v.front = texture.front;
		} else {
			v.useTexture = false;
			v.left = 0;
			v.front = 0;
		}
		
		this.render();
		
		
	}
	
	protected TexVertex advance()
	{
		this.advances++;
		
		TexVertex tmp = this.vtx0;
		
		vtx0 = vtx1;
		vtx1 = vtx2;
		vtx2 = tmp;
		
		return tmp;
		
	}
	
	protected void render()
	{
		if (this.advances <= 2) {
			return;
		}
		
		triangle.setVerteces(vtx0, vtx1, vtx2);
		fill(triangle);
		
	}
	
	protected void fill(TexTriangle tri)
	{
		
		if (tri == null) {
			return;
		}
		
		double minX = MathExt.floor(MathExt.min(tri.p0.vector.x, tri.p1.vector.x, tri.p2.vector.x));
		double minY = MathExt.floor(MathExt.min(tri.p0.vector.y, tri.p1.vector.y, tri.p2.vector.y));
		double maxX = MathExt.ceil(MathExt.max(tri.p0.vector.x, tri.p1.vector.x, tri.p2.vector.x));
		double maxY = MathExt.ceil(MathExt.max(tri.p0.vector.y, tri.p1.vector.y, tri.p2.vector.y));


		if (maxX < 0 || minX >= frameBuffer.getWidth() || maxY < 0 || minY >= frameBuffer.getHeight()) {
			return;
		}

		minX = MathExt.max(0, minX);
		maxX = MathExt.min(frameBuffer.getWidth() - 1, maxX);
		minY = MathExt.max(0, minY);
		maxY = MathExt.min(frameBuffer.getHeight() - 1, maxY);

		
		for (double y = minY; y <= maxY; y += 1.0) {
			for (double x = minX; x <= maxX; x += 1.0) {
			
				if (SignTrianglePointTest.contains(tri.p0.vector, tri.p1.vector, tri.p2.vector, x, y, 0)) {
					double z = tri.getInterpolatedZ(x, y);

					double left = tri.getInterpolatedLeft(x, y);
					double front = tri.getInterpolatedFront(x, y);
					
					IColor c = Colors.TRANSPARENT;
					if (left >= 0 && front >= 0 && texture != null) {
						c = this.textureColor(left, front, true);
					} else {
						int c0 = tri.getInterpolatedColor(x, y); // Temporary. Being lazy at the moment since I need to do something else
						c = new Color(c0); 
					}
					
					frameBuffer.set(x, y, z, c.asInt());
				}
			}
		}

		
	}
	
	
	
	
}
