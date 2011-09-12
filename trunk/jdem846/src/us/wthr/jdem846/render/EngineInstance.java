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

import us.wthr.jdem846.dbase.ClassLoadException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


public class EngineInstance {
	
	private static Log log = Logging.getLog(EngineInstance.class);
	
	private String clazzName;
	private String name;
	private String identifier;
	//private RenderEngine engineImpl = null;
	private boolean enabled;
	private boolean usesWidth;
	private boolean usesHeight;
	private boolean usesBackgroundColor;
	private boolean usesColoring;
	private boolean usesHillshading;
	private boolean usesLightMultiple;
	private boolean usesSpotExponent;
	private boolean usesTileSize;
	private boolean usesElevationMultiple;
	private boolean usesLightDirection;
	private boolean usesProjection;
	private boolean generatesImage;
	private boolean usesAntialiasing;
	private boolean usesPrecacheStrategy;
	
	@SuppressWarnings("unchecked")
	private Class needsOutputFileOfType;
	
	public EngineInstance(String clazzName, String name, String identifier)
	{
		this.clazzName = clazzName;
		this.name = name;
		this.identifier = identifier;
		
	
		
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
		return enabled;
	}


	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}


	public boolean getGeneratesImage() 
	{
		return generatesImage;
	}


	public void setGeneratesImage(boolean generatesImage) 
	{
		this.generatesImage = generatesImage;
	}


	public boolean usesWidth()
	{
		return usesWidth;
	}


	public void setUsesWidth(boolean usesWidth)
	{
		this.usesWidth = usesWidth;
	}


	public boolean usesHeight() 
	{
		return usesHeight;
	}


	public void setUsesHeight(boolean usesHeight) 
	{
		this.usesHeight = usesHeight;
	}


	public boolean usesBackgroundColor() 
	{
		return usesBackgroundColor;
	}


	public void setUsesBackgroundColor(boolean usesBackgroundColor) 
	{
		this.usesBackgroundColor = usesBackgroundColor;
	}


	public boolean usesColoring() 
	{
		return usesColoring;
	}


	public void setUsesColoring(boolean usesColoring)
	{
		this.usesColoring = usesColoring;
	}


	public boolean usesHillshading() 
	{
		return usesHillshading;
	}


	public void setUsesHillshading(boolean usesHillshading)
	{
		this.usesHillshading = usesHillshading;
	}

	
	
	public boolean usesLightDirection()
	{
		return usesLightDirection;
	}


	public void setUsesLightDirection(boolean usesLightDirection)
	{
		this.usesLightDirection = usesLightDirection;
	}


	public boolean usesSpotExponent()
	{
		return usesSpotExponent;
	}


	public void setUsesSpotExponent(boolean usesSpotExponent)
	{
		this.usesSpotExponent = usesSpotExponent;
	}


	public boolean usesLightMultiple() 
	{
		return usesLightMultiple;
	}


	public void setUsesLightMultiple(boolean usesLightMultiple)
	{
		this.usesLightMultiple = usesLightMultiple;
	}


	public boolean usesTileSize() 
	{
		return usesTileSize;
	}


	public void setUsesTileSize(boolean usesTileSize) 
	{
		this.usesTileSize = usesTileSize;
	}


	
	
	public boolean usesElevationMultiple()
	{
		return usesElevationMultiple;
	}


	public void setUsesElevationMultiple(boolean usesElevationMultiple)
	{
		this.usesElevationMultiple = usesElevationMultiple;
	}


	public boolean usesProjection()
	{
		return usesProjection;
	}


	public void setUsesProjection(boolean usesProjection)
	{
		this.usesProjection = usesProjection;
	}

	
	

	public boolean usesAntialiasing()
	{
		return usesAntialiasing;
	}


	public void setUsesAntialiasing(boolean usesAntialiasing)
	{
		this.usesAntialiasing = usesAntialiasing;
	}


	public boolean usesPrecacheStrategy()
	{
		return usesPrecacheStrategy;
	}


	public void setUsesPrecacheStrategy(boolean usesPrecacheStrategy)
	{
		this.usesPrecacheStrategy = usesPrecacheStrategy;
	}


	@SuppressWarnings("unchecked")
	public Class getNeedsOutputFileOfType()
	{
		return needsOutputFileOfType;
	}


	@SuppressWarnings("unchecked")
	public void setNeedsOutputFileOfType(Class needsOutputFileOfType)
	{
		this.needsOutputFileOfType = needsOutputFileOfType;
	}
	
	
	
	
}
