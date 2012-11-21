package us.wthr.jdem846ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.TableWrapData;

public class LabeledText extends LabeledControl<Text>
{
	
	protected LabeledText(Label label, Text control) {
		super(label, control);
	}

	
	public static LabeledText create(Composite form, String labelText)
	{
		Label label = new Label(form, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));
		
		Text text = new Text(form, SWT.BORDER);
		text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		return new LabeledText(label, text);
	}
	
}
