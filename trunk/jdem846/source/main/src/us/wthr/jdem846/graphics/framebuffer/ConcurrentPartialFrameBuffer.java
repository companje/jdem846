package us.wthr.jdem846.graphics.framebuffer;

import us.wthr.jdem846.graphics.ImageCapture;
import us.wthr.jdem846.util.ColorUtil;

public class ConcurrentPartialFrameBuffer extends AbstractFrameBuffer implements FrameBuffer
{
	
	private Long mutex = new Long(0);
	
	private IndexedBufferPoint top = null;
	
	private int background = 0x0;
	private boolean setBackground = false;
	
	public ConcurrentPartialFrameBuffer(int width, int height)
	{
		super(width, height);
	}

	
	public void merge(ConcurrentPartialFrameBuffer other)
	{
		synchronized(mutex) {
			if (this.top == null) {
				top = other.top;
			} else {
				top.addLeaf(other.top);
			}
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
		synchronized(mutex) {
			this.setBackground = setBackground;
			this.background = background;
			this.top = null;
		}
	}
	
	@Override
	public void set(int x, int y, BufferPoint point) 
	{
		synchronized(mutex) {
			if (top == null) {
				top = (IndexedBufferPoint) point;
			} else {
				top.addLeaf(point);
			}
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
	
	/** Loads the BSP framebuffer in synchronous mode.
	 * 
	 * @param frameBuffer The framebuffer to load
	 */
	public void loadBinarySpacePartitioningFrameBuffer(FrameBuffer frameBuffer)
	{
		loadBinarySpacePartitioningFrameBuffer(frameBuffer, false);
	}
	
	/** Loads the framebuffer synchronously or asychronously.
	 * 
	 * @param frameBuffer The framebuffer to load
	 * @param asynchWithReset Optionaly reset the tree and use the snapshot to load the framebuffer asynchronously to the graphics rendering
	 * thread.
	 */
	public void loadBinarySpacePartitioningFrameBuffer(FrameBuffer frameBuffer, boolean asynchWithReset)
	{	
		if (asynchWithReset) {
			loadBinarySpacePartitioningFrameBufferASync(frameBuffer);
		} else {
			loadBinarySpacePartitioningFrameBufferSync(frameBuffer);
		}
		
		
	}
	
	/** Loads all the points into the framebuffer. Does not reset the top of the tree, so it does the
	 * operations synchronously to prevent changes to the tree in mid flight.
	 * 
	 * @param frameBuffer The framebuffer to load
	 */
	protected void loadBinarySpacePartitioningFrameBufferSync(FrameBuffer frameBuffer)
	{	
		synchronized(mutex) {
			addPointsToFrameBuffer(top, frameBuffer);
		}
	}
	
	/** Loads all the points into the framebuffer. This will reset the tree to null and work with the snapshot 
	 * asynchronously while other threads are able to begin working. 
	 * 
	 * @param frameBuffer The framebuffer to load
	 */
	protected void loadBinarySpacePartitioningFrameBufferASync(FrameBuffer frameBuffer)
	{	
		IndexedBufferPoint point = null;
		synchronized(mutex) {
			point = top;
			top = null;
		}
		
		if (point != null) {
			addPointsToFrameBuffer(point, frameBuffer);
		}
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
		return captureImage(0x0);
	}
	
	@Override
	public ImageCapture captureImage(int backgroundColor)
	{
		int width = getWidth();
		int height = getHeight();
		ImageCapture image = new ImageCapture(width, height, backgroundColor);
		
		if (setBackground) {
			for (int y = 0; y < getHeight(); y++) {
				for (int x = 0; x < getWidth(); x++) {
					image.set(x, y, background);
				}
			}
		}
		
		synchronized(mutex) {
			capturePoint(image, top);
		}
		return image;
	}
	

}
