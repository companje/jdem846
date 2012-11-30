package us.wthr.jdem846ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.TableWrapData;

public class LabeledColor  extends LabeledControl<ColorEdit> {

	protected LabeledColor(Label label, ColorEdit control) {
		super(label, control);
	}

	
	
	public static LabeledColor create(Composite parent, String labelText, int style)
	{
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));
		
		
		ColorEdit colorEdit = new ColorEdit(parent, SWT.NONE);

		return new LabeledColor(label, colorEdit);
	}
}
