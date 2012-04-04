package us.wthr.jdem846.rasterdata.bil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


public class Bil16IntDataReader
{
	private static Log log = Logging.getLog(Bil16IntDataReader.class);
	
	private File dataFile;
	private int rows;
	private int columns;
	private ByteOrder byteOrder;
	private boolean isDisposed = false;
	private int skipBytes = 0;
	
	private RandomAccessFile dataReader = null;
	
	private byte[] buffer2 = new byte[4];

	
	public Bil16IntDataReader(File dataFile, int rows, int columns, int skipBytes, ByteOrder byteOrder)
	{
		this.dataFile = dataFile;
		this.rows = rows;
		this.columns = columns;
		this.skipBytes = skipBytes;
		this.byteOrder = byteOrder;

	}
	
	
	public void dispose() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Raster data reader already disposed.");
		}
		
		
		
		// TODO: Finish
		isDisposed = true;
	}

	
	public boolean isDisposed()
	{
		return isDisposed;
	}
	

	
	public int get(int row, int column) throws DataSourceException
	{
		RandomAccessFile dataReader = getFileReader(row, column);
		
		try {
			dataReader.readFully(buffer2);
		} catch (IOException ex) {
			throw new DataSourceException("Failed to read bytes: " + ex.getMessage(), ex);
		}
		
		//int intBits = (((buffer2[1] & 0xFF) << 8) | (buffer2[0] & 0xFF));
		int intBits = bytesToInt(buffer2[0], buffer2[1]);
		return intBits;
	}
	
	public void get(int row, int startColumn, int[] buffer)  throws DataSourceException
	{
		if (row < 0 || row >= rows || startColumn < 0 || startColumn >= columns) {
			return;
		}
		
		
		RandomAccessFile dataReader = getFileReader(row, startColumn);
		if (dataReader == null) {
			log.info("Data reader is null... I should be throwing an exception here...");
		}
		
		
		int bufferLength = buffer.length;
		if (bufferLength + startColumn >= columns) {
			bufferLength = columns - startColumn;
		}
		
		int readLength = bufferLength * (16 / 8);
		byte[] readBuffer = new byte[readLength];
		
		try {
			dataReader.readFully(readBuffer);
		} catch (IOException ex) {
			throw new DataSourceException("Failed to read " + readLength + " bytes: " + ex.getMessage(), ex);
		}
		
		for (int i = 0; i < bufferLength; i++) {
			int readBufferPos = i * 2;
			//int intBits = (((readBuffer[readBufferPos+1] & 0xFF) << 8) | (readBuffer[readBufferPos] & 0xFF));
			int intBits = bytesToInt(readBuffer[readBufferPos], readBuffer[readBufferPos+1]);
			buffer[i] = intBits;
		}
		
	}
	
	public void get(int startColumn, int startRow, int[][] buffer)  throws DataSourceException
	{
		for (int i = 0; i < buffer.length; i++) {
			get(startRow+i, startColumn, buffer[i]);
		}
	}
	
	public void open() throws DataSourceException
	{
		if (dataReader == null) {
			try {
				dataReader = new RandomAccessFile(dataFile, "r");
			} catch (FileNotFoundException ex) {
				throw new DataSourceException("File Not Found error opening BIL16INT file for reading: " + ex.getMessage(), ex);
			}
		}
	}
	
	public void close() throws DataSourceException
	{
		closeFileReader();
	}
	
	protected void closeFileReader() throws DataSourceException
	{
		if (dataReader != null) {
			try {
				dataReader.close();
			} catch (IOException ex) {
				throw new DataSourceException("Failed to close file pointer: " + ex.getMessage(), ex);
			} finally {
				dataReader = null;
			}
		}
	}
	
	
	protected RandomAccessFile getFileReader(int row, int column) throws DataSourceException
	{
		
		if (row < 0 || row >= rows || column < 0 || column >= columns)
			return null;
		
		open();
		
		long pos = (row * columns) + column;
		long seekStart = skipBytes + (pos * (16 / 8));
		long length = 0;
		
		try {
			length = dataReader.length();
		} catch (IOException ex) {
			throw new DataSourceException("Failed to determine file length: " + ex.getMessage(), ex);
		}
		
		if (seekStart >= length) {
			//return null;
			throw new DataSourceException("Cannot seek past end of file (seek to: " + seekStart + ", length: " + length + ")");
		}
		
		
		
		try {
			dataReader.seek(seekStart);
		} catch (IOException ex) {
			throw new DataSourceException("Failed on seek to row/column " + row + "/" + column + ", position " + pos + " (seek to " + seekStart + "): " + ex.getMessage(), ex);
		}
		return dataReader;
	}
	
	
	public int bytesToInt(byte b00, byte b01)
	{
		int intBits = 0;
		if (byteOrder == ByteOrder.INTEL_OR_MOTOROLA || byteOrder == ByteOrder.LSBFIRST) {
			intBits = (((b00 & 0xFF) << 8) | (b01 & 0xFF));
		} else if (byteOrder == ByteOrder.MSBFIRST || byteOrder == ByteOrder.INTEL_BYTE_ORDER) {
			intBits = (((b01 & 0xFF) << 8) | (b00 & 0xFF));
		}
		
		
		return intBits;
	}
	
}
