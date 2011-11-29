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

package us.wthr.jdem846.color;

import us.wthr.jdem846.dbase.ClassLoadException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ColoringInstance
{
	private static Log log = Logging.getLog(ColoringInstance.class);
	
	private String clazzName;
	private String name;
	private String identifier;
	private boolean requiresMinMaxElevation;
	private boolean allowGradientConfig;
	
	private ModelColoring coloringImpl = null;
	
	public ColoringInstance(GradientColoring gradientColoring) throws Exception
	{
		coloringImpl = gradientColoring;
		this.clazzName = GradientColoring.class.getCanonicalName();
		this.name = gradientColoring.getName();
		this.identifier = gradientColoring.getIdentifier();
		this.requiresMinMaxElevation = gradientColoring.needsMinMaxElevation();
		allowGradientConfig = true;
	}
			
	
	public ColoringInstance(String clazzName, String name, String identifier, boolean allowGradientConfig, boolean requiresMinMaxElevation) throws ClassLoadException
	{
		this.clazzName = clazzName;
		this.name = name;
		this.identifier = identifier;
		this.allowGradientConfig = allowGradientConfig;
		this.requiresMinMaxElevation = requiresMinMaxElevation;
		
		try {
			Class<?> impl = Class.forName(clazzName, true, Thread.currentThread().getContextClassLoader());
			coloringImpl = (ModelColoring) impl.newInstance();
		} catch (Exception ex) {
			log.error("Error loading coloring implementation class '" + clazzName + "': " + ex.getMessage(), ex);
			throw new ClassLoadException(clazzName, "Error loading coloring impementation class '" + clazzName + "'", ex);
		} 
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getIdentifier()
	{
		return identifier;
	}
	
	public String getClassName()
	{
		return clazzName;
	}
	
	public boolean allowGradientConfig()
	{
		return allowGradientConfig;
	}
	
	public boolean requiresMinMaxElevation()
	{
		return requiresMinMaxElevation;
	}
	
	public ModelColoring getImpl()
	{
		return this.coloringImpl;
	}
	
}
