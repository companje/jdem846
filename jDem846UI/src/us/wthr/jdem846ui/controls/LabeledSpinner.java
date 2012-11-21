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
		return create(form, labelText, style, true, min, max, digits, increments);
	}
	
	public static LabeledSpinner create(Composite form, String labelText)
	{
		return create(form, labelText, SWT.NULL, false, 0, 0, 0, 0);
	}
	
	public static LabeledSpinner create(Composite form, String labelText, int style)
	{
		return create(form, labelText, style, false, 0, 0, 0, 0);
	}
	
	protected static LabeledSpinner create(Composite form, String labelText, int style, boolean setBounds, int min, int max, int digits, int increments)
	{
		Label label = new Label(form, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));
		
		Spinner spinner = new Spinner(form, style | SWT.BORDER);
		spinner.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		if (setBounds) {
			spinner.setMinimum(min);
			spinner.setMaximum(max);
			spinner.setIncrement(increments);
			spinner.setDigits(digits);
		}
		
		return new LabeledSpinner(label, spinner);
	}

}
