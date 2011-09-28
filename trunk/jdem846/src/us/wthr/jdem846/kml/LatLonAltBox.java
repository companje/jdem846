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

public class LatLonAltBox extends KmlElement
{
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	
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

	@Override
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<LatLonAltBox>\r\n");
		
		buffer.append("<north>" + north + "</north>\r\n");
		buffer.append("<south>" + south + "</south>\r\n");
		buffer.append("<east>" + east + "</east>\r\n");
		buffer.append("<west>" + west + "</west>\r\n");
		
		buffer.append("</LatLonAltBox>\r\n");
		return buffer.toString();
	}
	
	
}
