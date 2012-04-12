package us.wthr.jdem846.ui.options;

import java.util.List;

import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.ModelProcessRegistry;
import us.wthr.jdem846.model.processing.ProcessInstance;
import us.wthr.jdem846.ui.base.JComboBoxModel;

public class ProcessTypeListModel extends JComboBoxModel<String>
{
	
	private GridProcessingTypesEnum processType;
	
	
	public ProcessTypeListModel(GridProcessingTypesEnum processType)
	{
		this.processType = processType;
		
		List<ProcessInstance> processInstanceList = ModelProcessRegistry.getInstances();
		
		for (ProcessInstance processInstance : processInstanceList) {
			
			if (processInstance.getType() == processType) {
				addItem(processInstance.getName(), processInstance.getId());
			}
			
		}
		
		
	}
	
	
}
