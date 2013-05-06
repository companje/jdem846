package us.wthr.jdem846.model.processing.coloring;

import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.ProcessOption;

public class NormalMapColorOptionModel implements OptionModel
{

	
	private boolean useFlatSurface = false;
	
	@ProcessOption(id = "us.wthr.jdem846.model.NormalMapColorOptionModel.useFlatSurface"
					, label = "Assume Flat Surface"
					, tooltip = ""
					, visible = true)
	public void setUseFlatSurface(boolean u)
	{
		this.useFlatSurface = u;
	}
	
	public boolean getUseFlatSurface()
	{
		return this.useFlatSurface;
	}
	
	@Override
	public NormalMapColorOptionModel copy()
	{
		NormalMapColorOptionModel clone = new NormalMapColorOptionModel();
		clone.useFlatSurface = this.useFlatSurface;
		
		return clone;
	}

}
