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
import java.awt.Component;
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
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.color.ColoringInstance;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.CanvasProjectionTypeEnum;
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
import us.wthr.jdem846.ui.base.TextField;
import us.wthr.jdem846.ui.border.StandardTitledBorder;
import us.wthr.jdem846.ui.coloring.ColoringValueControl;
import us.wthr.jdem846.ui.lighting.LightingValueControl;
import us.wthr.jdem846.ui.optionModels.AntialiasingOptionsListModel;
import us.wthr.jdem846.ui.optionModels.CanvasProjectionListModel;
import us.wthr.jdem846.ui.optionModels.ElevationScalerListModel;
import us.wthr.jdem846.ui.optionModels.EngineListModel;
import us.wthr.jdem846.ui.optionModels.HillShadingOptionsListModel;
import us.wthr.jdem846.ui.optionModels.MapProjectionListModel;
import us.wthr.jdem846.ui.optionModels.PlanetListModel;
import us.wthr.jdem846.ui.optionModels.PrecacheStrategyOptionsListModel;
import us.wthr.jdem846.ui.optionModels.SubpixelGridSizeListModel;
import us.wthr.jdem846.ui.panels.FlexGridPanel;
import us.wthr.jdem846.ui.panels.RoundedPanel;
import us.wthr.jdem846.ui.perspective.PerspectiveValueControl;
import us.wthr.jdem846.ui.projectionconfig.ProjectionConfigPanel;

@SuppressWarnings("serial")
public class ModelOptionsPanel extends Panel
{
	private static Log log = Logging.getLog(ModelOptionsPanel.class);
	
	private NumberTextField txtWidth;
	private NumberTextField txtHeight;
	//private NumberTextField txtTileSize;
	private ComboBox cmbEngine;
	//private ComboBox cmbColoring;
	//private ComboBox cmbHillshading;
	
	private ColoringValueControl coloringControl;
	//private LightingValueControl lightSourceControl;
	//private Spinner spnLightMultiple;
	//private Spinner spnSpotExponent;
	private Spinner spnElevationMultiple;
	//private Spinner spnRelativeLightIntensity;
	//private Spinner spnRelativeDarkIntensity;
	
	private CheckBox chkMaintainAscpectRatio;
	private CheckBox chkEstimatedElevationMinMax;
	//private CheckBox chkProject3d;
	//private CheckBox chkDoubleSampling;
	//private CheckBox chkUseFastRender;
	
	private PerspectiveValueControl perspectiveControl;
	
	private EngineListModel engineModel;
	//private BackgroundColorOptionsListModel backgroundModel;
	//private ColoringListModel coloringModel;
	//private HillShadingOptionsListModel hillShadingModel;
	
	private ComboBox cmbPlanet;
	private PlanetListModel planetListModel;
	
	private ComboBox cmbMapProjection;
	private MapProjectionListModel mapProjectionListModel;
	
	private ComboBox cmbCanvasProjection;
	private CanvasProjectionListModel canvasProjectionListModel;
	
	private ComboBox cmbAntialiasing;
	private AntialiasingOptionsListModel antialiasingModel;
	
	private ComboBox cmbElevationScaling;
	private ElevationScalerListModel elevationScalerListModel;
	
	private ComboBox cmbSubpixelGridSize;
	private SubpixelGridSizeListModel subpixelGridSizeListModel;
	
	//private ComboBox cmbPrecacheStrategy;
	//private PrecacheStrategyOptionsListModel precacheStrategyModel;
	
	private ColorSelection colorSelection;
	
	//private ProjectionConfigPanel projectionConfigPanel;
	//private GradientConfigPanel gradientConfigPanel;
	//private LightPositionConfigPanel lightPositionConfigPanel;
	
	private CheckBox chkLimitCoordinates;
	private TextField txtLimitNorth;
	private TextField txtLimitSouth;
	private TextField txtLimitEast;
	private TextField txtLimitWest;
	
	
	private ModelOptions modelOptions;
	
	private List<OptionsChangedListener> optionsChangedListeners = new LinkedList<OptionsChangedListener>();
	private GetAspectRatioHandler getAspectRatioHandler;
	
	private boolean ignoreValueChanges = false;
	
	public ModelOptionsPanel()
	{
		//super(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.controlGrid.title"));
		//TitledRoundedPanel controlGrid = new TitledRoundedPanel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.controlGrid.title"));
		//GridLayout gridLayout = new GridLayout(10, 2);
		//gridLayout.setVgap(2);
		//controlGrid.setLayout(gridLayout);
		//TitledRoundedPanel optionsGrid = new TitledRoundedPanel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.controlGrid.title"));
		
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		FlexGridPanel controlGrid = new FlexGridPanel(2);
		
		//optionsGrid.setLayout(new BorderLayout());
		//optionsGrid.add(controlGrid, BorderLayout.CENTER);
		
		//controlGrid.setBorder(new StandardTitledBorder("Model Options"));
		
		// Create components
		txtWidth = new NumberTextField(false);
		txtHeight = new NumberTextField(false);
		//txtTileSize = new NumberTextField(false);
		
		engineModel = new EngineListModel();
		cmbEngine = new ComboBox(engineModel);
		
		mapProjectionListModel = new MapProjectionListModel();
		cmbMapProjection = new ComboBox(mapProjectionListModel);
		
		canvasProjectionListModel = new CanvasProjectionListModel();
		cmbCanvasProjection = new ComboBox(canvasProjectionListModel);
		
		planetListModel = new PlanetListModel();
		cmbPlanet = new ComboBox(planetListModel);
		
		subpixelGridSizeListModel = new SubpixelGridSizeListModel();
		cmbSubpixelGridSize = new ComboBox(subpixelGridSizeListModel);

		
		
		elevationScalerListModel = new ElevationScalerListModel();
		cmbElevationScaling = new ComboBox(elevationScalerListModel);
		
		
		coloringControl = new ColoringValueControl();
		//lightSourceControl = new LightingValueControl();
		//backgroundModel = new BackgroundColorOptionsListModel();
		//cmbBackgroundColor = new ComboBox(backgroundModel);
		
		//coloringModel = new ColoringListModel();
		//cmbColoring = new ComboBox(coloringModel);
		
		//hillShadingModel = new HillShadingOptionsListModel();
		//cmbHillshading = new ComboBox(hillShadingModel);

		antialiasingModel = new AntialiasingOptionsListModel();
		cmbAntialiasing = new ComboBox(antialiasingModel);

		//precacheStrategyModel = new PrecacheStrategyOptionsListModel();
		//cmbPrecacheStrategy = new ComboBox(precacheStrategyModel);
		
		perspectiveControl = new PerspectiveValueControl();
		
		//chkProject3d = new CheckBox(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.project3d.label"));
		//chkDoubleSampling = new CheckBox(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.doubleSampling.label"));
		//chkUseFastRender = new CheckBox(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.useFastRender.label"));
		chkMaintainAscpectRatio = new CheckBox(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.maintainAspectRatio.label"));
		chkEstimatedElevationMinMax = new CheckBox(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.estimatedElevationMinMax.label"));
		
		
		colorSelection = new ColorSelection();

		//spnLightMultiple = new Spinner(new SpinnerNumberModel(50, 0, 100, 1));
		//spnSpotExponent = new Spinner(new SpinnerNumberModel(1, 1, 5, 1));
		spnElevationMultiple = new Spinner(new SpinnerNumberModel(0, 0, 100000, 1));
		//spnRelativeLightIntensity = new Spinner(new SpinnerNumberModel(1, 0, 100, 1));
		//spnRelativeDarkIntensity = new Spinner(new SpinnerNumberModel(1, 0, 100, 1));
		
		chkLimitCoordinates = new CheckBox(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.limitCoordinates.label"));
		txtLimitNorth = new TextField();
		txtLimitSouth = new TextField();
		txtLimitEast = new TextField();
		txtLimitWest = new TextField();
		
		// Set tool tips
		
		txtWidth.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.widthText.tooltip"));
		txtHeight.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.heightText.tooltip"));
		cmbEngine.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.engineCombo.tooltip"));
		cmbPlanet.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.planet.tooltip"));
		colorSelection.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.backgroundColorCombo.tooltip"));
		//cmbColoring.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.coloringCombo.tooltip"));
		//cmbHillshading.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.hillshadingCombo.tooltip"));
		//txtTileSize.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.tileSizeText.tooltip"));
		//spnLightMultiple.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.lightMultipleSlider.tooltip"));
		//jsldSpotExponent.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.spotExponentSlider.tooltip"));
		//spnSpotExponent.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.spotExponentSlider.tooltip"));
		spnElevationMultiple.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.elevationMultipleSlider.tooltip"));
		cmbElevationScaling.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.elevationScaler.tooltip"));
		cmbSubpixelGridSize.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.subpixelGridSize.tooltip"));
		//spnRelativeLightIntensity.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.relativeLightIntensity.tooltip"));
		//spnRelativeDarkIntensity.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.relativeDarkIntensity.tooltip"));
		cmbAntialiasing.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.antialiasingCombo.tooltip"));
		//cmbPrecacheStrategy.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.precacheStrategyCombo.tooltip"));
		cmbMapProjection.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.mapProjection.tooltip"));
		cmbCanvasProjection.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.canvasProjection.tooltip"));
		perspectiveControl.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.perspectiveValueControl.tooltip"));
		//chkDoubleSampling.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.doubleSampling.tooltip"));
		//chkUseFastRender.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.useFastRender.tooltip"));
		//chkProject3d.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.project3d.tooltip"));
		chkMaintainAscpectRatio.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.maintainAspectRatio.tooltip"));
		chkEstimatedElevationMinMax.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.estimatedElevationMinMax.tooltip"));
		chkLimitCoordinates.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.limitCoordinates.tooltip"));
		txtLimitNorth.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.limitCoordinates.north.tooltip"));
		txtLimitSouth.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.limitCoordinates.south.tooltip"));
		txtLimitEast.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.limitCoordinates.east.tooltip"));
		txtLimitWest.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.limitCoordinates.west.tooltip"));
		
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
		
		ActionListener modelSizeActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSizeChanged(e.getSource());
				fireOptionsChangedListeners();
			}
		};
		
		FocusListener modelSizeFocusListener = new FocusListener() {
			public void focusGained(FocusEvent e) { }
			public void focusLost(FocusEvent e) {
				onSizeChanged(e.getSource());
				fireOptionsChangedListeners();
			}
		};
		
		txtWidth.addActionListener(modelSizeActionListener);
		txtHeight.addActionListener(modelSizeActionListener);
		txtWidth.addFocusListener(modelSizeFocusListener);
		txtHeight.addFocusListener(modelSizeFocusListener);
		
		
		
		//txtTileSize.addActionListener(textFieldActionListener);
		//txtTileSize.addFocusListener(focusListener);
		
		ItemListener comboBoxItemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) 
					fireOptionsChangedListeners();
			}
		};
		
		cmbEngine.addItemListener(comboBoxItemListener);
		cmbPlanet.addItemListener(comboBoxItemListener);
		//cmbBackgroundColor.addItemListener(comboBoxItemListener);
		//cmbColoring.addItemListener(comboBoxItemListener);
		//cmbHillshading.addItemListener(comboBoxItemListener);
		cmbMapProjection.addItemListener(comboBoxItemListener);
		cmbCanvasProjection.addItemListener(comboBoxItemListener);
		cmbElevationScaling.addItemListener(comboBoxItemListener);
		cmbSubpixelGridSize.addItemListener(comboBoxItemListener);
		
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
		//spnSpotExponent.addChangeListener(spinnerChangeListener);
		//spnLightMultiple.addChangeListener(spinnerChangeListener);
		spnElevationMultiple.addChangeListener(spinnerChangeListener);
		//spnRelativeLightIntensity.addChangeListener(spinnerChangeListener);
		//spnRelativeDarkIntensity.addChangeListener(spinnerChangeListener);
		
		ActionListener checkBoxActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				fireOptionsChangedListeners();
			}
		};
		ChangeListener basicChangeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				fireOptionsChangedListeners();
			}
		};
		
		
	//	/chkDoubleSampling.getModel().addActionListener(checkBoxActionListener);
		//chkUseFastRender.getModel().addActionListener(checkBoxActionListener);
		chkMaintainAscpectRatio.getModel().addActionListener(checkBoxActionListener);
		chkEstimatedElevationMinMax.getModel().addActionListener(checkBoxActionListener);
		//chkDoubleSampling.addChangeListener(basicChangeListener);
		//chkUseFastRender.addChangeListener(basicChangeListener);
		//chkProject3d.addChangeListener(basicChangeListener);
		//chkMaintainAscpectRatio.addChangeListener(basicChangeListener);
		
		coloringControl.addChangeListener(basicChangeListener);
		colorSelection.addChangeListener(basicChangeListener);
		perspectiveControl.addChangeListener(basicChangeListener);
		
		chkLimitCoordinates.getModel().addActionListener(checkBoxActionListener);
		txtLimitNorth.addFocusListener(focusListener);
		txtLimitSouth.addFocusListener(focusListener);
		txtLimitEast.addFocusListener(focusListener);
		txtLimitWest.addFocusListener(focusListener);
		
		
		//lightSourceControl.addChangeListener(basicChangeListener);

		// Set Layout
		
		//controlGrid.add(new JLabel("Test Spinner:"));
		//controlGrid.add(new Spinner());
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.engineCombo.label") + ":"));
		controlGrid.add(cmbEngine);
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.widthText.label") + ":"));
		controlGrid.add(txtWidth);
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.heightText.label") + ":"));
		controlGrid.add(txtHeight);
		
		controlGrid.add(new JLabel());
		controlGrid.add(chkMaintainAscpectRatio);
		
		//controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.tileSizeText.label") + ":"));
		//controlGrid.add(txtTileSize);
		
		//controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.backgroundColorCombo.label") + ":"));
		//controlGrid.add(cmbBackgroundColor);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.planet.label") + ":"));
		controlGrid.add(cmbPlanet);
		
		
		controlGrid.add(new JLabel());
		controlGrid.add(chkEstimatedElevationMinMax);
		
		controlGrid.add(new JLabel());
		controlGrid.add(chkLimitCoordinates);
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.limitCoordinates.north.label") + ":"));
		controlGrid.add(txtLimitNorth);
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.limitCoordinates.south.label") + ":"));
		controlGrid.add(txtLimitSouth);
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.limitCoordinates.east.label") + ":"));
		controlGrid.add(txtLimitEast);
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.limitCoordinates.west.label") + ":"));
		controlGrid.add(txtLimitWest);
		
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.backgroundColorCombo.label") + ":"));
		controlGrid.add(colorSelection);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.coloringCombo.label") + ":"));
		controlGrid.add(coloringControl);
		
		//controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.lightSource.label") + ":"));
		//controlGrid.add(lightSourceControl);
		
		//controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.hillshadingCombo.label") + ":"));
		//controlGrid.add(cmbHillshading);
		
		//controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.antialiasingCombo.label") + ":"));
		//controlGrid.add(cmbAntialiasing);
		
		//controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.precacheStrategyCombo.label") + ":"));
		//controlGrid.add(cmbPrecacheStrategy);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.mapProjection.label")));
		controlGrid.add(cmbMapProjection);
		
		//controlGrid.add(new JLabel());
		//controlGrid.add(chkProject3d);
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.canvasProjection.label")));
		controlGrid.add(cmbCanvasProjection);
		
		
		//controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.lightMultipleSlider.label") + ":"));
		//controlGrid.add(spnLightMultiple);
		
		//controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.relativeLightIntensity.label") + ":"));
		//controlGrid.add(spnRelativeLightIntensity);
		
		//controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.relativeDarkIntensity.label") + ":"));
		//controlGrid.add(spnRelativeDarkIntensity);
		
		//controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.spotExponentSlider.label") + ":"));
		//controlGrid.add(spnSpotExponent);
		//controlGrid.add(jsldSpotExponent);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.elevationMultipleSlider.label") + ":"));
		controlGrid.add(spnElevationMultiple);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.elevationScaler.label") + ":"));
		controlGrid.add(cmbElevationScaling);
		
		
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.perspectiveValueControl.label") + ":"));
		controlGrid.add(perspectiveControl);
		
		controlGrid.add(new JLabel(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.subpixelGridSize.label") + ":"));
		controlGrid.add(cmbSubpixelGridSize);
		
		//controlGrid.add(new JLabel());
		//controlGrid.add(chkDoubleSampling);
		
		//controlGrid.add(new JLabel());
		//controlGrid.add(chkUseFastRender);
		
		
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		add(controlGrid, BorderLayout.CENTER);


		// Set initial values
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
		setModelOptions(new ModelOptions());
	}
	
	protected void applyOptionsToUI()
	{
		ignoreValueChanges = true;
		
		engineModel.setSelectedItemByValue(modelOptions.getEngine());
		planetListModel.setSelectedItemByValue(modelOptions.getOption(ModelOptionNamesEnum.PLANET));
		
		int width = modelOptions.getWidth();
		int height = modelOptions.getHeight();
		
		txtWidth.setText(""+width);
		txtHeight.setText(""+height);
		chkMaintainAscpectRatio.setSelected(modelOptions.getBooleanOption(ModelOptionNamesEnum.MAINTAIN_ASPECT_RATIO_TO_DATA));
		chkEstimatedElevationMinMax.setSelected(modelOptions.getBooleanOption(ModelOptionNamesEnum.ESTIMATE_ELEVATION_MIN_MAX));
		
		//backgroundModel.setSelectedItemByValue(modelOptions.getBackgroundColor());
		///coloringModel.setSelectedItemByValue(modelOptions.getColoringType());
		coloringControl.setColoringSelection(modelOptions.getColoringType());
		coloringControl.setConfigString(modelOptions.getGradientLevels());
		//lightSourceControl.setSolarAzimuth(modelOptions.getLightingAzimuth());
		//lightSourceControl.setSolarElevation(modelOptions.getLightingElevation());
		
		//hillShadingModel.setSelectedItemByValue(modelOptions.getHillShadeType());
		
		colorSelection.setValueString(modelOptions.getBackgroundColor());
		
		antialiasingModel.setSelectedItemByValue(modelOptions.isAntialiased());
		//precacheStrategyModel.setSelectedItemByValue(modelOptions.getPrecacheStrategy());
		mapProjectionListModel.setSelectedItemByValue(modelOptions.getMapProjection().identifier());
		canvasProjectionListModel.setSelectedItemByValue(modelOptions.getModelProjection().identifier());
		
		//txtTileSize.setText(""+modelOptions.getTileSize());
		//spnLightMultiple.setValue((int)Math.round(modelOptions.getLightingMultiple() * 100));
		//jsldSpotExponent.setValue(modelOptions.getSpotExponent());
		//spnSpotExponent.setValue(modelOptions.getSpotExponent());
		
		
		//spnRelativeLightIntensity.setValue((int)Math.round(modelOptions.getRelativeLightIntensity() * 100));
		//spnRelativeDarkIntensity.setValue((int)Math.round(modelOptions.getRelativeDarkIntensity() * 100));
		
		spnElevationMultiple.setValue((int)Math.round(modelOptions.getElevationMultiple()));
		elevationScalerListModel.setSelectedItemByValue(modelOptions.getElevationScaler().identifier());
		
		perspectiveControl.setRotateX(modelOptions.getProjection().getRotateX());
		perspectiveControl.setRotateY(modelOptions.getProjection().getRotateY());
		
		subpixelGridSizeListModel.setSelectedItemByValue(modelOptions.getIntegerOption(ModelOptionNamesEnum.SUBPIXEL_WIDTH));
		//chkDoubleSampling.setSelected(modelOptions.getDoublePrecisionHillshading());
		//chkUseFastRender.setSelected(modelOptions.getUseSimpleCanvasFill());
		//chkProject3d.setSelected(modelOptions.getProject3d());
		
		//gradientConfigPanel.setGradientIdentifier(modelOptions.getColoringType());
		//gradientConfigPanel.setConfigString(modelOptions.getGradientLevels());
		//lightPositionConfigPanel.setSolarAzimuth(modelOptions.getLightingAzimuth());
		//lightPositionConfigPanel.setSolarElevation(modelOptions.getLightingElevation());

		//projectionConfigPanel.setRotation(modelOptions.getProjection().getRotateX(),
		//						modelOptions.getProjection().getRotateY(),
		//						modelOptions.getProjection().getRotateZ());
		
		
		chkLimitCoordinates.getModel().setSelected(modelOptions.getBooleanOption(ModelOptionNamesEnum.LIMIT_COORDINATES));
		double limitNorth = modelOptions.getDoubleOption(ModelOptionNamesEnum.LIMITS_NORTH);
		if (limitNorth != DemConstants.ELEV_NO_DATA)
			txtLimitNorth.setText(""+limitNorth);
		else
			txtLimitNorth.setText("");
		
		double limitSouth = modelOptions.getDoubleOption(ModelOptionNamesEnum.LIMITS_SOUTH);
		if (limitSouth != DemConstants.ELEV_NO_DATA)
			txtLimitSouth.setText(""+limitSouth);
		else
			txtLimitSouth.setText("");
		
		double limitEast = modelOptions.getDoubleOption(ModelOptionNamesEnum.LIMITS_EAST);
		if (limitEast != DemConstants.ELEV_NO_DATA)
			txtLimitEast.setText(""+limitEast);
		else
			txtLimitEast.setText("");
		
		double limitWest = modelOptions.getDoubleOption(ModelOptionNamesEnum.LIMITS_WEST);
		if (limitWest != DemConstants.ELEV_NO_DATA)
			txtLimitWest.setText(""+limitWest);
		else
			txtLimitWest.setText("");
		
		onSizeChanged(txtWidth);
		checkControlState();
		
		ignoreValueChanges = false;
	}
	
	protected void applyOptionsToModel()
	{
		modelOptions.setEngine(engineModel.getSelectedItemValue());
		modelOptions.setWidth(txtWidth.getInteger());
		modelOptions.setHeight(txtHeight.getInteger());
		modelOptions.setOption(ModelOptionNamesEnum.PLANET, planetListModel.getSelectedItemValue());
		
		modelOptions.setOption(ModelOptionNamesEnum.MAINTAIN_ASPECT_RATIO_TO_DATA, chkMaintainAscpectRatio.getModel().isSelected());
		
		modelOptions.setOption(ModelOptionNamesEnum.ESTIMATE_ELEVATION_MIN_MAX, chkEstimatedElevationMinMax.getModel().isSelected());
		
		//modelOptions.setBackgroundColor(backgroundModel.getSelectedItemValue());
		modelOptions.setBackgroundColor(colorSelection.getValueString());
		
		//modelOptions.setColoringType(coloringModel.getSelectedItemValue());
		modelOptions.setColoringType(coloringControl.getColoringSelection());
		modelOptions.setGradientLevels(coloringControl.getConfigString());
		
		//modelOptions.setHillShadeType(hillShadingModel.getSelectedItemValue());
		//modelOptions.setLightingMultiple((double)((Integer)spnLightMultiple.getValue()) / 100.0);
		
		double elevationMultiple = (double)((Integer)spnElevationMultiple.getValue());
		modelOptions.setElevationMultiple(elevationMultiple);
		modelOptions.setElevationScaler(elevationScalerListModel.getSelectedItemValue());
		
		//modelOptions.setRelativeLightIntensity((double)((Integer)spnRelativeLightIntensity.getValue()) / 100.0);
		//modelOptions.setRelativeDarkIntensity((double)((Integer)spnRelativeDarkIntensity.getValue()) / 100.0);
		
		//modelOptions.setTileSize(txtTileSize.getInteger());
		//modelOptions.setGradientLevels(gradientConfigPanel.getConfigString());
		
		modelOptions.setAntialiased(antialiasingModel.getSelectedItemValue());
		//modelOptions.setPrecacheStrategy(precacheStrategyModel.getSelectedItemValue());
		
		//modelOptions.setLightingAzimuth(lightSourceControl.getSolarAzimuth());
		//modelOptions.setLightingElevation(lightSourceControl.getSolarElevation());
		
		
		modelOptions.setMapProjection(mapProjectionListModel.getSelectedItemValue());
		
		modelOptions.getProjection().setRotateX(perspectiveControl.getRotateX());
		modelOptions.getProjection().setRotateY(perspectiveControl.getRotateY());
		//modelOptions.setLightingAzimuth(lightPositionConfigPanel.getSolarAzimuth());
		//modelOptions.setLightingElevation(lightPositionConfigPanel.getSolarElevation());

		//modelOptions.setSpotExponent(jsldSpotExponent.getValue());
		//modelOptions.setSpotExponent((Integer)spnSpotExponent.getValue());
		
		//modelOptions.setDoublePrecisionHillshading(chkDoubleSampling.getModel().isSelected());
		//modelOptions.setUseSimpleCanvasFill(chkUseFastRender.getModel().isSelected());
		//modelOptions.setProject3d(chkProject3d.getModel().isSelected());
		
		modelOptions.setModelProjection(canvasProjectionListModel.getSelectedItemValue());
		
		
		modelOptions.setOption(ModelOptionNamesEnum.SUBPIXEL_WIDTH, subpixelGridSizeListModel.getSelectedItemValue());
		
		/*
		 * Temporary. Will add error checking.
		 */
		modelOptions.setOption(ModelOptionNamesEnum.LIMIT_COORDINATES, chkLimitCoordinates.getModel().isSelected());
		if (txtLimitNorth.getText().length() > 0) {
			modelOptions.setOption(ModelOptionNamesEnum.LIMITS_NORTH, Double.parseDouble(txtLimitNorth.getText()));
		} else {
			modelOptions.setOption(ModelOptionNamesEnum.LIMITS_NORTH, DemConstants.ELEV_NO_DATA);
		}
		
		if (txtLimitSouth.getText().length() > 0) {
			modelOptions.setOption(ModelOptionNamesEnum.LIMITS_SOUTH, Double.parseDouble(txtLimitSouth.getText()));
		} else {
			modelOptions.setOption(ModelOptionNamesEnum.LIMITS_SOUTH, DemConstants.ELEV_NO_DATA);
		}
		
		if (txtLimitEast.getText().length() > 0) {
			modelOptions.setOption(ModelOptionNamesEnum.LIMITS_EAST, Double.parseDouble(txtLimitEast.getText()));
		} else {
			modelOptions.setOption(ModelOptionNamesEnum.LIMITS_EAST, DemConstants.ELEV_NO_DATA);
		}
		
		if (txtLimitWest.getText().length() > 0) {
			modelOptions.setOption(ModelOptionNamesEnum.LIMITS_WEST, Double.parseDouble(txtLimitWest.getText()));
		} else {
			modelOptions.setOption(ModelOptionNamesEnum.LIMITS_WEST, DemConstants.ELEV_NO_DATA);
		}
		
		
		
		//modelOptions.getProjection().setRotateX(projectionConfigPanel.getRotateX());
		//modelOptions.getProjection().setRotateY(projectionConfigPanel.getRotateY());
		//modelOptions.getProjection().setRotateZ(projectionConfigPanel.getRotateZ());
	}
	
	
	protected void checkControlState()
	{
		String engineSelection = engineModel.getSelectedItemValue();
		EngineInstance engineInstance = EngineRegistry.getInstance(engineSelection);
		ColoringInstance coloringInstance = ColoringRegistry.getInstance(coloringControl.getColoringSelection());
		
		txtWidth.setEnabled(engineInstance.usesWidth());
		txtHeight.setEnabled(engineInstance.usesHeight());
		//cmbBackgroundColor.setEnabled(engineInstance.usesBackgroundColor());
		//cmbColoring.setEnabled(engineInstance.usesColoring());
		coloringControl.setEnabled(engineInstance.usesColoring());
		//cmbHillshading.setEnabled(engineInstance.usesHillshading());
		cmbAntialiasing.setEnabled(engineInstance.usesAntialiasing());
		//cmbPrecacheStrategy.setEnabled(engineInstance.usesPrecacheStrategy());
		cmbMapProjection.setEnabled(engineInstance.usesMapProjection());
		//spnLightMultiple.setEnabled(engineInstance.usesLightMultiple());
		//jsldSpotExponent.setEnabled(engineInstance.usesSpotExponent());
		//spnSpotExponent.setEnabled(engineInstance.usesSpotExponent());
		//txtTileSize.setEnabled(engineInstance.usesTileSize());
		//gradientConfigPanel.setEnabled(engineInstance.usesColoring());
		//lightSourceControl.setEnabled(engineInstance.usesLightDirection());
		
		//spnRelativeLightIntensity.setEnabled(engineInstance.usesRelativeLightMultiple());
		//spnRelativeDarkIntensity.setEnabled(engineInstance.usesRelativeDarkMultiple());
		
		
		
		//gradientConfigPanel.setVisible(coloringInstance.allowGradientConfig());
		//projectionConfigPanel.setVisible(engineInstance.usesProjection());
		//lightPositionConfigPanel.setVisible(engineInstance.usesLightDirection());
		
		//if (engineInstance.usesLightDirection()) {
		//	lightPositionConfigPanel.updatePreview(true);
		//}	
		
		CanvasProjectionTypeEnum canvasProjectionType = CanvasProjectionTypeEnum.getCanvasProjectionEnumFromIdentifier(canvasProjectionListModel.getSelectedItemValue());
		perspectiveControl.setEnabled(canvasProjectionType != CanvasProjectionTypeEnum.PROJECT_FLAT);
		spnElevationMultiple.setEnabled(canvasProjectionType != CanvasProjectionTypeEnum.PROJECT_FLAT);
		//cmbElevationScaling.setEnabled(canvasProjectionType != CanvasProjectionTypeEnum.PROJECT_FLAT);
		
		txtLimitNorth.setEnabled(chkLimitCoordinates.getModel().isSelected());
		txtLimitSouth.setEnabled(chkLimitCoordinates.getModel().isSelected());
		txtLimitEast.setEnabled(chkLimitCoordinates.getModel().isSelected());
		txtLimitWest.setEnabled(chkLimitCoordinates.getModel().isSelected());
		//spnLightMultiple.setEnabled(engineInstance.usesLightMultiple());
		
		
		//chkDoubleSampling.setEnabled(engineInstance.usesHillshading());
	}
	
	
	protected void onSizeChanged(Object object)
	{
		if (ignoreValueChanges || !modelOptions.getBooleanOption(ModelOptionNamesEnum.MAINTAIN_ASPECT_RATIO_TO_DATA)) {
			return;
		}
		
		
		int height = this.txtHeight.getInteger();
		int width = this.txtWidth.getInteger();
		
		if (object == this.txtHeight) {
			//log.info("Height modified!");
			width = (int) Math.round((double)height * getAspectRatio());
			this.txtWidth.setText(""+width);
		} else if (object == this.txtWidth) {
			//log.info("Width modified!");
			height = (int) Math.round((double)width / getAspectRatio());
			this.txtHeight.setText(""+height);
		}
		
	}
	
	protected double getAspectRatio()
	{
		if (getAspectRatioHandler != null) {
			return getAspectRatioHandler.getAspectRatio();
		} else {
			return 1.0;
		}
	}
	
	public void setGetAspectRatioHandler(GetAspectRatioHandler handler)
	{
		getAspectRatioHandler = handler;
	}
	
	public void setModelOptions(ModelOptions modelOptions)
	{
		this.modelOptions = modelOptions;
		applyOptionsToUI();
	}
	
	public ModelOptions getModelOptions()
	{
		return getModelOptions(true);
	}
	
	public ModelOptions getModelOptions(boolean sync)
	{
		if (sync) {
			applyOptionsToModel();
		}
		return modelOptions;
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
		checkControlState();
		for (OptionsChangedListener listener : optionsChangedListeners) {
			listener.onOptionsChanged(modelOptions);
		}
	}

	public interface GetAspectRatioHandler {
		public double getAspectRatio();
	}
	
	
}
