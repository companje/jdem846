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

public class Icon extends KmlElement
{
	private String href = null;
	private double viewBoundScale = -1;
	private String refreshMode = null;
	private String viewFreshMode = null;
	
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

	
	
	public String getRefreshMode()
	{
		return refreshMode;
	}

	public void setRefreshMode(String refreshMode)
	{
		this.refreshMode = refreshMode;
	}

	
	
	public String getViewFreshMode()
	{
		return viewFreshMode;
	}

	public void setViewFreshMode(String viewFreshMode)
	{
		this.viewFreshMode = viewFreshMode;
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
	
	
	@Override
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("		<Icon>\r\n");
		buffer.append("			<href>" + href + "</href>\r\n");
		
		if (viewBoundScale != -1) {
			buffer.append("			<viewBoundScale>" + viewBoundScale + "</viewBoundScale>\r\n");
		}
		
		if (refreshMode != null) {
			buffer.append("			<refreshMode>" + refreshMode + "</refreshMode>\r\n");
		}
		
		if (viewFreshMode != null) {
			buffer.append("			<viewFreshMode>" + viewFreshMode + "</viewFreshMode>\r\n");
		}
		
		buffer.append("		</Icon>\r\n");
		return buffer.toString();
	}
}
