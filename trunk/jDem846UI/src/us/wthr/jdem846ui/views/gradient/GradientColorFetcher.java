package us.wthr.jdem846ui.views.gradient;

import us.wthr.jdem846.color.GradientColorStop;
import us.wthr.jdem846.color.GradientLoader;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.graphics.IColor;

public class GradientColorFetcher
{
	
	private ModelColoring gradient;
	private int maxContainerValue;
	
	private double minStopValue;
	private double maxStopValue;
	
	public GradientColorFetcher(ModelColoring gradient, int maxContainerValue)
	{
		this.gradient = gradient;
		this.maxContainerValue = maxContainerValue;
		
		minStopValue = getMinimumStopValue();
		maxStopValue = getMaximumStopValue();
	}
	
	
	public IColor getColor(int containerValue)
	{
		double value = minStopValue + ((1.0 - ((double)containerValue / (double)maxContainerValue)) * (maxStopValue - minStopValue));
		return gradient.getColorByPercent(value);
	}
	
	
	
	protected double getMinimumStopValue()
	{
		GradientLoader loader = gradient.getGradientLoader();
		GradientColorStop stop = loader.getColorStops().get(0);
		if (stop != null) {
			return stop.getPosition();
		} else {
			return 0;
		}
	}
	
	protected double getMaximumStopValue()
	{
		GradientLoader loader = gradient.getGradientLoader();
		GradientColorStop stop = loader.getColorStops().get(loader.getColorStops().size() - 1);
		if (stop != null) {
			return stop.getPosition();
		} else {
			return 0;
		}
	}
	
}
