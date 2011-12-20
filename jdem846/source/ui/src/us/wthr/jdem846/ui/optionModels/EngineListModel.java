package us.wthr.jdem846.ui.optionModels;

import java.util.List;

import us.wthr.jdem846.render.EngineInstance;
import us.wthr.jdem846.render.EngineRegistry;
import us.wthr.jdem846.ui.base.JComboBoxModel;

public class EngineListModel extends JComboBoxModel<String>
{
	
	public EngineListModel()
	{
		List<EngineInstance> engineInstances = EngineRegistry.getInstances();
		for (EngineInstance engineInstance : engineInstances) {
			addItem(engineInstance.getName(), engineInstance.getIdentifier());
		}
	}
	
}