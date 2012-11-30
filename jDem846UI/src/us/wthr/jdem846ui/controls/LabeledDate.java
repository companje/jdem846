package us.wthr.jdem846ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.TableWrapData;


public class LabeledDate extends LabeledControl<DateEdit> 
{

	protected LabeledDate(Label label, DateEdit control) {
		super(label, control);
	}
	
	public static LabeledDate create(Composite parent, String labelText, int style)
	{
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));
		
		
		DateEdit dateEdit = new DateEdit(parent, SWT.NONE);

		return new LabeledDate(label, dateEdit);
	}

}
