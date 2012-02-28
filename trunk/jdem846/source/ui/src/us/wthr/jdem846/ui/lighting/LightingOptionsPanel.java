package us.wthr.jdem846.ui.lighting;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.lighting.LightSourceSpecifyTypeEnum;
import us.wthr.jdem846.lighting.LightingContext;
import us.wthr.jdem846.lighting.LightingOptionNamesEnum;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.OptionsChangedListener;
import us.wthr.jdem846.ui.base.CheckBox;
import us.wthr.jdem846.ui.base.ComboBox;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Spinner;
import us.wthr.jdem846.ui.optionModels.LightSourceSpecifyTypeListModel;
import us.wthr.jdem846.ui.panels.FlexGridPanel;
import us.wthr.jdem846.ui.panels.RoundedPanel;

@SuppressWarnings("serial")
public class LightingOptionsPanel extends RoundedPanel
{
	private static Log log = Logging.getLog(LightingOptionsPanel.class);
	
	private CheckBox chkLightingEnabled;
	private CheckBox chkRayTraceShadows;
	private Spinner spnShadowIntensity;
	private Spinner spnLightMultiple;
	private Spinner spnSpotExponent;
	private Spinner spnRelativeLightIntensity;
	private Spinner spnRelativeDarkIntensity;
	
	private LightingValueControl lightSourceControl;
	
	private SpinnerDateModel lightOnTimeModel;
	private Spinner spnLightOnTime;
	private JDateChooser jdtLightOnDate;
	private CheckBox chkRecalcLightOnEachPoint;
	
	private LightSourceSpecifyTypeListModel lightSourceSpecifyTypeModel;
	private ComboBox cmbLightSourceSpecifyType;
	
	
	
	
	private LightingContext lightingContext;
	
	private List<OptionsChangedListener> optionsChangedListeners = new LinkedList<OptionsChangedListener>();
	private boolean ignoreValueChanges = false;
	
	
	public LightingOptionsPanel()
	{
		// Create components
		FlexGridPanel controlGrid = new FlexGridPanel(2);
		
		lightSourceControl = new LightingValueControl();
		
		spnLightMultiple = new Spinner(new SpinnerNumberModel(50, 0, 100000, 1));
		spnSpotExponent = new Spinner(new SpinnerNumberModel(1, 1, 5, 1));
		spnRelativeLightIntensity = new Spinner(new SpinnerNumberModel(1, 0, 100, 1));
		spnRelativeDarkIntensity = new Spinner(new SpinnerNumberModel(1, 0, 100, 1));
		chkLightingEnabled = new CheckBox(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.lightingEnabled.label"));
		chkRayTraceShadows = new CheckBox(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.rayTraceShadows.label"));
		spnShadowIntensity = new Spinner(new SpinnerNumberModel(1, 0, 100, 1));
		
		jdtLightOnDate = new JDateChooser(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.dateChooser.datePattern"),
											I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.dateChooser.maskPattern"),
											I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.dateChooser.placeHolder").charAt(0));
		if (jdtLightOnDate.getDateEditor() instanceof JTextFieldDateEditor) {
			JTextFieldDateEditor dateEditor = (JTextFieldDateEditor) jdtLightOnDate.getDateEditor();
			dateEditor.setHorizontalAlignment(JTextField.RIGHT);
		}
		jdtLightOnDate.getJCalendar().setTodayButtonText(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.dateChooser.today"));
		jdtLightOnDate.getJCalendar().setTodayButtonVisible(true);
		jdtLightOnDate.setDate(new Date(System.currentTimeMillis()));
		
		chkRecalcLightOnEachPoint = new CheckBox(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.recalcLightOnEachPoint.label"));
		
		lightOnTimeModel = new SpinnerDateModel();
		spnLightOnTime = new Spinner(lightOnTimeModel);
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spnLightOnTime, I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.timeSpinner.format"));
		spnLightOnTime.setEditor(timeEditor);
		
		lightSourceSpecifyTypeModel = new LightSourceSpecifyTypeListModel();
		cmbLightSourceSpecifyType = new ComboBox(lightSourceSpecifyTypeModel);
		
		chkLightingEnabled.setToolTipText(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.lightingEnabled.tooltip"));
		spnLightMultiple.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.lightMultipleSlider.tooltip"));
		spnSpotExponent.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.spotExponentSlider.tooltip"));
		spnRelativeLightIntensity.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.relativeLightIntensity.tooltip"));
		spnRelativeDarkIntensity.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.relativeDarkIntensity.tooltip"));
		cmbLightSourceSpecifyType.setToolTipText(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.sourceSelect.tooltip"));
		jdtLightOnDate.setToolTipText(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.dateChooser.tooltip"));
		spnLightOnTime.setToolTipText(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.timeSpinner.tooltip"));
		chkRayTraceShadows.setToolTipText(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.rayTraceShadows.tooltip"));
		spnShadowIntensity.setToolTipText(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.shadowIntensity.tooltip"));
		chkRecalcLightOnEachPoint.setToolTipText(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.recalcLightOnEachPoint.tooltip"));
		
		// Add listeners
		ActionListener textFieldActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkControlState();
				fireOptionsChangedListeners();
				
			}
		};
		ActionListener checkBoxActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				checkControlState();
				fireOptionsChangedListeners();
			}
		};
		ItemListener lightSourceSpecifyTypeItemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					checkControlState();
					fireOptionsChangedListeners();
				}
			}
		};
		ItemListener comboBoxItemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					checkControlState();
					fireOptionsChangedListeners();
				}
			}
		};
		ChangeListener spinnerChangeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				checkControlState();
				fireOptionsChangedListeners();
			}
		};
		ChangeListener basicChangeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				checkControlState();
				fireOptionsChangedListeners();
			}
		};
		ChangeListener lightingEnabledChangeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				checkControlState();
				fireOptionsChangedListeners();
			}
		};
		PropertyChangeListener datePropertyChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt)
			{
				checkControlState();
				fireOptionsChangedListeners();
			}
		};
		
		cmbLightSourceSpecifyType.addItemListener(lightSourceSpecifyTypeItemListener);
		//chkLightingEnabled.addChangeListener(lightingEnabledChangeListener);
		chkLightingEnabled.getModel().addActionListener(checkBoxActionListener);
		spnLightMultiple.addChangeListener(spinnerChangeListener);
		spnSpotExponent.addChangeListener(spinnerChangeListener);
		spnRelativeLightIntensity.addChangeListener(spinnerChangeListener);
		spnRelativeDarkIntensity.addChangeListener(spinnerChangeListener);
		lightSourceControl.addChangeListener(basicChangeListener);
		spnLightOnTime.addChangeListener(spinnerChangeListener);
		//chkRayTraceShadows.addChangeListener(basicChangeListener);
		chkRayTraceShadows.getModel().addActionListener(checkBoxActionListener);
		spnShadowIntensity.addChangeListener(spinnerChangeListener);
		chkRecalcLightOnEachPoint.getModel().addActionListener(checkBoxActionListener);
		
		//jdtLightOnDate.get
		//jdtLightOnDate.
		jdtLightOnDate.addPropertyChangeListener("date", datePropertyChangeListener);
		
		
		
		controlGrid.add(new Label(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.lightingEnabled.label") + ":"));
		controlGrid.add(chkLightingEnabled);
		
		controlGrid.add(new Label(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.sourceSelect.label") + ":"));
		controlGrid.add(cmbLightSourceSpecifyType);
		
		controlGrid.add(new Label(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.lightSource.label") + ":"));
		controlGrid.add(lightSourceControl);
		controlGrid.add(new Label(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.dateChooser.label")));
		controlGrid.add(jdtLightOnDate);
		controlGrid.add(new Label(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.timeSpinner.label")));
		controlGrid.add(spnLightOnTime);
		
		controlGrid.add(new Label(""));
		controlGrid.add(chkRecalcLightOnEachPoint);
		
		
		controlGrid.add(new Label(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.lightMultipleSlider.label") + ":"));
		controlGrid.add(spnLightMultiple);
		controlGrid.add(new Label(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.relativeLightIntensity.label") + ":"));
		controlGrid.add(spnRelativeLightIntensity);
		controlGrid.add(new Label(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.relativeDarkIntensity.label") + ":"));
		controlGrid.add(spnRelativeDarkIntensity);
		controlGrid.add(new Label(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.spotExponentSlider.label") + ":"));
		controlGrid.add(spnSpotExponent);
		
		controlGrid.add(new Label());
		controlGrid.add(chkRayTraceShadows);
		controlGrid.add(new Label(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.shadowIntensity.label") + ":"));
		controlGrid.add(spnShadowIntensity);
		
		
		
		
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		add(controlGrid, BorderLayout.CENTER);
		
		resetDefaultOptions();
		checkControlState();
	}
	
	@Override
	public void dispose() throws ComponentException
	{
		log.info("Disposing of Model Options Panel");
		super.dispose();
	}
	
	public void resetDefaultOptions()
	{
		setLightingContext(new LightingContext());
	}
	
	protected void applyOptionsToUI()
	{
		ignoreValueChanges = true;
		
		lightSourceSpecifyTypeModel.setSelectedItemByValue(lightingContext.getLightSourceSpecifyType());
		chkLightingEnabled.setSelected(lightingContext.isLightingEnabled());
		lightSourceControl.setSolarAzimuth(lightingContext.getLightingAzimuth());
		lightSourceControl.setSolarElevation(lightingContext.getLightingElevation());
		
		spnLightMultiple.setValue((int)Math.round(lightingContext.getLightingMultiple()));
		
		spnSpotExponent.setValue(lightingContext.getSpotExponent());
		spnRelativeLightIntensity.setValue((int)Math.round(lightingContext.getRelativeLightIntensity() * 100));
		spnRelativeDarkIntensity.setValue((int)Math.round(lightingContext.getRelativeDarkIntensity() * 100));
		
		chkRayTraceShadows.setSelected(lightingContext.getRayTraceShadows());
		spnShadowIntensity.setValue((int)Math.round(lightingContext.getShadowIntensity() * 100));
		
		if (lightingContext.getLightingOnDate() != -1) {
			Date date = new Date(lightingContext.getLightingOnDate());
			lightOnTimeModel.setValue(date);
		}
		
		chkRecalcLightOnEachPoint.getModel().setSelected(lightingContext.getRecalcLightOnEachPoint());
		
		ignoreValueChanges = false;	
	}
	
	protected void applyOptionsToContext()
	{
		//lightSourceSpecifyTypeModel.setSelectedItemByValue(lightingContext.getLightSourceSpecifyType());
		
		
		lightingContext.setLightSourceSpecifyType(lightSourceSpecifyTypeModel.getSelectedItemValue());
		lightingContext.setLightingEnabled(chkLightingEnabled.getModel().isSelected());
		lightingContext.setLightingMultiple((double)((Integer)spnLightMultiple.getValue()));
		lightingContext.setRelativeLightIntensity((double)((Integer)spnRelativeLightIntensity.getValue()) / 100.0);
		lightingContext.setRelativeDarkIntensity((double)((Integer)spnRelativeDarkIntensity.getValue()) / 100.0);
		lightingContext.setLightingAzimuth(lightSourceControl.getSolarAzimuth());
		lightingContext.setLightingElevation(lightSourceControl.getSolarElevation());
		lightingContext.setRayTraceShadows(chkRayTraceShadows.getModel().isSelected());
		lightingContext.setShadowIntensity((double)((Integer)spnShadowIntensity.getValue()) / 100.0);
		lightingContext.setSpotExponent((Integer)spnSpotExponent.getValue());
		lightingContext.setLightingOnDate(lightOnTimeModel.getDate());
		
		lightingContext.setRecalcLightOnEachPoint(chkRecalcLightOnEachPoint.getModel().isSelected());
		//Date date = lightOnTimeModel.getDate();
		
		//log.info("Light on Date: " + date.getTime());
	}
	
	protected void checkControlState()
	{
		boolean enabled = chkLightingEnabled.getModel().isSelected();
		LightSourceSpecifyTypeEnum specType = lightSourceSpecifyTypeModel.getSelectedItemValue();
		boolean rayTraceShadowsEnabled = chkRayTraceShadows.getModel().isSelected();
		
		
		cmbLightSourceSpecifyType.setEnabled(enabled);
		spnLightMultiple.setEnabled(enabled);
		spnSpotExponent.setEnabled(enabled);
		spnRelativeLightIntensity.setEnabled(enabled);
		spnRelativeDarkIntensity.setEnabled(enabled);
		chkRayTraceShadows.setEnabled(enabled);
		spnShadowIntensity.setEnabled(enabled && rayTraceShadowsEnabled);
		//lightSourceControl.setEnabled(enabled);
		
		lightSourceControl.setEnabled(enabled && specType == LightSourceSpecifyTypeEnum.BY_AZIMUTH_AND_ELEVATION);
		jdtLightOnDate.setEnabled(enabled && specType == LightSourceSpecifyTypeEnum.BY_DATE_AND_TIME);
		spnLightOnTime.setEnabled(enabled && specType == LightSourceSpecifyTypeEnum.BY_DATE_AND_TIME);
	}
	

	
	
	public void setLightingContext(LightingContext lightingContext)
	{
		this.lightingContext = lightingContext;
		applyOptionsToUI();
	}
	
	public LightingContext getLightingContext()
	{
		return lightingContext;
	}
	
	
	public void addOptionsChangedListener(OptionsChangedListener listener)
	{
		optionsChangedListeners.add(listener);
	}
	
	public void fireOptionsChangedListeners()
	{
		if (ignoreValueChanges)
			return;
		
		applyOptionsToContext();
		for (OptionsChangedListener listener : optionsChangedListeners) {
			listener.onOptionsChanged(lightingContext);
		}
	}
}
