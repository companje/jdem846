package us.wthr.jdem846.ui.histogram;

import us.wthr.jdem846.math.MathExt;

public class HistogramModel
{
	
	
	private TonalDistribution channel0;
	private TonalDistribution channel1;
	private TonalDistribution channel2;
	private TonalDistribution channel3;
	
	
	public HistogramModel(TonalDistribution channel0, TonalDistribution channel1, TonalDistribution channel2, TonalDistribution channel3)
	{
		this.channel0 = channel0;
		this.channel1 = channel1;
		this.channel2 = channel2;
		this.channel3 = channel3;
	}

	public int getMax(int channels)
	{
		
		int max = 0;
		for (int i = 0; i < 256; i++) {
			int c0 = channel0.distribution[i];
			int c1 = channel1.distribution[i];
			int c2 = channel2.distribution[i];
			int c3 = channel3.distribution[i];
			
			if ((channels & Channels.CHANNEL_1) == Channels.CHANNEL_1)
				max = (int) MathExt.max(max, c0);
			
			if ((channels & Channels.CHANNEL_2) == Channels.CHANNEL_2)
				max = (int) MathExt.max(max, c1);
			
			if ((channels & Channels.CHANNEL_3) == Channels.CHANNEL_3)
				max = (int) MathExt.max(max, c2);
			
			if ((channels & Channels.CHANNEL_4) == Channels.CHANNEL_4)
				max = (int) MathExt.max(max, c3);
		}
		
		return max;
	}
	
	public TonalDistribution getChannel0()
	{
		return channel0;
	}


	public TonalDistribution getChannel1()
	{
		return channel1;
	}


	public TonalDistribution getChannel2()
	{
		return channel2;
	}


	public TonalDistribution getChannel3()
	{
		return channel3;
	}
	
	
}
