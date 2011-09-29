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

public class Lod extends KmlElement
{
	private static double VALUE_NOT_SET = -9999.9;
	
	
	private double minLodPixels;
	private double maxLodPixels;
	private double minFadeExtent = VALUE_NOT_SET;
	private double maxFadeExtent = VALUE_NOT_SET;
	
	public Lod(double minLodPixels, double maxLodPixels)
	{
		setMinLodPixels(minLodPixels);
		setMaxLodPixels(maxLodPixels);
	}
	
	public Lod(double minLodPixels, double maxLodPixels, double minFadeExtent, double maxFadeExtent)
	{
		setMinLodPixels(minLodPixels);
		setMaxLodPixels(maxLodPixels);
		setMinFadeExtent(minFadeExtent);
		setMaxFadeExtent(maxFadeExtent);
	}
	
	public double getMinLodPixels()
	{
		return minLodPixels;
	}

	public void setMinLodPixels(double minLodPixels)
	{
		this.minLodPixels = minLodPixels;
	}

	public double getMaxLodPixels()
	{
		return maxLodPixels;
	}

	public void setMaxLodPixels(double maxLodPixels)
	{
		this.maxLodPixels = maxLodPixels;
	}

	public double getMinFadeExtent()
	{
		return minFadeExtent;
	}

	public void setMinFadeExtent(double minFadeExtent)
	{
		this.minFadeExtent = minFadeExtent;
	}
	
	public double getMaxFadeExtent()
	{
		return maxFadeExtent;
	}

	public void setMaxFadeExtent(double maxFadeExtent)
	{
		this.maxFadeExtent = maxFadeExtent;
	}

	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		if (minLodPixels != VALUE_NOT_SET) {
			element.addElement("minLodPixels").addText(""+minLodPixels);
		}
		
		if (maxLodPixels != VALUE_NOT_SET) {
			element.addElement("maxLodPixels").addText(""+maxLodPixels);
		}
		
		if (minFadeExtent != VALUE_NOT_SET) {
			element.addElement("minFadeExtent").addText(""+minFadeExtent);
		}
		
		if (maxFadeExtent != VALUE_NOT_SET) {
			element.addElement("maxFadeExtent").addText(""+maxFadeExtent);
		}

	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("Lod");
		loadKmlChildren(element);
	}
	

	
}
