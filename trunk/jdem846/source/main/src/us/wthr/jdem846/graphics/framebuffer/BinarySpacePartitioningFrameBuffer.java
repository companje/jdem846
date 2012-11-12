package us.wthr.jdem846.graphics.framebuffer;

import us.wthr.jdem846.canvas.util.ColorUtil;

public class BinarySpacePartitioningFrameBuffer extends AbstractFrameBuffer implements FrameBuffer
{
	
	
	
	protected BufferPoint[] buffer = null;
	
	public BinarySpacePartitioningFrameBuffer(int width, int height)
	{
		super(width, height);
		buffer = new BufferPoint[bufferLength];
		for (int i = 0; i < bufferLength; i++) {
			buffer[i] = null;
		}
	}
	
	

	
	
	
	
	protected int overlay(BufferPoint point, int rgba)
	{
		if (point == null) {
			return rgba;
		}
		
		if (point.left != null) {
			rgba = this.overlay(point.left, rgba);
		}
		
		if (point.z >= FB_MINIMUM_Z_INDEX) {
			//rgba = point.rgba;
			rgba = ColorUtil.overlayColor(point.rgba, rgba);
		}
		
		if (point.right != null) {
			rgba = this.overlay(point.right, rgba);
		}
		
		return rgba;
	}
	
	protected boolean isVisible(double z, int rgba, BufferPoint point)
	{
		if (point == null) {
			return true;
		}
		
		if (point.z == z && rgba == point.rgba) {
			return false;
		}
		
		if (point.z < z) {
			return this.isVisible(z, rgba, point.right);
		}
		
		if (point.z > z && (0xFF & (rgba >>> 24)) == 0xFF) {
			return false;
		} else {
			return this.isVisible(z, rgba, point.left);
		}
		
	}
	
	@Override
	public boolean isVisible(double x, double y, double z, int rgba)
	{
		if ((0xFF & (rgba >>> 24)) == 0x0) {
			return false;
		}
		
		int idx = this.index(x, y);
		if (idx < 0 || idx >= this.bufferLength || this.buffer == null) {
			return false;
		}
		
		return this.isVisible(z, rgba, this.buffer[idx]);
		
		
	}
	
	
	
	@Override
	public void reset(boolean setBackground, int background)
	{
		for (int i = 0; i < this.bufferLength; i++) {
			this.buffer[i] = null;
			
			if (setBackground) {
				this.buffer[i] = new BufferPoint(background, FB_MINIMUM_Z_INDEX);
			}
			
		}
	}

	
	@Override
	public void set(int x, int y, BufferPoint point) 
	{
		
		int idx = this.index(x, y);
		
		if (idx < 0 || idx >= this.bufferLength || this.buffer == null) {
			return;
		}
		
		if (this.buffer[idx] == null) {
			this.buffer[idx] = point;
		} else {
			this.buffer[idx].addLeaf(point);
		}
	}
	
	
	
	@Override
	public void set(int x, int y, double z, int rgba)
	{
		if (!this.isVisible(x, y, z, rgba)) {
			return;
		}

		set(x, y, new BufferPoint(rgba, z));
	}
	
	@Override
	public int get(double x, double y)
	{
		int idx = this.index(x, y);
		if (idx < 0 || idx >= this.bufferLength || this.buffer == null) {
			return 0x0;
		}
		
		if (this.buffer[idx] == null) {
			return 0x0;
		}
		
		int rgba = this.overlay(this.buffer[idx], ColorUtil.BLACK);

		return rgba;
	}







	
	
	
}
