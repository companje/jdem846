package us.wthr.jdem846ui.views.modelconfig;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846ui.controls.LabeledCheck;
import us.wthr.jdem846ui.controls.LabeledText;
import us.wthr.jdem846ui.controls.SectionContainer;
import us.wthr.jdem846ui.controls.SectionFactory;

public class ModelConfigurationView extends ViewPart {
	public static final String ID = "jdem846ui.modelConfigurationView";

	private FormToolkit toolkit;
	private ScrolledForm form;

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText("Model Options");
		TableWrapLayout layout = new TableWrapLayout();
		form.getBody().setLayout(layout);
		
		layout.numColumns = 2;
		//toolkit.paintBordersFor(parent);

		
		
		LabeledText width = LabeledText.create(toolkit, form.getBody(), "Width:");
		LabeledText height = LabeledText.create(toolkit, form.getBody(), "Height:");
		LabeledCheck maintainAspectRatio = LabeledCheck.create(toolkit, form,
				"Maintain Aspect Ratio");

		LabeledCheck estimateElevationRange = LabeledCheck.create(toolkit,
				form.getBody(), "Estimate Elevation Range");

		
		CoordinatesSection coordinatesSection = CoordinatesSection.create(toolkit, form);
		ProjectionSection projectionSection = ProjectionSection.create(toolkit, form);

		
		
	}

	@Override
	public void setFocus() {
		form.setFocus();
	}

	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}
}
