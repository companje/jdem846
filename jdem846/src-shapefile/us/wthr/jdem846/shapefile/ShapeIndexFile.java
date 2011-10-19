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

package us.wthr.jdem846.shapefile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.shapefile.exception.ShapeFileException;
import us.wthr.jdem846.util.ByteConversions;

public class ShapeIndexFile
{
	private static Log log = Logging.getLog(ShapeIndexFile.class);
	
	
	@SuppressWarnings("unused")
	private File indexFile;
	private RandomAccessFile rndAccessFile = null;
	
	private long numRecords;
	
	public ShapeIndexFile(String filePath) throws ShapeFileException
	{
		indexFile = new File(filePath);
		if (!indexFile.exists())
			throw new ShapeFileException("Shape index file does not exist");
		
		long size = indexFile.length();
		numRecords = (size - 100) / 8;
		
		try {
			rndAccessFile = new RandomAccessFile(indexFile, "r");
		} catch (FileNotFoundException ex) {
			log.error("Failed to open shapefile index for reading: " + ex.getMessage(), ex);
			throw new ShapeFileException("Failed to open shape index file for reading", ex);
		}
	}
	
	public void close() throws ShapeFileException
	{
		try {
			if (rndAccessFile != null) {
				rndAccessFile.close();
				rndAccessFile = null;
			}
		} catch (Exception ex) {
			throw new ShapeFileException("Failed to close shape index file", ex);
		}
	}
	
	public long getNumRecords()
	{
		return numRecords;
	}
	
	public ShapeIndexRecord getIndexRecord(long index) throws ShapeFileException
	{
		ShapeIndexRecord indexRecord = null;
		byte[] buffer4 = new byte[4];
		long offset = 100 + (8 * index);
		try {
			rndAccessFile.seek(offset);
			rndAccessFile.readFully(buffer4);
			int shapeOffset = ByteConversions.bytesToInt(buffer4, ByteOrder.LSBFIRST);
			
			rndAccessFile.readFully(buffer4);
			int shapeLength = ByteConversions.bytesToInt(buffer4, ByteOrder.LSBFIRST);
			
			indexRecord = new ShapeIndexRecord(shapeOffset, shapeLength);
		} catch (IOException ex) {
			log.error("IO error reading from shape index file: " + ex.getMessage(), ex);
			throw new ShapeFileException("IO error reading from shape index file", ex);
		}
		
		return indexRecord; // TODO: 
	}
	
}
