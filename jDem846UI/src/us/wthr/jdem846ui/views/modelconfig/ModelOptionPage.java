package us.wthr.jdem846ui.views.modelconfig;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.AzimuthElevationAngles;
import us.wthr.jdem846.model.LightingDate;
import us.wthr.jdem846.model.LightingTime;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.RgbaColor;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;
import us.wthr.jdem846ui.views.modelconfig.controls.BooleanControl;
import us.wthr.jdem846ui.views.modelconfig.controls.DoubleControl;
import us.wthr.jdem846ui.views.modelconfig.controls.IntegerControl;
import us.wthr.jdem846ui.views.modelconfig.controls.ListControl;


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
		
		TableWrapLayout layout = new TableWrapLayout();
		this.setLayout(layout);
		layout.numColumns = 2;
		
		List<OptionModelPropertyContainer> properties = container.getProperties();
		for (OptionModelPropertyContainer propertyContainer : properties) {
			addPropertyControl(propertyContainer);
		}
	}
	
	

	protected void addPropertyControl(OptionModelPropertyContainer propertyContainer)
	{
		
		if (!propertyContainer.getListModelClass().equals(Object.class)) {
			addListPropertyControl(propertyContainer);
		} else if (propertyContainer.getType().equals(boolean.class)) {
			addBooleanPropertyControl(propertyContainer);
		} else if (propertyContainer.getType().equals(RgbaColor.class)) {
			//addColorPropertyControl(propertyContainer);
		} else if (propertyContainer.getType().equals(AzimuthElevationAngles.class)) {
			//addAzimuthElevationAnglesControl(propertyContainer);
		} else if (propertyContainer.getType().equals(ViewPerspective.class)) {
			//addViewPerspectiveControl(propertyContainer);
		} else if (propertyContainer.getType().equals(double.class)) {
			addDoublePropertyControl(propertyContainer);
		} else if (propertyContainer.getType().equals(int.class)) {
			addIntegerPropertyControl(propertyContainer);
		} else if (propertyContainer.getType().equals(LightingDate.class)) {
			//addDateControl(propertyContainer);
		} else if (propertyContainer.getType().equals(LightingTime.class)) {
			//addTimeControl(propertyContainer);
		}
		
		
		
	}
	
	
	protected void addBooleanPropertyControl(OptionModelPropertyContainer propertyContainer)
	{
		BooleanControl.create(this, propertyContainer, propertyContainer.getLabel());
	}
	
	protected void addIntegerPropertyControl(OptionModelPropertyContainer propertyContainer)
	{
		IntegerControl.create(this, propertyContainer, propertyContainer.getLabel() + ":");
	}
	
	protected void addDoublePropertyControl(OptionModelPropertyContainer propertyContainer)
	{
		DoubleControl.create(this, propertyContainer, propertyContainer.getLabel() + ":");
	}
	
	protected void addListPropertyControl(OptionModelPropertyContainer propertyContainer)
	{
		ListControl.create(this, propertyContainer, propertyContainer.getLabel() + ":");
	}
	
}
