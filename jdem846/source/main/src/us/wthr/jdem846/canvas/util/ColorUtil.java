package us.wthr.jdem846.canvas.util;

import us.wthr.jdem846.color.ColorAdjustments;

public class ColorUtil
{
	public static final int INT_MODE_RGBA = 0;
	public static final int INT_MODE_ARGB = 1;
	protected static final int DEFAULT_INT_MODE = INT_MODE_ARGB;

	public static final int BLACK = -16777216;
	public static final int WHITE = -1;
	public static final int RED = -65536;
	public static final int GREEN = -16711936;
	public static final int BLUE = -16776961;
	public static final int TRANSPARENT = 0x0;
	
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
	
	public static int rgbaToInt(int r, int g, int b, int a, int mode)
	{
		
		int v = 0;
		
		if (mode == ColorUtil.INT_MODE_ARGB) {
			v = (a << 24) |
					((r & 0xff) << 16) |
					((g & 0xff) << 8) |
					(b & 0xff);
		} else if (mode == ColorUtil.INT_MODE_RGBA) {
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
	
	public static void intToRGBA(int c, int[] rgba, int mode)
	{
		if (mode == ColorUtil.INT_MODE_ARGB) {
			rgba[3] = 0xFF & (c >>> 24);
			rgba[0] = 0xFF & (c >>> 16);
			rgba[1] = 0xFF & (c >>> 8);
			rgba[2] = 0xFF & c;
		} else if (mode == ColorUtil.INT_MODE_RGBA) {
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
			ColorAdjustments.interpolateColor(rgbaA, rgbaB, fill, r);
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
}
