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

public class IntegerControl {
	private static Log log = Logging.getLog(IntegerControl.class);
	
	public static LabeledSpinner create(Composite parent, final OptionModelPropertyContainer property,  String labelText)
	{
		ValueBounds bounds = property.getValueBounds();
		LabeledSpinner control = LabeledSpinner.create(parent, labelText, (int) bounds.minimum(), (int) bounds.maximum(), 0, (int) bounds.stepSize());
		
		control.getControl().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				Spinner spinner = (Spinner) e.widget;
				try {
					property.setValue(spinner.getSelection());
					log.info("Setting property " + property.getPropertyName() + " to " + spinner.getSelection());
				} catch (MethodContainerInvokeException ex) {
					log.error("Error setting property " + property.getPropertyName() + " to " + spinner.getSelection(), ex);
				}
			}
		});
		
		
		try {
			control.getControl().setSelection((Integer)property.getValue());
		} catch (MethodContainerInvokeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return control;
	}
	
}
