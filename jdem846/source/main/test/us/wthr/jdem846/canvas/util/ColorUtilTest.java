package us.wthr.jdem846.canvas.util;

import junit.framework.TestCase;

public class ColorUtilTest extends TestCase
{
	
	
	public void testIntToArrayRGBA()
	{
		int r = 25;
		int g = 50;
		int b = 75;
		int a = 100;
		
		int c = ColorUtil.rgbaToInt(r, g, b, a, ColorUtil.INT_MODE_RGBA);
		
		int[] rgba = new int[4];
		ColorUtil.intToRGBA(c, rgba, ColorUtil.INT_MODE_RGBA);
		
		assertEquals(rgba[0], r);
		assertEquals(rgba[1], g);
		assertEquals(rgba[2], b);
		assertEquals(rgba[3], a);
		
	}
	
	public void testIntToArrayARGB()
	{
		int r = 25;
		int g = 50;
		int b = 75;
		int a = 100;
		
		int c = ColorUtil.rgbaToInt(r, g, b, a, ColorUtil.INT_MODE_ARGB);
		
		int[] rgba = new int[4];
		ColorUtil.intToRGBA(c, rgba, ColorUtil.INT_MODE_ARGB);
		
		assertEquals(rgba[0], r);
		assertEquals(rgba[1], g);
		assertEquals(rgba[2], b);
		assertEquals(rgba[3], a);
		
	}
	
	
}
