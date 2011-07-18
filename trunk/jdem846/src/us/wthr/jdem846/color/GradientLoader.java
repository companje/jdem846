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

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class GradientLoader 
{
	private static Log log = Logging.getLog(GradientLoader.class);
	
	private List<GradientColorStop> colorStops = new LinkedList<GradientColorStop>();
	
	public GradientLoader(String configString)
	{
		load(configString);
	}
	
	public GradientLoader(URL url)
	{
		load(url);
	}
	
	public GradientLoader()
	{
		load(this.getClass().getResource("hypsometric.gradient"));
	}
	
	
	public void load(URL url)
	{
		try {
			StringBuilder configString = new StringBuilder();
			
			BufferedInputStream bis = new BufferedInputStream(url.openStream());
			byte[] buffer = new byte[128];
			int len = 0;
			
			while ((len = bis.read(buffer)) > 0) {
				String data = new String(buffer, 0, len);
				configString.append(data);
			}
			
			load(configString.toString());

		} catch (FileNotFoundException ex) {
			log.error("File Not Found error opening gradient file for reading: " + ex.getMessage(), ex);
		} catch (IOException ex) {
			log.error("IO error reading from gradient file: " + ex.getMessage(), ex);
		}
	}
	
	public void load(String configString)
	{
		clear();
		if (configString == null)
			return;
		
		String[] lines = configString.split("\n");
		for (String line : lines) {
			colorStops.add(new GradientColorStop(line));
		}
		
	}
	
	public void clear()
	{
		colorStops.clear();
	}
	
	public List<GradientColorStop> getColorStops()
	{
		return colorStops;
	}
	
	/*
	 * 0.000000, 0.000000, 0.388235, 0.274510
0.179381, 0.145098, 0.513725, 0.207843
0.301031, 0.894118, 0.811765, 0.494118
0.400000, 0.603922, 0.274510, 0.000000
0.550515, 0.486275, 0.133333, 0.141176
0.779381, 0.474510, 0.474510, 0.474510
0.929897, 0.7000, 0.7000, 0.7000
1.000000, 0.9000, 0.9000, 0.9000
	 */
	public String getConfigString()
	{
		StringWriter configStringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(configStringWriter);
		
		for (GradientColorStop colorStop : colorStops) {
			
			writer.printf("%f, %f, %f, %f\r\n", colorStop.getPosition(), 
											colorStop.getColor().red, 
											colorStop.getColor().green, 
											colorStop.getColor().blue);
			
		}
		return configStringWriter.toString();

	}
}
