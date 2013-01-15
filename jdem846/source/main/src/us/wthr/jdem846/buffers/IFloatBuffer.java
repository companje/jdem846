package us.wthr.jdem846.buffers;

public interface IFloatBuffer extends IBuffer<Float>
{
	public void put(float[] values, long startIndex, int offset, int count);
}
