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

public class FeatureType
{

	private String code;
	private FeatureTypeGroup featureTypeGroup;
	private String description;
	private FeatureTypeStroke featureTypeStroke;
	
	public FeatureType(String code, FeatureTypeGroup featureTypeGroup, String description)
	{
		this.code = code;
		this.featureTypeGroup = featureTypeGroup;
		this.description = description;
		this.featureTypeStroke = null;
	}
	
	public FeatureType(String code, FeatureTypeGroup featureTypeGroup, String description, FeatureTypeStroke featureTypeStroke)
	{
		this.code = code;
		this.featureTypeGroup = featureTypeGroup;
		this.description = description;
		this.featureTypeStroke = featureTypeStroke;
	}
	
	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public FeatureTypeGroup getFeatureTypeGroup()
	{
		return featureTypeGroup;
	}

	public void setFeatureTypeGroup(FeatureTypeGroup featureTypeGroup)
	{
		this.featureTypeGroup = featureTypeGroup;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public FeatureTypeStroke getFeatureTypeStroke()
	{
		return featureTypeStroke;
	}

	public void setFeatureTypeStroke(FeatureTypeStroke featureTypeStroke)
	{
		this.featureTypeStroke = featureTypeStroke;
	}

	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof FeatureType))
			return false;
		
		FeatureType other = (FeatureType) object;
		
		return this.code.equals(other.code);
	}
	
	
	public static FeatureType makeDefaultFeatureType()
	{
		FeatureType featureType = new FeatureType("", null, "");
		featureType.setFeatureTypeStroke(FeatureTypeStroke.getDefaultFeatureTypeStroke());
		return featureType;
	}
	
}
