package us.wthr.jdem846.ui.optionModels;

import us.wthr.jdem846.render.scaling.ElevationScalerEnum;
import us.wthr.jdem846.ui.base.JComboBoxModel;

public class ElevationScalerListModel extends JComboBoxModel<String>
{
	
	public ElevationScalerListModel()
	{
		
		for (ElevationScalerEnum scalerEnum : ElevationScalerEnum.values()) {
			addItem(scalerEnum.scalerName(), scalerEnum.identifier());
		}
		
	}
	
	
}
