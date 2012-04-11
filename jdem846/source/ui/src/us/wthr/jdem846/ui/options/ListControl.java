package us.wthr.jdem846.ui.options;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionListModel;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;
import us.wthr.jdem846.ui.base.ComboBox;

@SuppressWarnings("serial")
public class ListControl extends ComboBox implements ItemListener
{
	
	private static Log log = Logging.getLog(ListControl.class);
	
	private OptionModelPropertyContainer propertyContainer;
	private ComboBoxListModel listModel;
	
	public ListControl(OptionModelPropertyContainer property) 
	{
		this.propertyContainer = property;
		
		OptionListModel<?> optionListModel = null;
		try {
			optionListModel = (OptionListModel<?>) property.getListModelClass().newInstance();
		} catch (Exception ex) {
			log.error("Error creating new instance of " + property.getListModelClass().getName() + " for property " + property.getPropertyName(), ex);
			return;
		} 

		listModel = new ComboBoxListModel(optionListModel);
		this.setModel(listModel);
		
		
		this.addItemListener(this);
		
		try {
			if (propertyContainer.getValue() != null) {
				
				listModel.setSelectedItemByValue(propertyContainer.getValue());
			}
		} catch (MethodContainerInvokeException ex) {
			log.info("Error setting initial value of " + propertyContainer.getPropertyName());
		}
		
	}
	
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			
			log.info("Value changed for " + propertyContainer.getPropertyName() + ": " + listModel.getSelectedItemValue());
			
			try {
				propertyContainer.setValue(listModel.getSelectedItemValue());
			} catch (MethodContainerInvokeException ex) {
				log.error("Error setting value of " + propertyContainer.getPropertyName() + " to " + listModel.getSelectedItemValue());
			}
		}
	}
	
}
