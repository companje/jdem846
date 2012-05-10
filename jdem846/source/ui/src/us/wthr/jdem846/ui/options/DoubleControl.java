package us.wthr.jdem846.ui.options;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.annotations.ValueBounds;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;
import us.wthr.jdem846.ui.NumberTextField;
import us.wthr.jdem846.ui.base.Spinner;

@SuppressWarnings("serial")
public class DoubleControl extends Spinner implements ChangeListener, OptionModelUIControl
{
	
	private static Log log = Logging.getLog(DoubleControl.class);
	
	private final OptionModelPropertyContainer propertyContainer;
	
	public DoubleControl(OptionModelPropertyContainer propertyContainer)
	{

		this.propertyContainer = propertyContainer;
		this.setToolTipText(propertyContainer.getTooltip());
		

		
		
		ValueBounds bounds = propertyContainer.getValueBounds();
		if (bounds != null) {
			this.setModel(new SpinnerNumberModel(bounds.minimum(), bounds.minimum(), bounds.maximum(), bounds.stepSize()));
		} else {
			this.setModel(new SpinnerNumberModel(0.0, -100000.0, 100000.0, 1.0));
		}
		
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(this, "#,##0.00");
		this.setEditor(editor);
		
		refreshUI();

		this.addChangeListener(this);
	}
	
	public void refreshUI()
	{
		try {
			Double initialValue = (Double) propertyContainer.getValue();
			if (initialValue != null) {
				this.setValue(initialValue);
			}
		} catch (MethodContainerInvokeException ex) {
			log.error("Error setting initial value for property " + propertyContainer.getPropertyName(), ex);
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent arg0)
	{
		Object obj = this.getValue();
		
		double value = 0;
		
		if (obj instanceof Integer) {
			value = ((Integer)obj).doubleValue();
		} else if (obj instanceof Double) {
			value = (Double)obj;
		}
		
		try {
			propertyContainer.setValue(value);
		} catch (MethodContainerInvokeException ex) {
			log.error("Error setting value for property " + propertyContainer.getPropertyName());
		}
	}
	

	
}
