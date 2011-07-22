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

package us.wthr.jdem846.project;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.wthr.jdem846.shapefile.ShapeFileRequest;


public class ProjectModel 
{

	
	private Map<String, String> optionsMap = new HashMap<String, String>();
	private List<String> inputFiles = new LinkedList<String>();
	private List<ShapeFileRequest> shapeFiles = new LinkedList<ShapeFileRequest>();
	
	private String loadedFrom = null;
	
	public ProjectModel()
	{
		
	}
	
	

	public void setOption(String key, String value)
	{
		optionsMap.put(key, value);
	}
	
	public void setOption(String key, Object value)
	{
		optionsMap.put(key, value.toString());
	}
	
	public String getOption(String key)
	{
		return optionsMap.get(key);
	}
	
	public int getIntegerOption(String key)
	{
		return Integer.parseInt(getOption(key));
	}
	
	public double getDoubleOption(String key)
	{
		return Double.parseDouble(getOption(key));
	}
	
	public float getFloatOption(String key)
	{
		return Float.parseFloat(getOption(key));
	}
	
	public boolean getBooleanOption(String key)
	{
		return Boolean.parseBoolean(getOption(key));
	}
	
	
	
	public String removeOption(String key)
	{
		return optionsMap.remove(key);
	}
	
	
	public Set<String> getOptionKeys()
	{
		return optionsMap.keySet();
	}
	
	
	public List<String> getInputFiles() 
	{
		return inputFiles;
	}

	public void setInputFiles(List<String> inputFiles) 
	{
		this.inputFiles = inputFiles;
	}
	
	public List<ShapeFileRequest> getShapeFiles()
	{
		return shapeFiles;
	}

	public void setShapeFiles(List<ShapeFileRequest> shapeFiles)
	{
		this.shapeFiles = shapeFiles;
	}

	public String getLoadedFrom() 
	{
		return loadedFrom;
	}
	
	public void setLoadedFrom(String loadedFrom) 
	{
		this.loadedFrom = loadedFrom;
	}
	
	
	
	
	
}
