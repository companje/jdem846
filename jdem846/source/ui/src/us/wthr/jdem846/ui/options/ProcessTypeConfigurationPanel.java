package us.wthr.jdem846.ui.options;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

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
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.ScrollPane;

@SuppressWarnings("serial")
public class ProcessTypeConfigurationPanel extends Panel
{
	private static Log log = Logging.getLog(ProcessTypeConfigurationPanel.class);
	
	private GridProcessingTypesEnum processType;
	
	private ProcessTypeListModel processTypeListModel;
	private ComboBox cmbProcessSelection;
	
	private OptionModel currentOptionModel;
	private OptionModelContainer currentOptionModelContainer;
	private DynamicOptionsPanel currentOptionsPanel;
	
	private OptionModelChangeListener propertyChangeListener;
	
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	public ProcessTypeConfigurationPanel(GridProcessingTypesEnum processType)
	{
		this.processType = processType;
		
		processTypeListModel = new ProcessTypeListModel(processType);
		cmbProcessSelection = new ComboBox(processTypeListModel);
		
		propertyChangeListener = new OptionModelChangeListener() {
			public void onPropertyChanged(OptionModelChangeEvent e)
			{
				log.info("Property change for " + e.getPropertyName() + " from " + e.getOldValue() + " to " + e.getNewValue());
			}
		};
		
		
		ItemListener comboBoxItemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					onProcessSelectionChanged(processTypeListModel.getSelectedItemValue());
				}
					
			}
		};
		cmbProcessSelection.addItemListener(comboBoxItemListener);
		
		
		
		// Set Layout
		setLayout(new BorderLayout());
		add(cmbProcessSelection, BorderLayout.NORTH);
	}
	
	protected void onProcessSelectionChanged(String processId)
	{
		ProcessInstance processInstance = ModelProcessRegistry.getInstance(processId);
		
		if (processInstance != null) {
			log.info("Process Selected: " + processInstance.getId());
			
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
		
		currentOptionModel = null;
		try {
			currentOptionModel = (OptionModel) optionModelClass.newInstance();
		} catch (Exception ex) {
			// TODO: Display error dialog
			log.error("Error creating instance of option model: " + ex.getMessage(), ex);
			return;
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
		currentOptionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		ScrollPane scroll = new ScrollPane(currentOptionsPanel);
		add(scroll, BorderLayout.CENTER);
		
		this.validate();
	}

	public OptionModel getCurrentOptionModel()
	{
		return currentOptionModel;
	}

	public OptionModelContainer getCurrentOptionModelContainer()
	{
		return currentOptionModelContainer;
	}
	
	
	
	public void fireChangeListener()
	{
		
		ChangeEvent e = new ChangeEvent(this);
		
		for (ChangeListener listener : changeListeners) {
			listener.stateChanged(e);
		}
		
	}
	
	
	public void addChangeListener(ChangeListener listener)
	{
		this.changeListeners.add(listener);
	}
	
	
	public boolean removeChangeListener(ChangeListener listener)
	{
		return changeListeners.remove(listener);
	}
	
}
