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

public class Link extends KmlElement
{
	
	private String href;
	private String refreshMode = null;
	private String viewRefreshMode = null;
	private float refreshInterval = -1;
	private float viewRefreshTime = -1;
	private float viewBoundScale = -1;
	private String viewFormat = null;
	private String httpQuery = null;
	
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

	public float getRefreshInterval()
	{
		return refreshInterval;
	}

	public void setRefreshInterval(float refreshInterval)
	{
		this.refreshInterval = refreshInterval;
	}

	public float getViewRefreshTime()
	{
		return viewRefreshTime;
	}

	public void setViewRefreshTime(float viewRefreshTime)
	{
		this.viewRefreshTime = viewRefreshTime;
	}

	public float getViewBoundScale()
	{
		return viewBoundScale;
	}

	public void setViewBoundScale(float viewBoundScale)
	{
		this.viewBoundScale = viewBoundScale;
	}

	public String getViewFormat()
	{
		return viewFormat;
	}

	public void setViewFormat(String viewFormat)
	{
		this.viewFormat = viewFormat;
	}

	public String getHttpQuery()
	{
		return httpQuery;
	}

	public void setHttpQuery(String httpQuery)
	{
		this.httpQuery = httpQuery;
	}

	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		if (href != null) {
			element.addElement("href").addText(href);
		}
		
		if (refreshMode != null) {
			element.addElement("refreshMode").addText(refreshMode);
		}
		
		if (viewRefreshMode != null) {
			element.addElement("viewRefreshMode").addText(viewRefreshMode);
		}
		
		if (refreshInterval != -1) {
			element.addElement("refreshInterval").addText(""+refreshInterval);
		}
		
		if (viewRefreshTime != -1) {
			element.addElement("viewRefreshTime").addText(""+viewRefreshTime);
		}
		
		if (viewBoundScale != -1) {
			element.addElement("viewBoundScale").addText(""+viewBoundScale);
		}
		
		if (viewFormat != null) {
			element.addElement("viewFormat").addText(viewFormat);
		}
		
		if (httpQuery != null) {
			element.addElement("httpQuery").addText(httpQuery);
		}
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("Link");
		loadKmlChildren(element);
	}
	

}
