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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import java.io.InputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;

import us.wthr.jdem846.exception.GradientLoadException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class GradientLoader 
{
	private static Log log = Logging.getLog(GradientLoader.class);
	
	private List<GradientColorStop> colorStops = new LinkedList<GradientColorStop>();
	
	private String name;
	private String identifier;
	private boolean needsMinMaxElevation;
	private String units;
	
	protected GradientLoader(String configString) throws GradientLoadException
	{
		loadJSON(configString);
	}
	
	protected GradientLoader(File gradientFile) throws GradientLoadException
	{
		load(gradientFile);
	}
	
	
	public static GradientLoader loadGradient(String jsonTxt) throws GradientLoadException
	{
		return new GradientLoader(jsonTxt);
	}
	
	public static GradientLoader loadGradient(File gradientFile) throws GradientLoadException
	{
		return new GradientLoader(gradientFile);
	}
	
	public static GradientLoader loadDefaultGradient() throws GradientLoadException
	{
		try {
			File rootPathFile = new File(ColoringRegistry.class.getResource("/color/gradient/hypsometric.json").getPath());
			GradientLoader gradient = new GradientLoader(rootPathFile);
			return gradient;
		} catch (Exception ex) {
			throw new GradientLoadException("Failed to load default gradient: " + ex.getMessage(), ex);
		}
	}
	
	public void load(File gradientFile) throws GradientLoadException
	{
		log.info("Loading gradient from " + gradientFile.getAbsolutePath());

		InputStream is = null;
		String jsonTxt = null;
		
		if (gradientFile.exists()) {
			try {
				is = new BufferedInputStream(new FileInputStream(gradientFile));
			} catch (FileNotFoundException ex) {
				throw new GradientLoadException(gradientFile.getAbsolutePath(), "Failed to open gradient file @ '" + gradientFile.getAbsolutePath() + "': " + ex.getMessage(), ex);
			}
		}
		
		
		try {
			jsonTxt = IOUtils.toString( is );
		} catch (Exception ex) {
			throw new GradientLoadException(gradientFile.getAbsolutePath(), "Failed to read gradient file @ '" + gradientFile.getAbsolutePath() + "': " + ex.getMessage(), ex);
		}
		
		
		loadJSON(jsonTxt);

	}
	
	public void loadJSON(String jsonTxt) throws GradientLoadException
	{
		JSONObject json = null;
		
		try {
			json = (JSONObject) JSONSerializer.toJSON( jsonTxt );   
		} catch (JSONException ex) {
			log.warn("JSON parse error on: \n" + jsonTxt);
			throw new GradientLoadException("Error parsing JSON text: " + ex.getMessage(), ex);
		}
        
        name = json.getString("name");
        if (name == null) {
        	throw new GradientLoadException("Name cannot be null");
        }
        name = I18N.get(name, name);
        
        
        identifier = json.getString("identifier");
        if (identifier == null) {
        	throw new GradientLoadException("Identifier cannot be null");
        }
        
        if (!json.has("needsMinMaxElevation")) {
        	throw new GradientLoadException("needsMinMaxElevation cannot be null");
        }
        needsMinMaxElevation = json.getBoolean("needsMinMaxElevation");
        
        units = json.getString("units");
        if (units == null) {
        	throw new GradientLoadException("Units cannot be null");
        }
        
        JSONArray gradient = json.getJSONArray("gradient");
        
        for (int i = 0; i < gradient.size(); i++) {
        	JSONObject gradientStop = gradient.getJSONObject(i);
        	double stop = gradientStop.getDouble("stop");
        	if (stop > 1.0 || stop < 0.0) {
        		throw new GradientLoadException("Invalid color stop: " + stop);
        	}
        	
        	double red = gradientStop.getDouble("red");
        	double green = gradientStop.getDouble("green");
        	double blue = gradientStop.getDouble("blue");
        	
        	double alpha = 1.0;
        	if (gradientStop.has("alpha")) {
        		alpha = gradientStop.getDouble("alpha");
        	}
        	
        	
        	GradientColorStop colorStop = new GradientColorStop(stop, red, green, blue, alpha);
        	colorStops.add(colorStop);
        	
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
	
	
	
	public String getName()
	{
		return name;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public boolean needsMinMaxElevation()
	{
		return needsMinMaxElevation;
	}

	public String getUnits()
	{
		return units;
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
		return null;
		/*
		StringWriter configStringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(configStringWriter);
		
		for (GradientColorStop colorStop : colorStops) {
			
			writer.printf("%f, %f, %f, %f\r\n", colorStop.getPosition(), 
											colorStop.getColor().red, 
											colorStop.getColor().green, 
											colorStop.getColor().blue);
			
		}
		return configStringWriter.toString();
		*/
	}
}
