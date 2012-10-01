package us.wthr.jdem846.graphics;

import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.math.MathExt;

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
	
	protected int textureColor(double left, double front, boolean linear)
	{
		if (texture == null) {
			return 0x0;
		}
		
		
		if (linear) {
			
			double x = (left * (double)texture.width);
			double y = (front * (double)texture.height); 
			
			double _x = MathExt.floor(x);
			double _y = MathExt.floor(y);
			
			double xFrac = x - _x;
			double yFrac = y - _y;
			
			boolean b00 = isValidIndex(_x + 0, _y + 0);
			boolean b01 = isValidIndex(_x + 1, _y + 0);
			boolean b10 = isValidIndex(_x + 0, _y + 1);
			boolean b11 = isValidIndex(_x + 1, _y + 1);
			
			int c00 = (b00) ? texture.texture[(int)MathExt.round((_y * (double)texture.width) + _x)] : 0x0;
			int c01 = (b01) ? texture.texture[(int)MathExt.round((_y * (double)texture.width) + (_x + 1))] : 0x0;
			int c10 = (b10) ? texture.texture[(int)MathExt.round(((_y + 1) * (double)texture.width) + _x)] : 0x0;
			int c11 = (b11) ? texture.texture[(int)MathExt.round(((_y + 1) * (double)texture.width) + (_x + 1))] : 0x0;
			
			c00 = getValidColor(c00, b00, c01, b01, c10, b10, c11, b11);
			c01 = getValidColor(c01, b01, c00, b00, c11, b11, c10, b10);
			c10 = getValidColor(c10, b10, c11, b11, c00, b00, c01, b01);
			c11 = getValidColor(c11, b11, c10, b10, c01, b01, c00, b00);
			
			return ColorAdjustments.interpolateColor(c00, c01, c10, c11, xFrac, yFrac);
			
		} else { // Nearest Neighbor
			
			int x = (int) MathExt.round(left * (double)texture.width);
			int y = (int) MathExt.round(front * (double)texture.height);
			
			int idx = (y * texture.width) + x;
			
			if (idx < 0 || idx >= texture.texture.length) {
				return 0x0;
			} else {
				return texture.texture[idx];
			}
			
			
			
			
		}
		
		
	}
	
	
	
	
}
