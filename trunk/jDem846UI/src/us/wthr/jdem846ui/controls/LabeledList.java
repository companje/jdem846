package us.wthr.jdem846ui.controls;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

public class LabeledList extends LabeledControl<List>
{

	protected LabeledList(Label label, List control) {
		super(label, control);
	}
	
	
	public static LabeledList create(FormToolkit toolkit, Composite form, String labelText, int style)
	{
		Label label = toolkit.createLabel(form, labelText);
		List list = new List(form, style);
		list.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		return new LabeledList(label, list);
	}

}
