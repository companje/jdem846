package us.wthr.jdem846ui.views.modelconfig;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;
import us.wthr.jdem846ui.controls.LabeledControl;

public class ModelOptionPage extends Composite
{
	private static Log log = Logging.getLog(ModelOptionPage.class);

	private OptionModelContainer container;

	public ModelOptionPage(Composite parent, OptionModel optionModel)
	{
		super(parent, SWT.NONE);

		OptionModelContainer optionModelContainer = null;
		try {
			optionModelContainer = new OptionModelContainer(optionModel);
		} catch (InvalidProcessOptionException ex) {
			// TODO: Show error dialog
			log.error("Error creating option model container: " + ex.getMessage(), ex);
			return;
		}

		init(optionModelContainer);
	}

	public ModelOptionPage(Composite parent, OptionModelContainer container)
	{
		super(parent, SWT.NONE);
		init(container);
	}

	protected void init(OptionModelContainer container)
	{
		this.container = container;
		setLayout(new FillLayout(SWT.HORIZONTAL));

		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.V_SCROLL | SWT.H_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite composite = new Composite(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(composite);

		TableWrapLayout layout = new TableWrapLayout();
		composite.setLayout(layout);
		layout.numColumns = 2;

		List<OptionModelPropertyContainer> properties = container.getProperties();
		for (OptionModelPropertyContainer propertyContainer : properties) {
			if (propertyContainer.isVisible()) {
				LabeledControl<?> labeledControl = ModelOptionControlsFactory.createControl(propertyContainer.getListModelClass(), composite, container.getOptionModel(), propertyContainer);
			}
		}

		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		this.setRedraw(true);
	}
}
