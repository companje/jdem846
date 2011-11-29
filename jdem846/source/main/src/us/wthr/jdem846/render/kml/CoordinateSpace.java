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

package us.wthr.jdem846.render.kml;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

@SuppressWarnings("serial")
public class CoordinateSpace extends Path2D.Double
{
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	
	public CoordinateSpace(double north, double south, double east, double west)
	{
		setNorth(north);
		setSouth(south);
		setEast(east);
		setWest(west);
		
		
		//
		//       NW       NE
		//       |        ^
		//       |        |
		//       |        |
		//       V        |
		//       SW------>SE
		//
		
		
		
		
		this.moveTo(west, north);
		this.lineTo(west, south);
		this.lineTo(east, south);
		this.lineTo(east, north);
		this.closePath();
		
	}


	public double getNorth()
	{
		return north;
	}


	public void setNorth(double north)
	{
		this.north = north;
	}


	public double getSouth()
	{
		return south;
	}


	public void setSouth(double south)
	{
		this.south = south;
	}


	public double getEast()
	{
		return east;
	}


	public void setEast(double east)
	{
		this.east = east;
	}


	public double getWest()
	{
		return west;
	}


	public void setWest(double west)
	{
		this.west = west;
	}
	
	
	
}
