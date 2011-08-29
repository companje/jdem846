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

import java.net.URL;

public class GradientColoring implements ModelColoring
{
	
	private static DemColor defaultColor = new DemColor(0, 0, 0, 0xFF);
	private GradientLoader gradient;
	private String configFile = null;
	private GradientColorStop[] colorStops = null;
	                                       
	public GradientColoring(String configFile)
	{
		this.configFile = configFile;
		reset();
	}
	

	@Override
	public void reset()
	{
		//gradient = new GradientLoader("hypsometric.gradient");
		
		URL url = this.getClass().getResource(configFile);
		gradient = new GradientLoader(url);
		colorStops = new GradientColorStop[gradient.getColorStops().size()];
		gradient.getColorStops().toArray(colorStops);
	}
	
	@Override
	public GradientLoader getGradientLoader()
	{
		return gradient;
	}
	
	@Override
	public void getColor(double ratio, int[] color) 
	{
		
		if (ratio < 0 || ratio > 1) {
			defaultColor.toList(color);
			return;
			//return defaultColor.getCopy();
		}
		
		GradientColorStop lower = null;
		GradientColorStop upper = null;
		
		for (GradientColorStop stop : colorStops) {
			if (stop.getPosition() <= ratio) {
				lower = stop;
			}
			if (stop.getPosition() >= ratio) {
				upper = stop;
				break;
			}
		}
		
		if (upper == null)
			upper = lower;
		
		if (lower == null)
			lower = upper;

		if (upper == null && lower == null) {
			defaultColor.toList(color);
			return;
			//return defaultColor.getCopy();
		}
		
		
		if (ratio == 0.0f || (upper.getPosition() - lower.getPosition()) == 0.0f) {
			lower.getColor().toList(color);
			return;
			//return lower.getColor().getCopy();
		}
		
		double color_ratio = (ratio - lower.getPosition()) / (upper.getPosition() - lower.getPosition());
		
		double red = (lower.getColor().getRed() * (1.0 - color_ratio)) + (upper.getColor().getRed() * color_ratio);
		double green = (lower.getColor().getGreen() * (1.0 - color_ratio)) + (upper.getColor().getGreen() * color_ratio);
		double blue = (lower.getColor().getBlue() * (1.0 - color_ratio)) + (upper.getColor().getBlue() * color_ratio);

		color[0] = (int)Math.round((red * 0xFF));
		color[1] = (int)Math.round((green * 0xFF));
		color[2] = (int)Math.round((blue * 0xFF));
		color[3] = 0xFF;
		//return new DemColor(red, green, blue, 0xFF);
	}

	@Override
	public void getGradientColor(float elevation, float minElevation, float maxElevation, int[] color) 
	{
		double ratio = (elevation - minElevation) / (maxElevation - minElevation);
		
		if (ratio <= 0)
			ratio = .001;
		
		getColor(ratio, color);
		//return getColor(ratio);
	}
	
	
}
