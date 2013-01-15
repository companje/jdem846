package us.wthr.jdem846.buffers.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import us.wthr.jdem846.exception.BufferException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.TempFiles;

public class MMapBufferContainer
{
	private static Log log = Logging.getLog(MMapBufferContainer.class);
	
	protected MappedByteBuffer buffer;
	protected FileChannel channel;
	protected RandomAccessFile randomAccessFile;
	protected File file;
	
	protected boolean isOpen = false;
	
	public MMapBufferContainer(String path, int capacity)
	{
		this(new File(path), capacity);
	}
	
	public MMapBufferContainer(File file, int capacity)
	{
		this.file = file;
		
		log.info("Allocating mmap buffer of capacity " + capacity + " at location " + file.getAbsolutePath());
		
		try {
			randomAccessFile = new RandomAccessFile(file,"rw");
		} catch (FileNotFoundException ex) {
			throw new BufferException("Cannot create mmap buffer: File not found", ex);
		}
		
		channel = randomAccessFile.getChannel();
		
		try {
			buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, capacity);
		} catch (IOException ex) {
			throw new BufferException("Error mapping mmap buffer: " + ex.getMessage(), ex);
		}
		
		isOpen = true;
	}
	
	public void close()
	{
		if (!isOpen()) {
			throw new BufferException("Cannot close buffer: It's not open to begin with");
		}
		
		
		try {
			channel.close();
			channel = null;
			randomAccessFile.close();
			randomAccessFile = null;
		} catch (IOException ex) {
			throw new BufferException("Error closing mmap buffer: " + ex.getMessage(), ex);
		}
		
		TempFiles.releaseFile(file);
		
		buffer = null;

		isOpen = false;
	}
	
	
	
	
	public boolean isOpen()
	{
		return isOpen;
	}

	public MappedByteBuffer getBuffer()
	{
		return buffer;
	}
	
	
}
