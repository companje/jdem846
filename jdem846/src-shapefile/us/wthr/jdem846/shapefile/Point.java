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

public class Point extends Shape
{
	private double x;
	private double y;
	private double z;
	private double m;
	
	
	public Point(int recordNumber)
	{
		super(recordNumber, ShapeConstants.TYPE_POINTZ);
	}
	
	public Point(int recordNumber, double x, double y, double z, double m)
	{
		super(recordNumber, ShapeConstants.TYPE_POINTZ);
		this.x = x;
		this.y = y;
		this.z = z;
		this.m = m;
	}
	
	public Point(double x, double y)
	{
		super(-1, ShapeConstants.TYPE_POINT);
		this.x = x;
		this.y = y;
		this.z = 0;
		this.m = 0;
	}
	
	public Point(double x, double y, double z)
	{
		super(-1, ShapeConstants.TYPE_POINTZ);
		this.x = x;
		this.y = y;
		this.z = z;
		this.m = 0;
	}
	
	public Point(double x, double y, double z, double m)
	{
		super(-1, ShapeConstants.TYPE_POINTM);
		this.x = x;
		this.y = y;
		this.z = z;
		this.m = m;
	}
	
	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public double getZ()
	{
		return z;
	}

	public void setZ(double z)
	{
		this.z = z;
	}

	public double getM()
	{
		return m;
	}

	public void setM(double m)
	{
		this.m = m;
	}
	
	
	
}
