package us.wthr.jdem846.ui.options;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.annotations.ValueBounds;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;
import us.wthr.jdem846.ui.base.Spinner;

@SuppressWarnings("serial")
public class IntegerControl extends Spinner implements ChangeListener, OptionModelUIControl
{
	private static Log log = Logging.getLog(IntegerControl.class);
	
	private final OptionModelPropertyContainer propertyContainer;
	
	public IntegerControl(OptionModelPropertyContainer propertyContainer)
	{
		this.propertyContainer = propertyContainer;
		this.setToolTipText(propertyContainer.getTooltip());
		
		ValueBounds bounds = propertyContainer.getValueBounds();
		if (bounds != null) {
			this.setModel(new SpinnerNumberModel((int)bounds.minimum(), (int)bounds.minimum(), (int)bounds.maximum(), (int)bounds.stepSize()));
		} else {
			this.setModel(new SpinnerNumberModel(0, 0, 100000, 1));
		}
		
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(this, "#,##0");
		this.setEditor(editor);
		
		refreshUI();

		this.addChangeListener(this);
	}
	
	public void refreshUI()
	{
		try {
			Integer initialValue = (Integer) propertyContainer.getValue();
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
		int value = (Integer) this.getValue();
		
		try {
			propertyContainer.setValue(value);
		} catch (MethodContainerInvokeException ex) {
			log.error("Error setting value for property " + propertyContainer.getPropertyName());
		}
	}

	
}
