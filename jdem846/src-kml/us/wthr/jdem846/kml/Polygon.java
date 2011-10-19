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

public class Polygon extends Geometry
{

	private boolean extrude = false;
	private AltitudeModeEnum altitudeMode = null;
	private LinearRing outerBoundary = new LinearRing();
	private LinearRing innerBoundary = new LinearRing();
	
	public Polygon()
	{
		
	}
	
	public boolean extrude()
	{
		return extrude;
	}

	public void setExtrude(boolean extrude)
	{
		this.extrude = extrude;
	}


	public AltitudeModeEnum getAltitudeMode()
	{
		return altitudeMode;
	}

	public void setAltitudeMode(AltitudeModeEnum altitudeMode)
	{
		this.altitudeMode = altitudeMode;
	}

	public LinearRing getOuterBoundary()
	{
		return outerBoundary;
	}

	public void setOuterBoundary(LinearRing outerBoundary)
	{
		this.outerBoundary = outerBoundary;
	}

	public LinearRing getInnerBoundary()
	{
		return innerBoundary;
	}

	public void setInnerBoundary(LinearRing innerBoundary)
	{
		this.innerBoundary = innerBoundary;
	}
	
	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		element.addElement("extrude").addText((extrude ? "1" : "0"));
		if (altitudeMode != null) {
			element.addElement("altitudeMode").addText(altitudeMode.text());
		}
		
		if (outerBoundary != null) {
			outerBoundary.toKml(element);
		}
		
		if (innerBoundary != null) {
			innerBoundary.toKml(element);
		}

	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("LinearRing");
		loadKmlChildren(element);
	}
	

	
	
}
