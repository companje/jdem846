package us.wthr.jdem846.util;

import java.awt.Color;

public class ColorSerializationUtil
{
	
	private final static Color TRANSPARENT = new Color(0, 0, 0, 0);
	
	public static String colorToString(Color color)
	{
		return ""+color.getRed()+";"+color.getGreen()+";"+color.getBlue()+";"+color.getAlpha();
	}
	
	public static Color stringToColor(String colorString)
	{
		if (colorString == null)
			return TRANSPARENT;
		
		String[] tokens = colorString.split(";");
		if (tokens.length != 4) {
			return TRANSPARENT;
		}
		
		int red = Integer.valueOf(tokens[0]);
		int green = Integer.valueOf(tokens[1]);
		int blue = Integer.valueOf(tokens[2]);
		int alpha = Integer.valueOf(tokens[3]);
		
		return new Color(red, green, blue, alpha);
	}
	
	
}
