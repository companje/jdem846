package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Button;
import us.wthr.jdem846.ui.base.ColorChooser;
import us.wthr.jdem846.ui.base.Dialog;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.util.ColorSerializationUtil;

@SuppressWarnings("serial")
public class ColorSelection extends Panel
{
	private static Log log = Logging.getLog(ColorSelection.class);
	
	private Color value = Color.BLUE;
	private ColorPickerDialog picker = null;
	
	private ColorSamplePane colorSamplePane;
	private Button btnChange;
	
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	public ColorSelection()
	{
		
		
		
		
		colorSamplePane = new ColorSamplePane();
		btnChange = new Button(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.backgroundColorCombo.edit.label"));
		btnChange.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.backgroundColorCombo.edit.tooltip"));
		
		picker = new ColorPickerDialog(value);
		picker.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				onColorSelected();
			}
		});
		
		colorSamplePane.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e)
			{
				onColorClicked();
			}
		});
		btnChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onColorClicked();
			}
		});
		
		
		GridBagLayout gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.75;

		gridbag.setConstraints(colorSamplePane, constraints);
		add(colorSamplePane);
		
		constraints.weightx = 0.25;
		constraints.gridwidth  = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(btnChange, constraints);
		add(btnChange);

	}

	protected void onColorClicked()
	{
		picker.setVisible(true);
	}
	
	protected void onColorSelected()
	{
		picker.setVisible(false);
		
		setValue(picker.getSelectedColor());
		repaint();
		fireChangeListeners();
	}
	
	public Color getValue()
	{
		return value;
	}

	public String getValueString()
	{
		return ColorSerializationUtil.colorToString(getValue());
	}
	
	public void setValue(Color value)
	{
		this.value = value;
		colorSamplePane.setColor(value);
		colorSamplePane.repaint();
	}
	
	public void setValueString(String valueString)
	{
		Color color = ColorSerializationUtil.stringToColor(valueString);
		
		if (color != null) {
			setValue(color);
		}
		
	}
	
	/*
	@Override
	public void paint(Graphics g)
	{
		g.setColor(value);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		super.paint(g);
	}
	*/
	
	public void fireChangeListeners()
	{
		ChangeEvent event = new ChangeEvent(this);
		
		for (ChangeListener listener : changeListeners)
		{
			listener.stateChanged(event);
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
	
	
	class ColorSamplePane extends Panel
	{
		private Color color = Color.BLUE;
		
		
		public ColorSamplePane()
		{
			setBorder(BorderFactory.createEtchedBorder());
			this.setOpaque(false);
		}
		
		public void setColor(Color color)
		{
			this.color = color;
		}
		
		public Color getColor()
		{
			return color;
		}
		
		@Override
		public void paint(Graphics g)
		{
			g.setColor(color);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			super.paint(g);
		}
	}
	
	
	
	
	
	
	
	
	
	
	class ColorPickerDialog extends Dialog
	{
		
		private ColorChooser colorChooser;
		
		public ColorPickerDialog(Color selection)
		{
			
			colorChooser = new ColorChooser(selection);
			colorChooser.setPreviewPanel(new Panel());
			
			BorderLayout layout = new BorderLayout();
			setLayout(layout);
			
			add(colorChooser, BorderLayout.CENTER);
			doLayout();
			pack();
			this.setLocationRelativeTo(null);
		}
		
		public Color getSelectedColor()
		{
			return colorChooser.getSelectionModel().getSelectedColor();
		}
		
		public void addChangeListener(ChangeListener listener)
		{
			colorChooser.getSelectionModel().addChangeListener(listener);
		}
		
	}
}
