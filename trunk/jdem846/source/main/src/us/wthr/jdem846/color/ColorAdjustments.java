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

/** Facility for several simple image and color adjustment algorithms.
 * 
 * see: http://en.wikipedia.org/wiki/Image_editing
 * 
 * @author A345926
 *
 */
public class ColorAdjustments
{
	

	
	public static void adjustBrightness(int[] color, double brightness)
	{

		double r = (double)color[0] / 255.0;
		double g = (double)color[1] / 255.0;
		double b = (double)color[2] / 255.0;
		
		if (brightness < 0) {
			r = (r) * (1.0 + brightness);
			g = (g) * (1.0 + brightness);
			b = (b) * (1.0 + brightness);
		} else {
			r = r + (1 - r) * brightness;
			g = g + (1 - g) * brightness;
			b = b + (1 - b) * brightness;
		}

		color[0] = (int) Math.round(r * 255.0);
		color[1] = (int) Math.round(g * 255.0);
		color[2] = (int) Math.round(b * 255.0);
		
		checkColorChannelLevels(color);
	}
	
	public static void adjustBrightnessAndContrast(int[] color, double brightness, double contrast)
	{
		adjustBrightness(color, brightness);
		adjustContrast(color, contrast);
	}
	
	public static void adjustContrast(int[] color, double contrast)
	{

		color[0] = (int)Math.round((((double)color[0] / 255.0) - 0.5) * (Math.tan((contrast + 1) * Math.PI/4)) + 0.5);
		color[1] = (int)Math.round((((double)color[1] / 255.0) - 0.5) * (Math.tan((contrast + 1) * Math.PI/4)) + 0.5);
		color[2] = (int)Math.round((((double)color[2] / 255.0) - 0.5) * (Math.tan((contrast + 1) * Math.PI/4)) + 0.5);
		
		checkColorChannelLevels(color);
	}
	
	public static void checkColorChannelLevels(int[] color)
	{
		if (color[0] > 255)
			color[0] = 255;
		if (color[1] > 255)
			color[1] = 255;
		if (color[2] > 255)
			color[2] = 255;
		
		if (color[0] < 0)
			color[0] = 0;
		if (color[1] < 0)
			color[1] = 0;
		if (color[2] < 0)
			color[2] = 0;
	}
	
	
	/** Interpolates a color from c0 to c1 given the ratio.
	 * 
	 */
	public static void interpolateColor(int[] c0, int[] c1, int[] out, double ratio)
	{
		out[0] = (int) Math.round((c1[0] * ratio) + (c0[0] * (1.0 - ratio)));
		out[1] = (int) Math.round((c1[1] * ratio) + (c0[1] * (1.0 - ratio)));
		out[2] = (int) Math.round((c1[2] * ratio) + (c0[2] * (1.0 - ratio)));
	}
	
}
