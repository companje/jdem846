package us.wthr.jdem846.graphics.framebuffer;

import us.wthr.jdem846.graphics.ImageCapture;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ManagedConcurrentFrameBufferController extends Thread
{
	private static Log log = Logging.getLog(ManagedConcurrentFrameBufferController.class);
	
	private long pollDelay = 200;
	private boolean finish = false;
	
	private Long mutex = new Long(0);
	
	private BinarySpacePartitioningFrameBuffer bspBuffer;
	private ConcurrentPartialFrameBuffer[] partialBuffers;
	
	public ManagedConcurrentFrameBufferController(int width, int height)
	{
		this(width, height, 1, 200);
	}
	
	
	public ManagedConcurrentFrameBufferController(int width, int height, int numThreads)
	{
		this(width, height, numThreads, 200);
	}
	
	public ManagedConcurrentFrameBufferController(int width, int height, int numThreads, long pollDelay)
	{
		
		bspBuffer = new BinarySpacePartitioningFrameBuffer(width, height);
		partialBuffers = new ConcurrentPartialFrameBuffer[numThreads];
		
		for (int i = 0; i < numThreads; i++) {
			partialBuffers[i] = new ConcurrentPartialFrameBuffer(width, height);
		}
		
	}
	
	@Override
	public void run()
	{
		
		log.info("Starting managed frame buffer controller");
		while (!finish) {
			
			try {
				Thread.sleep(pollDelay);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			
			collectPoints();

		}
		
		log.info("Exiting managed frame buffer controller");
	}
	
	public void collectPoints()
	{
		synchronized(mutex) {
			for (ConcurrentPartialFrameBuffer partialBuffer : partialBuffers) {
				partialBuffer.loadBinarySpacePartitioningFrameBuffer(this.bspBuffer, true);
			}
		}
	}
	
	public void finish()
	{
		finish = true;
		log.info("Managed frame buffer controller requested to finish up");
	}
	
	public boolean isFinished()
	{
		return finish;
	}
	
	public ConcurrentPartialFrameBuffer getPartialBuffer(int threadNumber)
	{
		if (threadNumber < 0 || threadNumber >= partialBuffers.length) {
			return null;
		} else {
			return partialBuffers[threadNumber];
		}
	}
	
	
	public ImageCapture captureImage()
	{
		return captureImage(0x0);
	}
	
	public ImageCapture captureImage(int background)
	{
		synchronized(mutex) {
			return this.bspBuffer.captureImage(background);
		}
	}
	
}
