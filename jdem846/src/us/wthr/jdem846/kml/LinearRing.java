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

public class LinearRing extends Geometry
{
	
	private List<Coordinate> coordinates = new LinkedList<Coordinate>();
	
	public LinearRing()
	{
		
	}
	
	public void addCoordinate(Coordinate coordinate)
	{
		coordinates.add(coordinate);
	}
	
	public List<Coordinate> getCoordinates()
	{
		return coordinates;
	}
	
	
	@Override
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<LinearRing>\r\n");
		buffer.append("	<coordinates>\r\n");
		
		for (Coordinate coordinate : coordinates) {
			buffer.append(coordinate.toKml() + "\r\n");
		}
		
		buffer.append("	</coordinates>\r\n");
		buffer.append("</LinearRing>\r\n");
		return buffer.toString();
	}
}
