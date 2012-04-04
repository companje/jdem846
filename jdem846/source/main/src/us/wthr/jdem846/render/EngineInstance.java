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

package us.wthr.jdem846.render;

import us.wthr.jdem846.annotations.DemEngine;
import us.wthr.jdem846.dbase.ClassLoadException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


public class EngineInstance {
	
	private static Log log = Logging.getLog(EngineInstance.class);
	
	private String clazzName;
	private String name;
	private String identifier;
	
	private DemEngine annotation;
	

	public EngineInstance(Class<?> clazz)
	{

		annotation = (DemEngine) clazz.getAnnotation(DemEngine.class);

		String name = annotation.name();
		this.name = I18N.get(name, name);
		this.clazzName = clazz.getName();
		this.identifier = annotation.identifier();
		
	}


	public String getClazzName() 
	{
		return clazzName;
	}


	public String getName() 
	{
		return name;
	}


	public String getIdentifier() 
	{
		return identifier;
	}
	
	public RenderEngine getImpl() throws ClassLoadException
	{
		try {
			Class<?> impl = Class.forName(clazzName, true, Thread.currentThread().getContextClassLoader());
			RenderEngine engineImpl = (RenderEngine) impl.newInstance();
			return engineImpl;
		} catch (Exception ex) {
			log.error("Failed to load engine instance class '" + clazzName + "': " + ex.getMessage(), ex);
			throw new ClassLoadException(clazzName, "Failed to load engine instance class '" + clazzName + "'", ex);
		}
	}

	
	

	public boolean isEnabled()
	{
		return annotation.enabled();
	}


	public boolean getGeneratesImage() 
	{
		return annotation.generatesImage();
	}


	public boolean usesWidth()
	{
		return annotation.usesWidth();
	}


	public boolean usesHeight() 
	{
		return annotation.usesHeight();
	}

	public boolean usesBackgroundColor() 
	{
		return annotation.usesBackgroundColor();
	}


	public boolean usesColoring() 
	{
		return annotation.usesColoring();
	}

	public boolean usesHillshading() 
	{
		return annotation.usesHillshading();
	}


	
	public boolean usesLightDirection()
	{
		return annotation.usesLightDirection();
	}

	public boolean usesSpotExponent()
	{
		return annotation.usesSpotExponent();
	}


	public boolean usesLightMultiple() 
	{
		return annotation.usesLightMultiple();
	}

	public boolean usesRelativeLightMultiple()
	{
		return annotation.usesRelativeLightMultiple();
	}


	public boolean usesRelativeDarkMultiple()
	{
		return annotation.usesRelativeDarkMultiple();
	}


	public boolean usesTileSize() 
	{
		return annotation.usesTileSize();
	}

	
	
	public boolean usesElevationMultiple()
	{
		return annotation.usesElevationMultiple();
	}


	public boolean uses3DProjection()
	{
		return annotation.usesProjection();
	}

	

	public boolean usesAntialiasing()
	{
		return annotation.usesAntialiasing();
	}



	public boolean usesPrecacheStrategy()
	{
		return annotation.usesPrecacheStrategy();
	}


	public boolean usesMapProjection()
	{
		return annotation.usesMapProjection();
	}


	
	public Class getNeedsOutputFileOfType()
	{
		return annotation.needsOutputFileOfType();
	}


	
	
	
}
