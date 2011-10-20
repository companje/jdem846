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

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;

public class LinearRing extends Geometry
{
	
	private List<Coordinate> coordinates = new LinkedList<Coordinate>();
	
	public LinearRing()
	{
		
	}
	
	public void setCoordinates(List<Coordinate> coordinates)
	{
		this.coordinates = coordinates;
	}
	
	public void addCoordinate(Coordinate coordinate)
	{
		coordinates.add(coordinate);
	}
	
	public List<Coordinate> getCoordinates()
	{
		return coordinates;
	}
	
	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		StringBuffer coordsBuffer = new StringBuffer();
		for (Coordinate coordinate : coordinates) {
			coordsBuffer.append(coordinate.toString() + "\r\n");
		}
		
		Element coordsElement = element.addElement("coordinates");
		coordsElement.addText(coordsBuffer.toString());
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("LinearRing");
		loadKmlChildren(element);
	}

}
