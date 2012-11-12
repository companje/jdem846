package us.wthr.jdem846.graphics.framebuffer;

import us.wthr.jdem846.math.MathExt;

public class StandardFrameBuffer extends AbstractFrameBuffer implements FrameBuffer
{


	protected BufferPoint[][] buffer = null;
	
	
	public StandardFrameBuffer(int width, int height)
	{
		super(width, height);
		buffer = new BufferPoint[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				buffer[y][x] = new BufferPoint(0x0, FrameBuffer.FB_MINIMUM_Z_INDEX);
			}
		}
	}

	
	protected boolean isValidCoords(double x, double y)
	{
		if (x < 0 || x >= this.width || y < 0 || y >= this.height)
			return false;
		else
			return true;
	}

	
	
	public boolean isVisible(int x, int y, double z, int rgba) 
	{
		if ((0xFF & (rgba >>> 24)) == 0x0) {
			return false;
		}
		
		if (!isValidCoords(x, y)) {
			return false;
		}

		
		if (this.buffer[y][x] != null && this.buffer[y][x].z > z) {
			return false;
		}
		
		return true;
		
	}
	
	@Override
	public boolean isVisible(double x, double y, double z, int rgba) 
	{
		return isVisible((int)MathExt.floor(x), (int)MathExt.floor(y), z, rgba);
		
	}


	@Override
	public void reset(boolean setBackground, int background) 
	{
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (setBackground) {
					buffer[y][x] = new BufferPoint(background, FrameBuffer.FB_MINIMUM_Z_INDEX);
				} else {
					buffer[y][x].z = FrameBuffer.FB_MINIMUM_Z_INDEX;
					buffer[y][x].rgba = 0x0;
				}
				
			}
		}

	}

	
	@Override
	public void set(int x, int y, BufferPoint point) 
	{
		if (isValidCoords(x, y)) {
			buffer[y][x] = point;
		}
		
	}
	
	@Override
	public void set(int x, int y, double z, int rgba) 
	{
		if (!isVisible(x, y, z, rgba)) {
			return;
		} else {
			set(x, y, new BufferPoint(rgba, z));
		}

	}

	@Override
	public void set(double x, double y, double z, int rgba) 
	{
		if (!isVisible(x, y, z, rgba)) {
			return;
		}
		
		set((int)MathExt.floor(x), (int)MathExt.floor(y), z, rgba);
	}


	public int get(int x, int y) 
	{
		if (!this.isValidCoords(x, y)) {
			return 0x0;
		}
		
		if (buffer[y][x] != null) {
			return buffer[y][x].rgba;
		} else {
			return 0x0;
		}
		
	}
	
	
	@Override
	public int get(double x, double y) 
	{
		return get((int)MathExt.floor(x), (int)MathExt.floor(y));
	}


	

	
	
	
	
}
