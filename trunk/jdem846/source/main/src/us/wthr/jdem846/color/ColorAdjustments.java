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

import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846.math.MathExt;

/** Facility for several simple image and color adjustment algorithms.
 * 
 * see: http://en.wikipedia.org/wiki/Image_editing
 * 
 * @author Kevin M. Gill
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
	
	
	
	public static int interpolateColor(int c00, int c01, int c10, int c11, double xFrac, double yFrac)
	{
		int[] _c00 = {0, 0, 0, 0};
		int[] _c01 = {0, 0, 0, 0};
		int[] _c10 = {0, 0, 0, 0};
		int[] _c11 = {0, 0, 0, 0};
		int[] _o = {0, 0, 0, 0};
		
		ColorUtil.intToRGBA(c00, _c00);
		ColorUtil.intToRGBA(c01, _c01);
		ColorUtil.intToRGBA(c10, _c10);
		ColorUtil.intToRGBA(c11, _c11);
		
		interpolateColor(_c00, _c01, _c10, _c11, _o, xFrac, yFrac);
		
		return ColorUtil.rgbaToInt(_o);
	}
	
	public static void interpolateColor(int[] c00, int[] c01, int[] c10, int[] c11, int[] out, double xFrac, double yFrac)
	{
		interpolateColorChannel(c00, c01, c10, c11, 0, out, xFrac, yFrac);
		interpolateColorChannel(c00, c01, c10, c11, 1, out, xFrac, yFrac);
		interpolateColorChannel(c00, c01, c10, c11, 2, out, xFrac, yFrac);
		interpolateColorChannel(c00, c01, c10, c11, 3, out, xFrac, yFrac);
	}
	
	
	public static void interpolateColorChannel(int[] c00, int[] c01, int[] c10, int[] c11, int channel, int[] out, double xFrac, double yFrac)
	{
		int c = (int) MathExt.round(MathExt.interpolate((double)c00[channel], (double)c01[channel], (double)c10[channel], (double)c11[channel], xFrac, yFrac));
		c = ColorUtil.clamp(c);
		out[channel] = c;
	}
	
	public static int interpolateColor(int c0, int c1, double ratio)
	{
		
		int[] _c0 = {0, 0, 0, 0};
		int[] _c1 = {0, 0, 0, 0};
		int[] _o = {0, 0, 0, 0};
		
		ColorUtil.intToRGBA(c0, _c0);
		ColorUtil.intToRGBA(c1, _c1);
		
		interpolateColor(_c0, _c1, _o, ratio);
		
		return ColorUtil.rgbaToInt(_o);
	}
	
	
	/** Interpolates a color from c0 to c1 given the ratio.
	 * 
	 */
	public static void interpolateColor(int[] c0, int[] c1, int[] out, double ratio)
	{
		out[0] = (int) Math.round((c1[0] * ratio) + (c0[0] * (1.0 - ratio)));
		out[1] = (int) Math.round((c1[1] * ratio) + (c0[1] * (1.0 - ratio)));
		out[2] = (int) Math.round((c1[2] * ratio) + (c0[2] * (1.0 - ratio)));
		
		if (c0.length >= 4 && c1.length >= 4 && out.length >= 4) {
			out[3] = (int) Math.round((c1[3] * ratio) + (c0[3] * (1.0 - ratio)));
		}
		
	}
	
}
