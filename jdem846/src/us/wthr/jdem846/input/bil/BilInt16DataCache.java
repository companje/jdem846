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

package us.wthr.jdem846.input.bil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.input.DataCache;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class BilInt16DataCache extends DataCache
{
	private static Log log = Logging.getLog(BilInt16DataCache.class);
	
	private boolean isLoaded = false;
	private int size = 0;
	private ByteOrder byteOrder;
	private int skipBytes = 0;
	
	private File input;
	private RandomAccessFile inputData;
	private byte buffer[];
	private boolean isDisposed = false;
	
	public BilInt16DataCache(File input, int size, ByteOrder byteOrder, int skipBytes)
	{
		this.input = input;
		this.size = size;
		this.byteOrder = byteOrder;
		this.skipBytes = skipBytes;
		this.buffer = new byte[size];
		
		this.inputData = null;
	}
	
	public long getDataLength()
	{
		return input.length();
	}
	
	public void dispose() throws DataSourceException
	{
		if (isDisposed) {
			throw new DataSourceException("Object already disposed of");
		}
		
		if (inputData != null) {
			unload();
			buffer = null;
		}
		
		isDisposed = true;
	}
	
	public double get(int position)
	{
		double elevation = 0;
		
		int offset = (position * 2);
		
		byte[] intBuffer = {buffer[offset], buffer[offset+1]};
		int intBits = 0;
		
		if (byteOrder == ByteOrder.LSBFIRST || byteOrder == ByteOrder.INTEL_BYTE_ORDER) {
			intBits =  (((intBuffer[1] & 0xFF) << 8) |
				(intBuffer[0] & 0xFF));
		} else if (byteOrder == ByteOrder.MSBFIRST || byteOrder == ByteOrder.INTEL_OR_MOTOROLA) {
			intBits = (((intBuffer[0] & 0xFF) << 8) |
				(intBuffer[1] & 0xFF));
		}
		elevation = (float) intBits;
		if (elevation > 40000)
			elevation = 0;
		//elevation = Float.intBitsToFloat(intBits);
		return elevation;
	}
	
	public void load(long start)
	{
		if (inputData == null) {
			try {
				inputData = new RandomAccessFile(input, "r");
			} catch (FileNotFoundException e) {
				log.error("File Not Found error opening BIL data file for reading: " + e.getMessage(), e);
			}
		}
		
		if (buffer == null) {
			buffer = new byte[size];
		}
		

		try {
			inputData.seek(skipBytes + start);
			inputData.read(buffer);
		} catch (IOException e) {
			log.error("IO error when reading from BIL data file: " + e.getMessage(), e);
		}
		

		
		this.isLoaded = true;
		
	}
	
	
	public void unload()
	{
		this.buffer = null;
		try {
			this.inputData.close();
		} catch (IOException e) {
			log.error("IO error when closing BIL data file: " + e.getMessage(), e);
		}
		this.inputData = null;
		this.isLoaded = false;
	}

	public boolean isLoaded() 
	{
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded)
	{
		this.isLoaded = isLoaded;
	}
	
}
