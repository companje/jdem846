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

import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.shapefile.ShapeFileReference;

@Deprecated
public class ProjectModel 
{
	private ProjectTypeEnum projectType = ProjectTypeEnum.STANDARD_PROJECT;
	
	private Map<String, String> optionsMap = new HashMap<String, String>();
	private List<String> inputFiles = new LinkedList<String>();
	private List<ShapeFileReference> shapeFiles = new LinkedList<ShapeFileReference>();
	private List<SimpleGeoImage> imageFiles = new LinkedList<SimpleGeoImage>();
	
	private String userScript = null;
	private ScriptLanguageEnum scriptLanguage = null;
	
	private String loadedFrom = null;
	
	public ProjectModel()
	{
		
	}
	
	public boolean hasOption(String key)
	{
		return optionsMap.containsKey(key);
	}
	
	public boolean hasOption(ModelOptionNamesEnum key)
	{
		return hasOption(key.optionName());
	}

	public void setOption(String key, String value)
	{
		optionsMap.put(key, value);
	}
	
	public void setOption(ModelOptionNamesEnum key, String value)
	{
		setOption(key.optionName(), value);
	}
	
	public void setOption(String key, Object value)
	{
		optionsMap.put(key, value.toString());
	}
	
	public void setOption(ModelOptionNamesEnum key, Object value)
	{
		setOption(key.optionName(), value);
	}
	
	
	
	public String getOption(String key)
	{
		return optionsMap.get(key);
	}
	
	public String getOption(ModelOptionNamesEnum key)
	{
		return getOption(key.optionName());
	}
	
	public int getIntegerOption(String key)
	{
		return Integer.parseInt(getOption(key));
	}
	
	public int getIntegerOption(ModelOptionNamesEnum key)
	{
		return getIntegerOption(key.optionName());
	}
	
	public double getDoubleOption(String key)
	{
		return Double.parseDouble(getOption(key));
	}
	
	public double getDoubleOption(ModelOptionNamesEnum key)
	{
		return getDoubleOption(key.optionName());
	}
	
	public float getFloatOption(String key)
	{
		return Float.parseFloat(getOption(key));
	}
	
	public float getFloatOption(ModelOptionNamesEnum key)
	{
		return getFloatOption(key.optionName());
	}
	
	public boolean getBooleanOption(String key)
	{
		return Boolean.parseBoolean(getOption(key));
	}

	public boolean getBooleanOption(ModelOptionNamesEnum key)
	{
		return getBooleanOption(key.optionName());
	}
	
	
	public String removeOption(String key)
	{
		return optionsMap.remove(key);
	}
	
	public String removeOption(ModelOptionNamesEnum key)
	{
		return removeOption(key.optionName());
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
	
	public List<ShapeFileReference> getShapeFiles()
	{
		return shapeFiles;
	}

	public void setShapeFiles(List<ShapeFileReference> shapeFiles)
	{
		this.shapeFiles = shapeFiles;
	}
	
	public List<SimpleGeoImage> getImageFiles()
	{
		return imageFiles;
	}

	public void setImageFiles(List<SimpleGeoImage> imageFiles)
	{
		this.imageFiles = imageFiles;
	}

	public String getLoadedFrom() 
	{
		return loadedFrom;
	}
	
	public void setLoadedFrom(String loadedFrom) 
	{
		this.loadedFrom = loadedFrom;
	}

	public String getUserScript()
	{
		return userScript;
	}

	public void setUserScript(String userScript)
	{
		this.userScript = userScript;
	}

	public ProjectTypeEnum getProjectType()
	{
		return projectType;
	}
	
	public void setProjectType(ProjectTypeEnum projectType)
	{
		this.projectType = projectType;
	}
	
	public void setProjectType(String identifier)
	{
		this.projectType = ProjectTypeEnum.getProjectTypeFromIdentifier(identifier);
	}
	
	public ScriptLanguageEnum getScriptLanguage()
	{
		return scriptLanguage;
	}

	public void setScriptLanguage(ScriptLanguageEnum scriptLanguage)
	{
		this.scriptLanguage = scriptLanguage;
	}
	
	public void setScriptLanguage(String scriptLanguageString)
	{
		ScriptLanguageEnum scriptLanguage = ScriptLanguageEnum.getLanguageFromString(scriptLanguageString);
		this.setScriptLanguage(scriptLanguage);
	}
	
	
	
}
