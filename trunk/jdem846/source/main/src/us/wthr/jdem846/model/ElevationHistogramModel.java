package us.wthr.jdem846.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.util.ByteConversions;

public class ElevationHistogramModel
{
	
	private int[] distribution;
	
	private int minimum;
	private int maximum;
	private int bins;
	
	public ElevationHistogramModel(InputStream in) throws IOException
	{
		this.read(in);
	}
	
	public ElevationHistogramModel(int maxBins, double min, double max)
	{
		minimum = (int) MathExt.floor(min);
		maximum = (int) MathExt.ceil(max);

		int length = maximum - minimum + 1;
		if (length < maxBins) {
			bins = length;
		} else {
			bins = maxBins;
		}
		
		
		
		distribution = new int[bins];
		Arrays.fill(distribution, 0);
		
	}
	
	
	public void add(double elevation)
	{
		int index = getIndex(elevation);
		if (index >= 0 && index < distribution.length)
			distribution[index]++;
	}
	
	public int[] getDistribution()
	{
		return distribution;
	}

	protected int getIndex(double elevation)
	{
		int i = (int) MathExt.round(((double)(elevation - minimum) / (double)(maximum - minimum)) * (double)bins);
		return i;
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
	
	protected void read(InputStream in) throws IOException
	{
		byte[] buffer1k = new byte[1024];
		byte[] buffer4 = new byte[4];
		
		int intValue = 0;
		int len = 0;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		while((len = in.read(buffer1k)) != -1) {
			baos.write(buffer1k, 0, len);
		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		
		bais.read(buffer4);
		minimum = ByteConversions.bytesToInt(buffer4);
		
		bais.read(buffer4);
		maximum = ByteConversions.bytesToInt(buffer4);
		
		bais.read(buffer4);
		bins = ByteConversions.bytesToInt(buffer4);
		
		distribution = new int[bins];
		for (int i = 0; i < bins; i++) {
			bais.read(buffer4);
			distribution[i] = ByteConversions.bytesToInt(buffer4);
		}
		
	}
	
	public void write(OutputStream out) throws IOException
	{
		
		byte[] buffer4 = new byte[4];
		
		ByteConversions.intToBytes(minimum, buffer4);
		out.write(buffer4);
		
		ByteConversions.intToBytes(maximum, buffer4);
		out.write(buffer4);
		
		ByteConversions.intToBytes(bins, buffer4);
		out.write(buffer4);
		
		for (int i = 0; i < distribution.length; i++) {
			ByteConversions.intToBytes(distribution[i], buffer4);
			out.write(buffer4);
		}
		
		out.flush();
		
	}
	
}
