package us.wthr.jdem846.ui.preferences;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.NumberTextField;
import us.wthr.jdem846.ui.base.Button;
import us.wthr.jdem846.ui.base.CheckBox;
import us.wthr.jdem846.ui.base.ComboBox;
import us.wthr.jdem846.ui.base.Dialog;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.Slider;
import us.wthr.jdem846.ui.base.TabPane;
import us.wthr.jdem846.ui.optionModels.JGoodiesColorThemeListModel;
import us.wthr.jdem846.ui.optionModels.LanguageListModel;
import us.wthr.jdem846.ui.optionModels.PrecacheStrategyOptionsListModel;
import us.wthr.jdem846.ui.panels.FlexGridPanel;



@SuppressWarnings("serial")
public class PreferencesDialog extends Dialog
{
	private static Log log = Logging.getLog(PreferencesDialog.class);
	
	private Button btnOk;
	private Button btnCancel;
	
	
	private LanguageListModel languageListModel;
	private PrecacheStrategyOptionsListModel precacheStrategyModel;
	
	
	
	private ComboBox cmbGeneralLanguage;
	private JGoodiesColorThemeListModel colorThemeListModel;
	private ComboBox cmbGeneralColorTheme;
	private CheckBox chkGeneralDisplayToolbarText;
	private CheckBox chkGeneralAntialiasedScriptEditorText;
	private CheckBox chkGeneralDisplayMemoryMonitor;
	private CheckBox chkGeneralDisplayLogPanel;
	private CheckBox chkGeneralPreviewModelDuringRender;	
	private CheckBox chkGeneralLimitConsoleOutput;
	private NumberTextField txtGeneralConsoleBufferSize;
	
	
	private Slider sldPreviewingPreviewQuality;
	private CheckBox chkPreviewingIncludeRasterDataInPreview;
	private CheckBox chkPreviewAutoUpdate;
	
	private CheckBox chkPerfPipelineRendering;
	private CheckBox chkPerfNearestNeighborDataRetrieval;
	private CheckBox chkPerfInterpolateLowerResolutionData;
	private CheckBox chkPerfAverageOverlappingData;
	private NumberTextField txtPerfTileSize;
	private ComboBox cmbPerfPrecacheStrategy;
	private CheckBox chkPerfDoubleBuffered;
	
	
	public PreferencesDialog()
	{
		this.setTitle(I18N.get("us.wthr.jdem846.ui.preferencesDialog.title"));
		this.setModal(true);
		this.setSize(430, 300);
		this.setLocationRelativeTo(null);
		
		
		btnOk = new Button(I18N.get("us.wthr.jdem846.ui.ok"));
		btnCancel = new Button(I18N.get("us.wthr.jdem846.ui.cancel"));
		

		languageListModel = new LanguageListModel();
		cmbGeneralLanguage = new ComboBox(languageListModel);
		colorThemeListModel = new JGoodiesColorThemeListModel();
		cmbGeneralColorTheme = new ComboBox(colorThemeListModel);
		chkGeneralDisplayToolbarText = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.displayToolbarText"));
		chkGeneralAntialiasedScriptEditorText = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.antialiasedScriptEditorText"));
		chkGeneralDisplayMemoryMonitor = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.displayMemoryMonitor"));
		chkGeneralDisplayLogPanel = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.displayLogPanel"));
		chkGeneralPreviewModelDuringRender = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.previewModelDuringRender"));
		chkGeneralLimitConsoleOutput = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.limitConsoleOutput"));
		txtGeneralConsoleBufferSize = new NumberTextField(false);
		
		
		
		sldPreviewingPreviewQuality = new Slider(1, 100);
		chkPreviewingIncludeRasterDataInPreview = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.previewing.includeRasterDataInPreview"));
		chkPreviewAutoUpdate = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.previewing.autoUpdate"));
		
		chkPerfPipelineRendering = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.performance.pipelineRendering"));
		chkPerfNearestNeighborDataRetrieval = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.performance.nearestNeighborDataRetrieval"));
		chkPerfInterpolateLowerResolutionData = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.performance.interpolateLowerResolutionData"));
		chkPerfAverageOverlappingData = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.performance.averageOverlappingData"));
		txtPerfTileSize = new NumberTextField(false);
		precacheStrategyModel = new PrecacheStrategyOptionsListModel();
		cmbPerfPrecacheStrategy = new ComboBox(precacheStrategyModel);
		chkPerfDoubleBuffered = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.performance.doubleBuffered"));
		
		// Add listeners
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onOkClicked();
			}
		});
		
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onCancelClicked();
			}
		});
		
		// Set Layout
		setLayout(new BorderLayout());
		TabPane tabPane = new TabPane();
		add(tabPane, BorderLayout.CENTER);
		
		
		FlexGridPanel performancePanel = new FlexGridPanel(2);
		FlexGridPanel previewPanel = new FlexGridPanel(2);
		FlexGridPanel generalPanel = new FlexGridPanel(2);

		performancePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		previewPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		generalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		
		tabPane.add(generalPanel, I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.tab"));
		tabPane.add(performancePanel, I18N.get("us.wthr.jdem846.ui.preferencesDialog.performance.tab"));
		tabPane.add(previewPanel, I18N.get("us.wthr.jdem846.ui.preferencesDialog.previewing.tab"));
		
		
		generalPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.language") + ":"));
		generalPanel.add(cmbGeneralLanguage);
		
		generalPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.userInterfaceColorTheme") + ":"));
		generalPanel.add(cmbGeneralColorTheme);
		
		generalPanel.add(new Label(""));
		generalPanel.add(chkGeneralDisplayToolbarText);
		
		generalPanel.add(new Label(""));
		generalPanel.add(chkGeneralAntialiasedScriptEditorText);

		generalPanel.add(new Label(""));
		generalPanel.add(chkGeneralDisplayMemoryMonitor);
		
		generalPanel.add(new Label(""));
		generalPanel.add(chkGeneralPreviewModelDuringRender);
		
		generalPanel.add(new Label(""));
		generalPanel.add(chkGeneralDisplayLogPanel);
		
		generalPanel.add(new Label(""));
		generalPanel.add(chkGeneralLimitConsoleOutput);
		
		generalPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.consoleBufferSize") + ":"));
		generalPanel.add(txtGeneralConsoleBufferSize);
		
		
		
		previewPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.previewing.previewQuality") + ":"));
		previewPanel.add(sldPreviewingPreviewQuality);
		
		previewPanel.add(new Label(""));
		previewPanel.add(chkPreviewingIncludeRasterDataInPreview);
		
		previewPanel.add(new Label(""));
		previewPanel.add(chkPreviewAutoUpdate);
		
		performancePanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.performance.tileSize") + ":"));
		performancePanel.add(txtPerfTileSize);
		
		
		performancePanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.performance.precacheStrategy") + ":"));
		performancePanel.add(cmbPerfPrecacheStrategy);
		
		performancePanel.add(new Label(""));
		performancePanel.add(chkPerfDoubleBuffered);
		
		performancePanel.add(new Label(""));
		performancePanel.add(chkPerfPipelineRendering);
		
		performancePanel.add(new Label(""));
		performancePanel.add(chkPerfNearestNeighborDataRetrieval);
		
		performancePanel.add(new Label(""));
		performancePanel.add(chkPerfInterpolateLowerResolutionData);
		
		performancePanel.add(new Label(""));
		performancePanel.add(chkPerfAverageOverlappingData);
		
		
		
		
		
		Panel pnlButtons = new Panel();
		pnlButtons.add(btnOk);
		pnlButtons.add(btnCancel);
		add(pnlButtons, BorderLayout.SOUTH);
		
		//this.pack();
		
		setValuesToUserInterface();
	}
	
	public void setValuesToUserInterface()
	{

		
		languageListModel.setSelectedItemByValue(JDem846Properties.getProperty("us.wthr.jdem846.general.ui.i18n.default"));
		colorThemeListModel.setSelectedItemByValue(JDem846Properties.getProperty("us.wthr.jdem846.general.ui.jgoodies.theme"));
		chkGeneralDisplayToolbarText.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.displayToolbarText"));
		chkGeneralAntialiasedScriptEditorText.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.scriptEditorPane.textAA"));
		chkGeneralDisplayMemoryMonitor.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.jdemFrame.displayMemoryMonitor"));
		chkGeneralDisplayLogPanel.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.displayLogViewPanel"));
		chkGeneralPreviewModelDuringRender.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.renderInProcessPreviewing"));
	
		chkGeneralLimitConsoleOutput.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.console.limitOuput"));
		txtGeneralConsoleBufferSize.setText(JDem846Properties.getProperty("us.wthr.jdem846.general.ui.console.bufferSize"));
		
		
		sldPreviewingPreviewQuality.setValue((int)(JDem846Properties.getDoubleProperty("us.wthr.jdem846.previewing.ui.previewQuality") * 100.0));
		chkPreviewingIncludeRasterDataInPreview.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.rasterPreview"));
		chkPreviewAutoUpdate.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.autoUpdate"));
		
		
		chkPerfPipelineRendering.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.pipelineRender"));
		chkPerfNearestNeighborDataRetrieval.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.standardResolutionRetrieval"));
		chkPerfInterpolateLowerResolutionData.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.interpolateToHigherResolution"));
		chkPerfAverageOverlappingData.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.averageOverlappedData"));
		txtPerfTileSize.setText(JDem846Properties.getProperty("us.wthr.jdem846.performance.tileSize"));
		chkPerfDoubleBuffered.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.doubleBuffered"));
		
		precacheStrategyModel.setSelectedItemByValue(JDem846Properties.getProperty("us.wthr.jdem846.performance.precacheStrategy"));
		
	}
	
	public void setValuesFromUserInterface()
	{
		
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.i18n.default", languageListModel.getSelectedItemValue());
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.jgoodies.theme", colorThemeListModel.getSelectedItemValue());
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.displayToolbarText", ""+chkGeneralDisplayToolbarText.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.scriptEditorPane.textAA", ""+chkGeneralAntialiasedScriptEditorText.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.jdemFrame.displayMemoryMonitor", ""+chkGeneralDisplayMemoryMonitor.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.displayLogViewPanel", ""+chkGeneralDisplayLogPanel.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.renderInProcessPreviewing", ""+chkGeneralPreviewModelDuringRender.getModel().isSelected());
		
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.console.limitOuput", ""+chkGeneralLimitConsoleOutput.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.console.bufferSize", ""+txtGeneralConsoleBufferSize.getInteger());
		
		
		double quality = (double)sldPreviewingPreviewQuality.getValue() / 100.0;
		JDem846Properties.setProperty("us.wthr.jdem846.previewing.ui.previewQuality", ""+quality);
		JDem846Properties.setProperty("us.wthr.jdem846.previewing.ui.rasterPreview", ""+chkPreviewingIncludeRasterDataInPreview.getModel().isSelected()); 
		JDem846Properties.setProperty("us.wthr.jdem846.previewing.ui.autoUpdate", ""+chkPreviewAutoUpdate.getModel().isSelected());
		
		
		
		
		JDem846Properties.setProperty("us.wthr.jdem846.performance.pipelineRender", ""+chkPerfPipelineRendering.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.performance.standardResolutionRetrieval", ""+chkPerfNearestNeighborDataRetrieval.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.performance.interpolateToHigherResolution", ""+chkPerfInterpolateLowerResolutionData.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.performance.averageOverlappedData", ""+chkPerfAverageOverlappingData.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.performance.tileSize", txtPerfTileSize.getText());
		JDem846Properties.setProperty("us.wthr.jdem846.performance.precacheStrategy", precacheStrategyModel.getSelectedItemValue());
		JDem846Properties.setProperty("us.wthr.jdem846.performance.doubleBuffered", ""+chkPerfDoubleBuffered.getModel().isSelected());
		
		
	}
	
	
	
	protected void onOkClicked()
	{
		log.info("Preferences->ok");
		
		setValuesFromUserInterface();
		
		this.setVisible(false);
	}
	
	protected void onCancelClicked()
	{
		log.info("Preferences->cancel");
		this.setVisible(false);
	}
	
}
