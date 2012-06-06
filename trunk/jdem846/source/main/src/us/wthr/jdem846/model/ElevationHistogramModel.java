package us.wthr.jdem846.model;

import java.util.Arrays;

import us.wthr.jdem846.math.MathExt;

public class ElevationHistogramModel
{
	
	private int[] distribution;
	
	private int minimum;
	private int maximum;
	
	
	public ElevationHistogramModel(double min, double max)
	{
		
		minimum = (int) MathExt.round(min);
		maximum = (int) MathExt.round(max);
		
		int length = maximum - minimum + 1;
		distribution = new int[length];
		Arrays.fill(distribution, 0);
		
	}
	
	
	public void add(double elevation)
	{
		int index = getIndex(elevation);
		if (index < distribution.length)
			distribution[index]++;
	}
	
	public int[] getDistribution()
	{
		return distribution;
	}

	protected int getIndex(double elevation)
	{
		int ielevation = (int) MathExt.round(elevation);
		int index = ielevation - minimum;
		return index;
	}

	public int getMinimum()
	{
		return minimum;
	}


	public int getMaximum()
	{
		return maximum;
	}
	
	public int getCountAtElevation(double elevation)
	{
		int index = getIndex(elevation);
		if (index >= 0 && index < distribution.length)
			return distribution[index];
		else
			return 0;
	}
	
	public int getMaximumCount(int step)
	{
		int start = getMinimum();
		int stop = getMaximum();
		
		int max = 0;
		
		for (int e = start; e <= stop; e+=step) {
			int c = getCountWithinElevationRange(e, e+step-1);
			max = (int) MathExt.max(c, max);
		}
		
		return max;
	}
	
	public int getMinimumCount(int step)
	{
		int start = getMinimum();
		int stop = getMaximum();
		
		int min = 100000000;
		
		for (int e = start; e <= stop; e+=step) {
			int c = getCountWithinElevationRange(e, e+step-1);
			min = (int) MathExt.min(c, min);
		}
		
		return min;
	}
	
	public int getCountWithinElevationRange(double min, double max)
	{
		
		int start = getIndex(min);
		int end = getIndex(max);
		int count = 0;
		
		for (int i = start; i <= end && i < distribution.length; i++) {
			count += distribution[i];
		}
		
		return count;
	}
	
}
