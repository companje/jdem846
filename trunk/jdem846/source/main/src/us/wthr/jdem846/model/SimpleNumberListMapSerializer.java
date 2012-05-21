package us.wthr.jdem846.model;

import java.util.HashMap;
import java.util.Map;

public class SimpleNumberListMapSerializer
{
	
	/**
	 * TODO: More error checking and validation
	 * @param s
	 * @return
	 */
	public static Map<String, double[]> parseDoubleListString(String s)
	{
		Map<String, double[]> values = new HashMap<String, double[]>();
		
		String[] chunks = s.split(";");
		
		
		for (String chunk : chunks) {
			String name = extractName(chunk);
			double[] numbers = extractNumbersDouble(chunk);
			values.put(name, numbers);
		}
		
		return values;
	}
	
	protected static String extractName(String chunk)
	{
		return chunk.substring(0, chunk.indexOf(":"));
	}
	
	protected static double[] extractNumbersDouble(String chunk)
	{
		int leftIndex = chunk.indexOf("[");
		int rightIndex = chunk.indexOf("]");
		String numbersSection = chunk.substring(leftIndex+1, rightIndex);
		
		String[] numberChars = numbersSection.split(",");
		double[] numbers = new double[numberChars.length];
		for (int i = 0; i < numberChars.length; i++) {
			numbers[i] = Double.parseDouble(numberChars[i]);
		}
		
		return numbers;
	}
	
	
	public static Map<String, int[]> parseIntegerListString(String s)
	{
		Map<String, int[]> values = new HashMap<String, int[]>();
		
		String[] chunks = s.split(";");
		
		
		for (String chunk : chunks) {
			String name = extractName(chunk);
			int[] numbers = extractNumbersInt(chunk);
			values.put(name, numbers);
		}
		
		return values;
	}
	
	protected static int[] extractNumbersInt(String chunk)
	{
		int leftIndex = chunk.indexOf("[");
		int rightIndex = chunk.indexOf("]");
		String numbersSection = chunk.substring(leftIndex+1, rightIndex);
		
		String[] numberChars = numbersSection.split(",");
		int[] numbers = new int[numberChars.length];
		for (int i = 0; i < numberChars.length; i++) {
			numbers[i] = Integer.parseInt(numberChars[i]);
		}
		
		return numbers;
	}
	
	
	
	
	

	public static Map<String, long[]> parseLongListString(String s)
	{
		Map<String, long[]> values = new HashMap<String, long[]>();
		
		String[] chunks = s.split(";");
		
		
		for (String chunk : chunks) {
			String name = extractName(chunk);
			long[] numbers = extractNumbersLong(chunk);
			values.put(name, numbers);
		}
		
		return values;
	}
	
	protected static long[] extractNumbersLong(String chunk)
	{
		int leftIndex = chunk.indexOf("[");
		int rightIndex = chunk.indexOf("]");
		String numbersSection = chunk.substring(leftIndex+1, rightIndex);
		
		String[] numberChars = numbersSection.split(",");
		long[] numbers = new long[numberChars.length];
		for (int i = 0; i < numberChars.length; i++) {
			numbers[i] = Long.parseLong(numberChars[i]);
		}
		
		return numbers;
	}
}
