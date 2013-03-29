package us.wthr.jdem846ui.views.shape;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import us.wthr.jdem846.shapefile.ShapeBase;
import us.wthr.jdem846.shapefile.modeling.ShapeDataDefinition;
import us.wthr.jdem846.shapefile.modeling.ShapeDataDefinitionLoader;
import us.wthr.jdem846ui.Activator;
import us.wthr.jdem846ui.controls.LabeledCombo;

public class ShapePropertiesContainer extends Composite
{
	
	
	private Button btnApply;
	private Button btnReset;
	
	private ShapeBase shapeFile;
	private LabeledCombo cmbShapeDefinitionType;
	
	private ShapeDataDefinitionLoader shapeDataDefinitionLoader;
	
	public ShapePropertiesContainer(Composite parent, int style)
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

		SelectionListener selectionListener = new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateShapeDefinition();
			}
		};

		Section shapeDefinitionSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		td = new TableWrapData(TableWrapData.FILL_GRAB);

		shapeDefinitionSection.setLayoutData(td);
		shapeDefinitionSection.addExpansionListener(new ExpansionAdapter()
		{
			public void expansionStateChanged(ExpansionEvent e)
			{
				form.reflow(true);
			}
		});
		shapeDefinitionSection.setText("Shape Definition");

		Composite shapeDefinitionComposite = toolkit.createComposite(shapeDefinitionSection);
		layout = new TableWrapLayout();
		shapeDefinitionComposite.setLayout(layout);
		layout.numColumns = 2;
		
		shapeDefinitionSection.setClient(shapeDefinitionComposite);
		
		cmbShapeDefinitionType = LabeledCombo.create(shapeDefinitionComposite, "Shape Type:");
		cmbShapeDefinitionType.getControl().addSelectionListener(selectionListener);
		shapeDataDefinitionLoader = new ShapeDataDefinitionLoader();
		
		for (ShapeDataDefinition shapeDataDefinition : shapeDataDefinitionLoader.getShapeDataDefinitions()) {
			cmbShapeDefinitionType.getControl().add(shapeDataDefinition.getName());
		}
		
		Composite buttonComposite = toolkit.createComposite(form.getBody());
		layout = new TableWrapLayout();
		layout.numColumns = 2;
		buttonComposite.setLayout(layout);

		btnApply = new Button(buttonComposite, SWT.PUSH);
		btnApply.setText("Apply");

		btnReset = new Button(buttonComposite, SWT.PUSH);
		btnReset.setText("Reset");

		this.pack();
	}
	
	public void setShapeDefinition(ShapeBase shapeFile)
	{
		this.shapeFile = shapeFile;
		
		
		if (shapeFile != null) {
			
			int index = getIndexOfString(cmbShapeDefinitionType, getDefinitionNameFromId(shapeFile.getShapeFileReference().getShapeDataDefinitionId()));
			if (index >= 0) {
				cmbShapeDefinitionType.getControl().select(index);
			}
			
		} else {
			cmbShapeDefinitionType.getControl().select(0);
		}
		
		
	}
	
	
	protected void updateShapeDefinition()
	{
		if (shapeFile == null) {
			return;
		}
		
		String shapeDataDefinitionId = getDefinitionIdFromName(cmbShapeDefinitionType.getControl().getText());
		shapeFile.getShapeFileReference().setShapeDataDefinitionId(shapeDataDefinitionId);
	}
	
	protected String getDefinitionNameFromId(String id)
	{
		for (ShapeDataDefinition shapeDataDefinition : shapeDataDefinitionLoader.getShapeDataDefinitions()) {
			if (shapeDataDefinition.getId().equals(id)) {
				return shapeDataDefinition.getName();
			}
		}
		return null;
	}
	
	protected String getDefinitionIdFromName(String name)
	{
		for (ShapeDataDefinition shapeDataDefinition : shapeDataDefinitionLoader.getShapeDataDefinitions()) {
			if (shapeDataDefinition.getName().equals(name)) {
				return shapeDataDefinition.getId();
			}
		}
		return null;
	}
	
	protected int getIndexOfString(LabeledCombo combo, String label)
	{
		for (int i = 0; i < combo.getControl().getItemCount(); i++) {
			if (combo.getControl().getItem(i) != null && combo.getControl().getItem(i).equals(label)) {
				return i;
			}
		}

		return -1;
	}
}
