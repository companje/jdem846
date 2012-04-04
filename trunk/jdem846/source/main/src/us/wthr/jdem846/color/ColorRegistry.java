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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.wthr.jdem846.AppRegistry;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.Registry;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@Registry
@Deprecated
public class ColorRegistry implements AppRegistry 
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(ColorRegistry.class);
	
	@SuppressWarnings("unused")
	private static Map<String, ColorInstance> instances = new HashMap<String, ColorInstance>();
	
	
	static {

	}
	
	protected ColorRegistry()
	{
		
	}
	
	
	protected static void addColor(String fromFile)
	{
		/*
		fromFile = fromFile.replaceAll("[ ]+", " ");
		String[] parts = fromFile.split(" ");
		if (parts.length == 5) {
			log.info("Adding color: " + fromFile);
			//System.out.println("Adding color: " + fromFile);
			String name = parts[0];
			
			name = I18N.get(name, name);
			
			int red = Integer.parseInt(parts[1]);
			int green = Integer.parseInt(parts[2]);
			int blue = Integer.parseInt(parts[3]);
			int alpha = Integer.parseInt(parts[4]);
			instances.put(name, new ColorInstance(name, name, red, green, blue, alpha));
		} else {
			log.warn("Invalid color: " + fromFile);
			//System.out.println("Invalid color: " + fromFile);
		}
		*/
	}
	
	@Initialize
	public static void init()
	{
		//System.out.println("Static initialization of ColorRegistry");
		/*
		log.info("Static initialization of ColorRegistry");
		
		try {
			InputStream in = ColorRegistry.class.getResourceAsStream(JDem846Properties.getProperty("us.wthr.jdem846.color") + "/colors.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				ColorRegistry.addColor(line);
			}
			reader.close();
		} catch (FileNotFoundException ex) {
			log.error("Colors file not found: " + ex.getMessage(), ex);
			//ex.printStackTrace();
		} catch (IOException ex) {
			log.error("IO Exception attempting to read colors file: " + ex.getMessage(), ex);
			//ex.printStackTrace();
		}
		*/
	}

	public static ColorInstance getInstance(String identifier)
	{
		return null;//instances.get(identifier);
	}
	
	public static List<ColorInstance> getInstances()
	{
		/*
		List<ColorInstance> instanceList = new LinkedList<ColorInstance>();
		
		for (String identifier : instances.keySet()) {
			instanceList.add(instances.get(identifier));
		}
		
		return instanceList;
		*/
		return null;
	}
}
