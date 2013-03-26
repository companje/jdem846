package us.wthr.jdem846ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.TableWrapData;

public class LabeledCombo extends LabeledControl<Combo> 
{

	protected LabeledCombo(Label label, Combo control) {
		super(label, control);
	}
	
	
	public static LabeledCombo create(Composite parent, String labelText)
	{
		return LabeledCombo.create(parent, labelText, SWT.READ_ONLY);
	}
	
	public static LabeledCombo create(Composite parent, String labelText, int style)
	{
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));
		
		Combo combo = new Combo(parent, style);
		combo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		return new LabeledCombo(label, combo);
	}

}
