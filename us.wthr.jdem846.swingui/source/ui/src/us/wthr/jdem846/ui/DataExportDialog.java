package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Button;
import us.wthr.jdem846.ui.base.ComboBox;
import us.wthr.jdem846.ui.base.Dialog;
import us.wthr.jdem846.ui.base.FileChooser;
import us.wthr.jdem846.ui.base.JComboBoxModel;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.TextField;
import us.wthr.jdem846.ui.panels.FlexGridPanel;

@SuppressWarnings("serial")
public class DataExportDialog extends Dialog
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(DataExportDialog.class);
	
	private ComboBox cmbExportFormat;
	private ExportFormatListModel formatModel;
	
	private TextField txtExportLocation;
	
	//private DataPackage dataPackage;
	private ModelContext modelContext;
	
	public DataExportDialog(ModelContext modelContext)
	{
		// Set Properties
		setTitle(I18N.get("us.wthr.jdem846.ui.exportDialog.title"));
		this.modelContext = modelContext;
		setModal(true);
		this.setSize(400, 125);
		this.setLocationRelativeTo(null);
		
		// Create components
		formatModel = new ExportFormatListModel();
		cmbExportFormat = new ComboBox(formatModel);
		formatModel.setSelectedItemByValue(formatModel.getElementAt(0).getValue());
		
		txtExportLocation = new TextField();
		Button btnBrowse = new Button(I18N.get("us.wthr.jdem846.ui.browse") + "...");
		
		cmbExportFormat.setToolTipText(I18N.get("us.wthr.jdem846.ui.exportDialog.exportFormat.tooltip"));
		txtExportLocation.setToolTipText(I18N.get("us.wthr.jdem846.ui.exportDialog.exportLocation.tooltip"));
		btnBrowse.setToolTipText(I18N.get("us.wthr.jdem846.ui.exportDialog.browse.tooltip"));
		
		Button btnExport = new Button(I18N.get("us.wthr.jdem846.ui.exportDialog.export.label"));
		btnExport.setToolTipText(I18N.get("us.wthr.jdem846.ui.exportDialog.export.tooltip"));
		
		Button btnCancel = new Button(I18N.get("us.wthr.jdem846.ui.cancel"));
		btnCancel.setToolTipText(I18N.get("us.wthr.jdem846.ui.exportDialog.cancel.tooltip"));
		
		// Add listeners
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onBrowse();
			}
		});
		
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onExport();
			}
		});
		
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		
		// Set Layout
		FlexGridPanel controlGrid = new FlexGridPanel(3);
		
		controlGrid.add(new Label(I18N.get("us.wthr.jdem846.ui.exportDialog.exportFormat.label")));
		controlGrid.add(cmbExportFormat);
		controlGrid.add(new Label());
		
		controlGrid.add(new Label(I18N.get("us.wthr.jdem846.ui.exportDialog.exportLocation.label")));
		controlGrid.add(txtExportLocation);
		controlGrid.add(btnBrowse);

		
		setLayout(new BorderLayout());
		add(controlGrid, BorderLayout.CENTER);
		
		
		Panel pnlActions = new Panel();
		pnlActions.setLayout(new FlowLayout());
		pnlActions.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		pnlActions.add(btnCancel);
		pnlActions.add(btnExport);
		add(pnlActions, BorderLayout.SOUTH);
		
		
	}
	
	protected void onExport()
	{
		JOptionPane.showMessageDialog(getRootPane(),
				I18N.get("us.wthr.jdem846.ui.notYetImplemented.message"),
			    I18N.get("us.wthr.jdem846.ui.notYetImplemented.title"),
			    JOptionPane.INFORMATION_MESSAGE);
	}
	
	protected void onBrowse()
	{
		String path = txtExportLocation.getText();
		
		FileChooser chooser = new FileChooser();
		if (path != null && path.length() > 0) {
			File selected = new File(path);
			chooser.setSelectedFile(selected);
			chooser.setCurrentDirectory(selected);
		}
		
		chooser.setMultiSelectionEnabled(false);
		
	    int returnVal =  chooser.showSaveDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	File selectedFile = chooser.getSelectedFile();
	    	txtExportLocation.setText(selectedFile.getAbsolutePath());
	    }
		
	}
	
	class ExportFormatListModel extends JComboBoxModel<String>
	{
		
		public ExportFormatListModel()
		{
			addItem(I18N.get("us.wthr.jdem846.input.gridFloat.name"), "gridfloat");
			addItem(I18N.get("us.wthr.jdem846.input.edef.elevationDatasetExchangeFormat.name"), "edef");
		}
		
	}
	
}
