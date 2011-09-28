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

public class Placemark extends KmlElement
{
	
	private String name;
	private Geometry geometry;
	
	
	public Placemark(String name, Geometry geometry)
	{
		setName(name);
		setGeometry(geometry);
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Geometry getGeometry()
	{
		return geometry;
	}

	public void setGeometry(Geometry geometry)
	{
		this.geometry = geometry;
	}

	@Override
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<Placemark>\r\n");
		
		if (name != null) {
			buffer.append("<name>" + name + "</name>\r\n");
		}
		
		if (geometry != null) {
			buffer.append(geometry.toKml());
		}
		
		buffer.append("</Placemark>\r\n");
		
		return buffer.toString();
	}
}
