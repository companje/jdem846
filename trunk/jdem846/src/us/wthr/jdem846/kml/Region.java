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

public class Region extends KmlElement
{
	
	private LatLonAltBox latLonAltBox;
	private Lod lod;
	
	public Region(double north, double south, double east, double west)
	{
		
		latLonAltBox = new LatLonAltBox(north, south, east, west);
		
	}

	
	
	public Lod getLod()
	{
		return lod;
	}

	public void setLod(Lod lod)
	{
		this.lod = lod;
	}

	public LatLonAltBox getLatLonAltBox()
	{
		return latLonAltBox;
	}

	public void setLatLonAltBox(LatLonAltBox latLonAltBox)
	{
		this.latLonAltBox = latLonAltBox;
	}

	@Override
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		
		if (id != null) {
			buffer.append("<Region id=\"" + id + "\">\r\n");
		} else {
			buffer.append("<Region>\r\n");
		}
		
		buffer.append(latLonAltBox.toKml());
		
		if (lod != null) {
			buffer.append(lod.toKml());
		}
		
		buffer.append("</Region>\r\n");
		
		return buffer.toString();
	}
	
}
