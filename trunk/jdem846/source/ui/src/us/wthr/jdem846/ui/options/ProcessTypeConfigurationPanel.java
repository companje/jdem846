package us.wthr.jdem846.ui.options;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.OptionModelChangeEvent;
import us.wthr.jdem846.model.OptionModelChangeListener;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.ModelProcessRegistry;
import us.wthr.jdem846.model.processing.ProcessInstance;
import us.wthr.jdem846.ui.base.ComboBox;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.ScrollPane;
import us.wthr.jdem846.ui.panels.FlexGridPanel;

@SuppressWarnings("serial")
public class ProcessTypeConfigurationPanel extends Panel implements OptionModelUIControl
{
	private static Log log = Logging.getLog(ProcessTypeConfigurationPanel.class);

	private GridProcessingTypesEnum processType;

	private ProcessTypeListModel processTypeListModel;
	private ComboBox cmbProcessSelection;

	private List<OptionModel> providedOptionModelList = new LinkedList<OptionModel>();

	private String currentProcessId;
	private OptionModel currentOptionModel;
	private OptionModelContainer currentOptionModelContainer;
	private DynamicOptionsPanel currentOptionsPanel;
	private ScrollPane currentScrollPane;

	private OptionModelChangeListener propertyChangeListener;

	private List<ModelConfigurationChangeListener> modelConfigurationChangeListeners = new LinkedList<ModelConfigurationChangeListener>();

	public ProcessTypeConfigurationPanel(GridProcessingTypesEnum processType, String initialSelection)
	{
		this(processType, initialSelection, null);
	}

	public ProcessTypeConfigurationPanel(GridProcessingTypesEnum processType, String initialSelection, List<OptionModel> providedOptionModelList)
	{
		this.processType = processType;
		if (providedOptionModelList != null) {
			this.providedOptionModelList.addAll(providedOptionModelList);
		}

		processTypeListModel = new ProcessTypeListModel(processType);
		cmbProcessSelection = new ComboBox(processTypeListModel);

		propertyChangeListener = new OptionModelChangeListener()
		{
			public void onPropertyChanged(OptionModelChangeEvent e)
			{
				// log.info("Property change for " + e.getPropertyName() +
				// " from " + e.getOldValue() + " to " + e.getNewValue());
				firePropertyChangeListeners(e);
			}
		};

		ItemListener comboBoxItemListener = new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED) {
					onProcessSelectionChanged(processTypeListModel.getSelectedItemValue());
					fireProcessSelectedListeners(processTypeListModel.getSelectedItemValue());
				}

			}
		};
		cmbProcessSelection.addItemListener(comboBoxItemListener);

		// Set Layout
		setLayout(new BorderLayout());

		FlexGridPanel topGrid = new FlexGridPanel(2);
		topGrid.add(new Label("Process:"));
		topGrid.add(cmbProcessSelection);
		topGrid.closeGrid();

		add(topGrid, BorderLayout.NORTH);

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		if (initialSelection != null) {
			processTypeListModel.setSelectedItemByValue(initialSelection);
			onProcessSelectionChanged(initialSelection);
		}
	}

	public void refreshUI()
	{
		if (currentOptionsPanel != null) {
			currentOptionsPanel.refreshUI();
		}
	}

	public void setControlErrorDisplayed(String id, boolean display, String message)
	{
		currentOptionsPanel.setControlErrorDisplayed(id, display, message);
	}

	protected void onProcessSelectionChanged(String processId)
	{
		ProcessInstance processInstance = ModelProcessRegistry.getInstance(processId);

		if (processInstance != null) {
			log.info("Process Selected: " + processInstance.getId());
			this.currentProcessId = processInstance.getId();

			buildOptionsPanel(processInstance.getOptionModelClass());

		} else {
			log.info("Process not found with id " + processId);
		}

	}

	protected void buildOptionsPanel(Class<?> optionModelClass)
	{

		log.info("Building option panel for " + optionModelClass.getName());

		if (currentOptionModelContainer != null) {
			currentOptionModelContainer.removeOptionModelChangeListener(propertyChangeListener);
			currentOptionModelContainer = null;
		}

		if (currentOptionsPanel != null) {
			remove(currentOptionsPanel);
			currentOptionsPanel = null;
		}

		if (currentScrollPane != null) {
			remove(currentScrollPane);
			currentScrollPane = null;
		}

		currentOptionModel = getProvidedOptionModel(optionModelClass);
		if (currentOptionModel == null) {
			try {
				currentOptionModel = (OptionModel) optionModelClass.newInstance();
			} catch (Exception ex) {
				// TODO: Display error dialog
				log.error("Error creating instance of option model: " + ex.getMessage(), ex);
				return;
			}
		}

		currentOptionModelContainer = null;
		try {
			currentOptionModelContainer = new OptionModelContainer(currentOptionModel);
		} catch (InvalidProcessOptionException ex) {
			// TODO: Show error dialog
			log.error("Error creating option model container: " + ex.getMessage(), ex);
			return;
		}

		currentOptionModelContainer.addOptionModelChangeListener(propertyChangeListener);

		currentOptionsPanel = new DynamicOptionsPanel(currentOptionModelContainer);
		// currentOptionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10,
		// 10, 10));

		currentScrollPane = new ScrollPane(currentOptionsPanel);
		currentScrollPane.setBorder(null);
		add(currentScrollPane, BorderLayout.CENTER);

		this.validate();
	}

	protected OptionModel getProvidedOptionModel(Class<?> clazz)
	{

		for (OptionModel optionModel : this.providedOptionModelList) {
			if (optionModel.getClass().equals(clazz)) {
				return optionModel;
			}
		}

		return null;

	}

	public OptionModel getCurrentOptionModel()
	{
		return currentOptionModel;
	}

	public OptionModelContainer getCurrentOptionModelContainer()
	{
		return currentOptionModelContainer;
	}

	public String getCurrentProcessId()
	{
		return currentProcessId;
	}

	public void addModelConfigurationChangeListener(ModelConfigurationChangeListener listener)
	{
		modelConfigurationChangeListeners.add(listener);
	}

	public boolean removeModelConfigurationChangeListener(ModelConfigurationChangeListener listener)
	{
		return modelConfigurationChangeListeners.remove(listener);
	}

	protected void fireProcessSelectedListeners(String processId)
	{

		for (ModelConfigurationChangeListener listener : modelConfigurationChangeListeners) {
			listener.onProcessSelected(processId);
		}

	}

	protected void firePropertyChangeListeners(OptionModelChangeEvent e)
	{
		for (ModelConfigurationChangeListener listener : modelConfigurationChangeListeners) {
			listener.onPropertyChanged(e);
		}
	}

}
