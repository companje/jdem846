package us.wthr.jdem846.buffers;

public interface IDoubleBuffer extends IBuffer<Double>
{
	public void put(double[] values, long startIndex, int offset, int count);
}
