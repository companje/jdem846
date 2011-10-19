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

public class NetworkLink extends Feature
{
	private Link link;
	private boolean flyToView = false;
	private boolean refreshVisibility = false;
	
	public NetworkLink()
	{
		
	}
	
	public NetworkLink(String href)
	{
		link = new Link(href);
	}
	
	public NetworkLink(String name, String href)
	{
		setName(name);
		link = new Link(href);
	}
	
	public boolean isFlyToView()
	{
		return flyToView;
	}

	public void setFlyToView(boolean flyToView)
	{
		this.flyToView = flyToView;
	}

	public boolean isRefreshVisibility()
	{
		return refreshVisibility;
	}

	public void setRefreshVisibility(boolean refreshVisibility)
	{
		this.refreshVisibility = refreshVisibility;
	}

	public void setHref(String href)
	{
		link = new Link(href);
	}
	
	public String getHref()
	{
		if (link != null) {
			return link.getHref();
		} else {
			return null;
		}
	}
	
	public void setLink(Link link)
	{
		this.link = link;
	}
	
	public Link getLink()
	{
		return link;
	}
	
	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		if (link != null) {
			link.toKml(element);
		}
		
		if (flyToView) { // Default is false
			element.addElement("flyToView").addText("1");
		}
		
		if (refreshVisibility) { // Default is false
			element.addElement("refreshVisibility").addText("1");
		}
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("NetworkLink");
		loadKmlChildren(element);
	}
	
	

}
