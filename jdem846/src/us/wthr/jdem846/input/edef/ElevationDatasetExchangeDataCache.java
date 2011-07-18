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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.input.DataCache;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ElevationDatasetExchangeDataCache implements DataCache
{
	private static Log log = Logging.getLog(ElevationDatasetExchangeDataCache.class);
	
	private boolean isLoaded = false;
	private int size = 0;
	private ByteOrder byteOrder;

	private File input;
	private RandomAccessFile inputData;
	private byte buffer[];
	
	public ElevationDatasetExchangeDataCache(File input, int size, ByteOrder byteOrder)
	{
		this.input = input;
		this.size = size;
		this.byteOrder = byteOrder;
		this.buffer = new byte[size];
		
		this.inputData = null;
	}

	public float get(int position)
	{
		float elevation = 0;
		
		int offset = (position * 4);
		
		byte[] floatBuffer = {buffer[offset], buffer[offset+1], buffer[offset+2], buffer[offset+3]};
		int intBits = 0;
		
		if (byteOrder == ByteOrder.LSBFIRST) {
			intBits = (floatBuffer[3] << 24) |
				((floatBuffer[2] & 0xFF) << 16) |
				((floatBuffer[1] & 0xFF) << 8) |
				(floatBuffer[0] & 0xFF);
		} else if (byteOrder == ByteOrder.MSBFIRST) {
			intBits = (floatBuffer[0] << 24) |
				((floatBuffer[1] & 0xFF) << 16) |
				((floatBuffer[2] & 0xFF) << 8) |
				(floatBuffer[3] & 0xFF);
		}
		
		elevation = Float.intBitsToFloat(intBits);
		return elevation;
	}
	
	public void load(long start)
	{
		if (inputData == null) {
			try {
				inputData = new RandomAccessFile(input, "r");
			} catch (FileNotFoundException e) {
				log.error("File Not Found Exception opening edef file for reading: " + e.getMessage(), e);
			}
		}
		
		if (buffer == null) {
			buffer = new byte[size];
		}
		
		start += ElevationDatasetExchangeHeader.BYTE_SIZE; // Skip header data
		
		try {
			inputData.seek(start);
			inputData.read(buffer);
		} catch (IOException e) {
			log.error("IO error reading EDEF file: " + e.getMessage(), e);
		}

		
		this.isLoaded = true;
		
		
	}
	
	
	public void unload()
	{
		this.buffer = null;
		try {
			this.inputData.close();
		} catch (IOException e) {
			log.error("IO error closing EDEF file: " + e.getMessage(), e);
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
