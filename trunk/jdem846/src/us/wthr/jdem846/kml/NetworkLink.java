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

public class NetworkLink extends KmlElement
{
	private String name;
	private Region region;
	private Link link;
	
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
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public Region getRegion()
	{
		return region;
	}

	public void setRegion(Region region)
	{
		this.region = region;
	}
	
	public void setLink(Link link)
	{
		this.link = link;
	}
	
	public Link getLink()
	{
		return link;
	}
	
	@Override
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<NetworkLink>\r\n");
		
		if (name != null) {
			buffer.append("<name>" + name + "</name>\r\n");
		}
		
		if (region != null) {
			buffer.append(region.toKml());
		}
		
		if (link != null) {
			buffer.append(link.toKml());
		}
		
		buffer.append("</NetworkLink>\r\n");
		return buffer.toString();
	}
}
