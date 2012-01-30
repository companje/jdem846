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

import java.awt.Polygon;
import java.awt.geom.Path2D;

import us.wthr.jdem846.geom.Edge;

public class PolygonShape extends Shape
{

	private Box bounds;
	private int numParts;
	private int numPoints;
	private int[] parts;
	private Point[] points;
	
	private double[] zRange;
	private double[] zArray;
	
	private double[] mRange;
	private double[] mArray;
	
	
	
	public PolygonShape(int recordNumber)
	{
		super(recordNumber, ShapeConstants.TYPE_POLYGONZ);
	}
	
	
	public int getPartLength(int index)
	{
		int start = parts[index];
		int end = 0;
		if (index == numParts - 1) {
			end = (numPoints - 1);
		} else {
			end = parts[index+1] - 1;
		}
		
		return (end - start);
	}
	
	
	public Point[] getPart(int index)
	{
		int partLength = getPartLength(index);
		Point[] partPoints = new Point[partLength];
		
		for (int i = 0; i < partLength; i++) {
			int partIndex = index + i;
			partPoints[i] = points[partIndex];
		}
		
		return partPoints;
	}

	
	@Override
	public ShapePath getShapePath()
	{
		ShapePath path = getShapePath(0);
		
		for (int i = 1; i < this.numParts; i++) {
			ShapePath subPart = getShapePath(i);
			path.addSubPart(subPart);
		}
		
		if (getFeatureType() != null)
			path.setFeatureType(this.getFeatureType());
		
		
		return path;
	}
	
	public ShapePath getShapePath(int index)
	{
		ShapePath path = new ShapePath();
		Point[] mainPoints = getPart(index);
		
		double lastX = mainPoints[0].getX();
		double lastY = mainPoints[0].getY();
		
		for (int i = 1; i < mainPoints.length; i++) {
			
			double x = mainPoints[i].getX();
			double y = mainPoints[i].getY();
			
			path.addEdge(new Edge(lastX, lastY, x, y));
			
			lastX = x;
			lastY = y;
		}
		
		//Point[] mainPoints = getPart(index);
		
		//path.moveTo(mainPoints[0].getX(), mainPoints[0].getY());
		
		//for (int i = 1; i < mainPoints.length; i++) {
		//	Point point = mainPoints[i];
		//	path.lineTo(point.getX(), point.getY());
		//}
		//path.closePath();
		
		if (getFeatureType() != null)
			path.setFeatureType(this.getFeatureType());
		
		return path;
	}

	public Box getBounds()
	{
		return bounds;
	}



	public void setBounds(Box bounds)
	{
		this.bounds = bounds;
	}



	public int getNumParts()
	{
		return numParts;
	}



	public void setNumParts(int numParts)
	{
		this.numParts = numParts;
	}



	public int getNumPoints()
	{
		return numPoints;
	}



	public void setNumPoints(int numPoints)
	{
		this.numPoints = numPoints;
	}



	public int[] getParts()
	{
		return parts;
	}



	public void setParts(int[] parts)
	{
		this.parts = parts;
	}



	public Point[] getPoints()
	{
		return points;
	}



	public void setPoints(Point[] points)
	{
		this.points = points;
	}



	public double[] getzRange()
	{
		return zRange;
	}



	public void setzRange(double[] zRange)
	{
		this.zRange = zRange;
	}



	public double[] getzArray()
	{
		return zArray;
	}



	public void setzArray(double[] zArray)
	{
		this.zArray = zArray;
	}



	public double[] getmRange()
	{
		return mRange;
	}



	public void setmRange(double[] mRange)
	{
		this.mRange = mRange;
	}



	public double[] getmArray()
	{
		return mArray;
	}



	public void setmArray(double[] mArray)
	{
		this.mArray = mArray;
	}
	
	
}
