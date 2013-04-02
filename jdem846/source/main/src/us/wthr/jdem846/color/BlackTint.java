package us.wthr.jdem846.color;

import us.wthr.jdem846.annotations.DemColoring;
import us.wthr.jdem846.graphics.Colors;


@DemColoring(name="Black"
			, identifier="black-tint"
			, allowGradientConfig=false
			, needsMinMaxElevation=false)
public class BlackTint extends SingleColorTinting
{

	public BlackTint()
	{
		super(Colors.BLACK);
	}
	
	@Override
	public ModelColoring copy() throws Exception
	{
		return new BlackTint();
	}
}
