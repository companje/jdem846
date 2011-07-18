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

import java.io.IOException;
import java.io.RandomAccessFile;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.shapefile.exception.ShapeFileException;
import us.wthr.jdem846.util.ByteConversions;

public class ShapeFactory
{
	private static Log log = Logging.getLog(ShapeFactory.class);
	
	
	public static Shape getShape(ShapeIndexRecord indexRecord, RandomAccessFile file) throws ShapeFileException
	{
		
		int shapeType = 0;
		int recordNumber = 0;
		int length = 0;
		
		byte[] buffer4 = new byte[4];
		
		try {
			file.seek(indexRecord.getOffset()*2);
			file.readFully(buffer4);
			recordNumber = ByteConversions.bytesToInt(buffer4, ByteOrder.LSBFIRST);
			
			file.readFully(buffer4);
			length = ByteConversions.bytesToInt(buffer4, ByteOrder.LSBFIRST);
			
			file.readFully(buffer4);
			shapeType = ByteConversions.bytesToInt(buffer4, ByteOrder.MSBFIRST);
		} catch (Exception ex) {
			throw new ShapeFileException("Failed to read shape metadata.", ex);
		}
		
		try {
			switch(shapeType) {
			case ShapeConstants.TYPE_POINT:
				return getPoint(indexRecord, file, recordNumber, length);
			case ShapeConstants.TYPE_POINTM:
				log.info("Loading PointM");
				break;
			case ShapeConstants.TYPE_POINTZ:
				log.info("Loading PointZ");
				break;
				
			case ShapeConstants.TYPE_POLYLINE:
				return getPolyLineZ(indexRecord, file, recordNumber, false, false);
			case ShapeConstants.TYPE_POLYLINEZ:
				return getPolyLineZ(indexRecord, file, recordNumber, true, false);
			case ShapeConstants.TYPE_POLYLINEM:
				return getPolyLineZ(indexRecord, file, recordNumber, true, true);
				
			case ShapeConstants.TYPE_POLYGON:
				return getPolygonZ(indexRecord, file, recordNumber, false, false);
			case ShapeConstants.TYPE_POLYGONZ:
				return getPolygonZ(indexRecord, file, recordNumber, true, false);
			case ShapeConstants.TYPE_POLYGONM:
				return getPolygonZ(indexRecord, file, recordNumber, true, true);
				
			case ShapeConstants.TYPE_MULTIPOINT:
				log.info("Loading Multipoint");
				break;
			case ShapeConstants.TYPE_MULTIPOINTM:
				log.info("Loading MultipointM");
				break;
			case ShapeConstants.TYPE_MULTIPOINTZ:
				log.info("Loading MultipointZ");
				break;
			case ShapeConstants.TYPE_MULTIPATCH:
				log.info("Loading Multipatch");
				break;
			case ShapeConstants.TYPE_NULLSHAPE:
				log.info("Loading Nullshape");
				break;
			default:
				log.warn("Unsupported shape type: " + shapeType);
				break;
			}
		} catch (Exception ex) {
			throw new ShapeFileException("Failed to create shape", ex);
		}
		
		return null;
	}
	
	protected static Point getPoint(ShapeIndexRecord indexRecord, RandomAccessFile file, int recordNumber, int length)
	{
		log.info("Loading Point shape");
		
		return null;
	}
	
	protected static PolyLineShape getPolyLineZ(ShapeIndexRecord indexRecord, RandomAccessFile file, int recordNumber, boolean withZ, boolean withM) throws IOException
	{
		file.seek(indexRecord.getOffset()*2 + 12);
		
		byte[] buffer4 = new byte[4];
		byte[] buffer8 = new byte[8];
		

		file.readFully(buffer8);
		double minX = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
		
		file.readFully(buffer8);
		double minY = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
		
		file.readFully(buffer8);
		double maxX = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
		
		file.readFully(buffer8);
		double maxY = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
		
		Box bounds = new Box(maxX, maxY, minX, minY);
		
		
		file.readFully(buffer4);
		int numParts = ByteConversions.bytesToInt(buffer4, ByteOrder.MSBFIRST);
		
		file.readFully(buffer4);
		int numPoints = ByteConversions.bytesToInt(buffer4, ByteOrder.MSBFIRST);
		
		int[] parts = new int[numParts];
		for (int i = 0; i < numParts; i++) {
			file.readFully(buffer4);
			parts[i] = ByteConversions.bytesToInt(buffer4, ByteOrder.MSBFIRST);
		}
		

		Point[] points = new Point[numPoints];
        for (int i = 0; i < numPoints; i++) {
        	file.readFully(buffer8);
        	double x = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
        	
        	file.readFully(buffer8);
        	double y = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);

        	Point point = new Point(x, y);
        	points[i] = point;
        }
        
        
        double[] zRange = withZ ? new double[2] : null;
        double[] zArray = withZ ? new double[numPoints] : null;
        
        double[] mRange = withZ ? new double[2] : null;
        double[] mArray = withZ ? new double[numPoints] : null;
        
        if (withZ) {
	        file.readFully(buffer8);
	    	zRange[0] = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
	    	file.readFully(buffer8);
	    	zRange[1] = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
	    	
	    	
	    	for (int i = 0; i < numPoints; i++) {
	    		file.readFully(buffer8);
	    		zArray[i] = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
	    	}
        }
        
        if (withM) {
	    	file.readFully(buffer8);
	    	mRange[0] = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
	    	file.readFully(buffer8);
	    	mRange[1] = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
	    	
	    	for (int i = 0; i < numPoints; i++) {
	    		file.readFully(buffer8);
	    		mArray[i] = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
	    	}
        }
		
        
    	PolyLineShape polyZ = new PolyLineShape(recordNumber);
    	polyZ.setBounds(bounds);
    	polyZ.setmArray(mArray);
    	polyZ.setmRange(mRange);
    	polyZ.setNumParts(numParts);
    	polyZ.setNumPoints(numPoints);
    	polyZ.setParts(parts);
    	polyZ.setPoints(points);
    	polyZ.setzArray(zArray);
    	polyZ.setzRange(zRange);
		
		return polyZ;
	}

	protected static PolygonShape getPolygonZ(ShapeIndexRecord indexRecord, RandomAccessFile file, int recordNumber, boolean withZ, boolean withM) throws IOException
	{
		file.seek(indexRecord.getOffset()*2 + 12);
		
		byte[] buffer4 = new byte[4];
		byte[] buffer8 = new byte[8];
		
		file.readFully(buffer8);
		double minX = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
		
		file.readFully(buffer8);
		double minY = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
		
		file.readFully(buffer8);
		double maxX = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
		
		file.readFully(buffer8);
		double maxY = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
		
		Box bounds = new Box(maxX, maxY, minX, minY);

		file.readFully(buffer4);
		int numParts = ByteConversions.bytesToInt(buffer4, ByteOrder.MSBFIRST);

		file.readFully(buffer4);
		int numPoints = ByteConversions.bytesToInt(buffer4, ByteOrder.MSBFIRST);
		
		int[] parts = new int[numParts];
		for (int i = 0; i < numParts; i++) {
			file.readFully(buffer4);
			parts[i] = ByteConversions.bytesToInt(buffer4, ByteOrder.MSBFIRST);
		}
		
        //int startIndex = indexRecord.getOffset() + 8 + 44 + (numParts * 4);
		Point[] points = new Point[numPoints];
        for (int i = 0; i < numPoints; i++) {
        	file.readFully(buffer8);
        	double x = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
        	
        	file.readFully(buffer8);
        	double y = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);

        	Point point = new Point(x, y);
        	points[i] = point;
        }

        double[] zRange = withZ ? new double[2] : null;
        double[] zArray = withZ ? new double[numPoints] : null;
        
        double[] mRange = withZ ? new double[2] : null;
        double[] mArray = withZ ? new double[numPoints] : null;
        
        if (withZ) {
	        file.readFully(buffer8);
	    	zRange[0] = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
	    	file.readFully(buffer8);
	    	zRange[1] = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
	    	
	    	
	    	for (int i = 0; i < numPoints; i++) {
	    		file.readFully(buffer8);
	    		zArray[i] = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
	    	}
        }
        
        if (withM) {
	    	file.readFully(buffer8);
	    	mRange[0] = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
	    	file.readFully(buffer8);
	    	mRange[1] = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
	    	
	    	for (int i = 0; i < numPoints; i++) {
	    		file.readFully(buffer8);
	    		mArray[i] = ByteConversions.bytesToDouble(buffer8, ByteOrder.LSBFIRST);
	    	}
        }
        
    	log.debug("PolygonZ #" + recordNumber + " has " + numParts + " parts and " + numPoints + " points");
    	
    	PolygonShape polyZ = new PolygonShape(recordNumber);
    	polyZ.setBounds(bounds);
    	polyZ.setmArray(mArray);
    	polyZ.setmRange(mRange);
    	polyZ.setNumParts(numParts);
    	polyZ.setNumPoints(numPoints);
    	polyZ.setParts(parts);
    	polyZ.setPoints(points);
    	polyZ.setzArray(zArray);
    	polyZ.setzRange(zRange);
    	
    	
		return polyZ;
	}
	


}
