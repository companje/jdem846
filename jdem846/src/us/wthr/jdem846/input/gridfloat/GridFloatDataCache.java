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

package us.wthr.jdem846.input.gridfloat;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.input.DataCache;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.ByteConversions;

public class GridFloatDataCache extends DataCache
{
	private static Log log = Logging.getLog(GridFloatDataCache.class);
	private static byte[] buffer_2048 = new byte[2048];
	
	private boolean isLoaded = false;
	private int size = 0;
	private ByteOrder byteOrder;

	private File input;
	private RandomAccessFile inputData;
	private byte buffer[];

	private boolean isFullyCached = false;
	private int cacheOffset = 0;
	
	private boolean isDisposed = false;
	
	public GridFloatDataCache(File input, int size, ByteOrder byteOrder)
	{
		this.input = input;
		this.size = size;
		this.byteOrder = byteOrder;
		
		this.inputData = null;
		
		/*
		int cacheFullIfUnder = JDem846Properties.getIntProperty("us.wthr.jdem846.input.gridFloat.cacheFullyUnderBytes");
		if (input.length() < cacheFullIfUnder) {
			log.info("Data length of " + input.length() + " does not exceed limit of " + cacheFullIfUnder + ", loading entire dataset into memory");
			isFullyCached = true;
			load(0);
		}
		*/
	}
	
	public boolean setDataPrecached(boolean precached)
	{
		int cacheFullIfUnder = JDem846Properties.getIntProperty("us.wthr.jdem846.input.gridFloat.cacheFullyUnderBytes");
		
		if (precached && input.length() < cacheFullIfUnder) {
			log.info("Data length of " + input.length() + " does not exceed limit of " + cacheFullIfUnder + ", loading entire dataset into memory");
			isFullyCached = true;
			load(0);
			return true;
		} else {
			//log.info("Data length of " + input.length() + " exceeds limit of " + cacheFullIfUnder);
			isFullyCached = false;
			unload();
			return false;
		}
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
		
		log.info("Disposing of GridFloat cache and file pointer");
		
		if (inputData != null) {
			unload();
			buffer = null;
		}
		
		isDisposed = true;
	}
	
	public float get(int position)
	{
		int offset = cacheOffset + (position * 4);
		if (offset < 0 || offset+3 >= buffer.length)
			return DemConstants.ELEV_NO_DATA;
		//try {
			return ByteConversions.bytesToFloat(buffer[offset], buffer[offset+1], buffer[offset+2], buffer[offset+3], byteOrder);
		//} catch (ArrayIndexOutOfBoundsException ex) {
		//	return DemConstants.ELEV_NO_DATA;
		//}
	}
	
	@Override
	public void load(float[] valueBuffer, int start, int length) throws DataSourceException
	{
		if (inputData == null) {
			try {
				inputData = new RandomAccessFile(input, "r");
			} catch (FileNotFoundException ex) {
				throw new DataSourceException("File Not Found error opening GridFloat file for reading: " + ex.getMessage(), ex);
			}
		}
		
		Arrays.fill(buffer_2048, (byte)0x0);
		

		int seekStart = start * (Float.SIZE / 8);
		try {
			inputData.seek(seekStart);
		} catch (IOException ex) {
			throw new DataSourceException("IO error seeking within GridFloat data file: " + ex.getMessage(), ex);
		}
		
		
		
		int totalReadLength = length * (Float.SIZE / 8);
		try {
			for (int i = 0; i < totalReadLength; i+=2048) {
				inputData.read(buffer_2048);
				
				//for (int j = 0; j < length; j++) {
					//int p = j * 4;	// The position within the buffer that the float sits
					//if (p+3<2048)
				//for (int p = i; p < i+2040; p+=4) {
				for (int p = 0; p < 2048; p+=4) {
					int j = ((i / 4) + (p / 4)) ;
					if (j >= valueBuffer.length)
						break;
					valueBuffer[j] =  ByteConversions.bytesToFloat(buffer_2048[p], buffer_2048[p+1], buffer_2048[p+2], buffer_2048[p+3], byteOrder);
				}
				
			}
			
		} catch (IOException ex) {
			throw new DataSourceException("IO error reading from GridFloat data file: " + ex.getMessage(), ex);
		}
	}
	
	public void load(long start)
	{
		if (inputData == null) {
			try {
				inputData = new RandomAccessFile(input, "r");
			} catch (FileNotFoundException ex) {
				log.error("File Not Found error opening GridFloat file for reading: " + ex.getMessage(), ex);
			}
			
			if (isFullyCached) {
				cacheOffset = (int) start;
				start = 0;
			}
			
		} else if (inputData != null && isFullyCached) {
			cacheOffset = (int) start;
			return;
		}
		
		if (buffer == null) {
			buffer = new byte[(isFullyCached) ? (int)input.length() : size];
		}
		

		try {
			inputData.seek(start);
			inputData.read(buffer);
		} catch (IOException ex) {
			log.error("IO error reading from GridFloat data file: " + ex.getMessage(), ex);
		}
		
		
		this.isLoaded = true;
	}
	
	
	public void unload()
	{
		this.buffer = null;
		try {
			this.inputData.close();
		} catch (IOException e) {
			log.error("IO error closing GridFloat data file: " + e.getMessage(), e);
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