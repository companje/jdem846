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
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.shapefile.exception.ShapeFileException;
import us.wthr.jdem846.util.ByteConversions;

public class ShapeFile
{
	private static Log log = Logging.getLog(ShapeFile.class);
	
	private int fileCode;
	private int fileLength;
	private int version;
	private int shapeType;
	private Box bounds;
	
	private long shapeCount;
	
	private File shapeFile;
	private RandomAccessFile file;
	
	private List<Shape> shapes = new LinkedList<Shape>();

	private ShapeIndexFile indexFile;
	

	public ShapeFile(String filePath) throws ShapeFileException
	{
		shapeFile = new File(filePath);
		if (!shapeFile.exists())
			throw new ShapeFileException("Shapefile does not exist");
		
		indexFile = new ShapeIndexFile(filePath.replace(".shp", ".shx"));
		shapeCount = indexFile.getNumRecords();

		try {
		file = new RandomAccessFile(shapeFile, "r");
		file.seek(0);
		} catch (Exception ex) {
			throw new ShapeFileException("Failed opening shapefile for reading", ex);
		}
		
		
		byte[] buffer4 = new byte[4];
		byte[] buffer8 = new byte[8];
		
		try {
			file.readFully(buffer4);
			this.fileLength = ByteConversions.bytesToInt(buffer4);
	
	
			file.readFully(buffer4);
			this.version = ByteConversions.bytesToInt(buffer4, ByteOrder.LSBFIRST);
			
			
			file.readFully(buffer4);
			this.shapeType = ByteConversions.bytesToInt(buffer4, ByteOrder.LSBFIRST);
	
			file.seek(36);
			
			file.readFully(buffer8);
			double minX = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
			
			file.readFully(buffer8);
			double minY = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
			
			file.readFully(buffer8);
			double maxX = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
			
			file.readFully(buffer8);
			double maxY = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
			
			file.readFully(buffer8);
			double minZ = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
			
			file.readFully(buffer8);
			double maxZ = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
			
			file.readFully(buffer8);
			double minM = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
			
			file.readFully(buffer8);
			double maxM = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
			
			this.bounds = new Box(maxX, minX, maxY, minY, maxZ, minZ, maxM, minM);
		} catch (Exception ex) {
			throw new ShapeFileException("Failure reading from shapefile.", ex);
		}
	}
	

	public void close() throws ShapeFileException
	{
		try {
			if (file != null) {
				file.close();
				file = null;
			}
		} catch(Exception ex) {
			throw new ShapeFileException("Failed to close shapefile", ex);
		}
		
		try {
			if (indexFile != null) {
				indexFile.close();
			}
		} catch (Exception ex) {
			throw new ShapeFileException("Failed to close shape index file", ex);
		}
	}
	
	public Shape getShape(int index) throws ShapeFileException
	{
		ShapeIndexRecord indexRecord = this.indexFile.getIndexRecord(index);
		if (indexRecord == null)
			return null;
		
		Shape shape = null;
		
		try {
			shape = ShapeFactory.getShape(indexRecord, file);
		} catch (ShapeFileException ex) {
			log.error("Failed to read from shape data file: " + ex.getMessage(), ex);
			throw new ShapeFileException("Failed to read from shape data file", ex);
		}
		
		
		return shape;
	}

	public int getFileCode()
	{
		return fileCode;
	}

	public void setFileCode(int fileCode)
	{
		this.fileCode = fileCode;
	}

	public int getFileLength()
	{
		return fileLength;
	}

	public void setFileLength(int fileLength)
	{
		this.fileLength = fileLength;
	}

	public int getVersion()
	{
		return version;
	}

	public void setVersion(int version)
	{
		this.version = version;
	}

	public int getShapeType() throws ShapeFileException
	{
		if (shapeType == 0 && shapeCount > 0) {
			Shape first = getShape(0);
			shapeType = first.getShapeType();
		}
		
		return shapeType;
	}

	public void setShapeType(int shapeType)
	{
		this.shapeType = shapeType;
	}

	public Box getBounds()
	{
		return bounds;
	}

	public void setBounds(Box bounds)
	{
		this.bounds = bounds;
	}

	public long getShapeCount()
	{
		return shapeCount;
	}

	public void setShapeCount(long shapeCount)
	{
		this.shapeCount = shapeCount;
	}

	public File getShapeFile()
	{
		return shapeFile;
	}

	public void setShapeFile(File shapeFile)
	{
		this.shapeFile = shapeFile;
	}

	public List<Shape> getShapes()
	{
		return shapes;
	}

	public void setShapes(List<Shape> shapes)
	{
		this.shapes = shapes;
	}

	public ShapeIndexFile getIndexFile()
	{
		return indexFile;
	}

	public void setIndexFile(ShapeIndexFile indexFile)
	{
		this.indexFile = indexFile;
	}
	
	
	
}
