package us.wthr.jdem846.graphics.framebuffer;

import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846.graphics.ImageCapture;

public class ConcurrentPartialFrameBuffer extends AbstractFrameBuffer implements FrameBuffer
{

	private IndexedBufferPoint top = null;
	
	private int background = 0x0;
	private boolean setBackground = false;
	
	public ConcurrentPartialFrameBuffer(int width, int height)
	{
		super(width, height);
	}

	
	public void merge(ConcurrentPartialFrameBuffer other)
	{
		if (this.top == null) {
			top = other.top;
		} else {
			top.addLeaf(other.top);
		}
	}
	
	@Override
	public boolean isVisible(double x, double y, double z, int rgba) 
	{
		return true;
	}

	@Override
	public void reset(boolean setBackground, int background) 
	{
		this.setBackground = setBackground;
		this.background = background;
		this.top = null;
	}
	
	@Override
	public void set(int x, int y, BufferPoint point) 
	{
		if (top == null) {
			top = (IndexedBufferPoint) point;
		} else {
			top.addLeaf(point);
		}
	}
	
	@Override
	public void set(int x, int y, double z, int rgba)
	{
		IndexedBufferPoint bp = new IndexedBufferPoint(rgba, x, y, z);
		set(x, y, bp);
	}
	
	public IndexedBufferPoint getTop()
	{
		return top;
	}

	@Override
	public int get(double x, double y) 
	{
		
		return 0;
	}
	
	
	protected void addPointsToFrameBuffer(IndexedBufferPoint top, FrameBuffer frameBuffer)
	{
		if (top == null || frameBuffer == null) {
			return;
		}
		
		
		
		if (top.left != null) {
			addPointsToFrameBuffer((IndexedBufferPoint)top.left, frameBuffer);
		}
		
		frameBuffer.set(top.x, top.y, top.z, top.rgba);
		
		if (top.right != null) {
			addPointsToFrameBuffer((IndexedBufferPoint)top.right, frameBuffer);
		}
		
		
	}
	
	public void loadBinarySpacePartitioningFrameBuffer(BinarySpacePartitioningFrameBuffer frameBuffer)
	{
		addPointsToFrameBuffer(top, frameBuffer);
	}
	
	
	public BinarySpacePartitioningFrameBuffer toBinarySpacePartitioningFrameBuffer()
	{
		BinarySpacePartitioningFrameBuffer bsp = new BinarySpacePartitioningFrameBuffer(this.getWidth(), this.getHeight());
		
		loadBinarySpacePartitioningFrameBuffer(bsp);
		
		return bsp;
	}

	
	protected void capturePoint(ImageCapture image, IndexedBufferPoint top)
	{
		
		if (top == null || image == null) {
			return;
		}
		
		
		
		
		if (top.left != null) {
			capturePoint(image, (IndexedBufferPoint)top.left);
		}
		
		int pointRgba = top.rgba;
		int imageRgba = image.get(top.x, top.y);
		
		int rgba = ColorUtil.overlayColor(pointRgba, imageRgba);
		image.set(top.x, top.y, rgba);
		
		if (top.right != null) {
			capturePoint(image, (IndexedBufferPoint)top.right);
		}
		
	}
	
	@Override
	public ImageCapture captureImage()
	{
		int width = getWidth();
		int height = getHeight();
		ImageCapture image = new ImageCapture(width, height);
		
		if (setBackground) {
			for (int y = 0; y < getHeight(); y++) {
				for (int x = 0; x < getWidth(); x++) {
					image.set(x, y, background);
				}
			}
		}
		
		
		capturePoint(image, top);
		return image;
	}
	

}
