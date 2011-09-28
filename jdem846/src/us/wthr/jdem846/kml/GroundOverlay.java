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

public class GroundOverlay extends KmlElement
{
	
	private String name;
	private Icon icon;
	private LatLonBox latLonBox;
	private int drawOrder = 0;
	
	public GroundOverlay()
	{
		
	}
	
	public GroundOverlay(String name, String iconHref, LatLonBox latLonBox)
	{
		setName(name);
		setIcon(new Icon(iconHref));
		setLatLonBox(latLonBox);
	}
	
	public GroundOverlay(String name, Icon icon, LatLonBox latLonBox)
	{
		setName(name);
		setIcon(icon);
		setLatLonBox(latLonBox);
	}
	
	
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public int getDrawOrder()
	{
		return drawOrder;
	}

	public void setDrawOrder(int drawOrder)
	{
		this.drawOrder = drawOrder;
	}

	public Icon getIcon()
	{
		return icon;
	}

	public void setIcon(Icon icon)
	{
		this.icon = icon;
	}

	public LatLonBox getLatLonBox()
	{
		return latLonBox;
	}

	public void setLatLonBox(LatLonBox latLonBox)
	{
		this.latLonBox = latLonBox;
	}

	@Override
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("		<GroundOverlay>\r\n");
		buffer.append("		<name>" + name + "</name>\r\n");
		
		if (icon != null) {
			buffer.append(icon.toKml());
		}
		
		if (latLonBox != null) {
			buffer.append(latLonBox.toKml());
		}
		
		buffer.append("<drawOrder>" + drawOrder + "</drawOrder>\r\n");
		
		buffer.append("		</GroundOverlay>\r\n");
		
		return buffer.toString();
	}
}
