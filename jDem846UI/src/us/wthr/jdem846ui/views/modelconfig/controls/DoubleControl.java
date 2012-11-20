package us.wthr.jdem846ui.views.modelconfig.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.annotations.ValueBounds;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;
import us.wthr.jdem846ui.controls.LabeledSpinner;

public class DoubleControl {
	
	private static Log log = Logging.getLog(DoubleControl.class);
	
	public static LabeledSpinner create(Composite parent, final OptionModelPropertyContainer property,  String labelText)
	{
		ValueBounds bounds = property.getValueBounds();
		LabeledSpinner control = LabeledSpinner.create(parent, labelText, (int) bounds.minimum() * 100, (int) bounds.maximum() * 100, 2, (int) bounds.stepSize() * 100);
		
		control.getControl().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				Spinner spinner = (Spinner) e.widget;
				double value = (double)spinner.getSelection() / 100.0;
				try {
					property.setValue(value);
					log.info("Setting property " + property.getPropertyName() + " to " + value);
				} catch (MethodContainerInvokeException ex) {
					log.error("Error setting property " + property.getPropertyName() + " to " + value, ex);
				}
			}
		});
		
		
		try {
			control.getControl().setSelection((int) ((Double)property.getValue() * 100));
		} catch (MethodContainerInvokeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return control;
	}
}
