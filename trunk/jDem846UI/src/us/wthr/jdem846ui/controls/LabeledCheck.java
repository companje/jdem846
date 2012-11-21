package us.wthr.jdem846ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LabeledCheck extends LabeledControl<Button> {

	protected LabeledCheck(Label label, Button control) {
		super(label, control);
	}

	
	public static LabeledCheck create(Composite parent, String labelText)
	{
		Label label = new Label(parent, SWT.NONE);
		label.setText("");
		
		Button button = new Button(parent, SWT.CHECK);
		button.setText(labelText);
		
		return new LabeledCheck(label, button);
	}
}
