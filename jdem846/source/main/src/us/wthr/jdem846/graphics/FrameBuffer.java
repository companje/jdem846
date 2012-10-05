package us.wthr.jdem846.graphics;

import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846.math.MathExt;

public class FrameBuffer 
{
	protected static final double FB_MINIMUM_Z_INDEX = -9999999999.99;
	
	protected int width;
	protected int height;
	protected int bufferLength;
	protected BufferPoint[] buffer = null;
	
	public FrameBuffer(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.bufferLength = width * height;
		buffer = new BufferPoint[bufferLength];
		for (int i = 0; i < bufferLength; i++) {
			buffer[i] = null;
		}
	}
	
	
	protected int index(double x, double y)
	{
		return index((int) MathExt.floor(x), (int) MathExt.floor(y));
	}
	
	protected int index(int x, int y)
	{
		if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
			return -1;
		}
		
		int i = (y * width) + x;
		return i;
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
	
	
	public void reset()
	{
		reset(false, -1);
	}
	
	public void reset(boolean setBackground, int background)
	{
		for (int i = 0; i < this.bufferLength; i++) {
			this.buffer[i] = null;
			
			if (setBackground) {
				this.buffer[i] = new BufferPoint(background, FB_MINIMUM_Z_INDEX);
			}
			
		}
	}
	
	protected void set(BufferPoint point, BufferPoint addPoint)
	{
		if (point == null || addPoint == null) {
			return;
		}
		
		if (addPoint.z < point.z) {
			if (point.left == null) {
				point.left = addPoint;
			} else {
				this.set(point.left, addPoint);
			}
		} else {
			
			if (point.right == null) {
				point.right = addPoint;
			} else {
				this.set(point.right, addPoint);
			}
			
		}
		
	}
	
	
	public void set(double x, double y, double z, int rgba)
	{
		if (!this.isVisible(x, y, z, rgba)) {
			return;
		}
		
		int idx = this.index(x, y);
		
		if (idx < 0 || idx >= this.bufferLength || this.buffer == null) {
			return;
		}
		
		BufferPoint point = new BufferPoint(rgba, z);
		if (this.buffer[idx] == null) {
			this.buffer[idx] = point;
		} else {
			this.set(this.buffer[idx], point);
		}
		
	}
	
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
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
}
