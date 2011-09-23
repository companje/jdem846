/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.color.ColorInstance;
import us.wthr.jdem846.color.ColorRegistry;
import us.wthr.jdem846.color.ColoringInstance;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.EngineInstance;
import us.wthr.jdem846.render.EngineRegistry;
import us.wthr.jdem846.ui.MonitoredSlider.MonitoredValueListener;
import us.wthr.jdem846.ui.base.BoxContainer;
import us.wthr.jdem846.ui.base.ComboBox;
import us.wthr.jdem846.ui.base.JComboBoxModel;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.border.StandardTitledBorder;
import us.wthr.jdem846.ui.panels.FlexGridPanel;
import us.wthr.jdem846.ui.projectionconfig.ProjectionConfigPanel;

@SuppressWarnings("serial")
public class ModelOptionsPanel extends Panel
{
	private static Log log = Logging.getLog(ModelOptionsPanel.class);
	
	private NumberTextField txtWidth;
	private NumberTextField txtHeight;
	private NumberTextField txtTileSize;
	private ComboBox cmbEngine;
	private ComboBox cmbBackgroundColor;
	private ComboBox cmbColoring;
	private ComboBox cmbHillshading;
	private MonitoredSlider jsldLightMultiple;
	private MonitoredSlider jsldSpotExponent;
	private MonitoredSlider jsldElevationMultiple;
	private MonitoredSlider jsldRelativeLightIntensity;
	private MonitoredSlider jsldRelativeDarkIntensity;
	
	private EngineListModel engineModel;
	private BackgroundColorOptionsListModel backgroundModel;
	private ColoringListModel coloringModel;
	private HillShadingOptionsListModel hillShadingModel;
	
	private ComboBox cmbAntialiasing;
	private AntialiasingOptionsListModel antialiasingModel;
	
	private ComboBox cmbPrecacheStrategy;
	private PrecacheStrategyOptionsListModel precacheStrategyModel;
	
	
	private ProjectionConfigPanel projectionConfigPanel;
	private GradientConfigPanel gradientConfigPanel;
	private LightPositionConfigPanel lightPositionConfigPanel;
	
	private ModelOptions modelOptions;
	
	private List<OptionsChangedListener> optionsChangedListeners = new LinkedList<OptionsChangedListener>();
	
	private boolean ignoreValueChanges = false;
	
	public ModelOptionsPanel()
	{
		//TitledRoundedPanel controlGrid = new TitledRoundedPanel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.controlGrid.title"));
		//GridLayout gridLayout = new GridLayout(10, 2);
		//gridLayout.setVgap(2);
		//controlGrid.setLayout(gridLayout);
		TitledRoundedPanel optionsGrid = new TitledRoundedPanel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.controlGrid.title"));
		
		FlexGridPanel controlGrid = new FlexGridPanel(2);
		optionsGrid.setLayout(new BorderLayout());
		optionsGrid.add(controlGrid, BorderLayout.CENTER);
		
		//controlGrid.setBorder(new StandardTitledBorder("Model Options"));
		
		// Create components
		txtWidth = new NumberTextField(false);
		txtHeight = new NumberTextField(false);
		txtTileSize = new NumberTextField(false);
		
		engineModel = new EngineListModel();
		cmbEngine = new ComboBox(engineModel);
		
		backgroundModel = new BackgroundColorOptionsListModel();
		cmbBackgroundColor = new ComboBox(backgroundModel);
		
		coloringModel = new ColoringListModel();
		cmbColoring = new ComboBox(coloringModel);
		
		hillShadingModel = new HillShadingOptionsListModel();
		cmbHillshading = new ComboBox(hillShadingModel);

		antialiasingModel = new AntialiasingOptionsListModel();
		cmbAntialiasing = new ComboBox(antialiasingModel);

		precacheStrategyModel = new PrecacheStrategyOptionsListModel();
		cmbPrecacheStrategy = new ComboBox(precacheStrategyModel);

		
		
		jsldLightMultiple = new MonitoredSlider(0, 100, 50, new MonitoredValueListener() {
			NumberFormat format = NumberFormat.getInstance();
			public String getValueString()
			{
				format.setMaximumFractionDigits(3);
				format.setMinimumFractionDigits(3);
				double value = (double)jsldLightMultiple.getValue() / 100.0;
				return format.format(value);
			}
		});
		
		jsldSpotExponent = new MonitoredSlider(ModelOptions.SPOT_EXPONENT_MINIMUM, ModelOptions.SPOT_EXPONENT_MAXIMUM, 5, new MonitoredValueListener() {
			NumberFormat format = NumberFormat.getIntegerInstance();
			public String getValueString()
			{
				return format.format(jsldSpotExponent.getValue());
			}
		}); 
		jsldSpotExponent.setSnapToTicks(true);
		
		jsldElevationMultiple = new MonitoredSlider(0, 100, 0, new MonitoredValueListener() {
			NumberFormat format = NumberFormat.getIntegerInstance();
			public String getValueString()
			{
				return format.format(jsldElevationMultiple.getValue());
			}
		});
		
		jsldRelativeLightIntensity =  new MonitoredSlider(0, 100, 0, new MonitoredValueListener() {
			NumberFormat format = NumberFormat.getIntegerInstance();
			public String getValueString()
			{
				return format.format(jsldRelativeLightIntensity.getValue());
			}
		});
		
		jsldRelativeDarkIntensity =  new MonitoredSlider(0, 100, 0, new MonitoredValueListener() {
			NumberFormat format = NumberFormat.getIntegerInstance();
			public String getValueString()
			{
				return format.format(jsldRelativeDarkIntensity.getValue());
			}
		});
		
		gradientConfigPanel = new GradientConfigPanel();
		projectionConfigPanel = new ProjectionConfigPanel();
		//projectionConfigPanel.setBorder(new StandardTitledBorder("Projection"));
		
		lightPositionConfigPanel = new LightPositionConfigPanel();
		lightPositionConfigPanel.setPreferredSize(new Dimension(200, 200));
		lightPositionConfigPanel.setSize(new Dimension(200, 200));
		
		//lightPositionConfigPanel.setBorder(new StandardTitledBorder("Light Direction"));
		//gradientConfigPanel.setBorder(new StandardTitledBorder("Gradients"));
		// Set tool tips
		
		txtWidth.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.widthText.tooltip"));
		txtHeight.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.heightText.tooltip"));
		cmbEngine.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.engineCombo.tooltip"));
		cmbBackgroundColor.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.backgroundColorCombo.tooltip"));
		cmbColoring.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.coloringCombo.tooltip"));
		cmbHillshading.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.hillshadingCombo.tooltip"));
		txtTileSize.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.tileSizeText.tooltip"));
		jsldLightMultiple.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.lightMultipleSlider.tooltip"));
		jsldSpotExponent.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.spotExponentSlider.tooltip"));
		jsldElevationMultiple.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.elevationMultipleSlider.tooltip"));
		jsldRelativeLightIntensity.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.relativeLightIntensity.tooltip"));
		jsldRelativeDarkIntensity.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.relativeDarkIntensity.tooltip"));
		cmbAntialiasing.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.antialiasingCombo.tooltip"));
		cmbPrecacheStrategy.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.precacheStrategyCombo.tooltip"));
		
		
		
		// Add listeners
		ActionListener textFieldActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireOptionsChangedListeners();
			}
		};
		
		FocusListener focusListener = new FocusListener() {
			public void focusGained(FocusEvent arg0) { }
			public void focusLost(FocusEvent arg0) {
				fireOptionsChangedListeners();
			}
		};
		
		txtWidth.addActionListener(textFieldActionListener);
		txtHeight.addActionListener(textFieldActionListener);
		txtTileSize.addActionListener(textFieldActionListener);
		
		txtWidth.addFocusListener(focusListener);
		txtHeight.addFocusListener(focusListener);
		txtTileSize.addFocusListener(focusListener);
		
		ItemListener comboBoxItemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) 
					fireOptionsChangedListeners();
			}
		};
		
		cmbEngine.addItemListener(comboBoxItemListener);
		cmbBackgroundColor.addItemListener(comboBoxItemListener);
		cmbColoring.addItemListener(comboBoxItemListener);
		cmbHillshading.addItemListener(comboBoxItemListener);
		
		ChangeListener sliderChangeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				
				JSlider source = (JSlider) e.getSource();
				
				if (!source.getValueIsAdjusting()) {
					//System.out.println("Slider value Changed: " + source.getValue());
					fireOptionsChangedListeners();
				}
			}
		};
		
		
		jsldSpotExponent.addChangeListener(sliderChangeListener);
		jsldLightMultiple.addChangeListener(sliderChangeListener);
		jsldElevationMultiple.addChangeListener(sliderChangeListener);
		jsldRelativeLightIntensity.addChangeListener(sliderChangeListener);
		jsldRelativeDarkIntensity.addChangeListener(sliderChangeListener);
		
		ChangeListener basicChangeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				fireOptionsChangedListeners();
			}
		};
		
		projectionConfigPanel.addChangeListener(basicChangeListener);
		gradientConfigPanel.addChangeListener(basicChangeListener);
		
		this.addOptionsChangedListener(new OptionsChangedListener() {
			public void onOptionsChanged(ModelOptions options)
			{
				gradientConfigPanel.setGradientIdentifier(options.getColoringType());
			}
		});
		
		lightPositionConfigPanel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				fireOptionsChangedListeners();
			}
		});
		
		// Set Layout
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.engineCombo.label") + ":"));
		controlGrid.add(cmbEngine);
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.widthText.label") + ":"));
		controlGrid.add(txtWidth);
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.heightText.label") + ":"));
		controlGrid.add(txtHeight);
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.tileSizeText.label") + ":"));
		controlGrid.add(txtTileSize);
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.backgroundColorCombo.label") + ":"));
		controlGrid.add(cmbBackgroundColor);
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.coloringCombo.label") + ":"));
		controlGrid.add(cmbColoring);
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.hillshadingCombo.label") + ":"));
		controlGrid.add(cmbHillshading);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.antialiasingCombo.label") + ":"));
		controlGrid.add(cmbAntialiasing);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.precacheStrategyCombo.label") + ":"));
		controlGrid.add(cmbPrecacheStrategy);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.lightMultipleSlider.label") + ":"));
		controlGrid.add(jsldLightMultiple);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.relativeLightIntensity.label") + ":"));
		controlGrid.add(jsldRelativeLightIntensity);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.relativeDarkIntensity.label") + ":"));
		controlGrid.add(jsldRelativeDarkIntensity);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.spotExponentSlider.label") + ":"));
		controlGrid.add(jsldSpotExponent);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.elevationMultipleSlider.label") + ":"));
		controlGrid.add(jsldElevationMultiple);
		
		
		//SpringLayout layout = new SpringLayout();
		//setLayout(layout);
		
		BoxLayout boxLayout = new BoxLayout(this, BoxLayout.X_AXIS);
		setLayout(boxLayout);
		
		add(optionsGrid);
		
		lightPositionConfigPanel.setMinimumSize(new Dimension(150, 150));
		projectionConfigPanel.setMinimumSize(new Dimension(150, 150));
		lightPositionConfigPanel.setPreferredSize(new Dimension(150, 150));
		projectionConfigPanel.setPreferredSize(new Dimension(150, 150));
		
		Panel lightAndProjectionPanel = new Panel();
		BoxLayout lightAndProjectionBoxLayout = new BoxLayout(lightAndProjectionPanel, BoxLayout.PAGE_AXIS);
		lightAndProjectionPanel.setLayout(lightAndProjectionBoxLayout);
		//Box lightAndProjectionBox = Box.createVerticalBox();
		BoxContainer lightAndProjectionBox = new BoxContainer(BoxLayout.Y_AXIS);
		lightAndProjectionBox.add(lightPositionConfigPanel);
		lightAndProjectionBox.add(projectionConfigPanel);
		lightAndProjectionPanel.add(lightAndProjectionBox);
		add(lightAndProjectionPanel);
		
		
		
		add(gradientConfigPanel);

		
		// Set initial values
		resetDefaultOptions();
		onEngineSelectionChanged();
	}
	
	@Override
	public void dispose() throws ComponentException
	{
		log.info("Disposing of Model Options Panel");
		super.dispose();
	}
	
	public void resetDefaultOptions()
	{
		setModelOptions(new ModelOptions());
	}
	
	protected void applyOptionsToUI()
	{
		ignoreValueChanges = true;
		
		engineModel.setSelectedItemByValue(modelOptions.getEngine());
		txtWidth.setText(""+modelOptions.getWidth());
		txtHeight.setText(""+modelOptions.getHeight());
		backgroundModel.setSelectedItemByValue(modelOptions.getBackgroundColor());
		coloringModel.setSelectedItemByValue(modelOptions.getColoringType());
		hillShadingModel.setSelectedItemByValue(modelOptions.getHillShadeType());
		
		antialiasingModel.setSelectedItemByValue(modelOptions.isAntialiased());
		precacheStrategyModel.setSelectedItemByValue(modelOptions.getPrecacheStrategy());
		
		txtTileSize.setText(""+modelOptions.getTileSize());
		jsldLightMultiple.setValue((int)Math.round(modelOptions.getLightingMultiple() * 100));
		jsldSpotExponent.setValue(modelOptions.getSpotExponent());
		
		jsldRelativeLightIntensity.setValue((int)Math.round(modelOptions.getRelativeLightIntensity() * 100));
		jsldRelativeDarkIntensity.setValue((int)Math.round(modelOptions.getRelativeDarkIntensity() * 100));
		
		jsldElevationMultiple.setValue((int)Math.round(modelOptions.getElevationMultiple()));
		
		gradientConfigPanel.setGradientIdentifier(modelOptions.getColoringType());
		gradientConfigPanel.setConfigString(modelOptions.getGradientLevels());
		lightPositionConfigPanel.setSolarAzimuth(modelOptions.getLightingAzimuth());
		lightPositionConfigPanel.setSolarElevation(modelOptions.getLightingElevation());

		projectionConfigPanel.setRotation(modelOptions.getProjection().getRotateX(),
								modelOptions.getProjection().getRotateY(),
								modelOptions.getProjection().getRotateZ());
		
		onEngineSelectionChanged();
		
		ignoreValueChanges = false;
	}
	
	protected void applyOptionsToModel()
	{
		modelOptions.setEngine(engineModel.getSelectedItemValue());
		modelOptions.setWidth(txtWidth.getInteger());
		modelOptions.setHeight(txtHeight.getInteger());
		
		modelOptions.setBackgroundColor(backgroundModel.getSelectedItemValue());
		modelOptions.setColoringType(coloringModel.getSelectedItemValue());
		modelOptions.setHillShadeType(hillShadingModel.getSelectedItemValue());
		modelOptions.setLightingMultiple((double)jsldLightMultiple.getValue() / 100.0);
		modelOptions.setElevationMultiple((double)jsldElevationMultiple.getValue());
		
		modelOptions.setRelativeLightIntensity((double)jsldRelativeLightIntensity.getValue() / 100.0);
		modelOptions.setRelativeDarkIntensity((double)jsldRelativeDarkIntensity.getValue() / 100.0);
		
		modelOptions.setTileSize(txtTileSize.getInteger());
		modelOptions.setGradientLevels(gradientConfigPanel.getConfigString());
		
		modelOptions.setAntialiased(antialiasingModel.getSelectedItemValue());
		modelOptions.setPrecacheStrategy(precacheStrategyModel.getSelectedItemValue());
		
		
		
		
		modelOptions.setLightingAzimuth(lightPositionConfigPanel.getSolarAzimuth());
		modelOptions.setLightingElevation(lightPositionConfigPanel.getSolarElevation());
		
		///int spotExp = (int) Math.round((((double)jsldSpotExponent.getValue() / 100) * (ModelOptions.SPOT_EXPONENT_MAXIMUM - ModelOptions.SPOT_EXPONENT_MINIMUM)) + ModelOptions.SPOT_EXPONENT_MINIMUM);
		//if (spotExp < 1)
		//	spotExp = 1;
		
		modelOptions.setSpotExponent(jsldSpotExponent.getValue());
		
		
		
		modelOptions.getProjection().setRotateX(projectionConfigPanel.getRotateX());
		modelOptions.getProjection().setRotateY(projectionConfigPanel.getRotateY());
		modelOptions.getProjection().setRotateZ(projectionConfigPanel.getRotateZ());
	}
	
	
	protected void onEngineSelectionChanged()
	{
		String engineSelection = engineModel.getSelectedItemValue();
		EngineInstance engineInstance = EngineRegistry.getInstance(engineSelection);
		ColoringInstance coloringInstance = ColoringRegistry.getInstance(this.coloringModel.getSelectedItemValue());
		
		txtWidth.setEnabled(engineInstance.usesWidth());
		txtHeight.setEnabled(engineInstance.usesHeight());
		cmbBackgroundColor.setEnabled(engineInstance.usesBackgroundColor());
		cmbColoring.setEnabled(engineInstance.usesColoring());
		cmbHillshading.setEnabled(engineInstance.usesHillshading());
		cmbAntialiasing.setEnabled(engineInstance.usesAntialiasing());
		cmbPrecacheStrategy.setEnabled(engineInstance.usesPrecacheStrategy());
		jsldLightMultiple.setEnabled(engineInstance.usesLightMultiple());
		jsldSpotExponent.setEnabled(engineInstance.usesSpotExponent());
		txtTileSize.setEnabled(engineInstance.usesTileSize());
		gradientConfigPanel.setEnabled(engineInstance.usesColoring());
		
		jsldRelativeLightIntensity.setEnabled(engineInstance.usesRelativeLightMultiple());
		jsldRelativeDarkIntensity.setEnabled(engineInstance.usesRelativeDarkMultiple());
		
		gradientConfigPanel.setVisible(coloringInstance.allowGradientConfig());
		projectionConfigPanel.setVisible(engineInstance.usesProjection());
		lightPositionConfigPanel.setVisible(engineInstance.usesLightDirection());
		
		if (engineInstance.usesLightDirection()) {
			lightPositionConfigPanel.updatePreview(true);
		}	
		
		jsldLightMultiple.setEnabled(engineInstance.usesLightMultiple());
		jsldElevationMultiple.setEnabled(engineInstance.usesElevationMultiple());
	}
	
	
	public void setModelOptions(ModelOptions modelOptions)
	{
		this.modelOptions = modelOptions;
		applyOptionsToUI();
	}
	
	public ModelOptions getModelOptions()
	{
		applyOptionsToModel();
		return modelOptions;
	}
	
	class EngineListModel extends JComboBoxModel<String>
	{
		
		public EngineListModel()
		{
			List<EngineInstance> engineInstances = EngineRegistry.getInstances();
			for (EngineInstance engineInstance : engineInstances) {
				addItem(engineInstance.getName(), engineInstance.getIdentifier());
			}
		}
		
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
	
	class BackgroundColorOptionsListModel extends JComboBoxModel<String>
	{
		
		public BackgroundColorOptionsListModel()
		{
			for (ColorInstance colorInstance : ColorRegistry.getInstances()) {
				addItem(colorInstance.getName(), colorInstance.getIdentifier());
			}
		}
	}
	
	class HillShadingOptionsListModel extends JComboBoxModel<Integer>
	{
		
		public HillShadingOptionsListModel()
		{
			addItem(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.hillShadeOptions.lighten"), DemConstants.HILLSHADING_LIGHTEN);
			addItem(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.hillShadeOptions.darken"), DemConstants.HILLSHADING_DARKEN);
			addItem(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.hillShadeOptions.combined"), DemConstants.HILLSHADING_COMBINED);
			addItem(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.hillShadeOptions.none"), DemConstants.HILLSHADING_NONE);
		}
	}
	
	class AntialiasingOptionsListModel extends JComboBoxModel<Boolean>
	{
		
		public AntialiasingOptionsListModel()
		{
			addItem(I18N.get("us.wthr.jdem846.ui.yes"), true);
			addItem(I18N.get("us.wthr.jdem846.ui.no"), false);
		}
		
	}
	
	class PrecacheStrategyOptionsListModel extends JComboBoxModel<String>
	{
		
		public PrecacheStrategyOptionsListModel()
		{
			addItem(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.precacheStrategyOptions.tiled"), DemConstants.PRECACHE_STRATEGY_TILED);
			addItem(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.precacheStrategyOptions.default"), DemConstants.PRECACHE_STRATEGY_DEFAULT);
			addItem(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.precacheStrategyOptions.full"), DemConstants.PRECACHE_STRATEGY_FULL);
			
		}
		
	}	
	
	
	
	public void addOptionsChangedListener(OptionsChangedListener listener)
	{
		optionsChangedListeners.add(listener);
	}
	
	public void fireOptionsChangedListeners()
	{
		if (ignoreValueChanges)
			return;
		
		applyOptionsToModel();
		onEngineSelectionChanged();
		for (OptionsChangedListener listener : optionsChangedListeners) {
			listener.onOptionsChanged(modelOptions);
		}
	}
	
	
	public interface OptionsChangedListener
	{
		public void onOptionsChanged(ModelOptions options);
	}
	
}
