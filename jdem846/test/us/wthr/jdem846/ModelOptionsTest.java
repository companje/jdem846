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

package us.wthr.jdem846;

import junit.framework.TestCase;

public class ModelOptionsTest extends TestCase
{
	
	private JDem846Properties properties;
	
	@Override
	protected void setUp() throws Exception
	{
		//properties = new JDem846Properties(JDem846Properties.CORE_PROPERTIES);
		properties = JDem846Properties.getInstance();
		super.setUp();
	}
	
	public void testDefaultProperties()
	{
		
		ModelOptions modelOptions = new ModelOptions();
		assert modelOptions.getEngine().equals(properties.getProperty("us.wthr.jdem846.modelOptions.engine"));
		assert modelOptions.getColoringType().equals(properties.getProperty("us.wthr.jdem846.modelOptions.coloringType"));
		assert modelOptions.getBackgroundColor().equals(properties.getProperty("us.wthr.jdem846.modelOptions.backgroundColor"));
		assert modelOptions.isHillShading() == Boolean.parseBoolean(properties.getProperty("us.wthr.jdem846.modelOptions.hillShading"));
		assert modelOptions.getWriteTo().equals(properties.getProperty("us.wthr.jdem846.modelOptions.writeTo"));
		assert modelOptions.getWidth() == Integer.parseInt(properties.getProperty("us.wthr.jdem846.modelOptions.width"));
		assert modelOptions.getHeight() == Integer.parseInt(properties.getProperty("us.wthr.jdem846.modelOptions.height"));
		assert modelOptions.getHillShadeType() == Integer.parseInt(properties.getProperty("us.wthr.jdem846.modelOptions.hillShadeType"));
		assert modelOptions.getTileSize() == Integer.parseInt(properties.getProperty("us.wthr.jdem846.modelOptions.tileSize"));
		assert modelOptions.getLightingMultiple() == Double.parseDouble(properties.getProperty("us.wthr.jdem846.modelOptions.lightingMultiple"));
		
		/*
		 * 
		 * us.wthr.jdem846.modelOptions.engine=dem2d-gen
us.wthr.jdem846.modelOptions.width=3000
us.wthr.jdem846.modelOptions.height=3000
us.wthr.jdem846.modelOptions.hillShading=true
us.wthr.jdem846.modelOptions.hillShadeType=1000
us.wthr.jdem846.modelOptions.coloringType=hypsometric-tint
us.wthr.jdem846.modelOptions.tileSize=1000
us.wthr.jdem846.modelOptions.lightingMultiple=0.75
us.wthr.jdem846.modelOptions.backgroundColor=Blue
us.wthr.jdem846.modelOptions.writeTo=
		 */
		
		
	}
	
	
}
