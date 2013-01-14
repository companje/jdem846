package us.wthr.jdem846.buffers;

import us.wthr.jdem846.buffers.impl.HighCapacityMappedByteBuffer;
import us.wthr.jdem846.buffers.impl.HighCapacityMappedDoubleBuffer;
import us.wthr.jdem846.buffers.impl.HighCapacityMappedFloatBuffer;
import us.wthr.jdem846.buffers.impl.HighCapacityMappedIntBuffer;

public class BufferFactory
{
	
	public static IByteBuffer allocateByteBuffer(long capacity)
	{
		IByteBuffer buffer = new HighCapacityMappedByteBuffer(capacity);
		return buffer;
	}
	
	
	public static IIntBuffer allocateIntBuffer(long capacity)
	{
		IIntBuffer buffer = new HighCapacityMappedIntBuffer(capacity);
		return buffer;
	}
	
	public static IFloatBuffer allocateFloatBuffer(long capacity)
	{
		IFloatBuffer buffer = new HighCapacityMappedFloatBuffer(capacity);
		return buffer;
	}
	
	public static IDoubleBuffer allocateDoubleBuffer(long capacity)
	{
		IDoubleBuffer buffer = new HighCapacityMappedDoubleBuffer(capacity);
		return buffer;
	}
}
