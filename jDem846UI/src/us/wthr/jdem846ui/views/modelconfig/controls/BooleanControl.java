package us.wthr.jdem846ui.views.modelconfig.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;
import us.wthr.jdem846ui.controls.LabeledCheck;

public class BooleanControl
{

	private static Log log = Logging.getLog(BooleanControl.class);
	
	
	public static LabeledCheck create(Composite parent, final OptionModelPropertyContainer property,  String labelText)
	{
		LabeledCheck check = LabeledCheck.create(parent, labelText);
		check.getControl().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				Button check = (Button) event.widget;
				try {
					property.setValue(check.getSelection());
					log.info("Setting property " + property.getPropertyName() + " to " + check.getSelection());
				} catch (MethodContainerInvokeException ex) {
					log.error("Error setting property " + property.getPropertyName() + " to " + check.getSelection(), ex);
				}
			}
		});
		
		
		try {
			check.getControl().setSelection((Boolean)property.getValue());
		} catch (MethodContainerInvokeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return check;
		
	}
	
	
	
	
}
