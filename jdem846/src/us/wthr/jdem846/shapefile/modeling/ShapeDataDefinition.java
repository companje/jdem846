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

import java.util.HashMap;
import java.util.Map;

/*
 <jdem846 spec-version="0.1" generated="Fri Jun 24 10:24:43 EDT 2011">

	<shape-types>
		<name>USGS Transportation - Roads</name>
		<columns>
			<featureTypeColumn definition="cfcc_codes">CFCC_Code</featureTypeColumn>
			<column name="Name">Full_Stree</column>
 */


public class ShapeDataDefinition
{
	
	private String name;
	private String id;
	
	private String featureTypeDefinitionId;
	private String featureTypeColumn;
	
	private Map<String, String> columnNameMap = new HashMap<String, String>();
	
	
	public ShapeDataDefinition()
	{
		
	}
	
	public ShapeDataDefinition(String name, String id)
	{
		this.name = name;
		this.id = id;
	}

	public void addColumn(String name, String header)
	{
		columnNameMap.put(name, header);
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

	public String getFeatureTypeDefinitionId()
	{
		return featureTypeDefinitionId;
	}

	public void setFeatureTypeDefinitionId(String featureTypeDefinitionId)
	{
		this.featureTypeDefinitionId = featureTypeDefinitionId;
	}

	public String getFeatureTypeColumn()
	{
		return featureTypeColumn;
	}

	public void setFeatureTypeColumn(String featureTypeColumn)
	{
		this.featureTypeColumn = featureTypeColumn;
	}

	public Map<String, String> getColumnNameMap()
	{
		return columnNameMap;
	}

	public void setColumnNameMap(Map<String, String> columnNameMap)
	{
		this.columnNameMap = columnNameMap;
	}
	
	
	
	
	
}
