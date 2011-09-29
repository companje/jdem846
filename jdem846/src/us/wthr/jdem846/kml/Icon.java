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

public class Icon extends KmlElement
{
	private String href = null;
	private double viewBoundScale = -1;
	private RefreshModeEnum refreshMode = null;
	private ViewRefreshModeEnum viewFreshMode = null;
	private double refreshInterval = -1;
	private String viewFormat = null;
	private String httpQuery = null;
	
	
	public Icon()
	{
		
	}
	
	public Icon(String href)
	{
		setHref(href);
	}
	
	public Icon(String href, double viewBoundScale)
	{
		setHref(href);
		setViewBoundScale(viewBoundScale);
	}

	public String getHref()
	{
		return href;
	}

	public void setHref(String href)
	{
		this.href = href;
	}

	public double getViewBoundScale()
	{
		return viewBoundScale;
	}

	public void setViewBoundScale(double viewBoundScale)
	{
		this.viewBoundScale = viewBoundScale;
	}
	
	public RefreshModeEnum getRefreshMode()
	{
		return refreshMode;
	}

	public void setRefreshMode(RefreshModeEnum refreshMode)
	{
		this.refreshMode = refreshMode;
	}

	public ViewRefreshModeEnum getViewFreshMode()
	{
		return viewFreshMode;
	}

	public void setViewFreshMode(ViewRefreshModeEnum viewFreshMode)
	{
		this.viewFreshMode = viewFreshMode;
	}

	public double getRefreshInterval()
	{
		return refreshInterval;
	}

	public void setRefreshInterval(double refreshInterval)
	{
		this.refreshInterval = refreshInterval;
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
		
		if (viewBoundScale != -1) {
			element.addElement("viewBoundScale").addText(""+viewBoundScale);
		}
		
		if (refreshMode != null) {
			element.addElement("refreshMode").addText(refreshMode.text());
		}
	
		if (viewFreshMode != null) {
			element.addElement("viewFreshMode").addText(viewFreshMode.text());
		}
		
		if (refreshInterval != -1) {
			element.addElement("refreshInterval").addText(""+refreshInterval);
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
		Element element = parent.addElement("Icon");
		loadKmlChildren(element);
	}
	
	
	
}
