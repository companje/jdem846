package us.wthr.jdem846ui.views.modelconfig;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;
import us.wthr.jdem846ui.project.ProjectContext;

public class ModelConfigurationView extends ViewPart {
	private static Log log = Logging.getLog(ModelConfigurationView.class);
	public static final String ID = "jdem846ui.modelConfigurationView";

	private FormToolkit toolkit;
	private ScrolledForm form;

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		
		final TabFolder tabFolder = new TabFolder (parent, SWT.TOP);
		TabItem item0 = new TabItem(tabFolder, SWT.NONE);
		item0.setText("General");

		form = toolkit.createScrolledForm(tabFolder);
		
		
		form.setText("Model Options");
		TableWrapLayout layout = new TableWrapLayout();
		form.getBody().setLayout(layout);
		
		layout.numColumns = 2;
		
		OptionModelContainer globalOptionModelContainer = null;
		try {
			globalOptionModelContainer = new OptionModelContainer(ProjectContext.getInstance().getModelProcessManifest().getGlobalOptionModel());
		} catch (InvalidProcessOptionException ex) {
			// TODO Display error dialog
			log.error("Error loading global option model container: " + ex.getMessage(), ex);
			return;
		}
		
		ModelOptionPage globalOptionPage = new ModelOptionPage(form.getBody(), globalOptionModelContainer);
		item0.setControl(form);
		
		/*
		form = toolkit.createScrolledForm(tabFolder);
		
		
		form.setText("Model Options");
		TableWrapLayout layout = new TableWrapLayout();
		form.getBody().setLayout(layout);
		
		layout.numColumns = 2;

		LabeledText width = LabeledText.create(toolkit, form.getBody(), "Width:");
		LabeledText height = LabeledText.create(toolkit, form.getBody(), "Height:");
		LabeledCheck maintainAspectRatio = LabeledCheck.create(form, "Maintain Aspect Ratio");

		LabeledCheck estimateElevationRange = LabeledCheck.create(form.getBody(), "Estimate Elevation Range");

		
		CoordinatesSection coordinatesSection = CoordinatesSection.create(toolkit, form);
		ProjectionSection projectionSection = ProjectionSection.create(toolkit, form);


		item0.setControl(form);
		*/
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
