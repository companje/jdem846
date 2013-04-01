package us.wthr.jdem846.rasterdata.generic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.BytesUtil;

public class GenericRasterDataReader
{
	private static Log log = Logging.getLog(GenericRasterDataReader.class);

	private File dataFile;
	private IRasterDefinition rasterDefinition;

	private boolean isDisposed = false;

	private RandomAccessFile dataReader = null;

	private byte[] lineBuffer;

	private byte[] buffer8 = new byte[8];
	
	public GenericRasterDataReader(File dataFile, IRasterDefinition rasterDefinition)
	{
		this.dataFile = dataFile;
		this.rasterDefinition = rasterDefinition;
		if (rasterDefinition != null) {
			this.rasterDefinition.addDefinitionChangeListener(new DefinitionChangeListener()
			{
	
				@Override
				public void onDefinitionChanged(IRasterDefinition rasterDefinition)
				{
					definitionChanged();
				}
	
			});
		}
	}

	protected void definitionChanged()
	{
		
	}

	public void dispose() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Raster data reader already disposed.");
		}

		lineBuffer = null;

		// TODO: Finish
		isDisposed = true;
	}

	public boolean isDisposed()
	{
		return isDisposed;
	}

	public Number get(int row, int column) throws DataSourceException
	{
		RandomAccessFile dataReader = getFileReader(row, column);

		Number value = null;

		try {
			value = readSingleValue(dataReader);
		} catch (IOException ex) {
			throw new DataSourceException("Error reading data value: " + ex.getMessage(), ex);
		}

		return value;
	}

	public void get(int row, int startColumn, Number[] buffer) throws DataSourceException
	{
		if (row < 0 || row >= rasterDefinition.getImageHeight() || startColumn < 0 || startColumn >= rasterDefinition.getImageWidth()) {
			return;
		}

		RandomAccessFile dataReader = getFileReader(row, startColumn);
		if (dataReader == null) {
			log.info("Data reader is null... I should be throwing an exception here...");
		}

		try {
			readLine(dataReader, startColumn, buffer);
		} catch (IOException ex) {
			throw new DataSourceException("Error reading from data file: " + ex.getMessage(), ex);
		}

	}

	public void get(int startColumn, int startRow, Number[][] buffer) throws DataSourceException
	{
		for (int i = 0; i < buffer.length; i++) {
			get(startRow + i, startColumn, buffer[i]);
		}
	}

	public void open() throws DataSourceException
	{
		if (dataReader == null) {
			try {
				dataReader = new RandomAccessFile(dataFile, "r");
			} catch (FileNotFoundException ex) {
				throw new DataSourceException("File Not Found error opening file for reading: " + ex.getMessage(), ex);
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

		if (row < 0 || row >= rasterDefinition.getImageHeight() || column < 0 || column >= rasterDefinition.getImageWidth())
			return null;

		open();

		long pos = ((long) row * (long) rasterDefinition.getImageWidth()) + (long) column;
		long seekStart = pos * ((long) (rasterDefinition.getDataType().numberOfBytes()));
		long length = 0;

		try {
			length = dataReader.length();
		} catch (IOException ex) {
			throw new DataSourceException("Failed to determine file length: " + ex.getMessage(), ex);
		}

		if (seekStart >= length) {
			// return null;
			throw new DataSourceException("Cannot seek past end of file (seek to: " + seekStart + ", length: " + length + ")");
		}

		try {
			dataReader.seek(seekStart);
		} catch (IOException ex) {
			throw new DataSourceException("Failed on seek to row/column " + row + "/" + column + ", position " + pos + " (seek to " + seekStart + "): " + ex.getMessage(), ex);
		}
		return dataReader;
	}

	protected Number readSingleValue(RandomAccessFile dataReader) throws DataSourceException, IOException
	{
		Number o = readNext(dataReader);

		return o;
	}

	
	private Number readNext(RandomAccessFile dataReader) throws IOException
	{
		dataReader.readFully(buffer8, 0, rasterDefinition.getDataType().numberOfBytes());
		return BytesUtil.fromBytes(buffer8
				, 0
				, rasterDefinition.getDataType()
				, rasterDefinition.getByteOrder());
	}
	



	protected void readLine(RandomAccessFile dataReader, int startColumn, Number[] line) throws DataSourceException, IOException
	{
		int bufferLength = line.length;
		if (bufferLength + startColumn >= rasterDefinition.getImageWidth()) {
			bufferLength = rasterDefinition.getImageWidth() - startColumn;
		}

		int readLength = bufferLength * (rasterDefinition.getDataType().numberOfBytes());
		if (lineBuffer == null || lineBuffer.length != readLength) {
			lineBuffer = new byte[readLength];
		}

		dataReader.readFully(lineBuffer, 0, readLength);

		for (int i = 0; i < bufferLength; i++) {
			int p = i * rasterDefinition.getDataType().numberOfBytes();
			Number value = convertBytes(lineBuffer, p);
			line[i] = value;
		}

	}

	protected Number convertBytes(byte[] buffer, int offset) throws DataSourceException
	{
		return BytesUtil.fromBytes(buffer
									, offset
									, rasterDefinition.getDataType()
									, rasterDefinition.getByteOrder());
	}
}
