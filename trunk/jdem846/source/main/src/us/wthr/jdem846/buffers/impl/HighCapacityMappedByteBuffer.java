package us.wthr.jdem846.buffers.impl;

import java.io.File;
import java.io.IOException;

import us.wthr.jdem846.buffers.IByteBuffer;
import us.wthr.jdem846.exception.BufferException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.util.TempFiles;

public class HighCapacityMappedByteBuffer implements IByteBuffer
{
	private static Log log = Logging.getLog(HighCapacityMappedByteBuffer.class);
	
	protected static long MAX_CHUNK_CAPACITY = 1073741824; // 1GB
	
	private long capacity;
	private int chunkCount = 0;
	
	private MMapBufferContainer[] buffers;
	
	protected boolean isOpen = false;
	

	
	public HighCapacityMappedByteBuffer(long capacity)
	{
		this.capacity = capacity;
		
		chunkCount = (int) MathExt.ceil((double)capacity / (double)MAX_CHUNK_CAPACITY);
		
		buffers = new MMapBufferContainer[chunkCount];
		
		long allocatesNeeded = capacity;
		
		int chunkNum = 0;
		while (allocatesNeeded > 0) {
			int chunkSize = (int)MathExt.min(allocatesNeeded, MAX_CHUNK_CAPACITY);
			
			MMapBufferContainer chunk = null;
			
			try {
				chunk = createMappedByteBuffer(chunkSize);
			} catch (IOException ex) {
				throw new BufferException("Failed to create memory mapped buffer: " + ex.getMessage(), ex);
			}
			
			buffers[chunkNum] = chunk;
			
			chunkNum++;
			allocatesNeeded = allocatesNeeded - chunkSize;
		}
		
		isOpen = true;
		
	}
	
	
	
	protected MMapBufferContainer createMappedByteBuffer(int capacity) throws IOException
	{
		File temp = TempFiles.getTemporaryFile("jdem-mmap");
		MMapBufferContainer mmap = new MMapBufferContainer(temp, capacity);
		return mmap;
	}
	
	@Override
	public void close()
	{
		if (!isOpen()) {
			throw new BufferException("Cannot close buffer: Not open to begin with");
		}
		
		for (MMapBufferContainer mmap : buffers) {
			mmap.close();
		}
		
		buffers = null;
		
		isOpen = false;
	}
	
	@Override
	public boolean isOpen()
	{
		return isOpen;
	}
	
	
	protected int getIndexOfChunk(long byteIndex)
	{
		int index = (int) MathExt.floor(byteIndex / MAX_CHUNK_CAPACITY);
		return index;
	}
	
	protected int getIndexWithinChunk(long byteIndex) 
	{
		int index = (int) (byteIndex % MAX_CHUNK_CAPACITY);
		return index;
	}
	
	protected MMapBufferContainer getBuffer(long index)
	{
		if (!isOpen()) {
			throw new BufferException("Cannot fetch value: Buffer is closed");
		}
		
		if (index < 0 || index >= this.capacity) {
			throw new BufferException("Index out of bounds: " + index + " (capacity is " + capacity + " bytes)");
		}
		
		int chunkNumber = getIndexOfChunk(index);
		return buffers[chunkNumber];
	}
	
	@Override
	public Byte get(long index)
	{
		MMapBufferContainer buffer = getBuffer(index);
		int indexWithinChunk = getIndexWithinChunk(index);
		return buffer.getBuffer().get(indexWithinChunk);
	}

	@Override
	public void put(long index, Byte value)
	{
		MMapBufferContainer buffer = getBuffer(index);
		int indexWithinChunk = getIndexWithinChunk(index);
		
		buffer.getBuffer().put(indexWithinChunk, value);
	}
	
	@Override
	public void put(byte[] values, long startIndex, int offset, int count)
	{
		// Really Slow & inefficient
		for (int i = 0; i < count; i++) {
			put(startIndex + i, values[offset + i]);
		}
	}
	
	@Override
	public void put(Byte[] values, long startIndex, int offset, int count)
	{
		// Really Slow & inefficient
		for (int i = 0; i < count; i++) {
			put(startIndex + i, values[offset + i]);
		}
	}

	@Override
	public long capacity()
	{
		return capacity;
	}
	
	@Override
	public long capacityBytes()
	{
		return capacity();
	}
	
	
	@Override
	public void dispose()
	{
		this.close();
	}
	
	@Override
	public IByteBuffer duplicate()
	{
		return null;
	}

}
