package us.wthr.jdem846.graphics;

import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.util.ColorUtil;

/** Represents an immutable RGBA color
 * 
 * @author Kevin M. Gill <kmsmgill@gmail.com>
 *
 */
@SuppressWarnings("serial")
public class Color implements IColor
{

	
	protected int[] rgba = new int[4];

	
	public Color(IColor c)
	{
		rgba[0] = c.getRed();
		rgba[1] = c.getGreen();
		rgba[2] = c.getBlue();
		rgba[3] = c.getAlpha();
	}
	
	public Color(int c)
	{
		ColorUtil.intToRGBA(c, rgba);
		clampValues();
	}
	
	public Color(int[] a)
	{
		this(a, 0);
	}
	
	public Color(int[] a, int offset)
	{
		rgba[0] = a[offset + 0];
		rgba[1] = a[offset + 1];
		rgba[2] = a[offset + 2];
		if (a.length >= offset + 4) {
			rgba[3] = a[offset + 3];
		} else {
			rgba[3] = 0xFF;
		}
		clampValues();
	}
	
	public Color(int r, int g, int b)
	{
		this(r, g, b, 0xFF);
	}
	
	public Color(int r, int g, int b, int a)
	{
		rgba[0] = r;
		rgba[1] = g;
		rgba[2] = b;
		rgba[3] = a;
		clampValues();
	}
	
	public Color(float r, float g, float b)
	{
		this((int) MathExt.round(r * 255.0)
				, (int) MathExt.round(g * 255.0)
				, (int) MathExt.round(b * 255.0));
	}
	
	public Color(float r, float g, float b, float a)
	{
		this((int) MathExt.round(r * 255.0)
				, (int) MathExt.round(g * 255.0)
				, (int) MathExt.round(b * 255.0)
				, (int) MathExt.round(a * 255.0));
	}
	
	public Color(double r, double g, double b)
	{
		this((int) MathExt.round(r * 255.0)
				, (int) MathExt.round(g * 255.0)
				, (int) MathExt.round(b * 255.0));
	}
	
	public Color(double r, double g, double b, double a)
	{
		this((int) MathExt.round(r * 255.0)
				, (int) MathExt.round(g * 255.0)
				, (int) MathExt.round(b * 255.0)
				, (int) MathExt.round(a * 255.0));
	}
	
	/**
	 * 
	 * @param hex A hex value (e.g. "#01ABCDEF") representing either a 3 byte RGB or 4 byte RGBA color. A leading '#' is optional and ignored if present.
	 */
	public Color(String hex)
	{
		if (hex.charAt(0) == '#') {
			hex = hex.substring(1);
		}
		
		rgba[0] = fromHex(hex, 0);
		rgba[1] = fromHex(hex, 1);
		rgba[2] = fromHex(hex, 2);
		if (hex.length() >= 8) {
			rgba[3] = fromHex(hex, 3);
		} else {
			rgba[3] = 0xFF;
		}
		
		clampValues();
	}
	
	
	@Override
	public int getRed()
	{
		return rgba[0];
	}
	
	@Override
	public int getGreen()
	{
		return rgba[1];
	}
	
	@Override
	public int getBlue()
	{
		return rgba[2];
	}
	
	@Override
	public int getAlpha()
	{
		return rgba[3];
	}
	
	@Override
	public int asInt()
	{
		return ColorUtil.rgbaToInt(rgba);
	}
	
	@Override
	public void toArray(int[] array)
	{
		toArray(array, 0);
	}
	
	@Override
	public void toArray(int[] array, int offset)
	{
		array[offset + 0] = rgba[0];
		array[offset + 1] = rgba[1];
		array[offset + 2] = rgba[2];
		
		if (array.length >= offset + 4) {
			array[offset + 3] = rgba[3];
		}
	}
	
	/** Fills an array with the RGBA integers within the range of 0 - 2147483647 (max integer range). 
	 * 
	 * @param array
	 * @param offset
	 */
	@Override
	public void toArrayGl(int[] array)
	{
		toArray(array, 0);
	}
	
	
	/** Fills an array with the RGBA integers within the range of 0 - 2147483647 (max integer range). 
	 * 
	 * @param array
	 * @param offset
	 */
	@Override
	public void toArrayGl(int[] array, int offset)
	{
		array[offset + 0] = (int) MathExt.round(((double)rgba[0] / 255.0) * (double)Integer.MAX_VALUE);
		array[offset + 1] = (int) MathExt.round(((double)rgba[1] / 255.0) * (double)Integer.MAX_VALUE);
		array[offset + 2] = (int) MathExt.round(((double)rgba[2] / 255.0) * (double)Integer.MAX_VALUE);
		
		if (array.length >= offset + 4) {
			array[offset + 3] = (int) MathExt.round(((double)rgba[3] / 255.0) * (double)Integer.MAX_VALUE);
		}
	}
	
	
	@Override
	public void toArray(float[] array)
	{
		toArray(array, 0);
	}
	
	@Override
	public void toArray(float[] array, int offset)
	{
		array[offset + 0] = (float)rgba[0] / 255.0f;
		array[offset + 1] = (float)rgba[1] / 255.0f;
		array[offset + 2] = (float)rgba[2] / 255.0f;
		
		if (array.length >= offset + 4) {
			array[offset + 3] = (float)rgba[3] / 255.0f;
		}
	}
	
	
	@Override
	public void toArray(double[] array)
	{
		toArray(array, 0);
	}
	
	@Override
	public void toArray(double[] array, int offset)
	{
		array[offset + 0] = (double)rgba[0] / 255.0;
		array[offset + 1] = (double)rgba[1] / 255.0;
		array[offset + 2] = (double)rgba[2] / 255.0;
		
		if (array.length >= offset + 4) {
			array[offset + 3] = (double)rgba[3] / 255.0;
		}
	}
	
	public String toString()
	{
		String hex = "#" + toHex(rgba[0]) + toHex(rgba[1]) + toHex(rgba[2]) + toHex(rgba[3]);
		return hex;
	}
	
	

	
	@Override
	public boolean equals(Object other)
	{
		if (other != null && other instanceof Color) {
			Color c = (Color) other;
			return (c.rgba[0] == this.rgba[0] 
					&& c.rgba[1] == this.rgba[1] 
					&& c.rgba[2] == this.rgba[2] 
					&& c.rgba[3] == this.rgba[3]);
		}
		return true;
	}
	
	protected void clampValues()
	{
		rgba[0] = ColorUtil.clamp(rgba[0]);
		rgba[1] = ColorUtil.clamp(rgba[1]);
		rgba[2] = ColorUtil.clamp(rgba[2]);
		rgba[3] = ColorUtil.clamp(rgba[3]);
	}
	
	protected String toHex(int v)
	{
		String h = Integer.toHexString(v);
		if (h.length() == 1) {
			h = "0" + h;
		}
		return h.toUpperCase();
	}
	
		
	/**
	 * 
	 * @param h A hex value (e.g. #01ABCDEF) representing either a 3 byte RGB or 4 byte RGBA color.
	 * @param position Position of the two char hex value, not counting any leading '#'s. For example, given #01ABCFEF, position 0 is '01', position 1 is 'AB', and so on.
	 * @return
	 */
	protected static int fromHex(String h, int position)
	{
		if (h == null) {
			return 0x00;
		}
		
		if (h.contains("#")) {
			h = h.replace("#", "");
		}
		
		if (h.length() <= (position * 2) + 1) {
			return 0x0;
		}
		
		h = h.substring((position * 2), (position * 2) + 2);
		return fromHex(h);
	}
	
	protected static int fromHex(String h)
	{
		if (h == null) {
			return 0x00;
		}
		
		if (h.contains("#")) {
			h = h.replace("#", "");
		}
		if (h.length() >= 2) {
			return Integer.parseInt(h, 16);
		} else {
			return 0x0;
		}
	}
	
	
}
