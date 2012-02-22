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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Properties manager class.
 * 
 * @author Kevin M. Gill
 *
 */
@SuppressWarnings("serial")
public class JDem846Properties 
{

	private static Log log = Logging.getLog(JDem846Properties.class);
	
	private static boolean loaded = false;
	private static Properties properties = new Properties();
	
	static {
		overrideWithSystemProperties();
	}
	
	
	public static boolean hasProperty(String name)
	{
		return properties.containsKey(name);
	}
	
	public static void setProperty(String name, String value)
	{
		properties.setProperty(name, value);
	}
	
	public static String getProperty(String name)
	{
		return properties.getProperty(name);
	}
	
	public static double getDoubleProperty(String name)
	{
		return Double.parseDouble(getProperty(name));
	}
	
	public static int getIntProperty(String name)
	{
		return Integer.parseInt(getProperty(name));
	}
	
	public static boolean getBooleanProperty(String name)
	{
		return Boolean.parseBoolean(getProperty(name));
	}
	
	protected static void load(String path, boolean ignoreFailure)
	{
		try {
			properties.load(JDemResourceLoader.getAsInputStream(path));
			loaded = true;
		} catch (IOException e) {
			log.error("IO error loading properties file from '" + path + "': " + e.getMessage(), e);
			loaded = false;
		}
		overrideWithSystemProperties();
	}
	
	public static boolean isLoaded()
	{
		return loaded;
	}
	
	protected static void overrideWithSystemProperties()
	{
		for (Object key : System.getProperties().keySet()) {
			properties.setProperty((String)key, System.getProperty((String)key));
		}
	}
	
	
	public static void initializeApplicationProperties()
	{
		//load("resources://jdem846.properties", false);
		String path = "resources://jdem846.properties";
		try {
			properties.load(JDemResourceLoader.getAsInputStream(path));
			loaded = true;
		} catch (IOException e) {
			log.error("IO error loading properties file from '" + path + "': " + e.getMessage(), e);
			loaded = false;
		}
		overrideWithSystemProperties();
		
	}
	
	public static void initializeUserProperties()
	{
		String path = "user://.jdem846/application.properties";
		
		InputStream in = null;
		
		try {
			in = JDemResourceLoader.getAsInputStream(path);
		} catch (FileNotFoundException  ex) {
			log.warn("User properties file not found: " + path, ex);
			return;
		}
		
		if (in == null) {
			log.warn("User properties file not found: " + path);
			return;
		}
		
		try {
			properties.load(in);
		} catch (IOException e) {
			log.error("IO error loading properties file from '" + path + "': " + e.getMessage(), e);
		} 
		overrideWithSystemProperties();
	}
	
	
	protected static void verifyUserDirectoryExists() throws Exception
	{
		String path = getProperty("us.wthr.jdem846.user.directory");
		File file = JDemResourceLoader.getAsFile(path);
		
		if (!file.exists()) {
			file.mkdir();
		}
	}
	
	private static boolean propertyMatchesPrefixes(String property, String[] prefixList)
	{
		
		for (String prefix : prefixList) {
			if (property.indexOf(prefix) == 0) {
				return true;
			}
		}
		return false;		
	}
	
	public static void writeUserPropertiesFile() throws Exception
	{
		verifyUserDirectoryExists();
		
		String path = getProperty("us.wthr.jdem846.user.directory") + "/" + getProperty("us.wthr.jdem846.user.properties");
		
		String[] prefixList = getProperty("us.wthr.jdem846.storePropertyPrefixes").split(",");
		
		Properties tmpProps = new Properties();
		
		// Not really the best way to weed out properties we don't want to write...
		for (Object o_key : properties.keySet()) {
			String key = (String) o_key;
			if (propertyMatchesPrefixes(key, prefixList)) {
				tmpProps.setProperty(key, properties.getProperty(key));
			}
			//if (!System.getProperties().containsKey(key)) {
			//	tmpProps.setProperty(key, properties.getProperty(key));
			//}
		}
		
		OutputStream out = JDemResourceLoader.getAsOutputStream(path);
		
		tmpProps.store(out, "Autowritten by " + JDem846Properties.class.getCanonicalName());
		
	}
	
}
