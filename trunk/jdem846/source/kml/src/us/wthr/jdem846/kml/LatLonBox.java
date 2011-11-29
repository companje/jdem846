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

package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class LatLonBox extends KmlElement
{
	
	// Longitude
	private double east;
	private double west;
	
	// Latitude
	private double north;
	private double south;
	
	private double rotation = 0;
	
	public LatLonBox()
	{
		
	}
	
	public LatLonBox(double east, double west, double north, double south)
	{
		setEast(east);
		setWest(west);
		setNorth(north);
		setSouth(south);
	}

	public LatLonBox(double west, double south, double cellsize, int rows, int columns)
	{
		setWest(west);
		setSouth(south);
		
		setEast(west + (cellsize * (double)columns));
		setNorth(south + (cellsize * (double)rows));
	}
			
	
	
	public double getRotation()
	{
		return rotation;
	}

	public void setRotation(double rotation)
	{
		this.rotation = rotation;
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
	
	
	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		element.addElement("north").addText(""+north);
		element.addElement("south").addText(""+south);
		element.addElement("east").addText(""+east);
		element.addElement("west").addText(""+west);
		element.addElement("rotation").addText(""+rotation);
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("LatLonBox");
		loadKmlChildren(element);
	}

}
