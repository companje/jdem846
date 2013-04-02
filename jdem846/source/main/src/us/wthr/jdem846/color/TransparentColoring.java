package us.wthr.jdem846.color;

import us.wthr.jdem846.annotations.DemColoring;
import us.wthr.jdem846.graphics.Color;

@DemColoring(name="Transparent"
			, identifier="transparent-tint"
			, allowGradientConfig=false
			, needsMinMaxElevation=false)
public class TransparentColoring extends SingleColorTinting
{

	public TransparentColoring()
	{
		super(new Color(0, 0, 0, 0));
	}
	
	@Override
	public ModelColoring copy() throws Exception
	{
		return new TransparentColoring();
	}
}
