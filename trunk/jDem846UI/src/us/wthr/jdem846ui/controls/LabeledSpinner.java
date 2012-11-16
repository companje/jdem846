package us.wthr.jdem846ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

public class LabeledSpinner extends LabeledControl<Spinner>
{

	protected LabeledSpinner(Label label, Spinner control) {
		super(label, control);
	}
	
	
	public static LabeledSpinner create(FormToolkit toolkit, Composite form, String labelText, int min, int max, int digits, int increments)
	{
		return create(toolkit, form, labelText, SWT.NULL, min, max, digits, increments);
	}
	
	public static LabeledSpinner create(FormToolkit toolkit, Composite form, String labelText, int style, int min, int max, int digits, int increments)
	{
		Label label = toolkit.createLabel(form, labelText);
		Spinner spinner = new Spinner(form, style);
		spinner.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		spinner.setMinimum(min);
		spinner.setMaximum(max);
		spinner.setIncrement(increments);
		spinner.setDigits(digits);
		return new LabeledSpinner(label, spinner);
	}

}
