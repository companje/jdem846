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
import us.wthr.jdem846.ui.base.TextField;
import us.wthr.jdem846.ui.optionModels.ImageFormatListModel;
import us.wthr.jdem846.ui.optionModels.ImageQualityListModel;
import us.wthr.jdem846.ui.optionModels.LanguageListModel;
import us.wthr.jdem846.ui.optionModels.PrecacheStrategyOptionsListModel;
import us.wthr.jdem846.ui.optionModels.RenderEngineListModel;
import us.wthr.jdem846.ui.panels.FlexGridPanel;



@SuppressWarnings("serial")
public class PreferencesDialog extends Dialog
{
	private static Log log = Logging.getLog(PreferencesDialog.class);
	
	private Button btnOk;
	private Button btnCancel;
	
	
	private LanguageListModel languageListModel;
	private PrecacheStrategyOptionsListModel precacheStrategyModel;
	private ImageFormatListModel imageFormatListModel;
	private RenderEngineListModel renderEngineListModel;
	
	private ComboBox cmbGeneralLanguage;
	private ComboBox cmbGeneralDefaultImageFormat;
	//private JGoodiesColorThemeListModel colorThemeListModel;
	//private ComboBox cmbGeneralColorTheme;
	private CheckBox chkGeneralDisplayToolbarText;
	private CheckBox chkGeneralAntialiasedScriptEditorText;
	private CheckBox chkGeneralDisplayMemoryMonitor;
	private CheckBox chkGeneralDisplayLogPanel;
	//private CheckBox chkGeneralPreviewModelDuringRender;	
	private CheckBox chkGeneralLimitConsoleOutput;
	private NumberTextField txtGeneralConsoleBufferSize;
	private CheckBox chkGeneralReportUsage;
	
	private ComboBox cmbGeneralQuality;
	private ImageQualityListModel qualityModel;
	private TextField txtTempPath;
	
	
	private Slider sldPreviewingPreviewModelQuality;
	private Slider sldPreviewingPreviewTextureQuality;
	private CheckBox chkPreviewingIncludeRasterDataInPreview;
	private CheckBox chkPreviewAutoUpdate;
	private CheckBox chkPreviewScripting;
	
	
	//private CheckBox chkPerfPipelineRendering;
	//private CheckBox chkPerfDoubleBuffered;
	private CheckBox chkPerfNearestNeighborDataRetrieval;
	private CheckBox chkPerfInterpolateLowerResolutionData;
	private CheckBox chkPerfAverageOverlappingData;
	private NumberTextField txtPerfTileSize;
	private ComboBox cmbPerfPrecacheStrategy;
	
	private ComboBox cmbRenderingRenderEngine;
	private CheckBox chkRenderingMultisampling;
	private NumberTextField txtRenderingMultisamplingSamples;
	
	
	
	private TextField txtDefaultsSubject;
	private TextField txtDefaultsDescription;
	private TextField txtDefaultsAuthor;
	private TextField txtDefaultsAuthorContact;
	private TextField txtDefaultsInstitution;
	private TextField txtDefaultsInstitutionContact;
	private TextField txtDefaultsInstitutionAddress;

	
	public PreferencesDialog()
	{
		this.setTitle(I18N.get("us.wthr.jdem846.ui.preferencesDialog.title"));
		this.setModal(true);
		this.setSize(430, 375);
		this.setLocationRelativeTo(null);
		
		
		btnOk = new Button(I18N.get("us.wthr.jdem846.ui.ok"));
		btnCancel = new Button(I18N.get("us.wthr.jdem846.ui.cancel"));
		

		languageListModel = new LanguageListModel();
		cmbGeneralLanguage = new ComboBox(languageListModel);
		
		imageFormatListModel = new ImageFormatListModel();
		cmbGeneralDefaultImageFormat = new ComboBox(imageFormatListModel);
		
		//colorThemeListModel = new JGoodiesColorThemeListModel();
		//cmbGeneralColorTheme = new ComboBox(colorThemeListModel);
		chkGeneralDisplayToolbarText = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.displayToolbarText"));
		chkGeneralAntialiasedScriptEditorText = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.antialiasedScriptEditorText"));
		chkGeneralDisplayMemoryMonitor = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.displayMemoryMonitor"));
		chkGeneralDisplayLogPanel = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.displayLogPanel"));
		//chkGeneralPreviewModelDuringRender = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.previewModelDuringRender"));
		chkGeneralLimitConsoleOutput = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.limitConsoleOutput"));
		txtGeneralConsoleBufferSize = new NumberTextField(false);
		chkGeneralReportUsage = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.reportUsage"));
		
		qualityModel = new ImageQualityListModel();
		cmbGeneralQuality = new ComboBox(qualityModel);
		txtTempPath = new TextField();
		
		
		sldPreviewingPreviewModelQuality = new Slider(1, 100);
		sldPreviewingPreviewTextureQuality = new Slider(1, 100);
		chkPreviewingIncludeRasterDataInPreview = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.previewing.includeRasterDataInPreview"));
		chkPreviewAutoUpdate = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.previewing.autoUpdate"));
		chkPreviewScripting = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.previewing.scripting"));
		
		//chkPerfPipelineRendering = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.performance.pipelineRendering"));
		chkPerfNearestNeighborDataRetrieval = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.performance.nearestNeighborDataRetrieval"));
		chkPerfInterpolateLowerResolutionData = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.performance.interpolateLowerResolutionData"));
		chkPerfAverageOverlappingData = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.performance.averageOverlappingData"));
		txtPerfTileSize = new NumberTextField(false);
		precacheStrategyModel = new PrecacheStrategyOptionsListModel();
		cmbPerfPrecacheStrategy = new ComboBox(precacheStrategyModel);
		//chkPerfDoubleBuffered = new CheckBox(I18N.get("us.wthr.jdem846.ui.preferencesDialog.performance.doubleBuffered"));
		
		renderEngineListModel = new RenderEngineListModel();
		cmbRenderingRenderEngine = new ComboBox(renderEngineListModel);
		chkRenderingMultisampling = new CheckBox("Multisampling");
		txtRenderingMultisamplingSamples = new NumberTextField(false);
		
		/*
		 renderEngineListModel
		cmbRenderingRenderEngine
		chkRenderingMultisampling
		txtRenderingMultisamplingSamples

		 */
		
		txtDefaultsSubject = new TextField();
		txtDefaultsDescription = new TextField();
		txtDefaultsAuthor = new TextField();
		txtDefaultsAuthorContact = new TextField();
		txtDefaultsInstitution = new TextField();
		txtDefaultsInstitutionContact = new TextField();
		txtDefaultsInstitutionAddress = new TextField();
		

		
		
		
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
		FlexGridPanel renderingPanel = new FlexGridPanel(2);
		FlexGridPanel previewPanel = new FlexGridPanel(2);
		FlexGridPanel generalPanel = new FlexGridPanel(2);
		FlexGridPanel defaultsPanel = new FlexGridPanel(2);
		
		performancePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		renderingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		previewPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		generalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		defaultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		tabPane.add(generalPanel, I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.tab"));
		tabPane.add(renderingPanel, "Rendering");
		tabPane.add(performancePanel, I18N.get("us.wthr.jdem846.ui.preferencesDialog.performance.tab"));
		tabPane.add(previewPanel, I18N.get("us.wthr.jdem846.ui.preferencesDialog.previewing.tab"));
		tabPane.add(defaultsPanel, I18N.get("us.wthr.jdem846.ui.preferencesDialog.defaults.tab"));
		
		generalPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.language") + ":"));
		generalPanel.add(cmbGeneralLanguage);
		
		//generalPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.userInterfaceColorTheme") + ":"));
		//generalPanel.add(cmbGeneralColorTheme);
		generalPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.quality") + ":"));
		generalPanel.add(cmbGeneralQuality);
		
		generalPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.defaultImageFormat") + ":"));
		generalPanel.add(cmbGeneralDefaultImageFormat);
		
		
		generalPanel.add(new Label(""));
		generalPanel.add(chkGeneralDisplayToolbarText);
		
		generalPanel.add(new Label(""));
		generalPanel.add(chkGeneralAntialiasedScriptEditorText);

		generalPanel.add(new Label(""));
		generalPanel.add(chkGeneralDisplayMemoryMonitor);
		
		//generalPanel.add(new Label(""));
		//generalPanel.add(chkGeneralPreviewModelDuringRender);
		
		generalPanel.add(new Label(""));
		generalPanel.add(chkGeneralDisplayLogPanel);
		
		generalPanel.add(new Label(""));
		generalPanel.add(chkGeneralLimitConsoleOutput);
		
		generalPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.consoleBufferSize") + ":"));
		generalPanel.add(txtGeneralConsoleBufferSize);
		
		generalPanel.add(new Label(""));
		generalPanel.add(chkGeneralReportUsage);
		
		generalPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.general.tempPath")));
		generalPanel.add(txtTempPath);
		
		
		renderingPanel.add(new Label("Render Engine:"));
		renderingPanel.add(cmbRenderingRenderEngine);
		
		renderingPanel.add(new Label(""));
		renderingPanel.add(chkRenderingMultisampling);
		
		renderingPanel.add(new Label("Multisample Samples:"));
		renderingPanel.add(txtRenderingMultisamplingSamples);
		
		
		
		previewPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.previewing.previewTextureQuality") + ":"));
		previewPanel.add(sldPreviewingPreviewTextureQuality);
		
		previewPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.previewing.previewModelQuality") + ":"));
		previewPanel.add(sldPreviewingPreviewModelQuality);
		
		previewPanel.add(new Label(""));
		previewPanel.add(chkPreviewingIncludeRasterDataInPreview);
		
		previewPanel.add(new Label(""));
		previewPanel.add(chkPreviewAutoUpdate);
		
		previewPanel.add(new Label(""));
		previewPanel.add(chkPreviewScripting);
		
		performancePanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.performance.tileSize") + ":"));
		performancePanel.add(txtPerfTileSize);
		
		
		performancePanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.performance.precacheStrategy") + ":"));
		performancePanel.add(cmbPerfPrecacheStrategy);
		
		//performancePanel.add(new Label(""));
		//performancePanel.add(chkPerfDoubleBuffered);
		
		//performancePanel.add(new Label(""));
		//performancePanel.add(chkPerfPipelineRendering);
		
		performancePanel.add(new Label(""));
		performancePanel.add(chkPerfNearestNeighborDataRetrieval);
		
		performancePanel.add(new Label(""));
		performancePanel.add(chkPerfInterpolateLowerResolutionData);
		
		performancePanel.add(new Label(""));
		performancePanel.add(chkPerfAverageOverlappingData);
		

		defaultsPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.defaults.author") + ":"));
		defaultsPanel.add(txtDefaultsAuthor);
		
		defaultsPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.defaults.author-contact") + ":"));
		defaultsPanel.add(txtDefaultsAuthorContact);
		
		defaultsPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.defaults.institution") + ":"));
		defaultsPanel.add(txtDefaultsInstitution);
		
		defaultsPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.defaults.institution-contact") + ":"));
		defaultsPanel.add(txtDefaultsInstitutionContact);
		
		defaultsPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.defaults.institution-address") + ":"));
		defaultsPanel.add(txtDefaultsInstitutionAddress);
		
		defaultsPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.defaults.subject") + ":"));
		defaultsPanel.add(txtDefaultsSubject);
		
		defaultsPanel.add(new Label(I18N.get("us.wthr.jdem846.ui.preferencesDialog.defaults.description") + ":"));
		defaultsPanel.add(txtDefaultsDescription);
		
		
		Panel pnlButtons = new Panel();
		pnlButtons.add(btnOk);
		pnlButtons.add(btnCancel);
		add(pnlButtons, BorderLayout.SOUTH);
		
		renderingPanel.closeGrid();
		performancePanel.closeGrid();
		previewPanel.closeGrid();
		generalPanel.closeGrid();
		defaultsPanel.closeGrid();
		
		//this.pack();
		
		setValuesToUserInterface();
	}
	
	public void setValuesToUserInterface()
	{

		qualityModel.setSelectedItemByValue(JDem846Properties.getIntProperty("us.wthr.jdem846.general.view.imageScaling.quality"));
		languageListModel.setSelectedItemByValue(JDem846Properties.getProperty("us.wthr.jdem846.general.ui.i18n.default"));
		imageFormatListModel.setSelectedItemByValue(JDem846Properties.getProperty("us.wthr.jdem846.general.ui.defaultImageFormat"));
		//colorThemeListModel.setSelectedItemByValue(JDem846Properties.getProperty("us.wthr.jdem846.general.ui.jgoodies.theme"));
		chkGeneralDisplayToolbarText.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.displayToolbarText"));
		chkGeneralAntialiasedScriptEditorText.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.scriptEditorPane.textAA"));
		chkGeneralDisplayMemoryMonitor.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.jdemFrame.displayMemoryMonitor"));
		chkGeneralDisplayLogPanel.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.displayLogViewPanel"));
		//chkGeneralPreviewModelDuringRender.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.renderInProcessPreviewing"));
		chkGeneralReportUsage.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.usage.report"));
		
		chkGeneralLimitConsoleOutput.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.console.limitOuput"));
		txtGeneralConsoleBufferSize.setText(JDem846Properties.getProperty("us.wthr.jdem846.general.ui.console.bufferSize"));
		
		txtTempPath.setText(JDem846Properties.getProperty("us.wthr.jdem846.general.temp", false));
		
		renderEngineListModel.setSelectedItemByValue(JDem846Properties.getProperty("us.wthr.jdem846.rendering.renderEngine"));
		chkRenderingMultisampling.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.rendering.opengl.multisampling.enabled"));
		txtRenderingMultisamplingSamples.setText(JDem846Properties.getProperty("us.wthr.jdem846.rendering.opengl.multisampling.samples"));
		/*
		 * 
		renderEngineListModel
		cmbRenderingRenderEngine
		chkRenderingMultisampling
		txtRenderingMultisamplingSamples
		 */
		
		sldPreviewingPreviewModelQuality.setValue((int)(JDem846Properties.getDoubleProperty("us.wthr.jdem846.previewing.ui.previewModelQuality") * 100.0));
		sldPreviewingPreviewTextureQuality.setValue((int)(JDem846Properties.getDoubleProperty("us.wthr.jdem846.previewing.ui.previewTextureQuality") * 100.0));
		
		chkPreviewingIncludeRasterDataInPreview.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.rasterPreview"));
		chkPreviewAutoUpdate.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.autoUpdate"));
		chkPreviewScripting.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.scripting"));
		
		//chkPerfPipelineRendering.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.pipelineRender"));
		chkPerfNearestNeighborDataRetrieval.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.rendering.standardResolutionRetrieval"));
		chkPerfInterpolateLowerResolutionData.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.rendering.interpolateToHigherResolution"));
		chkPerfAverageOverlappingData.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.rendering.averageOverlappedData"));
		txtPerfTileSize.setText(JDem846Properties.getProperty("us.wthr.jdem846.performance.tileSize"));
		//chkPerfDoubleBuffered.getModel().setSelected(JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.doubleBuffered"));
		
		precacheStrategyModel.setSelectedItemByValue(JDem846Properties.getProperty("us.wthr.jdem846.performance.precacheStrategy"));
		
		
		txtDefaultsSubject.setText(JDem846Properties.getProperty("us.wthr.jdem846.defaults.subject"));
		txtDefaultsDescription.setText(JDem846Properties.getProperty("us.wthr.jdem846.defaults.description"));
		txtDefaultsAuthor.setText(JDem846Properties.getProperty("us.wthr.jdem846.defaults.author"));
		txtDefaultsAuthorContact.setText(JDem846Properties.getProperty("us.wthr.jdem846.defaults.author-contact"));
		txtDefaultsInstitution.setText(JDem846Properties.getProperty("us.wthr.jdem846.defaults.institution"));
		txtDefaultsInstitutionContact.setText(JDem846Properties.getProperty("us.wthr.jdem846.defaults.institution-contact"));
		txtDefaultsInstitutionAddress.setText(JDem846Properties.getProperty("us.wthr.jdem846.defaults.institution-address"));
		
	}
	
	public void setValuesFromUserInterface()
	{
		

		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.defaultImageFormat", imageFormatListModel.getSelectedItem());
		JDem846Properties.setProperty("us.wthr.jdem846.general.view.imageScaling.quality", qualityModel.getSelectedItemValue());
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.i18n.default", languageListModel.getSelectedItemValue());
	//	JDem846Properties.setProperty("us.wthr.jdem846.general.ui.jgoodies.theme", colorThemeListModel.getSelectedItemValue());
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.displayToolbarText", ""+chkGeneralDisplayToolbarText.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.scriptEditorPane.textAA", ""+chkGeneralAntialiasedScriptEditorText.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.jdemFrame.displayMemoryMonitor", ""+chkGeneralDisplayMemoryMonitor.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.displayLogViewPanel", ""+chkGeneralDisplayLogPanel.getModel().isSelected());
		//JDem846Properties.setProperty("us.wthr.jdem846.general.ui.renderInProcessPreviewing", ""+chkGeneralPreviewModelDuringRender.getModel().isSelected());
		
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.console.limitOuput", ""+chkGeneralLimitConsoleOutput.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.console.bufferSize", ""+txtGeneralConsoleBufferSize.getInteger());
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.usage.report", chkGeneralReportUsage.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.general.temp", txtTempPath.getText());
		
		
		double modelQuality = (double)sldPreviewingPreviewModelQuality.getValue() / 100.0;
		double textureQuality = (double)sldPreviewingPreviewTextureQuality.getValue() / 100.0;
		
		JDem846Properties.setProperty("us.wthr.jdem846.previewing.ui.previewModelQuality", ""+modelQuality);
		JDem846Properties.setProperty("us.wthr.jdem846.previewing.ui.previewTextureQuality", ""+textureQuality);
		JDem846Properties.setProperty("us.wthr.jdem846.previewing.ui.rasterPreview", ""+chkPreviewingIncludeRasterDataInPreview.getModel().isSelected()); 
		JDem846Properties.setProperty("us.wthr.jdem846.previewing.ui.autoUpdate", ""+chkPreviewAutoUpdate.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.previewing.ui.scripting", ""+chkPreviewScripting.getModel().isSelected());
		
		
		JDem846Properties.setProperty("us.wthr.jdem846.rendering.renderEngine", renderEngineListModel.getSelectedItemValue());
		JDem846Properties.setProperty("us.wthr.jdem846.rendering.opengl.multisampling.enabled", chkRenderingMultisampling.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.rendering.opengl.multisampling.samples", txtRenderingMultisamplingSamples.getInteger());
	
		
		//JDem846Properties.setProperty("us.wthr.jdem846.performance.pipelineRender", ""+chkPerfPipelineRendering.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.rendering.standardResolutionRetrieval", ""+chkPerfNearestNeighborDataRetrieval.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.rendering.interpolateToHigherResolution", ""+chkPerfInterpolateLowerResolutionData.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.rendering.averageOverlappedData", ""+chkPerfAverageOverlappingData.getModel().isSelected());
		JDem846Properties.setProperty("us.wthr.jdem846.performance.tileSize", txtPerfTileSize.getText());
		JDem846Properties.setProperty("us.wthr.jdem846.performance.precacheStrategy", precacheStrategyModel.getSelectedItemValue());
		//JDem846Properties.setProperty("us.wthr.jdem846.performance.doubleBuffered", ""+chkPerfDoubleBuffered.getModel().isSelected());
		
		JDem846Properties.setProperty("us.wthr.jdem846.defaults.subject", txtDefaultsSubject.getText());
		JDem846Properties.setProperty("us.wthr.jdem846.defaults.description", txtDefaultsDescription.getText());
		JDem846Properties.setProperty("us.wthr.jdem846.defaults.author", txtDefaultsAuthor.getText());
		JDem846Properties.setProperty("us.wthr.jdem846.defaults.author-contact", txtDefaultsAuthorContact.getText());
		JDem846Properties.setProperty("us.wthr.jdem846.defaults.institution", txtDefaultsInstitution.getText());
		JDem846Properties.setProperty("us.wthr.jdem846.defaults.institution-contact", txtDefaultsInstitutionContact.getText());
		JDem846Properties.setProperty("us.wthr.jdem846.defaults.institution-address", txtDefaultsInstitutionAddress.getText());
		
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
