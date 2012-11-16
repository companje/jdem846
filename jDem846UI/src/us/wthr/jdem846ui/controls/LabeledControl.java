package us.wthr.jdem846ui.controls;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class LabeledControl<T extends Control>
{
	private Label label;
	private T control;
	
	protected LabeledControl(Label label, T control)
	{
		this.label = label;
		this.control = control;
		
		//control.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		//control.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		
	}

	public Label getLabel() {
		return label;
	}

	public T getControl() {
		return control;
	}
	
	
}
