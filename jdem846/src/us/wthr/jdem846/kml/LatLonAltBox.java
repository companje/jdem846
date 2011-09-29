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

public class LatLonAltBox extends KmlElement
{
	private double VALUE_NOT_SET = -9999.9;
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	private double minAltitude = VALUE_NOT_SET;
	private double maxAltitude = VALUE_NOT_SET;
	
	private AltitudeModeEnum altitudeMode = null;
	
	public LatLonAltBox(double north, double south, double east, double west)
	{
		setNorth(north);
		setSouth(south);
		setEast(east);
		setWest(west);
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
	
	
	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		element.addElement("north").addText(""+north);
		element.addElement("south").addText(""+south);
		element.addElement("east").addText(""+east);
		element.addElement("west").addText(""+west);
		
		if (minAltitude != VALUE_NOT_SET) {
			element.addElement("minAltitude").addText(""+minAltitude);
		}
		
		if (maxAltitude != VALUE_NOT_SET) {
			element.addElement("maxAltitude").addText(""+maxAltitude);
		}
		
		if (altitudeMode != null) {
			element.addElement("altitudeMode").addText(altitudeMode.text());
		}

	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("LatLonAltBox");
		loadKmlChildren(element);
	}
	
	
	
}
