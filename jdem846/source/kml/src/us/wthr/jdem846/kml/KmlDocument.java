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

public class KmlDocument extends Container
{
	private String version = "2.2";


	public KmlDocument()
	{
		
	}
	
	
	
	
	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		
		
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("Document");
		loadKmlChildren(element);
	}


}
