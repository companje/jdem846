package us.wthr.jdem846.ui.coloring;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.color.ColoringInstance;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Button;
import us.wthr.jdem846.ui.base.ComboBox;
import us.wthr.jdem846.ui.base.JComboBoxModel;
import us.wthr.jdem846.ui.base.Panel;

@SuppressWarnings("serial")
public class ColoringValueControl extends Panel 
{
	private static Log log = Logging.getLog(ColoringValueControl.class);
	
	private ComboBox cmbColoring;
	private Button btnEdit;
	
	private ColoringListModel coloringModel;
	
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	private String configString;
	
	public ColoringValueControl()
	{
		
		// Create components
		coloringModel = new ColoringListModel();
		cmbColoring = new ComboBox(coloringModel);
		
		btnEdit = new Button(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.coloringCombo.edit.label"));
		btnEdit.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.coloringCombo.edit.tooltip"));
		
		cmbColoring.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.coloringCombo.tooltip"));
		
		// Add Listeners
		ItemListener comboBoxItemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					onSelectionChanged();
					fireChangeListeners();
				}
					//fireOptionsChangedListeners();
			}
		};
		cmbColoring.addItemListener(comboBoxItemListener);
		
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onEditClicked();
			}
		});
		
		
		// Set layout
		
		GridBagLayout gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.75;
		//constraints.weighty = 1.0;
		
		
		gridbag.setConstraints(cmbColoring, constraints);
		add(cmbColoring);
		
		constraints.weightx = 0.25;
		constraints.gridwidth  = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(btnEdit, constraints);
		add(btnEdit);
		
	}
	
	protected void onSelectionChanged()
	{
		
		checkEditButtonEnabledState();
	}
	
	protected void checkEditButtonEnabledState()
	{
		ColoringInstance coloringInstance = ColoringRegistry.getInstance(getColoringSelection());
		if (coloringInstance != null) {
			btnEdit.setEnabled(isEnabled() && coloringInstance.allowGradientConfig());
		}
	}
	
	protected void onEditClicked()
	{
		ColoringEditDialog dialog = new ColoringEditDialog();
		dialog.setGradientIdentifier(coloringModel.getSelectedItemValue());
		dialog.setConfigString(configString);
		dialog.showDialog(new ColoringEditListener() {
			public void onColoringEdited(String gradientIdentifier, String configString) {
				log.info("Gradient levels changed!");
				setConfigString(configString);
				fireChangeListeners();
			}
		});
	}
	
	
	public String getConfigString()
	{
		return configString;
	}
	
	public void setConfigString(String configString)
	{
		this.configString = configString;
	}
	
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		cmbColoring.setEnabled(enabled);
		btnEdit.setEnabled(enabled);
		
		if (enabled) {
			checkEditButtonEnabledState();
		}
	}
	
	public void setColoringSelection(String value)
	{
		coloringModel.setSelectedItemByValue(value);
	}
	
	public String getColoringSelection()
	{
		return coloringModel.getSelectedItemValue();
	}
	
	protected void fireChangeListeners()
	{
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener listener : changeListeners)	{
			listener.stateChanged(e);
		}
	}
	
	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}
	
	public boolean removeChangeListener(ChangeListener listener)
	{
		return changeListeners.remove(listener);
	}
	
	class ColoringListModel extends JComboBoxModel<String>
	{
		
		public ColoringListModel()
		{
			List<ColoringInstance> colorings = ColoringRegistry.getInstances();
			for (ColoringInstance colorInstance : colorings) {
				addItem(colorInstance.getName(), colorInstance.getIdentifier());
			}
		}
		
	}
	
}
