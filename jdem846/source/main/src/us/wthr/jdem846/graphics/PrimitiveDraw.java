package us.wthr.jdem846.graphics;

import us.wthr.jdem846.graphics.framebuffer.FrameBuffer;
import us.wthr.jdem846.util.ColorUtil;

public abstract class PrimitiveDraw
{
	
	protected FrameBuffer frameBuffer = null;
	protected int color = 0x0;
	protected Texture texture = null;
	
	
	public PrimitiveDraw(FrameBuffer frameBuffer)
	{
		this.frameBuffer = frameBuffer;
	}
	
	public abstract void vertex(double x, double y, double z);
	
	
	public void setColor(int color)
	{
		this.color = color;
	}
	
	public void setTexture(Texture texture)
	{
		this.texture = texture;
	}
	
	
	protected int getValidColor(int c00, boolean b00, int c01, boolean b01, int c10, boolean b10, int c11, boolean b11)
	{
		if (b00)
			return c00;
		if (b01)
			return c01;
		if (b10)
			return c10;
		if (b11)
			return c11;
		
		return 0x0;
	}
	
	protected boolean isValidIndex(double x, double y)
	{
		if (texture == null) {
			return false;
		}
		
		if (x >= 0 && x < texture.width && y >= 0 && y < texture.height) {
			return true;
		} else {
			return false;
		}
		
	}
	
	protected boolean isOpaque(int color)
	{
		int[] rgba = {0x0, 0x0, 0x0, 0x0};
		ColorUtil.intToRGBA(color, rgba);
		
		if (rgba[3] < 255) {
			return false;
		} else {
			return true;
		}
	}
	
	protected IColor textureColor(double left, double front, boolean linear)
	{
		if (texture != null && linear) { // Linear Interpolation
			return texture.getColorLinear(left, front);
		} else if (texture != null && !linear) { // Nearest Neighbor
			return texture.getColorNearest(left, front);
		} else {
			return Colors.TRANSPARENT;
		}
	}
	
	
	
	
}
