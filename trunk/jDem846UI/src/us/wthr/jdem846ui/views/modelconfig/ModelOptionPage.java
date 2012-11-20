package us.wthr.jdem846ui.views.modelconfig;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import us.wthr.jdem846.model.AzimuthElevationAngles;
import us.wthr.jdem846.model.LightingDate;
import us.wthr.jdem846.model.LightingTime;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.RgbaColor;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846ui.views.modelconfig.controls.BooleanControl;
import us.wthr.jdem846ui.views.modelconfig.controls.DoubleControl;
import us.wthr.jdem846ui.views.modelconfig.controls.IntegerControl;
import us.wthr.jdem846ui.views.modelconfig.controls.ListControl;


public class ModelOptionPage
{
	private OptionModelContainer container;
	private Composite parent;
	
	public ModelOptionPage(Composite parent, OptionModelContainer container) 
	{
		this.parent = parent;
		this.container = container;
		
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
		BooleanControl.create(parent, propertyContainer, propertyContainer.getLabel());
	}
	
	protected void addIntegerPropertyControl(OptionModelPropertyContainer propertyContainer)
	{
		IntegerControl.create(parent, propertyContainer, propertyContainer.getLabel() + ":");
	}
	
	protected void addDoublePropertyControl(OptionModelPropertyContainer propertyContainer)
	{
		DoubleControl.create(parent, propertyContainer, propertyContainer.getLabel() + ":");
	}
	
	protected void addListPropertyControl(OptionModelPropertyContainer propertyContainer)
	{
		ListControl.create(parent, propertyContainer, propertyContainer.getLabel() + ":");
	}
	
}
