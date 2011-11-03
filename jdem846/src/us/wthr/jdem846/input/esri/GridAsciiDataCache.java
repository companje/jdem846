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

package us.wthr.jdem846.input.esri;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.input.DataCache;
import us.wthr.jdem846.input.gridfloat.GridFloatDataCache;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.ByteConversions;
import us.wthr.jdem846.util.TempFiles;

@Deprecated
public class GridAsciiDataCache extends DataCache
{
	private static Log log = Logging.getLog(GridAsciiDataCache.class);
	
	private static int LOAD_BUFFER_SIZE = 2048;
	private static byte[] load_buffer = new byte[GridAsciiDataCache.LOAD_BUFFER_SIZE];
	
	private boolean isLoaded = false;
	
	private int size;

	private ByteOrder byteOrder = ByteOrder.LSBFIRST;

	private File input;
	private RandomAccessFile inputData;
	private byte buffer[];
	private boolean isDisposed = false;
	
	File cacheFile = null;
	
	public GridAsciiDataCache(File input, int columns)
	{
		this.input = input;
		this.size = (columns * 4) * 2;

		this.buffer = new byte[size];
		
		this.inputData = null;
		this.cacheFile = null;
		
		
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
	
	@Override
	public double get(int position) throws DataSourceException
	{
		int offset = (position * 4);
		return ByteConversions.bytesToFloat(buffer[offset], buffer[offset+1], buffer[offset+2], buffer[offset+3], byteOrder);
	}
	

	@Override
	public void load(double[] valueBuffer, int start, int length) throws DataSourceException
	{
		if (cacheFile == null) {
			createBinaryCacheFile();
		}
		
		if (inputData == null) {
			try {
				inputData = new RandomAccessFile(input, "r");
			} catch (FileNotFoundException ex) {
				throw new DataSourceException("File Not Found error opening GridFloat file for reading: " + ex.getMessage(), ex);
			}
		}
		
		Arrays.fill(load_buffer, (byte)0x0);
		

		long seekStart = (long)start * (long)  (Float.SIZE / 8);
		try {
			inputData.seek(seekStart);
		} catch (IOException ex) {
			throw new DataSourceException("IO error seeking within GridFloat data file: " + ex.getMessage(), ex);
		}
		
		
		
		int totalReadLength = length * (Float.SIZE / 8);
		try {
			for (int i = 0; i < totalReadLength; i+=GridAsciiDataCache.LOAD_BUFFER_SIZE) {
				inputData.read(load_buffer);
				
				//for (int j = 0; j < length; j++) {
					//int p = j * 4;	// The position within the buffer that the float sits
					//if (p+3<2048)
				//for (int p = i; p < i+2040; p+=4) {
				for (int p = 0; p < GridAsciiDataCache.LOAD_BUFFER_SIZE; p+=4) {
					int j = ((i / 4) + (p / 4)) ;
					if (j >= valueBuffer.length)
						break;
					valueBuffer[j] =  ByteConversions.bytesToFloat(load_buffer[p], load_buffer[p+1], load_buffer[p+2], load_buffer[p+3], byteOrder);
				}
				
			}
			
		} catch (IOException ex) {
			throw new DataSourceException("IO error reading from GridFloat data file: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void load(long start) throws DataSourceException
	{
		if (cacheFile == null) {
			createBinaryCacheFile();
		}
		
		if (inputData == null) {
			try {
				inputData = new RandomAccessFile(cacheFile, "r");
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
		}
		
		if (buffer == null) {
			buffer = new byte[size];
		}
		

		try {
			inputData.seek(start);
			inputData.read(buffer);
		} catch (IOException ex) {
			log.error("Error reading from binary data: " + ex.getMessage(), ex);
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		
		this.isLoaded = true;
		
	}

	
	
	public void createBinaryCacheFile() throws DataSourceException
	{
		if (cacheFile != null) {
			throw new DataSourceException("Binary cache file already exists!");
		}
		try {
			cacheFile = TempFiles.getTemporaryFile("gridascii.cache", ".tmp");
			loadBinaryCacheFile(cacheFile);
		} catch (Exception ex) {
			throw new DataSourceException("Failed to create GridAscii binary cache file: " + ex.getMessage(), ex);
		}
		
	}
	
	protected void loadBinaryCacheFile(File cachefile) throws IOException
	{
		log.info("Writing GridAscii cache to " + cacheFile.getAbsolutePath());

		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(cacheFile));
		
		BufferedReader inputReader = new BufferedReader(new FileReader(input));
		for (int i = 0; i < 6; i++)
			inputReader.readLine();
		
		String line = null;
		while ((line = inputReader.readLine()) != null) {
			String parts[] = line.split(" ");
			for (int i = 0; i < parts.length; i++) {
				float value = Float.parseFloat(parts[i]);
				byte[] buffer = ByteConversions.floatToBytes(value, byteOrder);
				out.write(buffer);
			}
		}
		out.close();
		
		log.info("Done writing gridascii cache");
	}
	
	@Override
	public void unload()
	{
		this.buffer = null;
		try {
			this.inputData.close();
		} catch (IOException e) {
			log.error("Error closing input data file: " + e.getMessage(), e);
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		this.inputData = null;
		this.isLoaded = false;
		this.cacheFile = null;
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
