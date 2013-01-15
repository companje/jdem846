package us.wthr.jdem846.buffers;


public interface IByteBuffer extends IBuffer<Byte>
{
	public void put(byte[] values, long startIndex, int offset, int count);
}
