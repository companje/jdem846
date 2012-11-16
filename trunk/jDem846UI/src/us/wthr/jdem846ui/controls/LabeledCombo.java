package us.wthr.jdem846ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

public class LabeledCombo extends LabeledControl<Combo> 
{

	protected LabeledCombo(Label label, Combo control) {
		super(label, control);
	}
	
	
	
	public static LabeledCombo create(FormToolkit toolkit, Composite form, String labelText, int style)
	{
		Label label = toolkit.createLabel(form, labelText);
		Combo combo = new Combo(form, style);
		combo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		return new LabeledCombo(label, combo);
	}

}
