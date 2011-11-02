package us.wthr.jdem846.rasterdata.gridfloat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.ByteConversions;

public class GridFloatDataReader
{
	private static Log log = Logging.getLog(GridFloatDataReader.class);
	
	private File dataFile;
	private int rows;
	private int columns;
	private ByteOrder byteOrder;
	private boolean isDisposed = false;
	
	private RandomAccessFile dataReader = null;
	
	private byte[] buffer4 = {0, 0, 0, 0};
	
	
	public GridFloatDataReader(File dataFile, int rows, int columns, ByteOrder byteOrder)
	{
		this.dataFile = dataFile;
		this.rows = rows;
		this.columns = columns;
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
	
	public double get(int row, int column) throws DataSourceException
	{
		RandomAccessFile dataReader = getFileReader(row, column);
		
		try {
			dataReader.readFully(buffer4);
		} catch (IOException ex) {
			throw new DataSourceException("Failed to read bytes: " + ex.getMessage(), ex);
		}
		
		float value = ByteConversions.bytesToFloat(buffer4[0], buffer4[1], buffer4[2], buffer4[3], byteOrder);

		return value;
	}
	
	public void get(int row, int startColumn, double[] buffer)  throws DataSourceException
	{
		RandomAccessFile dataReader = getFileReader(row, startColumn);
		
		int bufferLength = buffer.length;
		if (bufferLength + startColumn > columns) {
			bufferLength = columns - startColumn;
		}
		
		int readLength = bufferLength * (Float.SIZE / 8);
		byte[] readBuffer = new byte[readLength];
		
		try {
			dataReader.readFully(readBuffer);
		} catch (IOException ex) {
			throw new DataSourceException("Failed to read " + readLength + " bytes: " + ex.getMessage(), ex);
		}
		
		for (int i = 0; i < bufferLength; i++) {
			int readBufferPos = i * 4;
			float value = ByteConversions.bytesToFloat(readBuffer[readBufferPos], readBuffer[readBufferPos+1], readBuffer[readBufferPos+2], readBuffer[readBufferPos+3], byteOrder);
			buffer[i] = value;
		}
		
	}
	
	public void get(int startRow, int startColumn, double[][] buffer)  throws DataSourceException
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
				throw new DataSourceException("File Not Found error opening GridFloat file for reading: " + ex.getMessage(), ex);
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
		open();
		
		long pos = (row * columns) + column;
		long seekStart = (long) pos * (long) (Float.SIZE / 8);
		
		try {
			long length = dataReader.length();
			if (seekStart >= length) {
				throw new DataSourceException("Cannot seek past end of file (seek to: " + seekStart + ", length: " + length + ")");
			}
		} catch (IOException ex) {
			throw new DataSourceException("Failed to determine file length: " + ex.getMessage(), ex);
		}
		
		try {
			dataReader.seek(seekStart);
		} catch (IOException ex) {
			throw new DataSourceException("Failed on seek to row/column " + row + "/" + column + ", position " + pos + " (seek to " + seekStart + "): " + ex.getMessage(), ex);
		}
		return dataReader;
	}
}
