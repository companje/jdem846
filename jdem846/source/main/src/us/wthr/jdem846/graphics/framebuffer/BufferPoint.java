package us.wthr.jdem846.graphics.framebuffer;

public class BufferPoint {
	
	public int rgba = 0;
	public double z = 0;
	
	public BufferPoint left = null;
	public BufferPoint right = null;
	
	public BufferPoint()
	{
		
	}
	
	public BufferPoint(int rgba, double z)
	{
		this.rgba = rgba;
		this.z = z;
	}
	
	public boolean isOpaque()
	{
		return (0xFF & (rgba >>> 24)) == 0xFF;
	}
	
}
