package us.wthr.jdem846.color;

import us.wthr.jdem846.annotations.DemColoring;
import us.wthr.jdem846.render.scaling.ElevationScaler;


@DemColoring(name="Black", identifier="black-tint", allowGradientConfig=false, needsMinMaxElevation=false)
public class BlackTint implements ModelColoring
{
	private DemColor defaultColor = new DemColor(0, 0, 0, 1.0);
	
	public BlackTint()
	{
		
	}
	
	@Override
	public void reset()
	{
		// Nothing to reset...
	}
	
	
	@Override
	public GradientLoader getGradientLoader()
	{
		return null;
	}
	
	@Override
	public void getColorByMeters(double ratio, int[] color) 
	{
		
	}
	
	@Override
	public void getColorByPercent(double ratio, int[] color) 
	{
		defaultColor.toList(color);
		//return defaultColor.getCopy();
	}

	@Override
	public void getGradientColor(double elevation, double minElevation,
			double maxElevation, int[] color) 
	{
		defaultColor.toList(color);
		//return defaultColor.getCopy();
	}
	
	public double getMinimumSupported()
	{
		return 0;
	}
	
	public double getMaximumSupported()
	{
		return 1.0;
	}
	
	@Override
	public void setElevationScaler(ElevationScaler elevationScaler) 
	{
		
	}
}
