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
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.color.ColoringInstance;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.EngineInstance;
import us.wthr.jdem846.render.EngineRegistry;
import us.wthr.jdem846.gis.projections.MapProjectionEnum;
import us.wthr.jdem846.ui.MonitoredSlider.MonitoredValueListener;
import us.wthr.jdem846.ui.base.BoxContainer;
import us.wthr.jdem846.ui.base.CheckBox;
import us.wthr.jdem846.ui.base.ComboBox;
import us.wthr.jdem846.ui.base.JComboBoxModel;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.Spinner;
import us.wthr.jdem846.ui.border.StandardTitledBorder;
import us.wthr.jdem846.ui.coloring.ColoringValueControl;
import us.wthr.jdem846.ui.lighting.LightingValueControl;
import us.wthr.jdem846.ui.panels.FlexGridPanel;
import us.wthr.jdem846.ui.panels.RoundedPanel;
import us.wthr.jdem846.ui.perspective.PerspectiveValueControl;
import us.wthr.jdem846.ui.projectionconfig.ProjectionConfigPanel;

@SuppressWarnings("serial")
public class ModelOptionsPanel extends RoundedPanel
{
	private static Log log = Logging.getLog(ModelOptionsPanel.class);
	
	private NumberTextField txtWidth;
	private NumberTextField txtHeight;
	private NumberTextField txtTileSize;
	private ComboBox cmbEngine;
	//private ComboBox cmbColoring;
	private ComboBox cmbHillshading;
	private ComboBox cmbMapProjection;
	private ColoringValueControl coloringControl;
	private LightingValueControl lightSourceControl;
	//private MonitoredSlider jsldLightMultiple;
	//private MonitoredSlider jsldSpotExponent;
	//private MonitoredSlider jsldElevationMultiple;
	//private MonitoredSlider jsldRelativeLightIntensity;
	//private MonitoredSlider jsldRelativeDarkIntensity;
	private Spinner spnLightMultiple;
	private Spinner spnSpotExponent;
	private Spinner spnElevationMultiple;
	private Spinner spnRelativeLightIntensity;
	private Spinner spnRelativeDarkIntensity;
	
	private CheckBox chkDoubleSampling;
	private CheckBox chkUseFastRender;
	
	private PerspectiveValueControl perspectiveControl;
	
	private EngineListModel engineModel;
	//private BackgroundColorOptionsListModel backgroundModel;
	//private ColoringListModel coloringModel;
	private HillShadingOptionsListModel hillShadingModel;
	private MapProjectionListModel mapProjectionListModel;
	
	private ComboBox cmbAntialiasing;
	private AntialiasingOptionsListModel antialiasingModel;
	
	private ComboBox cmbPrecacheStrategy;
	private PrecacheStrategyOptionsListModel precacheStrategyModel;
	
	private ColorSelection colorSelection;
	
	//private ProjectionConfigPanel projectionConfigPanel;
	//private GradientConfigPanel gradientConfigPanel;
	//private LightPositionConfigPanel lightPositionConfigPanel;
	
	private ModelOptions modelOptions;
	
	private List<OptionsChangedListener> optionsChangedListeners = new LinkedList<OptionsChangedListener>();
	
	private boolean ignoreValueChanges = false;
	
	public ModelOptionsPanel()
	{
		//super(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.controlGrid.title"));
		//TitledRoundedPanel controlGrid = new TitledRoundedPanel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.controlGrid.title"));
		//GridLayout gridLayout = new GridLayout(10, 2);
		//gridLayout.setVgap(2);
		//controlGrid.setLayout(gridLayout);
		//TitledRoundedPanel optionsGrid = new TitledRoundedPanel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.controlGrid.title"));
		
		FlexGridPanel controlGrid = new FlexGridPanel(2);
		
		//optionsGrid.setLayout(new BorderLayout());
		//optionsGrid.add(controlGrid, BorderLayout.CENTER);
		
		//controlGrid.setBorder(new StandardTitledBorder("Model Options"));
		
		// Create components
		txtWidth = new NumberTextField(false);
		txtHeight = new NumberTextField(false);
		txtTileSize = new NumberTextField(false);
		
		engineModel = new EngineListModel();
		cmbEngine = new ComboBox(engineModel);
		
		mapProjectionListModel = new MapProjectionListModel();
		cmbMapProjection = new ComboBox(mapProjectionListModel);
		
		coloringControl = new ColoringValueControl();
		lightSourceControl = new LightingValueControl();
		//backgroundModel = new BackgroundColorOptionsListModel();
		//cmbBackgroundColor = new ComboBox(backgroundModel);
		
		//coloringModel = new ColoringListModel();
		//cmbColoring = new ComboBox(coloringModel);
		
		hillShadingModel = new HillShadingOptionsListModel();
		cmbHillshading = new ComboBox(hillShadingModel);

		antialiasingModel = new AntialiasingOptionsListModel();
		cmbAntialiasing = new ComboBox(antialiasingModel);

		precacheStrategyModel = new PrecacheStrategyOptionsListModel();
		cmbPrecacheStrategy = new ComboBox(precacheStrategyModel);
		
		perspectiveControl = new PerspectiveValueControl();
		
		chkDoubleSampling = new CheckBox(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.doubleSampling.label"));
		chkUseFastRender = new CheckBox(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.useFastRender.label"));
		
		colorSelection = new ColorSelection();

		spnLightMultiple = new Spinner(new SpinnerNumberModel(50, 0, 100, 1));
		spnSpotExponent = new Spinner(new SpinnerNumberModel(1, 1, 5, 1));
		spnElevationMultiple = new Spinner(new SpinnerNumberModel(0, 0, 100, 1));
		spnRelativeLightIntensity = new Spinner(new SpinnerNumberModel(1, 0, 100, 1));
		spnRelativeDarkIntensity = new Spinner(new SpinnerNumberModel(1, 0, 100, 1));

		// Set tool tips
		
		txtWidth.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.widthText.tooltip"));
		txtHeight.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.heightText.tooltip"));
		cmbEngine.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.engineCombo.tooltip"));
		colorSelection.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.backgroundColorCombo.tooltip"));
		//cmbColoring.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.coloringCombo.tooltip"));
		cmbHillshading.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.hillshadingCombo.tooltip"));
		txtTileSize.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.tileSizeText.tooltip"));
		spnLightMultiple.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.lightMultipleSlider.tooltip"));
		//jsldSpotExponent.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.spotExponentSlider.tooltip"));
		spnSpotExponent.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.spotExponentSlider.tooltip"));
		spnElevationMultiple.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.elevationMultipleSlider.tooltip"));
		spnRelativeLightIntensity.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.relativeLightIntensity.tooltip"));
		spnRelativeDarkIntensity.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.relativeDarkIntensity.tooltip"));
		cmbAntialiasing.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.antialiasingCombo.tooltip"));
		cmbPrecacheStrategy.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.precacheStrategyCombo.tooltip"));
		cmbMapProjection.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.mapProjection.tooltip"));
		perspectiveControl.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.perspectiveValueControl.tooltip"));
		chkDoubleSampling.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.doubleSampling.tooltip"));
		chkUseFastRender.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.useFastRender.tooltip"));
		
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
		//cmbBackgroundColor.addItemListener(comboBoxItemListener);
		//cmbColoring.addItemListener(comboBoxItemListener);
		cmbHillshading.addItemListener(comboBoxItemListener);
		cmbMapProjection.addItemListener(comboBoxItemListener);
		
		ChangeListener sliderChangeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				
				JSlider source = (JSlider) e.getSource();
				
				if (!source.getValueIsAdjusting()) {
					//System.out.println("Slider value Changed: " + source.getValue());
					fireOptionsChangedListeners();
				}
			}
		};
		ChangeListener spinnerChangeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				fireOptionsChangedListeners();
			}
		};
		
		
		//jsldSpotExponent.addChangeListener(sliderChangeListener);
		spnSpotExponent.addChangeListener(spinnerChangeListener);
		spnLightMultiple.addChangeListener(spinnerChangeListener);
		spnElevationMultiple.addChangeListener(spinnerChangeListener);
		spnRelativeLightIntensity.addChangeListener(spinnerChangeListener);
		spnRelativeDarkIntensity.addChangeListener(spinnerChangeListener);
		
		
		ChangeListener basicChangeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				fireOptionsChangedListeners();
			}
		};
		chkDoubleSampling.addChangeListener(basicChangeListener);
		chkUseFastRender.addChangeListener(basicChangeListener);
		coloringControl.addChangeListener(basicChangeListener);
		colorSelection.addChangeListener(basicChangeListener);
		perspectiveControl.addChangeListener(basicChangeListener);
		lightSourceControl.addChangeListener(basicChangeListener);

		// Set Layout
		
		//controlGrid.add(new JLabel("Test Spinner:"));
		//controlGrid.add(new Spinner());
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.engineCombo.label") + ":"));
		controlGrid.add(cmbEngine);
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.widthText.label") + ":"));
		controlGrid.add(txtWidth);
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.heightText.label") + ":"));
		controlGrid.add(txtHeight);
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.tileSizeText.label") + ":"));
		controlGrid.add(txtTileSize);
		
		//controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.backgroundColorCombo.label") + ":"));
		//controlGrid.add(cmbBackgroundColor);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.backgroundColorCombo.label") + ":"));
		controlGrid.add(colorSelection);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.coloringCombo.label") + ":"));
		controlGrid.add(coloringControl);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.lightSource.label") + ":"));
		controlGrid.add(lightSourceControl);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.hillshadingCombo.label") + ":"));
		controlGrid.add(cmbHillshading);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.antialiasingCombo.label") + ":"));
		controlGrid.add(cmbAntialiasing);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.precacheStrategyCombo.label") + ":"));
		controlGrid.add(cmbPrecacheStrategy);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.mapProjection.label")));
		controlGrid.add(cmbMapProjection);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.lightMultipleSlider.label") + ":"));
		controlGrid.add(spnLightMultiple);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.relativeLightIntensity.label") + ":"));
		controlGrid.add(spnRelativeLightIntensity);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.relativeDarkIntensity.label") + ":"));
		controlGrid.add(spnRelativeDarkIntensity);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.spotExponentSlider.label") + ":"));
		controlGrid.add(spnSpotExponent);
		//controlGrid.add(jsldSpotExponent);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.elevationMultipleSlider.label") + ":"));
		controlGrid.add(spnElevationMultiple);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.perspectiveValueControl.label") + ":"));
		controlGrid.add(perspectiveControl);
		
		controlGrid.add(new JLabel());
		controlGrid.add(chkDoubleSampling);
		
		controlGrid.add(new JLabel());
		controlGrid.add(chkUseFastRender);
		
		
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		add(controlGrid, BorderLayout.CENTER);


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
		//backgroundModel.setSelectedItemByValue(modelOptions.getBackgroundColor());
		///coloringModel.setSelectedItemByValue(modelOptions.getColoringType());
		coloringControl.setColoringSelection(modelOptions.getColoringType());
		coloringControl.setConfigString(modelOptions.getGradientLevels());
		lightSourceControl.setSolarAzimuth(modelOptions.getLightingAzimuth());
		lightSourceControl.setSolarElevation(modelOptions.getLightingElevation());
		
		hillShadingModel.setSelectedItemByValue(modelOptions.getHillShadeType());
		
		colorSelection.setValueString(modelOptions.getBackgroundColor());
		
		antialiasingModel.setSelectedItemByValue(modelOptions.isAntialiased());
		precacheStrategyModel.setSelectedItemByValue(modelOptions.getPrecacheStrategy());
		mapProjectionListModel.setSelectedItemByValue(modelOptions.getMapProjection().identifier());
		
		txtTileSize.setText(""+modelOptions.getTileSize());
		spnLightMultiple.setValue((int)Math.round(modelOptions.getLightingMultiple() * 100));
		//jsldSpotExponent.setValue(modelOptions.getSpotExponent());
		spnSpotExponent.setValue(modelOptions.getSpotExponent());
		
		
		spnRelativeLightIntensity.setValue((int)Math.round(modelOptions.getRelativeLightIntensity() * 100));
		spnRelativeDarkIntensity.setValue((int)Math.round(modelOptions.getRelativeDarkIntensity() * 100));
		
		spnElevationMultiple.setValue((int)Math.round(modelOptions.getElevationMultiple()));
		
		perspectiveControl.setRotateX(modelOptions.getProjection().getRotateX());
		perspectiveControl.setRotateY(modelOptions.getProjection().getRotateY());
		
		chkDoubleSampling.setSelected(modelOptions.getDoublePrecisionHillshading());
		chkUseFastRender.setSelected(modelOptions.getUseSimpleCanvasFill());
		
		//gradientConfigPanel.setGradientIdentifier(modelOptions.getColoringType());
		//gradientConfigPanel.setConfigString(modelOptions.getGradientLevels());
		//lightPositionConfigPanel.setSolarAzimuth(modelOptions.getLightingAzimuth());
		//lightPositionConfigPanel.setSolarElevation(modelOptions.getLightingElevation());

		//projectionConfigPanel.setRotation(modelOptions.getProjection().getRotateX(),
		//						modelOptions.getProjection().getRotateY(),
		//						modelOptions.getProjection().getRotateZ());
		
		onEngineSelectionChanged();
		
		ignoreValueChanges = false;
	}
	
	protected void applyOptionsToModel()
	{
		modelOptions.setEngine(engineModel.getSelectedItemValue());
		modelOptions.setWidth(txtWidth.getInteger());
		modelOptions.setHeight(txtHeight.getInteger());
		
		//modelOptions.setBackgroundColor(backgroundModel.getSelectedItemValue());
		modelOptions.setBackgroundColor(colorSelection.getValueString());
		
		//modelOptions.setColoringType(coloringModel.getSelectedItemValue());
		modelOptions.setColoringType(coloringControl.getColoringSelection());
		modelOptions.setGradientLevels(coloringControl.getConfigString());
		
		modelOptions.setHillShadeType(hillShadingModel.getSelectedItemValue());
		modelOptions.setLightingMultiple((double)((Integer)spnLightMultiple.getValue()) / 100.0);
		modelOptions.setElevationMultiple((double)((Integer)spnElevationMultiple.getValue()));
		
		modelOptions.setRelativeLightIntensity((double)((Integer)spnRelativeLightIntensity.getValue()) / 100.0);
		modelOptions.setRelativeDarkIntensity((double)((Integer)spnRelativeDarkIntensity.getValue()) / 100.0);
		
		modelOptions.setTileSize(txtTileSize.getInteger());
		//modelOptions.setGradientLevels(gradientConfigPanel.getConfigString());
		
		modelOptions.setAntialiased(antialiasingModel.getSelectedItemValue());
		modelOptions.setPrecacheStrategy(precacheStrategyModel.getSelectedItemValue());
		
		modelOptions.setLightingAzimuth(lightSourceControl.getSolarAzimuth());
		modelOptions.setLightingElevation(lightSourceControl.getSolarElevation());
		
		
		modelOptions.setMapProjection(mapProjectionListModel.getSelectedItemValue());
		
		modelOptions.getProjection().setRotateX(perspectiveControl.getRotateX());
		modelOptions.getProjection().setRotateY(perspectiveControl.getRotateY());
		//modelOptions.setLightingAzimuth(lightPositionConfigPanel.getSolarAzimuth());
		//modelOptions.setLightingElevation(lightPositionConfigPanel.getSolarElevation());

		//modelOptions.setSpotExponent(jsldSpotExponent.getValue());
		modelOptions.setSpotExponent((Integer)spnSpotExponent.getValue());
		
		modelOptions.setDoublePrecisionHillshading(chkDoubleSampling.getModel().isSelected());
		modelOptions.setUseSimpleCanvasFill(chkUseFastRender.getModel().isSelected());
		
		//modelOptions.getProjection().setRotateX(projectionConfigPanel.getRotateX());
		//modelOptions.getProjection().setRotateY(projectionConfigPanel.getRotateY());
		//modelOptions.getProjection().setRotateZ(projectionConfigPanel.getRotateZ());
	}
	
	
	protected void onEngineSelectionChanged()
	{
		String engineSelection = engineModel.getSelectedItemValue();
		EngineInstance engineInstance = EngineRegistry.getInstance(engineSelection);
		ColoringInstance coloringInstance = ColoringRegistry.getInstance(coloringControl.getColoringSelection());
		
		txtWidth.setEnabled(engineInstance.usesWidth());
		txtHeight.setEnabled(engineInstance.usesHeight());
		//cmbBackgroundColor.setEnabled(engineInstance.usesBackgroundColor());
		//cmbColoring.setEnabled(engineInstance.usesColoring());
		coloringControl.setEnabled(engineInstance.usesColoring());
		cmbHillshading.setEnabled(engineInstance.usesHillshading());
		cmbAntialiasing.setEnabled(engineInstance.usesAntialiasing());
		cmbPrecacheStrategy.setEnabled(engineInstance.usesPrecacheStrategy());
		cmbMapProjection.setEnabled(engineInstance.usesMapProjection());
		spnLightMultiple.setEnabled(engineInstance.usesLightMultiple());
		//jsldSpotExponent.setEnabled(engineInstance.usesSpotExponent());
		spnSpotExponent.setEnabled(engineInstance.usesSpotExponent());
		txtTileSize.setEnabled(engineInstance.usesTileSize());
		//gradientConfigPanel.setEnabled(engineInstance.usesColoring());
		lightSourceControl.setEnabled(engineInstance.usesLightDirection());
		
		spnRelativeLightIntensity.setEnabled(engineInstance.usesRelativeLightMultiple());
		spnRelativeDarkIntensity.setEnabled(engineInstance.usesRelativeDarkMultiple());
		
		
		perspectiveControl.setEnabled(engineInstance.uses3DProjection());
		//gradientConfigPanel.setVisible(coloringInstance.allowGradientConfig());
		//projectionConfigPanel.setVisible(engineInstance.usesProjection());
		//lightPositionConfigPanel.setVisible(engineInstance.usesLightDirection());
		
		//if (engineInstance.usesLightDirection()) {
		//	lightPositionConfigPanel.updatePreview(true);
		//}	
		
		spnLightMultiple.setEnabled(engineInstance.usesLightMultiple());
		spnElevationMultiple.setEnabled(engineInstance.usesElevationMultiple());
		
		chkDoubleSampling.setEnabled(engineInstance.usesHillshading());
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
	
	/*
	class BackgroundColorOptionsListModel extends JComboBoxModel<String>
	{
		
		public BackgroundColorOptionsListModel()
		{
			for (ColorInstance colorInstance : ColorRegistry.getInstances()) {
				addItem(colorInstance.getName(), colorInstance.getIdentifier());
			}
		}
	}
	*/
	
	class MapProjectionListModel extends JComboBoxModel<String>
	{
		
		public MapProjectionListModel()
		{
			for (MapProjectionEnum projectionEnum : MapProjectionEnum.values()) {
				addItem(projectionEnum.projectionName(), projectionEnum.identifier());
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
			addItem(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.precacheStrategyOptions.none"), DemConstants.PRECACHE_STRATEGY_NONE);
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

	
}
