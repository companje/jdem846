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

package us.wthr.jdem846.shapefile.modeling;

public class FeatureTypeGroup
{
	private String name;
	private String id;
	private FeatureTypeStroke defaultStroke;
	
	
	public FeatureTypeGroup(String name, String id)
	{
		this.name = name;
		this.id = id;
		this.defaultStroke = null;
	}
	
	public FeatureTypeGroup(String name, String id, FeatureTypeStroke defaultStroke)
	{
		this.name = name;
		this.id = id;
		this.defaultStroke = defaultStroke;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public FeatureTypeStroke getDefaultStroke()
	{
		return defaultStroke;
	}

	public void setDefaultStroke(FeatureTypeStroke defaultStroke)
	{
		this.defaultStroke = defaultStroke;
	}
	
	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof FeatureTypeGroup))
			return false;
		
		FeatureTypeGroup other = (FeatureTypeGroup) object;
		
		return this.id.equals(other.id);
	}
	
}
