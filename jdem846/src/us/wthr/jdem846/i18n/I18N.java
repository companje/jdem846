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

package us.wthr.jdem846.i18n;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Provides internationalization of strings
 * 
 * @author Kevin M. Gill
 *
 */
public class I18N
{
	private static Log log = Logging.getLog(I18N.class);
			
	protected static Map<String, String> messages = new HashMap<String, String>();
	
	
	static {
		try {
			I18N.setDefaults();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setDefaults() throws Exception
	{
		I18N.messages.clear();
		
		//JDem846Properties uiProperties = new JDem846Properties(JDem846Properties.UI_PROPERTIES);
		String i18nDefaultLang = JDem846Properties.getProperty("us.wthr.jdem846.ui.i18n.default");
		
		try {
			I18N.loadLanguage(i18nDefaultLang, false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	public static void loadLanguage(String languageCode) throws Exception
	{
		I18N.loadLanguage(languageCode, true);
	}
	
	public static void loadLanguage(String languageCode, boolean reset) throws Exception
	{
		if (reset)
			I18N.setDefaults();
		
		String i18nPropertiesFile = JDem846Properties.getProperty("us.wthr.jdem846.i18n") + "/messages_" + languageCode + ".properties";
		I18N.loadLanguageFile(i18nPropertiesFile);
	}
	
	public static void loadLanguageFile(String i18nPropertiesFile) throws Exception
	{
		Properties props = new Properties();
		try {

			props.load(I18N.class.getResourceAsStream(i18nPropertiesFile));
			
			for (Object key : props.keySet()) {
				String sKey = (String) key;
				String value = props.getProperty(sKey);
				I18N.messages.put(sKey, value);
			}
			
			log.info("Loaded internationalization properties file: " + i18nPropertiesFile);
		} catch (Exception ex) {
			log.error("Failed to load i18n properties", ex);
			throw ex;
		}
		
		
	}
	
	/** Gets a string given the specified key.
	 * 
	 * @param key A key identifier
	 * @return The value string or null if not found.
	 */
	public static String get(String key)
	{
		return I18N.get(key, null);
	}
	
	/** Gets a string given the specified key, or returns the value of 'ifNull' if the key has not
	 * been defined.
	 * 
	 * @param key A key identifier
	 * @param ifNull A value to return if the key has not been defined.
	 * @return The value string or ifNull if the key has not been defined.
	 */
	public static String get(String key, String ifNull)
	{
		String value = I18N.messages.get(key);
		if (value != null)
			return value;
		else
			return ifNull;
	}
	
}
