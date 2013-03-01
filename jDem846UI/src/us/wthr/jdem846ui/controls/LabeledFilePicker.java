package us.wthr.jdem846ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.TableWrapData;


public class LabeledFilePicker extends LabeledControl<FilePicker>  
{

	protected LabeledFilePicker(Label label, FilePicker control)
	{
		super(label, control);
	}
	
	
	public static LabeledFilePicker create(Composite parent, String labelText, int style)
	{
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));
		
		
		FilePicker picker = new FilePicker(parent, SWT.TIME);
		picker.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.MIDDLE));
		
		return new LabeledFilePicker(label, picker);
	}
	
}
