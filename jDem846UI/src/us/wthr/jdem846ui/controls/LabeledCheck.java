package us.wthr.jdem846ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class LabeledCheck extends LabeledControl<Button> {

	protected LabeledCheck(Label label, Button control) {
		super(label, control);
	}

	
	public static LabeledCheck create(FormToolkit toolkit, Composite form, String labelText)
	{
		Label label = toolkit.createLabel(form, "");
		Button button = toolkit.createButton(form, labelText, SWT.CHECK);

		return new LabeledCheck(label, button);
	}
}
