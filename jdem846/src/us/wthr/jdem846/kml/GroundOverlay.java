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

public class GroundOverlay extends Overlay
{
	
	
	private AltitudeModeEnum altitudeMode = null;
	private LatLonBox latLonBox;
	
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
	




	public LatLonBox getLatLonBox()
	{
		return latLonBox;
	}

	public void setLatLonBox(LatLonBox latLonBox)
	{
		this.latLonBox = latLonBox;
	}

	
	
	
	public AltitudeModeEnum getAltitudeMode()
	{
		return altitudeMode;
	}

	public void setAltitudeMode(AltitudeModeEnum altitudeMode)
	{
		this.altitudeMode = altitudeMode;
	}

	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		
		
		if (altitudeMode != null) {
			element.addElement("altitudeMode").addText(altitudeMode.text());
		}
		
		if (latLonBox != null) {
			latLonBox.toKml(element);
		}

	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("GroundOverlay");
		loadKmlChildren(element);
	}
	
	
}
