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

import java.io.IOException;
import java.util.Properties;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Properties manager class.
 * 
 * @author Kevin M. Gill
 *
 */
@SuppressWarnings("serial")
public class JDem846Properties extends Properties
{
	private static Log log = Logging.getLog(JDem846Properties.class);
	private static JDem846Properties instance = null;
	
	static {
		JDem846Properties.instance = new JDem846Properties();
	}
	
	
	public static final String CORE_PROPERTIES_PATH = "/us/wthr/jdem846/jdem846.properties";
	
	private boolean loaded = false;
	
	public JDem846Properties()
	{
		load(CORE_PROPERTIES_PATH);
	}
	
	
	public JDem846Properties(String propertiesPath)
	{
		load(propertiesPath);
	}
	
	public double getDoubleProperty(String name)
	{
		return Double.parseDouble(getProperty(name));
	}
	
	public int getIntProperty(String name)
	{
		return Integer.parseInt(getProperty(name));
	}
	
	public boolean getBooleanProperty(String name)
	{
		return Boolean.parseBoolean(getProperty(name));
	}
	
	protected void load(String path)
	{
		try {
			load(getClass().getResourceAsStream(path));
			loaded = true;
		} catch (IOException e) {
			log.error("IO error loading properties file from '" + path + "': " + e.getMessage(), e);
			loaded = false;
		}
		overrideWithSystemProperties();
	}
	
	public boolean isLoaded()
	{
		return loaded;
	}
	
	protected void overrideWithSystemProperties()
	{
		for (Object key : System.getProperties().keySet()) {
			this.setProperty((String)key, System.getProperty((String)key));
		}
	}
	
	public static JDem846Properties getInstance()
	{
		return JDem846Properties.instance;
	}
}
