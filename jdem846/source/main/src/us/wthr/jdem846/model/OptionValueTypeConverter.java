package us.wthr.jdem846.model;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class OptionValueTypeConverter
{
	
	private static Log log = Logging.getLog(OptionValueTypeConverter.class);
	
	
	
	public static Object fromString(String object, Class<?> type)
	{
		if (type.equals(String.class)) {
			return object;
		} else if (type.equals(Integer.class) || type.equals(int.class)) {
			return Integer.parseInt(object);
		} else if (type.equals(Double.class) || type.equals(double.class)) {
			return Double.parseDouble(object);
		} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			return Boolean.parseBoolean(object);
		} else if (type.equals(Float.class) || type.equals(float.class)) {
			return Float.parseFloat(object);
		} else if (type.equals(AzimuthElevationAngles.class)) {
			return AzimuthElevationAngles.fromString(object);
		} else if (type.equals(LightingDate.class)) {
			return LightingDate.fromString(object);
		} else if (type.equals(LightingTime.class)) {
			return LightingTime.fromString(object);
		} else if (type.equals(RgbaColor.class)) {
			return RgbaColor.fromString(object);
		} else if (type.equals(ViewPerspective.class)) {
			return ViewPerspective.fromString(object);
		} else if (type.equals(ViewerPosition.class)) {
			return ViewerPosition.fromString(object);
		} else if (type.equals(FilePath.class)) {
			return FilePath.fromString(object);
		} else {
			log.warn("###### Unsupported data type: " + type.getName());
			return null;
		}
		
		
	}
	
	
}
