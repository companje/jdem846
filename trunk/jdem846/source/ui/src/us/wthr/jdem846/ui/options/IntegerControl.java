package us.wthr.jdem846.ui.options;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;
import us.wthr.jdem846.ui.NumberTextField;

@SuppressWarnings("serial")
public class IntegerControl extends NumberTextField implements FocusListener
{
	private static Log log = Logging.getLog(IntegerControl.class);
	
	private final OptionModelPropertyContainer propertyContainer;
	
	public IntegerControl(OptionModelPropertyContainer propertyContainer)
	{
		super(false);
		this.propertyContainer = propertyContainer;
		
		try {
			Integer initialValue = (Integer) propertyContainer.getValue();
			if (initialValue != null) {
				this.setText(""+initialValue);
			}
		} catch (MethodContainerInvokeException ex) {
			log.error("Error setting initial value for property " + propertyContainer.getPropertyName(), ex);
		}
		
		this.addFocusListener(this);
	}
	
	
	@Override
	public void focusGained(FocusEvent e) { }

	@Override
	public void focusLost(FocusEvent e)
	{
		
		int value = this.getInteger();
		
		try {
			propertyContainer.setValue(value);
		} catch (MethodContainerInvokeException ex) {
			log.error("Error setting value for property " + propertyContainer.getPropertyName());
		}
		
	}
	
}
