package us.wthr.jdem846.util;

import us.wthr.jdem846.math.MathExt;

/** General purpose color utilities. Include type conversions and some modification algorithms. 
 * 
 * Note: Many of the modifications are derived from the GIMP project (http://www.gimp.org).
 * 
 * @see http://en.wikipedia.org/wiki/Image_editing
 * @author Kevin M. Gill <kmsmgill@gmail.com>
 *
 */
public final class ColorUtil
{

	protected static final ColorIntFormatEnum DEFAULT_INT_MODE = ColorIntFormatEnum.RGBA;

	public static final int BLACK =       ColorUtil.rgbaToInt(0x00, 0x00, 0x00, 0xFF);
	public static final int WHITE =       ColorUtil.rgbaToInt(0xFF, 0xFF, 0xFF, 0xFF);
	public static final int RED =         ColorUtil.rgbaToInt(0xFF, 0x00, 0x00, 0xFF);
	public static final int GREEN =       ColorUtil.rgbaToInt(0x00, 0xFF, 0x00, 0xFF);
	public static final int BLUE =        ColorUtil.rgbaToInt(0x00, 0x00, 0xFF, 0xFF);
	public static final int TRANSPARENT = ColorUtil.rgbaToInt(0x00, 0x00, 0x00, 0x00);
	
	public static int setOverlayChannel(int c, int value, int channel)
	{
		if (channel < 0 || channel > 3) {
			return c;
		}
		
		int[] rgba = {0x0, 0x0, 0x0, 0x0};
		ColorUtil.intToRGBA(c, rgba);
		rgba[channel] = clamp(value);
		return ColorUtil.rgbaToInt(rgba);
	}
	
	
	
	public static int rgbaToInt(int[] rgba)
	{
		int r = rgba[0];
		int g = rgba[1];
		int b = rgba[2];
		int a = rgba[3];
	
		return rgbaToInt(r, g, b, a);
	}
	
	public static int rgbaToInt(int r, int g, int b)
	{	
		return rgbaToInt(r, g, b, 0xFF);
	}
	
	
	public static int rgbaToInt(int r, int g, int b, int a)
	{	
		return rgbaToInt(r, g, b, a, ColorUtil.DEFAULT_INT_MODE);
	}
	
	public static int rgbaToInt(int r, int g, int b, int a, ColorIntFormatEnum mode)
	{
		
		int v = 0;
		
		if (mode == ColorIntFormatEnum.ARGB) {
			v = (a << 24) |
					((r & 0xff) << 16) |
					((g & 0xff) << 8) |
					(b & 0xff);
		} else if (mode == ColorIntFormatEnum.RGBA) {
			v = ((r) << 24) |
					((g & 0xff) << 16) |
					((b & 0xff) << 8) |
					(a & 0xff);
		}		
		return v;
	}
	
	public static void intToRGBA(int c, int[] rgba)
	{
		intToRGBA(c, rgba, ColorUtil.DEFAULT_INT_MODE);
	}
	
	public static void intToRGBA(int c, int[] rgba, ColorIntFormatEnum mode)
	{
		if (mode == ColorIntFormatEnum.ARGB) {
			rgba[3] = 0xFF & (c >>> 24);
			rgba[0] = 0xFF & (c >>> 16);
			rgba[1] = 0xFF & (c >>> 8);
			rgba[2] = 0xFF & c;
		} else if (mode == ColorIntFormatEnum.RGBA) {
			rgba[0] = 0xFF & (c >>> 24);
			rgba[1] = 0xFF & (c >>> 16);
			rgba[2] = 0xFF & (c >>> 8);
			rgba[3] = 0xFF & c;
		}
	}
	
	
	public static int overlayColor(int rgbaA, int rgbaB)
	{
		int[] bufferA = {0, 0, 0, 0};//new int[4];
		int[] bufferB = {0, 0, 0, 0};//new int[4];
		int[] bufferC = {0, 0, 0, 0};//new int[4];
		
		intToRGBA(rgbaA, bufferA);
		intToRGBA(rgbaB, bufferB);
		
		overlayColor(bufferA, bufferB, bufferC);
		
		return rgbaToInt(bufferC);
		
	}
	
	public static void overlayColor(int[] rgbaA, int[] rgbaB, int[] fill)
	{
		if (rgbaA[3] == 0) {
			fill[0] = rgbaB[0];
			fill[1] = rgbaB[1];
			fill[2] = rgbaB[2];
			fill[3] = rgbaB[3];
		} else if (rgbaA[3] == 255) {
			fill[0] = rgbaA[0];
			fill[1] = rgbaA[1];
			fill[2] = rgbaA[2];
			fill[3] = rgbaA[3];
		} else {
			double r = 1.0 - ((double)rgbaA[3] / 255.0);
			if (r > 1.0)
				r = 1.0;
			if (r < 0.0) {
				r = 0.0;
			}
			int a = Math.max(rgbaA[3], rgbaB[3]);
			interpolateColor(rgbaA, rgbaB, fill, r);
			fill[3] = a;
		}
	}
	
	
	public static void clamp(int[] rgba)
	{
		if (rgba != null) {
			for (int i = 0; i < 4; i++) {
				if (rgba.length >= i + 1)
					rgba[i] = clamp(rgba[i]);
			}
		}
	}
	
	
	public static int clamp(int c)
	{
		if (c > 255)
			c = 255;
		if (c < 0)
			c = 0;
		return c;
	}
	

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
