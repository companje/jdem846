package us.wthr.jdem846.ui.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;
import us.wthr.jdem846.ui.base.CheckBox;

@SuppressWarnings("serial")
public class BooleanControl extends CheckBox implements ActionListener, OptionModelUIControl
{
	private static Log log = Logging.getLog(BooleanControl.class);
	
	private final OptionModelPropertyContainer propertyContainer;
	
	//private List<ModelConfigurationChangeListener> modelConfigurationChangeListeners = new LinkedList<ModelConfigurationChangeListener>();
	
	public BooleanControl(OptionModelPropertyContainer property)
	{
		this.propertyContainer = property;
		this.setText(propertyContainer.getLabel());
		this.setToolTipText(propertyContainer.getTooltip());
		
		this.getModel().addActionListener(this);

		refreshUI();
		
	}
	
	public void refreshUI()
	{
		try {
			this.getModel().setSelected((Boolean)propertyContainer.getValue());
		} catch (MethodContainerInvokeException ex) {
			log.error("Error setting initial value to property " + propertyContainer.getPropertyName());
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		log.info("Change to " + propertyContainer.getLabel() + ": " + getModel().isSelected());
		try {
			propertyContainer.setValue(getModel().isSelected());
		} catch (MethodContainerInvokeException ex) {
			log.error("Error setting property " + propertyContainer.getPropertyName() + " to " + getModel().isSelected(), ex);
		}
	}
	
	

}
