/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.input.edef;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.ByteConversions;

public class ElevationDatasetExchangeWriter
{
	private static Log log = Logging.getLog(ElevationDatasetExchangeWriter.class);
	
	private ElevationDatasetExchangeHeader header;
	private File file;
	private OutputStream outputStream;
	
	private boolean headerWritten = false;
	private boolean dataWritten = false;
	
	public ElevationDatasetExchangeWriter(String path, ElevationDatasetExchangeHeader header)
	{
		
		this.header = header;
		this.file = new File(path);
		
		
	}
	
	public boolean isOpen()
	{
		if (outputStream != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public void open() throws FileNotFoundException, IOException
	{
		if (isOpen())
			return;
		
		outputStream = new BufferedOutputStream(new FileOutputStream(file));
		
	}
	
	public void close() throws IOException
	{
		if (!isOpen())
			return;
		
		outputStream.close();
		outputStream = null;
	}
	
	public void flush() throws IOException
	{
		if (!isOpen()) {
			return;
		}
		
		outputStream.flush();
	}
	
	public ElevationDatasetExchangeHeader getHeader()
	{
		return header;
	}
	
	public boolean isHeaderWritten()
	{
		return headerWritten;
	}
	
	public boolean isDataWritten()
	{
		return dataWritten;
	}
	
	public void writeHeader() throws IOException
	{
		if (!isOpen()) 
			throw new IOException("File is not been opened or was closed");
		
		
		if (isHeaderWritten())
			throw new IOException("Header has already been written");
		
		if (isDataWritten()) // We _shouldn't_ need this, but....
			throw new IOException("Data has already been written");
		
		byte[] headerBytes = header.toBytes();
		
		outputStream.write(headerBytes);
		
		headerWritten = true;
	}
	
	
	public void write(float value) throws IOException
	{
		if (!isOpen()) {
			throw new IOException("File is not been opened or was closed");
		}
		
		if (!isHeaderWritten()) {
			// No, I'm not going to be nice and just write the header for you
			throw new IOException("Cannot write data: header has not yet been written");
		}
		
		byte[] buffer = ByteConversions.floatToBytes(value, header.getByteOrder());
		outputStream.write(buffer);
		
		dataWritten = true;
	}
	
	
}
