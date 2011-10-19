package us.wthr.jdem846.util;

import java.math.RoundingMode;
import java.security.InvalidParameterException;
import java.text.NumberFormat;

public class NumberFormattingUtil
{
	
	private static NumberFormat integerInstance;
	private static NumberFormat decimalInstance;
	
	static {

		integerInstance = NumberFormat.getIntegerInstance();
		integerInstance.setRoundingMode(RoundingMode.HALF_EVEN);
		integerInstance.setGroupingUsed(false);
		
		decimalInstance = NumberFormat.getInstance();
		decimalInstance.setMaximumFractionDigits(15);
		decimalInstance.setMinimumFractionDigits(5);
		decimalInstance.setRoundingMode(RoundingMode.HALF_EVEN);
		decimalInstance.setGroupingUsed(false);
	}
	
	protected NumberFormattingUtil()
	{
		
	}
	
	public static String format(Integer value)
	{
		return NumberFormattingUtil.integerInstance.format(value);
	}
	
	public static String format(Long value)
	{
		return NumberFormattingUtil.integerInstance.format(value);
	}
	
	public static String format(Double value)
	{
		return NumberFormattingUtil.decimalInstance.format(value);
	}
	
	public static String format(Float value)
	{
		return NumberFormattingUtil.decimalInstance.format(value);
	}
	
	public static String format(Object object)
	{
		if (object == null)
			return null;
		
		if (object instanceof Integer) {
			return NumberFormattingUtil.format((Integer)object);
		} else if (object instanceof Double) {
			return NumberFormattingUtil.format((Double)object);
		} else if (object instanceof Float) {
			return NumberFormattingUtil.format((Float)object);
		} else if (object instanceof Long) {
			return NumberFormattingUtil.format((Long)object);
		} else {
			throw new InvalidParameterException("Invalid parameter type: " + object.getClass().getName());
		}
		
	}
	
}
