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

import java.util.LinkedList;

import java.util.List;


public class FeatureTypesDefinition
{

	private String name;
	private String id;
	
	private FeatureType defaultFeatureType = null;
	
	private List<FeatureTypeGroup> featureTypeGroups = new LinkedList<FeatureTypeGroup>();
	private List<FeatureType> featureTypes = new LinkedList<FeatureType>();
	
	public FeatureTypesDefinition(String name, String id)
	{
		this.name = name;
		this.id = id;
		
		defaultFeatureType = FeatureType.makeDefaultFeatureType(); 
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

	public void addFeatureTypeGroup(FeatureTypeGroup featureTypeGroup)
	{
		this.featureTypeGroups.add(featureTypeGroup);
	}
	
	public FeatureTypeGroup getFeatureTypeGroup(String id)
	{
		for (FeatureTypeGroup featureTypeGroup : featureTypeGroups) {
			if (featureTypeGroup.getId().equals(id))
				return featureTypeGroup;
		}
		return null;
	}
	
	public List<FeatureTypeGroup> getFeatureTypeGroups()
	{
		return featureTypeGroups;
	}

	public void setFeatureTypeGroups(List<FeatureTypeGroup> featureTypeGroups)
	{
		this.featureTypeGroups = featureTypeGroups;
	}

	public FeatureType getFeatureType(String code)
	{
		for (FeatureType featureType : featureTypes) {
			if (featureType.getCode().equals(code))
				return featureType;
		}
		
		return null;
	}
	
	public void addFeatureType(FeatureType featureType)
	{
		this.featureTypes.add(featureType);
	}
	
	public List<FeatureType> getFeatureTypes()
	{
		return featureTypes;
	}

	public void setFeatureTypes(List<FeatureType> featureTypes)
	{
		this.featureTypes = featureTypes;
	}

	
	
	public FeatureType getDefaultFeatureType()
	{
		return defaultFeatureType;
	}

	public void setDefaultFeatureType(FeatureType defaultFeatureType)
	{
		this.defaultFeatureType = defaultFeatureType;
	}

	public FeatureTypeStroke getDefaultStroke()
	{
		return defaultFeatureType.getFeatureTypeStroke();
	}

	public void setDefaultStroke(FeatureTypeStroke defaultStroke)
	{
		defaultFeatureType.setFeatureTypeStroke(defaultStroke);
	}
	
	
	
	
}
