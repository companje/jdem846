package us.wthr.jdem846.canvas.util;

import us.wthr.jdem846.color.ColorAdjustments;

public class ColorUtil
{
	private static int[] bufferA = new int[4];
	private static int[] bufferB = new int[4];
	private static int[] bufferC = new int[4];
	
	public static int rgbaToInt(int[] rgba)
	{
		int r = rgba[0];
		int g = rgba[1];
		int b = rgba[2];
		int a = 0xFF;
		if (rgba.length >= 4) {
			a = rgba[3];
		}
		return rgbaToInt(r, g, b, a);
	}
	
	public static int rgbaToInt(int r, int g, int b)
	{	
		return rgbaToInt(r, g, b, 0xFF);
	}
	
	public static int rgbaToInt(int r, int g, int b, int a)
	{	
		int v = (a << 24) |
				((r & 0xff) << 16) |
				((g & 0xff) << 8) |
				(b & 0xff);
		return v;
	}
	
	public static void intToRGBA(int c, int[] rgba)
	{
		rgba[0] = 0xFF & (c >>> 16);
		rgba[1] = 0xFF & (c >>> 8);
		rgba[2] = 0xFF & c;
		if (rgba.length >= 4) {
			rgba[3] = 0xFF & (c >>> 24);
		}
	}
	
	
	public static int overlayColor(int rgbaA, int rgbaB)
	{
		intToRGBA(rgbaA, bufferA);
		intToRGBA(rgbaB, bufferB);
		
		overlayColor(bufferA, bufferB, bufferC);
		
		return rgbaToInt(bufferC);
		
	}
	
	public static void overlayColor(int[] rgbaA, int[] rgbaB, int[] fill)
	{
		double r = 1.0 - ((double)rgbaA[3] / 255.0);
		int a = Math.max(rgbaA[3], rgbaB[3]);
		ColorAdjustments.interpolateColor(rgbaA, rgbaB, fill, r);
		fill[3] = a;
	}
	
}
