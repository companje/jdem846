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

public class Link extends KmlElement
{
	
	private String href;
	private String refreshMode = null;
	private String viewRefreshMode = null;
	
	public Link()
	{
		
	}
	
	public Link(String href)
	{
		setHref(href);
	}
	
	public String getHref()
	{
		return href;
	}

	public void setHref(String href)
	{
		this.href = href;
	}

	public String getRefreshMode()
	{
		return refreshMode;
	}

	public void setRefreshMode(String refreshMode)
	{
		this.refreshMode = refreshMode;
	}

	public String getViewRefreshMode()
	{
		return viewRefreshMode;
	}

	public void setViewRefreshMode(String viewRefreshMode)
	{
		this.viewRefreshMode = viewRefreshMode;
	}

	@Override
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<Link>\r\n");
		
		if (href != null) {
			buffer.append("<href>" + href + "</href>\r\n");
		}
		
		if (refreshMode != null) {
			buffer.append("<refreshMode>" + refreshMode + "</refreshMode>\r\n");
		}
		
		if (viewRefreshMode != null) {
			buffer.append("<viewRefreshMode>" + viewRefreshMode + "</viewRefreshMode>\r\n");
		}
		
		buffer.append("</Link>\r\n");
		return buffer.toString();
	}
}
