package us.wthr.jdem846.rasterdata.generic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DataTypeEnum;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.ByteConversions;

import com.google.common.primitives.Ints;

public class GenericRasterDataReader
{
	private static Log log = Logging.getLog(GenericRasterDataReader.class);

	private File dataFile;
	private IRasterDefinition rasterDefinition;

	private boolean isDisposed = false;

	private RandomAccessFile dataReader = null;

	private byte[] wordBuffer;
	private byte[] lineBuffer;

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
		wordBuffer = new byte[this.rasterDefinition.getDataType().numberOfBytes()];
	}

	public void dispose() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Raster data reader already disposed.");
		}

		lineBuffer = null;
		wordBuffer = null;

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
		DataTypeEnum dataType = this.rasterDefinition.getDataType();

		if (this.wordBuffer == null || this.wordBuffer.length != dataType.numberOfBytes()) {
			this.wordBuffer = new byte[dataType.numberOfBytes()];
		}

		Number o = null;

		if (dataType == DataTypeEnum.Byte) {
			o = readByte(dataReader);
		} else if (dataType == DataTypeEnum.UInt16) {
			o = readUInt16(dataReader);
		} else if (dataType == DataTypeEnum.Int16) {
			o = readInt16(dataReader);
		} else if (dataType == DataTypeEnum.Uint32) {
			o = readUInt32(dataReader);
		} else if (dataType == DataTypeEnum.Int32) {
			o = readInt32(dataReader);
		} else if (dataType == DataTypeEnum.CInt16) {
			o = readCInt16(dataReader);
		} else if (dataType == DataTypeEnum.CInt32) {
			o = readCInt32(dataReader);
		} else if (dataType == DataTypeEnum.Float32) {
			o = readFloat32(dataReader);
		} else if (dataType == DataTypeEnum.CFloat32) {
			o = readCFloat32(dataReader);
		} else if (dataType == DataTypeEnum.Float64) {
			o = readDouble64(dataReader);
		} else if (dataType == DataTypeEnum.CFloat64) {
			o = readCDouble64(dataReader);
		} else {
			throw new DataSourceException("Invalid or unsupported data type specified: " + dataType.name());
		}

		return o;
	}

	private byte readByte(RandomAccessFile dataReader) throws IOException
	{
		return dataReader.readByte();
	}

	private int readUInt16(RandomAccessFile dataReader) throws IOException
	{
		dataReader.readFully(this.wordBuffer, 0, 2);
		byte[] bytes = {0, 0, wordBuffer[1], wordBuffer[0]};
		return Ints.fromByteArray(bytes);
		//return Shorts.fromBytes(wordBuffer[1], wordBuffer[0]);
		//return UnsignedBytes.
		//return ByteConversions.bytesToShort(wordBuffer, this.rasterDefinition.getByteOrder());
	}

	private int readInt16(RandomAccessFile dataReader) throws IOException
	{
		short v = (short) readUInt16(dataReader);
		return v;
		//dataReader.readFully(this.wordBuffer, 0, 2);
		//byte[] bytes = {wordBuffer[1], wordBuffer[0], 0, 0};
		//return Ints.fromByteArray(bytes);
		//return Shorts.fromBytes(wordBuffer[1], wordBuffer[0]);
		//return ByteConversions.bytesToShort(wordBuffer, this.rasterDefinition.getByteOrder());
	}

	private int readCInt16(RandomAccessFile dataReader) throws IOException
	{

		return 0x0;
	}

	private int readUInt32(RandomAccessFile dataReader) throws IOException
	{
		dataReader.readFully(this.wordBuffer, 0, 4);
		return ByteConversions.bytesToInt(wordBuffer, rasterDefinition.getByteOrder());
	}

	private int readInt32(RandomAccessFile dataReader) throws IOException
	{
		dataReader.readFully(this.wordBuffer, 0, 4);
		return ByteConversions.bytesToInt(wordBuffer, rasterDefinition.getByteOrder());
	}

	private int readCInt32(RandomAccessFile dataReader) throws IOException
	{

		return 0x0;
	}

	private float readFloat32(RandomAccessFile dataReader) throws IOException
	{
		dataReader.readFully(this.wordBuffer, 0, 4);
		return ByteConversions.bytesToFloat(wordBuffer, rasterDefinition.getByteOrder());
	}

	private float readCFloat32(RandomAccessFile dataReader) throws IOException
	{

		return 0.0F;
	}

	private double readDouble64(RandomAccessFile dataReader) throws IOException
	{
		dataReader.readFully(this.wordBuffer, 0, 8);
		return ByteConversions.bytesToDouble(wordBuffer, rasterDefinition.getByteOrder());
	}

	private double readCDouble64(RandomAccessFile dataReader) throws IOException
	{

		return 0.0;
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
		Number o = null;
		DataTypeEnum dataType = rasterDefinition.getDataType();
		ByteOrder byteOrder = rasterDefinition.getByteOrder();

		if (dataType == DataTypeEnum.Byte) {
			o = buffer[offset];
		} else if (dataType == DataTypeEnum.UInt16) {
			//o = ByteConversions.bytesToShort(buffer[offset], buffer[offset + 1], byteOrder);
			byte[] bytes = {0, 0, buffer[offset + 1], buffer[offset]};
			o = Ints.fromByteArray(bytes);
		} else if (dataType == DataTypeEnum.Int16) {
			//o = ByteConversions.bytesToShort(buffer[offset], buffer[offset + 1], byteOrder);
			byte[] bytes = {0, 0, buffer[offset + 1], buffer[offset]};
			short v = (short) Ints.fromByteArray(bytes);
			o = v;
		} else if (dataType == DataTypeEnum.Uint32) {
			o = ByteConversions.bytesToInt(buffer[offset], buffer[offset + 1], buffer[offset + 2], buffer[offset + 3], byteOrder);
		} else if (dataType == DataTypeEnum.Int32) {
			o = ByteConversions.bytesToInt(buffer[offset], buffer[offset + 1], buffer[offset + 2], buffer[offset + 3], byteOrder);
		} else if (dataType == DataTypeEnum.CInt16) {
			o = null;
		} else if (dataType == DataTypeEnum.CInt32) {
			o = null;
		} else if (dataType == DataTypeEnum.Float32) {
			o = ByteConversions.bytesToFloat(buffer[offset], buffer[offset + 1], buffer[offset + 2], buffer[offset + 3], byteOrder);
		} else if (dataType == DataTypeEnum.CFloat32) {
			o = null;
		} else if (dataType == DataTypeEnum.Float64) {
			o = ByteConversions.bytesToDouble(buffer[offset], buffer[offset + 1], buffer[offset + 2], buffer[offset + 3], buffer[offset + 4], buffer[offset + 5], buffer[offset + 6],
					buffer[offset + 7], byteOrder);
		} else if (dataType == DataTypeEnum.CFloat64) {
			o = null;
		} else {
			throw new DataSourceException("Invalid or unsupported data type specified: " + dataType.name());
		}

		return o;
	}
}
