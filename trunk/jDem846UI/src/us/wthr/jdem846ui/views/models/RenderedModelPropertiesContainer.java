package us.wthr.jdem846ui.views.models;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846ui.Activator;
import us.wthr.jdem846ui.controls.LabeledText;

public class RenderedModelPropertiesContainer extends Composite
{

	private LabeledText txtSubject;
	private LabeledText txtDescription;
	private LabeledText txtAuthor;
	private LabeledText txtDateRendered;
	
	private ElevationModel elevationModel = null;
	
	private Button btnApply;
	private Button btnReset;
	
	public RenderedModelPropertiesContainer(Composite parent, int style)
	{
		super(parent, style);
		this.setLayout(new FillLayout());
		
		
		
		TableWrapLayout layout;

		TableWrapData td;

		Image variableIcon = Activator.getImageDescriptor("icons/eclipse/variable_view.gif").createImage();

		FormToolkit toolkit = new FormToolkit(this.getDisplay());
		final ScrolledForm form = toolkit.createScrolledForm(this);

		layout = new TableWrapLayout();
		form.getBody().setLayout(layout);

		
		Section generalPropertiesSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		td = new TableWrapData(TableWrapData.FILL_GRAB);

		generalPropertiesSection.setLayoutData(td);
		generalPropertiesSection.addExpansionListener(new ExpansionAdapter()
		{
			public void expansionStateChanged(ExpansionEvent e)
			{
				form.reflow(true);
			}
		});
		generalPropertiesSection.setText("General Properties");
		
		
		Composite generalPropertiesComposite = toolkit.createComposite(generalPropertiesSection);
		layout = new TableWrapLayout();
		generalPropertiesComposite.setLayout(layout);
		layout.numColumns = 2;
		
		txtSubject = LabeledText.create(generalPropertiesComposite, "Subject:");
		txtDescription = LabeledText.create(generalPropertiesComposite, "Description:");
		txtAuthor = LabeledText.create(generalPropertiesComposite, "Author:");
		txtDateRendered = LabeledText.create(generalPropertiesComposite, "Date Rendered:");
		
		generalPropertiesSection.setClient(generalPropertiesComposite);
		
		
		Composite buttonComposite = toolkit.createComposite(form.getBody());
		layout = new TableWrapLayout();
		layout.numColumns = 2;
		buttonComposite.setLayout(layout);

		btnApply = new Button(buttonComposite, SWT.PUSH);
		btnApply.setText("Apply");
		btnApply.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0)
			{
				updateValuesToModel();
			}
		});
		
		btnReset = new Button(buttonComposite, SWT.PUSH);
		btnReset.setText("Reset");
		btnReset.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0)
			{
				updateValuesFromModel();
			}
		});
	}

	public void updateAsync()
	{
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				updateValuesFromModel();
			}
			
		});
	}
	
	protected void updateValuesToModel()
	{
		if (elevationModel == null) {
			return;
		}
		
		elevationModel.setProperty("subject", txtSubject.getControl().getText());
		elevationModel.setProperty("description", txtDescription.getControl().getText());
		elevationModel.setProperty("author", txtAuthor.getControl().getText());
		elevationModel.setProperty("render-date", txtDateRendered.getControl().getText());
		
	}
	
	protected void updateValuesFromModel()
	{
		String subject = (elevationModel != null) ? elevationModel.getProperty("subject") : "";
		String description = (elevationModel != null) ? elevationModel.getProperty("description") : "";
		String author = (elevationModel != null) ? elevationModel.getProperty("author") : "";
		String renderDate = (elevationModel != null) ? elevationModel.getProperty("render-date") : "";
		
		txtSubject.getControl().setText(subject);
		txtDescription.getControl().setText(description);
		txtAuthor.getControl().setText(author);
		txtDateRendered.getControl().setText(renderDate);
	}
	
	public void setElevationModel(ElevationModel elevationModel)
	{
		this.elevationModel = elevationModel;
		updateAsync();
	}
}
