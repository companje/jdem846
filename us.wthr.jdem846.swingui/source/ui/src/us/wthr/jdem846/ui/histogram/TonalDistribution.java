package us.wthr.jdem846.ui.histogram;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class TonalDistribution
{
	public int channel;
	public int[] distribution;
	
	
	public TonalDistribution(int channel)
	{
		this.channel = channel;
		
		distribution = new int[256];
		Arrays.fill(distribution, 0);
	}
	
	
	
	
}
