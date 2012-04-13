package us.wthr.jdem846.ui.options;

import java.awt.BorderLayout;
import java.util.List;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.AzimuthElevationAngles;
import us.wthr.jdem846.model.LightingDate;
import us.wthr.jdem846.model.LightingTime;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.RgbaColor;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.ui.base.CheckBox;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.panels.FlexGridPanel;

@SuppressWarnings("serial")
public class DynamicOptionsPanel extends Panel
{
	private static Log log = Logging.getLog(DynamicOptionsPanel.class);
	
	private OptionModelContainer container;
	private FlexGridPanel controlGrid;
	
	public DynamicOptionsPanel(OptionModelContainer container)
	{
		this.container = container;
		
		
		controlGrid = new FlexGridPanel(2);
		
		List<OptionModelPropertyContainer> properties = container.getProperties();
		for (OptionModelPropertyContainer propertyContainer : properties) {
			addPropertyControl(propertyContainer);
		}
		
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		add(controlGrid, BorderLayout.CENTER);
		
		controlGrid.closeGrid();
	}
	
	
	protected void addPropertyControl(OptionModelPropertyContainer propertyContainer)
	{
		
		if (!propertyContainer.getListModelClass().equals(Object.class)) {
			addListPropertyControl(propertyContainer);
		} else if (propertyContainer.getType().equals(boolean.class)) {
			addBooleanPropertyControl(propertyContainer);
		} else if (propertyContainer.getType().equals(RgbaColor.class)) {
			addColorPropertyControl(propertyContainer);
		} else if (propertyContainer.getType().equals(AzimuthElevationAngles.class)) {
			addAzimuthElevationAnglesControl(propertyContainer);
		} else if (propertyContainer.getType().equals(ViewPerspective.class)) {
			addViewPerspectiveControl(propertyContainer);
		} else if (propertyContainer.getType().equals(double.class)) {
			addDoubleControl(propertyContainer);
		} else if (propertyContainer.getType().equals(int.class)) {
			addIntegerControl(propertyContainer);
		} else if (propertyContainer.getType().equals(LightingDate.class)) {
			addDateControl(propertyContainer);
		} else if (propertyContainer.getType().equals(LightingTime.class)) {
			addTimeControl(propertyContainer);
		}
		
		
		
	}
	
	protected void addTimeControl(OptionModelPropertyContainer propertyContainer)
	{
		controlGrid.add(new Label(propertyContainer.getLabel() + ":"));
		controlGrid.add(new TimeControl(propertyContainer));
	}
	
	protected void addDateControl(OptionModelPropertyContainer propertyContainer)
	{
		controlGrid.add(new Label(propertyContainer.getLabel() + ":"));
		controlGrid.add(new DateControl(propertyContainer));
	}
	
	
	protected void addDoubleControl(OptionModelPropertyContainer propertyContainer)
	{
		controlGrid.add(new Label(propertyContainer.getLabel() + ":"));
		controlGrid.add(new DoubleControl(propertyContainer));
	}
	
	protected void addIntegerControl(OptionModelPropertyContainer propertyContainer)
	{
		controlGrid.add(new Label(propertyContainer.getLabel() + ":"));
		controlGrid.add(new IntegerControl(propertyContainer));
	}
	
	protected void addViewPerspectiveControl(OptionModelPropertyContainer propertyContainer)
	{
		controlGrid.add(new Label(propertyContainer.getLabel() + ":"));
		controlGrid.add(new ViewPerspectiveControl(propertyContainer));
	}
	
	protected void addAzimuthElevationAnglesControl(OptionModelPropertyContainer propertyContainer)
	{
		controlGrid.add(new Label(propertyContainer.getLabel() + ":"));
		controlGrid.add(new AzimuthElevationAnglesControl(propertyContainer));
	}

	protected void addColorPropertyControl(OptionModelPropertyContainer propertyContainer)
	{
		controlGrid.add(new Label(propertyContainer.getLabel() + ":"));
		controlGrid.add(new ColorSelectControl(propertyContainer));
	}
	
	protected void addListPropertyControl(OptionModelPropertyContainer propertyContainer)
	{
		controlGrid.add(new Label(propertyContainer.getLabel() + ":"));
		controlGrid.add(new ListControl(propertyContainer));
		
		
	}
	
	protected void addBooleanPropertyControl(OptionModelPropertyContainer propertyContainer)
	{
		controlGrid.add(new Label());
		controlGrid.add(new BooleanControl(propertyContainer));
	}
	
}
