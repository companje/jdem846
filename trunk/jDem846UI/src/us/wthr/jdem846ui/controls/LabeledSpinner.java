package us.wthr.jdem846ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.widgets.TableWrapData;

public class LabeledSpinner extends LabeledControl<Spinner>
{

	protected LabeledSpinner(Label label, Spinner control) {
		super(label, control);
	}
	
	
	public static LabeledSpinner create(Composite form, String labelText, int min, int max, int digits, int increments)
	{
		return create(form, labelText, SWT.NULL, min, max, digits, increments);
	}
	
	public static LabeledSpinner create(Composite form, String labelText, int style, int min, int max, int digits, int increments)
	{
		Label label = new Label(form, SWT.NONE);
		label.setBackground(form.getBackground());
		label.setText(labelText);
		
		Spinner spinner = new Spinner(form, style | SWT.BORDER);
		spinner.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		spinner.setMinimum(min);
		spinner.setMaximum(max);
		spinner.setIncrement(increments);
		spinner.setDigits(digits);
		return new LabeledSpinner(label, spinner);
	}

}
