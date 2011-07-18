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

public class Box 
{
	private double xMin;
	private double xMax;
	private double yMin;
	private double yMax;
	private double zMin;
	private double zMax;
	private double mMin;
	private double mMax;
	
	
	public Box()
	{
		
	}
	
	public Box(double xMax, double xMin, double yMax, double yMin)
	{
		this.xMax = xMax;
		this.xMin = xMin;
		this.yMax = yMax;
		this.yMin = yMin;
		this.zMax = 0;
		this.zMin = 0;
		this.mMax = 0;
		this.mMin = 0;
	}
	
	public Box(double xMax, double xMin, double yMax, double yMin, double zMax, double zMin)
	{
		this.xMax = xMax;
		this.xMin = xMin;
		this.yMax = yMax;
		this.yMin = yMin;
		this.zMax = zMax;
		this.zMin = zMin;
		this.mMax = 0;
		this.mMin = 0;
	}
	
	public Box(double xMax, double xMin, double yMax, double yMin, double zMax, double zMin, double mMax, double mMin)
	{
		this.xMax = xMax;
		this.xMin = xMin;
		this.yMax = yMax;
		this.yMin = yMin;
		this.zMax = zMax;
		this.zMin = zMin;
		this.mMax = mMax;
		this.mMin = mMin;
	}

	public double getxMin()
	{
		return xMin;
	}

	public void setxMin(double xMin)
	{
		this.xMin = xMin;
	}

	public double getxMax()
	{
		return xMax;
	}

	public void setxMax(double xMax)
	{
		this.xMax = xMax;
	}

	public double getyMin()
	{
		return yMin;
	}

	public void setyMin(double yMin)
	{
		this.yMin = yMin;
	}

	public double getyMax()
	{
		return yMax;
	}

	public void setyMax(double yMax)
	{
		this.yMax = yMax;
	}

	public double getzMin()
	{
		return zMin;
	}

	public void setzMin(double zMin)
	{
		this.zMin = zMin;
	}

	public double getzMax()
	{
		return zMax;
	}

	public void setzMax(double zMax)
	{
		this.zMax = zMax;
	}

	public double getmMin()
	{
		return mMin;
	}

	public void setmMin(double mMin)
	{
		this.mMin = mMin;
	}

	public double getmMax()
	{
		return mMax;
	}

	public void setmMax(double mMax)
	{
		this.mMax = mMax;
	}
	
	public String toString()
	{
		String s = "[" + xMax + "," + xMin + "," + yMax + "," + yMin + "," + zMax + "," + zMin + "," + mMax + "," + mMin + "]";
		return s;
	}
	
}
