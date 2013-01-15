package us.wthr.jdem846.buffers;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.buffers.impl.HighCapacityMappedByteBuffer;
import us.wthr.jdem846.buffers.impl.HighCapacityMappedDoubleBuffer;
import us.wthr.jdem846.buffers.impl.HighCapacityMappedFloatBuffer;
import us.wthr.jdem846.buffers.impl.HighCapacityMappedIntBuffer;
import us.wthr.jdem846.buffers.impl.HighCapacityMappedLongBuffer;
import us.wthr.jdem846.buffers.impl.StandardCapacityByteBuffer;
import us.wthr.jdem846.buffers.impl.StandardCapacityDoubleBuffer;
import us.wthr.jdem846.buffers.impl.StandardCapacityFloatBuffer;
import us.wthr.jdem846.buffers.impl.StandardCapacityIntBuffer;
import us.wthr.jdem846.buffers.impl.StandardCapacityLongBuffer;

/** Allocates buffers. Determines memory conditions and requirements and allocate
 * either the mmapped buffers or (in)direct java.nio.* buffers.
 * 
 * @author Kevin M. Gill <kmsmgill@gmail.com>
 *
 */
public class BufferFactory
{
	
	public static IByteBuffer allocateByteBuffer(long capacity)
	{
		IByteBuffer buffer = null;
		if (capacity <= getMaximumHeapBufferSizeBytes()) {
			buffer = allocateStandardCapacityByteBuffer((int)capacity);
		} else {
			buffer = allocateHighCapacityByteBuffer(capacity);
		}
		return buffer;
	}
	
	
	public static IIntBuffer allocateIntBuffer(long capacity)
	{
		IIntBuffer buffer = null;
		if ((capacity * (Integer.SIZE / 8)) <= getMaximumHeapBufferSizeBytes()) {
			buffer = allocateStandardCapacityIntBuffer((int)capacity);
		} else {
			buffer = allocateHighCapacityIntBuffer(capacity);
		}
		return buffer;
	}
	
	public static IFloatBuffer allocateFloatBuffer(long capacity)
	{
		IFloatBuffer buffer = null;
		if ((capacity * (Float.SIZE / 8)) <= getMaximumHeapBufferSizeBytes()) {
			buffer = allocateStandardCapacityFloatBuffer((int)capacity);
		} else {
			buffer = allocateHighCapacityFloatBuffer(capacity);
		}
		return buffer;
	}
	
	public static IDoubleBuffer allocateDoubleBuffer(long capacity)
	{
		IDoubleBuffer buffer = null;
		if ((capacity * (Double.SIZE / 8)) <= getMaximumHeapBufferSizeBytes()) {
			buffer = allocateStandardCapacityDoubleBuffer((int)capacity);
		} else {
			buffer = allocateHighCapacityDoubleBuffer(capacity);
		}
		return buffer;
	}
	
	public static ILongBuffer allocateLongBuffer(long capacity)
	{
		ILongBuffer buffer = null;
		if ((capacity * (Long.SIZE / 8)) <= getMaximumHeapBufferSizeBytes()) {
			buffer = allocateStandardCapacityLongBuffer((int)capacity);
		} else {
			buffer = allocateHighCapacityLongBuffer(capacity);
		}
		return buffer;
	}
	
	
	//////////// Standard Capacity Allocators
	
	public static IByteBuffer allocateStandardCapacityByteBuffer(int capacity)
	{
		IByteBuffer buffer = new StandardCapacityByteBuffer(capacity);
		return buffer;
	}
	
	
	public static IIntBuffer allocateStandardCapacityIntBuffer(int capacity)
	{
		IIntBuffer buffer = new StandardCapacityIntBuffer(capacity);
		return buffer;
	}
	
	public static IFloatBuffer allocateStandardCapacityFloatBuffer(int capacity)
	{
		IFloatBuffer buffer = new StandardCapacityFloatBuffer(capacity);
		return buffer;
	}
	
	public static IDoubleBuffer allocateStandardCapacityDoubleBuffer(int capacity)
	{
		IDoubleBuffer buffer = new StandardCapacityDoubleBuffer(capacity);
		return buffer;
	}
	
	public static ILongBuffer allocateStandardCapacityLongBuffer(int capacity)
	{
		ILongBuffer buffer = new StandardCapacityLongBuffer(capacity);
		return buffer;
	}
	
	
	
	//////////// High Capacity Allocators
	public static IByteBuffer allocateHighCapacityByteBuffer(long capacity)
	{
		IByteBuffer buffer = new HighCapacityMappedByteBuffer(capacity);
		return buffer;
	}
	
	
	public static IIntBuffer allocateHighCapacityIntBuffer(long capacity)
	{
		IIntBuffer buffer = new HighCapacityMappedIntBuffer(capacity);
		return buffer;
	}
	
	public static IFloatBuffer allocateHighCapacityFloatBuffer(long capacity)
	{
		IFloatBuffer buffer = new HighCapacityMappedFloatBuffer(capacity);
		return buffer;
	}
	
	public static IDoubleBuffer allocateHighCapacityDoubleBuffer(long capacity)
	{
		IDoubleBuffer buffer = new HighCapacityMappedDoubleBuffer(capacity);
		return buffer;
	}
	
	public static ILongBuffer allocateHighCapacityLongBuffer(long capacity)
	{
		ILongBuffer buffer = new HighCapacityMappedLongBuffer(capacity);
		return buffer;
	}
	
	
	
	protected static long getMaximumHeapBufferSizeBytes()
	{
		return JDem846Properties.getIntProperty("us.wthr.jdem846.general.buffers.maxHeapBufferSizeBytes");
	}
}
