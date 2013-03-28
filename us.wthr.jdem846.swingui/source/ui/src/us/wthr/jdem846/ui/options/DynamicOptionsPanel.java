package us.wthr.jdem846.ui.options;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.AzimuthElevationAngles;
import us.wthr.jdem846.model.LightingDate;
import us.wthr.jdem846.model.LightingTime;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.RgbaColor;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.panels.FlexGridPanel;

@SuppressWarnings("serial")
public class DynamicOptionsPanel extends Panel implements OptionModelUIControl
{
	private static Log log = Logging.getLog(DynamicOptionsPanel.class);

	private OptionModelContainer container;
	private FlexGridPanel controlGrid;

	private Map<String, LabeledControlContainer> optionModelUiControls = new HashMap<String, LabeledControlContainer>();

	// private List<ModelConfigurationChangeListener>
	// modelConfigurationChangeListeners = new
	// LinkedList<ModelConfigurationChangeListener>();

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

	public void refreshUI()
	{
		for (LabeledControlContainer container : optionModelUiControls.values()) {
			container.getControl().refreshUI();
		}
	}

	public void setControlErrorDisplayed(String id, boolean display, String message)
	{
		LabeledControlContainer container = optionModelUiControls.get(id);

		if (container != null) {
			container.setDisplayError(display, message);
		}

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

	protected void addControl(String id, String label, String toolTipText, OptionModelUIControl control)
	{
		Label jlabel = new Label(label);
		jlabel.setToolTipText(toolTipText);

		optionModelUiControls.put(id, new LabeledControlContainer(id, jlabel, control));

		controlGrid.add(jlabel);
		controlGrid.add((Component) control);
	}

	protected void addTimeControl(OptionModelPropertyContainer propertyContainer)
	{
		addControl(propertyContainer.getId(), propertyContainer.getLabel() + ":", propertyContainer.getTooltip(), new TimeControl(propertyContainer));
	}

	protected void addDateControl(OptionModelPropertyContainer propertyContainer)
	{
		addControl(propertyContainer.getId(), propertyContainer.getLabel() + ":", propertyContainer.getTooltip(), new DateControl(propertyContainer));
	}

	protected void addDoubleControl(OptionModelPropertyContainer propertyContainer)
	{
		addControl(propertyContainer.getId(), propertyContainer.getLabel() + ":", propertyContainer.getTooltip(), new DoubleControl(propertyContainer));
	}

	protected void addIntegerControl(OptionModelPropertyContainer propertyContainer)
	{
		addControl(propertyContainer.getId(), propertyContainer.getLabel() + ":", propertyContainer.getTooltip(), new IntegerControl(propertyContainer));
	}

	protected void addViewPerspectiveControl(OptionModelPropertyContainer propertyContainer)
	{
		addControl(propertyContainer.getId(), propertyContainer.getLabel() + ":", propertyContainer.getTooltip(), new ViewPerspectiveControl(propertyContainer));
	}

	protected void addAzimuthElevationAnglesControl(OptionModelPropertyContainer propertyContainer)
	{
		addControl(propertyContainer.getId(), propertyContainer.getLabel() + ":", propertyContainer.getTooltip(), new AzimuthElevationAnglesControl(propertyContainer));
	}

	protected void addColorPropertyControl(OptionModelPropertyContainer propertyContainer)
	{
		addControl(propertyContainer.getId(), propertyContainer.getLabel() + ":", propertyContainer.getTooltip(), new ColorSelectControl(propertyContainer));
	}

	protected void addListPropertyControl(OptionModelPropertyContainer propertyContainer)
	{
		addControl(propertyContainer.getId(), propertyContainer.getLabel() + ":", propertyContainer.getTooltip(), new ListControl(propertyContainer));
	}

	protected void addBooleanPropertyControl(OptionModelPropertyContainer propertyContainer)
	{
		addControl(propertyContainer.getId(), "", propertyContainer.getTooltip(), new BooleanControl(propertyContainer));
	}

}
