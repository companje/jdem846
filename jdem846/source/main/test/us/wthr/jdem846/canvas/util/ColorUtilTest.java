package us.wthr.jdem846.canvas.util;

import junit.framework.TestCase;
import us.wthr.jdem846.util.ColorIntFormatEnum;
import us.wthr.jdem846.util.ColorUtil;

public class ColorUtilTest extends TestCase
{
	
	
	public void testIntToArrayRGBA()
	{
		int r = 25;
		int g = 50;
		int b = 75;
		int a = 100;
		
		int c = ColorUtil.rgbaToInt(r, g, b, a, ColorIntFormatEnum.RGBA);
		
		int[] rgba = new int[4];
		ColorUtil.intToRGBA(c, rgba, ColorIntFormatEnum.RGBA);
		
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
		
		int c = ColorUtil.rgbaToInt(r, g, b, a, ColorIntFormatEnum.ARGB);
		
		int[] rgba = new int[4];
		ColorUtil.intToRGBA(c, rgba, ColorIntFormatEnum.ARGB);
		
		assertEquals(rgba[0], r);
		assertEquals(rgba[1], g);
		assertEquals(rgba[2], b);
		assertEquals(rgba[3], a);
		
	}
	
	
}
