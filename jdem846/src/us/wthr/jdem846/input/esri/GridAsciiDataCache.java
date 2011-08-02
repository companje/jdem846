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

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.input.DataCache;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.ByteConversions;
import us.wthr.jdem846.util.TempFiles;

public class GridAsciiDataCache implements DataCache
{
	private static Log log = Logging.getLog(GridAsciiDataCache.class);
	
	private boolean isLoaded = false;
	
	private int size;

	private ByteOrder byteOrder = ByteOrder.LSBFIRST;

	private File input;
	private RandomAccessFile inputData;
	private byte buffer[];
	
	File cacheFile = null;
	
	public GridAsciiDataCache(File input, int columns)
	{
		this.input = input;
		this.size = (columns * 4) * 2;

		this.buffer = new byte[size];
		
		this.inputData = null;
		
		try {
			//cacheFile = File.createTempFile("jdem.gridascii.cache.", ".tmp", new File(System.getProperty("java.io.tmpdir")));
			cacheFile = TempFiles.getTemporaryFile("gridascii.cache", ".tmp");
			createBinaryCacheFile(cacheFile);
		} catch (Exception ex) {
			log.error("Failed to create GridAscii binary cache file: " + ex.getMessage(), ex);
			//ex.printStackTrace();
		}
	}

	@Override
	public float get(int position)
	{
		int offset = (position * 4);
		return ByteConversions.bytesToFloat(buffer[offset], buffer[offset+1], buffer[offset+2], buffer[offset+3], byteOrder);
	}

	@Override
	public void load(long start)
	{
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

	public void createBinaryCacheFile(File cacheFile) throws IOException
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
