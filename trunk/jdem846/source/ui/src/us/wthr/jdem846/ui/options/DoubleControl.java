package us.wthr.jdem846.ui.options;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;
import us.wthr.jdem846.ui.NumberTextField;

@SuppressWarnings("serial")
public class DoubleControl extends NumberTextField implements FocusListener
{
	
	private static Log log = Logging.getLog(DoubleControl.class);
	
	private final OptionModelPropertyContainer propertyContainer;
	
	public DoubleControl(OptionModelPropertyContainer propertyContainer)
	{
		super(true);
		this.propertyContainer = propertyContainer;
		
		
		
		try {
			Double initialValue = (Double) propertyContainer.getValue();
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
		
		double value = this.getDouble();
		
		try {
			propertyContainer.setValue(value);
		} catch (MethodContainerInvokeException ex) {
			log.error("Error setting value for property " + propertyContainer.getPropertyName());
		}
		
	}
	
	
}
