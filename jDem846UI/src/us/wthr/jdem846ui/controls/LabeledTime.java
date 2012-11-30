package us.wthr.jdem846ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.TableWrapData;

public class LabeledTime extends LabeledControl<DateTime>  {

	protected LabeledTime(Label label, DateTime control) {
		super(label, control);
	}
	
	public static LabeledTime create(Composite parent, String labelText, int style)
	{
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));
		
		
		DateTime dateControl = new DateTime(parent, SWT.TIME);
		dateControl.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		return new LabeledTime(label, dateControl);
	}
}
