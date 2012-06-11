package us.wthr.jdem846.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import us.wthr.jdem846.JDemElevationModel;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Button;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.ScrollPane;
import us.wthr.jdem846.ui.base.TextArea;
import us.wthr.jdem846.ui.base.TextField;
import us.wthr.jdem846.ui.panels.FlexGridPanel;

@SuppressWarnings("serial")
public class ModelPropertiesPanel extends FlexGridPanel
{
	private static Log log = Logging.getLog(ModelPropertiesPanel.class);
	
	private static Font textFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
	private JDemElevationModel jdemElevationModel;
	
	private PropertyTextField txtAuthor;
	private PropertyTextField txtInstitution;
	private PropertyTextField txtSubject;
	private PropertyTextArea txtDescription;
	
	private PropertyTextField txtRenderDate;
	
	private ModelPropertiesTable modelPropertiesTable;
	
	public ModelPropertiesPanel()
	{
		this(null);
	}
	
	public ModelPropertiesPanel(JDemElevationModel jdemElevationModel)
	{
		super(2);
		
		
		
		txtAuthor = new PropertyTextField();
		txtInstitution = new PropertyTextField();
		txtSubject = new PropertyTextField();
		txtDescription = new PropertyTextArea();
		txtRenderDate = new PropertyTextField();

		txtDescription.setWrapStyleWord(true);
		txtDescription.setLineWrap(true);
		txtRenderDate.setEditable(false);
		
		ScrollPane scrlDescription = new ScrollPane(txtDescription);
		scrlDescription.setPreferredSize(new Dimension(150, 100));
		
		modelPropertiesTable = new ModelPropertiesTable();
		ScrollPane scrlPropertiesTable = new ScrollPane(modelPropertiesTable);
		scrlPropertiesTable.setPreferredSize(new Dimension(150, 100));
		scrlPropertiesTable.setColumnHeaderView(null);
		
		Button btnApply = new Button("Apply");
		
		
		add(new PropertyLabel("Author:"));
		add(txtAuthor);
		
		add(new PropertyLabel("Institution:"));
		add(txtInstitution);
		
		add(new PropertyLabel("Subject:"));
		add(txtSubject);
		
		
		
		add(new PropertyLabel("Render Date:"));
		add(txtRenderDate);
		
		add(new Label());
		add(btnApply);
		
		//add(new PropertyLabel("Description:"));
		//add(scrlDescription);
		
		PropertyLabel lblDescription = new PropertyLabel("Description:");
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 2.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(2, 0, 0, 0);
		constraints.gridwidth  = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(lblDescription, constraints);
		addDirect(lblDescription);
		
		
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 3.0;
		constraints.insets = new Insets(2, 0, 2, 0);
		constraints.gridwidth  = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(scrlDescription, constraints);
		addDirect(scrlDescription);
		
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 2.0;
		constraints.weighty = 4.0;
		constraints.insets = new Insets(2, 0, 2, 0);
		constraints.gridwidth  = 2;//GridBagConstraints.REMAINDER;
		gridbag.setConstraints(scrlPropertiesTable, constraints);
		addDirect(scrlPropertiesTable);
		
		
		txtAuthor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				updateModelPropertiesFromUI();
				modelPropertiesTable.updateData();
			}
		});
		
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				log.info("On Apply Properties");
				updateModelPropertiesFromUI();
				modelPropertiesTable.updateData();
			}
		});
		
		modelPropertiesTable.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e)
			{
				updatePropertiesUI();
			}
		});

		this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		
		this.setJdemElevationModel(jdemElevationModel);

	}

	public void updateModelPropertiesFromUI()
	{
		if (jdemElevationModel == null) {
			return;
		}
		
		jdemElevationModel.setProperty("author", txtAuthor.getText());
		jdemElevationModel.setProperty("institution", txtInstitution.getText());
		jdemElevationModel.setProperty("subject", txtSubject.getText());
		jdemElevationModel.setProperty("description", txtDescription.getText());
	}
	
	public void updatePropertiesUI()
	{
		if (jdemElevationModel == null) {
			return;
		}
		
		if (jdemElevationModel.hasProperty("author")) {
			txtAuthor.setText(jdemElevationModel.getProperty("author"));
			txtAuthor.setCaretPosition(0);
		}
		
		if (jdemElevationModel.hasProperty("institution")) {
			txtInstitution.setText(jdemElevationModel.getProperty("institution"));
			txtInstitution.setCaretPosition(0);
		}
		
		if (jdemElevationModel.hasProperty("subject")) {
			txtSubject.setText(jdemElevationModel.getProperty("subject"));
			txtSubject.setCaretPosition(0);
		}
		
		if (jdemElevationModel.hasProperty("description")) {
			txtDescription.setText(jdemElevationModel.getProperty("description"));
			txtDescription.setCaretPosition(0);
		}
		
		if (jdemElevationModel.hasProperty("render-date")) {
			txtRenderDate.setText(jdemElevationModel.getProperty("render-date"));
			txtRenderDate.setCaretPosition(0);
		}
		
		
		
	}
	
	public JDemElevationModel getJdemElevationModel()
	{
		return jdemElevationModel;
	}

	public void setJdemElevationModel(JDemElevationModel jdemElevationModel)
	{
		this.jdemElevationModel = jdemElevationModel;
		updatePropertiesUI();
		
		modelPropertiesTable.setJdemElevationModel(jdemElevationModel);
	}
	
	
	class PropertyTextArea extends TextArea
	{
		public PropertyTextArea()
		{
			setFont(textFont);
		}
	}
	
	class PropertyTextField extends TextField
	{
		public PropertyTextField()
		{
			setFont(textFont);
		}
	}
	
	class PropertyLabel extends Label
	{
		
		public PropertyLabel(String text)
		{
			super(text);
			setFont(textFont);
		}
		
	}
	
}
