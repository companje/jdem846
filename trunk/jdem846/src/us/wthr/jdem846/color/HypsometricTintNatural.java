package us.wthr.jdem846.color;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.annotations.DemColoring;
import us.wthr.jdem846.exception.GradientLoadException;

//@DemColoring(name="us.wthr.jdem846.color.hypsometricTintNatural.name", identifier="hypsometric-tint-natural", needsMinMaxElevation=true)
public class HypsometricTintNatural extends GradientColoring
{
	
	public HypsometricTintNatural() throws GradientLoadException
	{
		super(JDem846Properties.getProperty("us.wthr.jdem846.color") + "/hypsometric-natural.gradient");
	}
	
}
