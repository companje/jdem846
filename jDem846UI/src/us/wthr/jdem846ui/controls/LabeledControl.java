package us.wthr.jdem846ui.controls;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class LabeledControl<T extends Control>
{
	private Label label;
	private T control;
	
	protected LabeledControl(Label label, T control)
	{
		this.label = label;
		this.control = control;

	}

	public Label getLabel() {
		return label;
	}

	public T getControl() {
		return control;
	}
	
	
}
