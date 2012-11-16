package us.wthr.jdem846ui.controls;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;

public class LabeledText extends LabeledControl<Text>
{
	
	protected LabeledText(Label label, Text control) {
		super(label, control);
	}

	
	public static LabeledText create(FormToolkit toolkit, Composite form, String labelText)
	{
		Label label = toolkit.createLabel(form, labelText);
		Text text = toolkit.createText(form, "");
		text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		return new LabeledText(label, text);
	}
	
}
