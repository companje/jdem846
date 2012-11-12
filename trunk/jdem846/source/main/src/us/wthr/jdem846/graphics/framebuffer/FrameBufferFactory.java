package us.wthr.jdem846.graphics.framebuffer;

public class FrameBufferFactory {
	
	
	public static FrameBuffer createFrameBufferInstance(int width, int height, FrameBufferModeEnum bufferMode)
	{
		FrameBuffer buffer = null;
		
		if (bufferMode == FrameBufferModeEnum.STANDARD) {
			buffer = new StandardFrameBuffer(width, height);
		} else if (bufferMode == FrameBufferModeEnum.BINARY_SPACE_PARTITIONING) {
			buffer = new BinarySpacePartitioningFrameBuffer(width, height);
		} else if (bufferMode == FrameBufferModeEnum.CONCURRENT_PARTIAL_FRAME_BUFFER) {
			buffer = new ConcurrentPartialFrameBuffer(width, height);
		} else {
			
		}
		
		return buffer;
		
	}
	
}
