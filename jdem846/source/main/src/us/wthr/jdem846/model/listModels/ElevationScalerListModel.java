package us.wthr.jdem846.model.listModels;

import us.wthr.jdem846.model.OptionListModel;
import us.wthr.jdem846.scaling.ElevationScalerEnum;

public class ElevationScalerListModel extends OptionListModel<String>
{
	
	
	public ElevationScalerListModel()
	{
		
		for (ElevationScalerEnum scalerEnum : ElevationScalerEnum.values()) {
			addItem(scalerEnum.scalerName(), scalerEnum.identifier());
		}
		
	}
	
	
	
}
